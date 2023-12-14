package de.dafuqs.spectrum.items.map;

import com.mojang.datafixers.util.Pair;
import de.dafuqs.spectrum.mixin.accessors.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.map.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.server.world.*;
import net.minecraft.structure.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.structure.*;
import org.jetbrains.annotations.*;

public class StructureMapState extends MapState {

    private final MapStateAccessor accessor;
    private BlockPos displayedCenter;
    @Nullable
    private StructureStart target;
    private Identifier targetId;
    private boolean displayNeedsUpdate;

    public StructureMapState(double centerX, double centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked, RegistryKey<World> dimension) {
        super((int) centerX, (int) centerZ, scale, showIcons, unlimitedTracking, locked, dimension);
        this.accessor = (MapStateAccessor) this;
        this.displayedCenter = new BlockPos((int) centerX, 0, (int) centerZ);
        this.displayNeedsUpdate = true;
    }

    public StructureMapState(double centerX, double centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked, RegistryKey<World> dimension, NbtCompound nbt) {
        this((int) centerX, (int) centerZ, scale, showIcons, unlimitedTracking, locked, dimension);

        if (nbt.contains("targetId", NbtElement.STRING_TYPE)) {
            this.targetId = new Identifier(nbt.getString("targetId"));
        } else {
            this.targetId = null;
        }
        this.target = null;

        int xDisplay = nbt.contains("displayX", NbtElement.INT_TYPE) ? nbt.getInt("displayX") : this.displayedCenter.getX();
        int zDisplay = nbt.contains("displayZ", NbtElement.INT_TYPE) ? nbt.getInt("displayZ") : this.displayedCenter.getZ();
        this.displayedCenter = new BlockPos(xDisplay, 0, zDisplay);
    }

    public static @Nullable Pair<Identifier, StructureStart> locateAnyStructureAtBlock(ServerWorld world, BlockPos pos) {
        Registry<Structure> registry = world.getRegistryManager().getOptional(RegistryKeys.STRUCTURE).orElse(null);
        if (registry != null) {
            for (Structure structure : registry.stream().toList()) {
                Identifier id = registry.getId(structure);
                StructureStart start = world.getStructureAccessor().getStructureContaining(pos, structure);
                if (start != StructureStart.DEFAULT && id != null) {
                    return new Pair<>(id, start);
                }
            }
        }
        return null;
    }

    public static @Nullable StructureStart locateNearestStructure(ServerWorld world, Identifier structureId, BlockPos center, int radius) {
        Registry<Structure> registry = getStructureRegistry(world);
        if (registry != null) {
            return locateNearestStructure(world, registry.get(structureId), center, radius);
        }
        return null;
    }

    public static @Nullable StructureStart locateNearestStructure(ServerWorld world, Structure structure, BlockPos center, int radius) {
        if (world.getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
            Registry<Structure> registry = getStructureRegistry(world);
            if (registry != null) {
                RegistryEntryList<Structure> entryList = null; //new RegistryEntryList.Direct<>(List.of(registry.getEntry(structure)));
                Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(world, entryList, center, radius, false);
                if (pair != null) {
                    BlockPos pos = pair.getFirst();
                    return locateStructureAtBlock(world, structure, pos);
                }
            }
        }
        return null;
    }

    public static @Nullable StructureStart locateStructureAtBlock(ServerWorld world, Structure structure, BlockPos pos) {
        Registry<Structure> registry = getStructureRegistry(world);
        if (registry != null) {
            for (StructureStart start : world.getStructureAccessor().getStructureStarts(ChunkSectionPos.from(pos), structure)) {
                if (start == StructureStart.DEFAULT) continue;
                for (StructurePiece piece : start.getChildren()) {
                    BlockBox box = piece.getBoundingBox();
                    if (box.getMinX() <= pos.getX() && pos.getX() <= box.getMaxX() && box.getMinZ() <= pos.getZ() && pos.getZ() <= box.getMaxZ()) {
                        return start;
                    }
                }
            }
        }
        return null;
    }

    public static @Nullable Registry<Structure> getStructureRegistry(ServerWorld world) {
        return world.getRegistryManager().getOptional(RegistryKeys.STRUCTURE).orElse(null);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt = super.writeNbt(nbt);

        nbt.putBoolean("isSpectrumMap", true);

        nbt.putInt("displayX", displayedCenter.getX());
        nbt.putInt("displayZ", displayedCenter.getZ());

        if (this.targetId != null) {
            nbt.putString("targetId", targetId.toString());
        }

        return nbt;
    }

    @Override
    public MapState zoomOut(int zoomOutScale) {
        return of(this.centerX, this.centerZ, (byte) MathHelper.clamp(this.scale + zoomOutScale, 0, 4), accessor.getShowIcons(), accessor.getUnlimitedTracking(), this.dimension);
    }

