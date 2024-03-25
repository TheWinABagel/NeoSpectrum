package de.dafuqs.spectrum.items.map;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureLocatorAsync {

    private final MinecraftServer server;
    private final ServerLevel world;
    private final StructureLocatorAsync.Acceptor acceptor;
    private final ResourceLocation targetId;
    private final int maxRadius;
    private ChunkPos center;
    private Holder<Structure> registryEntry;

    @Nullable
    private LocatorThread thread;
    private int radius;

    public StructureLocatorAsync(ServerLevel world, StructureLocatorAsync.Acceptor acceptor, ResourceLocation targetId, ChunkPos center, int maxRadius) {
        this.server = world.getServer();
        this.world = world;
        this.acceptor = acceptor;
        this.targetId = targetId;
        this.center = center;
        this.maxRadius = maxRadius;

        thread = null;
        radius = 1;

        start();
    }

    private void start() {
        thread = new LocatorThread();
        thread.start();
    }

    public void move(int deltaX, int deltaZ) {
        if (deltaX == 0 && deltaZ == 0) return;

        cancel();

        // If we move two chunks in a direction, continuing at the same radius would skip a strip of chunks.
        // So, we reduce the radius to make sure nothing is skipped. Of course, outer chunks would get
        // skipped regardless.
        radius -= Math.max(Math.abs(deltaX), Math.abs(deltaZ));
        if (radius < 1) radius = 1;

        center = new ChunkPos(center.x + deltaX, center.z + deltaZ);

        start();
    }

    public void cancel() {
        if (thread == null) return;

        thread.stopRunning();
        thread.interrupt();

        while (true) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
                continue;
            }
            break;
        }

        thread = null;
    }

    private class LocatorThread extends Thread {

        private static final int MAX_RUNNING_TASKS = 32;
        private static final AtomicInteger currentRunningThreads = new AtomicInteger(0);

        private final Semaphore semaphore;
        private boolean running;
        private boolean ringHadTargets;

        public LocatorThread() {
            super("Structure Locator #" + currentRunningThreads.getAndIncrement());
            setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(SpectrumCommon.LOGGER));
            semaphore = new Semaphore(MAX_RUNNING_TASKS);
        }

        public void stopRunning() {
            running = false;
        }

        @Override
        public void run() {
            running = true;
            ringHadTargets = false;

            registryEntry = getRegistryEntry();
            if (registryEntry == null) return;

            checkConcentricRingsStructures();

            while(running && !ringHadTargets && radius <= maxRadius) {
                for (int i = 0; running && i < radius * 2; i++) {
                    searchChunk(center.x - radius + i, center.z + radius);     // Top-left     -> Top-right
                    searchChunk(center.x + radius,     center.z + radius - i); // Top-right    -> Bottom-right
                    searchChunk(center.x + radius - i, center.z - radius);     // Bottom-right -> Bottom-left
                    searchChunk(center.x - radius,     center.z - radius + i); // Bottom-left  -> Top-left
                }

                radius++;
            }
        }

        private Holder<Structure> getRegistryEntry() {
            Registry<Structure> registry = world.registryAccess().registry(Registries.STRUCTURE).orElse(null);
            if (registry == null) return null;

            Structure structure = registry.get(targetId);
            if (structure == null) return null;

            return registry.wrapAsHolder(structure);
        }

        private void checkConcentricRingsStructures() {
            ChunkGeneratorStructureState calculator = world.getChunkSource().getGeneratorState();

            double minDistance = Double.MAX_VALUE;
            StructureStart concentricStart = null;

            for (StructurePlacement placement : calculator.getPlacementsForStructure(registryEntry)) {
                if (placement instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
                    List<ChunkPos> positions = calculator.getRingPositionsFor(concentricRingsStructurePlacement);
                    if (positions != null) {
                        for (ChunkPos pos : positions) {
                            double dx = (double) pos.x - (double) center.x;
                            double dz = (double) pos.z - (double) center.z;
                            double distance = dx * dx + dz * dz;
                            if (distance < minDistance) {
                                minDistance = distance;
                                concentricStart = locateStructureAtChunk(pos);
                            }
                        }
                    }
                }
            }

            if (concentricStart != null) {
                acceptTarget(concentricStart);
            }
        }

        private void searchChunk(int x, int z) {
            while (running) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ignored) {
                    continue;
                }

                server.tell(new TickTask(server.getTickCount(), () -> {
                    StructureStart target = locateStructureAtChunk(new ChunkPos(x, z));
                    if (target != null) {
                        acceptTarget(target);
                    }

                    semaphore.release();
                }));

                break;
            }
        }

        @Nullable
        private StructureStart locateStructureAtChunk(ChunkPos pos) {
            StructureManager accessor = world.structureManager();
            Structure structure = registryEntry.value();

            StructureCheckResult presence = accessor.checkStructurePresence(pos, structure, false);
            if (presence == StructureCheckResult.START_NOT_PRESENT) return null;

            ChunkAccess chunk = world.getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS);
            return accessor.getStartForStructure(SectionPos.bottomOf(chunk), structure, chunk);
        }

        private void acceptTarget(StructureStart target) {
            synchronized (this) {
                if (running) {
                    ringHadTargets = true;
                    acceptor.accept(world, target);
                }
            }
        }

    }

    public interface Acceptor {
        void accept(LevelAccessor world, StructureStart target);
    }

}