    @Override
    public void update(PlayerEntity player, ItemStack stack) {
        BlockPos oldBlockPos = this.displayedCenter;
        BlockPos newBlockPos = player.getBlockPos();
        if (oldBlockPos.getX() != newBlockPos.getX() || oldBlockPos.getZ() != newBlockPos.getZ()) {
            this.displayNeedsUpdate = true;
            this.displayedCenter = newBlockPos;
            accessor.getIcons().clear();
        }
        super.update(player, stack);
    }

    @Override
    public void addIcon(MapIcon.Type type, @Nullable WorldAccess world, String key, double x, double z, double rotation, @Nullable Text text) {
        int scale = 1 << this.scale;

        float scaledX = (float)(x - this.displayedCenter.getX()) / scale;
        float scaledZ = (float)(z - this.displayedCenter.getZ()) / scale;

        byte pixelX = (byte)(scaledX * 2.0F + 0.5F);
        byte pixelZ = (byte)(scaledZ * 2.0F + 0.5F);

        rotation += rotation < 0.0 ? -8.0 : 8.0;
        byte rotationByte = (byte)(rotation * 16.0 / 360.0);
        if (this.dimension == World.NETHER && world != null) {
            int light = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
            rotationByte = (byte)(light * light * 34187121 + light * 121 >> 15 & 15);
        }

        if (scaledX < -63.0F || scaledZ < -63.0F || scaledX > 63.0F || scaledZ > 63.0F) {
            double borderRotation;
            if (scaledZ >= 63.0F) {
                pixelZ = 127;
                if (scaledX <= -63.0F) {
                    pixelX = -128;
                    borderRotation = -135.0F;
                } else if (scaledX >= 63.0F) {
                    pixelX = 127;
                    borderRotation = 135.0F;
                } else {
                    borderRotation = 180.0F;
                }
            } else if (scaledZ <= -63.0F) {
                pixelZ = -128;
                if (scaledX <= -63.0F) {
                    pixelX = -128;
                    borderRotation = -45.0F;
                } else if (scaledX >= 63.0F) {
                    pixelX = 127;
                    borderRotation = 45.0F;
                } else {
                    borderRotation = 0;
                }
            } else if (scaledX <= -63.0F) {
                pixelX = -128;
                borderRotation = -90.0F;
            } else {
                pixelX = 127;
                borderRotation = 90.0F;
            }

            if (type == MapIcon.Type.PLAYER) {
                type = MapIcon.Type.PLAYER_OFF_MAP;
                rotationByte = 0;
            } else if (type == MapIcon.Type.TARGET_POINT) {
                borderRotation += borderRotation < 0.0 ? -8.0 : 8.0;
                rotationByte = (byte)(borderRotation * 16.0 / 360.0);
            }
        }

        MapIcon icon = new MapIcon(type, pixelX, pixelZ, rotationByte, text);
        MapIcon previousIcon = accessor.getIcons().put(key, icon);
        if (!icon.equals(previousIcon)) {
            if (previousIcon != null && previousIcon.getType().shouldUseIconCountLimit()) {
                accessor.setIconCount(accessor.getIconCount() - 1);
            }

            if (type.shouldUseIconCountLimit()) {
                accessor.setIconCount(accessor.getIconCount() + 1);
            }

            accessor.invokeMarkIconsDirty();
        }
    }

    @Override
    public boolean addBanner(WorldAccess world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;

        int scale = 1 << this.scale;
        double scaledX = (x - this.displayedCenter.getX()) / scale;
        double scaledZ = (z - this.displayedCenter.getZ()) / scale;

        if (scaledX >= -63.0 && scaledZ >= -63.0 && scaledX <= 63.0 && scaledZ <= 63.0) {
            MapBannerMarker marker = MapBannerMarker.fromWorldBlock(world, pos);
            if (marker == null) {
                return false;
            }

            String key = marker.getKey();

            if (accessor.getBanners().remove(key, marker)) {
                accessor.invokeRemoveIcon(marker.getKey());
                return true;
            }

            if (!this.iconCountNotLessThan(256)) {
                accessor.getBanners().put(key, marker);
                this.addIcon(marker.getIconType(), world, key, x, z, 180.0, marker.getName());
                return true;
            }
        }

        return false;
    }

    public static void removeDecorationsNbt(ItemStack stack, String id) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("Decorations", NbtElement.LIST_TYPE)) {
            NbtList decorations = nbt.getList("Decorations", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < decorations.size(); i++) {
                NbtCompound decoration = decorations.getCompound(i);
                if (decoration.contains("id", NbtElement.STRING_TYPE)) {
                    String decorationId = decoration.getString("id");
                    if (decorationId.equals(id)) {
                        decorations.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public BlockPos getDisplayedCenter() {
        return this.displayedCenter;
    }

    public @Nullable StructureStart getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable StructureStart target) {
        this.target = target;
    }

    public @Nullable Identifier getTargetId() {
        return this.targetId;
    }

    public void setTargetId(@Nullable Identifier targetId) {
        if (this.targetId != targetId) {
            this.targetId = targetId;
            this.markDirty();
        }
    }

    public boolean displayNeedsUpdate() {
        return this.displayNeedsUpdate;
    }

    public void markDisplayUpdated() {
        this.displayNeedsUpdate = false;
    }

}
