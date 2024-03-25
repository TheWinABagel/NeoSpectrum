package de.dafuqs.spectrum.items.map;

import com.mojang.datafixers.util.Pair;
import de.dafuqs.spectrum.mixin.accessors.MapStateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ArtisansAtlasState extends MapItemSavedData {

    private final MapStateAccessor accessor;
    private final Set<StructureStart> targets;
    private BlockPos displayedCenter;
    private ResourceLocation targetId;
    @Nullable
    private Vec3i displayDelta;
    @Nullable
    private StructureLocatorAsync locator;

    public ArtisansAtlasState(byte scale, boolean locked, ResourceKey<Level> dimension) {
        this(0, 0, scale, false, false, locked, dimension);
    }

    public ArtisansAtlasState(double centerX, double centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked, ResourceKey<Level> dimension) {
        super((int) centerX, (int) centerZ, scale, showIcons, unlimitedTracking, locked, dimension);
        this.accessor = (MapStateAccessor) this;
        this.targets = new HashSet<>();
        this.displayedCenter = new BlockPos((int) centerX, 0, (int) centerZ);
        this.displayDelta = null;
        this.locator = null;
    }

    public ArtisansAtlasState(double centerX, double centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked, ResourceKey<Level> dimension, CompoundTag nbt) {
        this((int) centerX, (int) centerZ, scale, showIcons, unlimitedTracking, locked, dimension);
	
		// We'll use the colors from nbt
		this.displayDelta = Vec3i.ZERO;
	
		if (nbt.contains("targetId", Tag.TAG_STRING)) {
			this.targetId = new ResourceLocation(nbt.getString("targetId"));
		} else {
			this.targetId = null;
		}
	
		int xDisplay = nbt.contains("displayX", Tag.TAG_ANY_NUMERIC) ? nbt.getInt("displayX") : this.displayedCenter.getX();
		int zDisplay = nbt.contains("displayZ", Tag.TAG_ANY_NUMERIC) ? nbt.getInt("displayZ") : this.displayedCenter.getZ();
		this.displayedCenter = new BlockPos(xDisplay, 0, zDisplay);
	}

    public static @Nullable Pair<ResourceLocation, StructureStart> locateAnyStructureAtBlock(ServerLevel world, BlockPos pos) {
        Registry<Structure> registry = world.registryAccess().registry(Registries.STRUCTURE).orElse(null);
        if (registry != null) {
            for (Structure structure : registry.stream().toList()) {
                ResourceLocation id = registry.getKey(structure);
                StructureStart start = world.structureManager().getStructureWithPieceAt(pos, structure);
                if (start != StructureStart.INVALID_START && id != null) {
                    return new Pair<>(id, start);
                }
            }
        }
        return null;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt = super.save(nbt);

        nbt.putBoolean("isArtisansAtlas", true);

        nbt.putInt("displayX", displayedCenter.getX());
        nbt.putInt("displayZ", displayedCenter.getZ());

        if (this.targetId != null) {
            nbt.putString("targetId", targetId.toString());
        }

        return nbt;
    }

    @Override
    public MapItemSavedData scaled(int zoomOutScale) {
        return createFresh(this.centerX, this.centerZ, (byte) Mth.clamp(this.scale + zoomOutScale, 0, 4), accessor.getShowIcons(), accessor.getUnlimitedTracking(), this.dimension);
    }

    @Override
    public void tickCarriedBy(Player player, ItemStack stack) {
        if (this.displayDelta != null) {
            if (this.locator == null && this.targetId != null && player.level() instanceof ServerLevel world) {
                startLocator(world);
            }

            this.displayDelta = player.blockPosition().subtract(this.displayedCenter);
        } else {
            this.displayedCenter = player.blockPosition();
        }

        this.accessor.getIcons().clear();

        super.tickCarriedBy(player, stack);

        for (StructureStart target : this.targets) {
            addTargetIcon(player.level(), target);
        }
    }

    @Override
    public void addDecoration(MapDecoration.Type type, @Nullable LevelAccessor world, String key, double x, double z, double rotation, @Nullable Component text) {
        int scale = 1 << this.scale;

        float scaledX = (float)(x - this.displayedCenter.getX()) / scale;
        float scaledZ = (float)(z - this.displayedCenter.getZ()) / scale;

        byte pixelX = (byte)(scaledX * 2.0F + 0.5F);
        byte pixelZ = (byte)(scaledZ * 2.0F + 0.5F);

        rotation += rotation < 0.0 ? -8.0 : 8.0;
        byte rotationByte = (byte)(rotation * 16.0 / 360.0);
        if (this.dimension == Level.NETHER && world != null) {
            int light = (int)(world.getLevelData().getDayTime() / 10L);
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

            if (type == MapDecoration.Type.PLAYER) {
                type = MapDecoration.Type.PLAYER_OFF_MAP;
                rotationByte = 0;
            } else if (type == MapDecoration.Type.TARGET_POINT) {
                borderRotation += borderRotation < 0.0 ? -8.0 : 8.0;
                rotationByte = (byte)(borderRotation * 16.0 / 360.0);
            }
        }

        MapDecoration icon = new MapDecoration(type, pixelX, pixelZ, rotationByte, text);
        MapDecoration previousIcon = accessor.getIcons().put(key, icon);
        if (!icon.equals(previousIcon)) {
            if (previousIcon != null && previousIcon.getType().shouldTrackCount()) {
                accessor.setIconCount(accessor.getIconCount() - 1);
            }

            if (type.shouldTrackCount()) {
                accessor.setIconCount(accessor.getIconCount() + 1);
            }

            accessor.invokeMarkIconsDirty();
        }
    }

    @Override
    public boolean toggleBanner(LevelAccessor world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;

        int scale = 1 << this.scale;
        double scaledX = (x - this.displayedCenter.getX()) / scale;
        double scaledZ = (z - this.displayedCenter.getZ()) / scale;

        if (scaledX >= -63.0 && scaledZ >= -63.0 && scaledX <= 63.0 && scaledZ <= 63.0) {
            MapBanner marker = MapBanner.fromWorld(world, pos);
            if (marker == null) {
                return false;
            }

            String key = marker.getId();

            if (accessor.getBanners().remove(key, marker)) {
                accessor.invokeRemoveIcon(marker.getId());
                return true;
            }

            if (!this.isTrackedCountOverLimit(256)) {
                accessor.getBanners().put(key, marker);
                this.addDecoration(marker.getDecoration(), world, key, x, z, 180.0, marker.getName());
                return true;
            }
        }

        return false;
    }

    private void addTargetIcon(LevelAccessor world, StructureStart target) {
        if (target != null) {
            addDecoration(MapDecoration.Type.TARGET_POINT, world, getTargetKey(target), target.getChunkPos().getMiddleBlockX(), target.getChunkPos().getMiddleBlockZ(), 180, null);
        }
    }

    private String getTargetKey(StructureStart start) {
        return String.format("target-%d-%d", start.getChunkPos().x, start.getChunkPos().z);
    }

    public void startLocator(ServerLevel world) {
        if (targetId == null) return;
        this.locator = new StructureLocatorAsync(world, this::addTarget, this.targetId, new ChunkPos(this.displayedCenter), 32);
    }

    public void cancelLocator() {
        if (this.locator != null) {
            this.locator.cancel();
        }
    }

    public BlockPos getDisplayedCenter() {
        return this.displayedCenter;
    }

    public void addTarget(LevelAccessor world, StructureStart target) {
        this.targets.add(target);
        addTargetIcon(world, target);
    }
    
    public void setTargetId(@Nullable ResourceLocation targetId) {
        if (this.targetId != targetId) {
            this.targetId = targetId;
            this.setDirty();
        }
    }
    
    public @Nullable ResourceLocation getTargetId() {
        return this.targetId;
    }
    
    @Nullable
    public Vec3i getDisplayDelta() {
        return this.displayDelta;
    }
    
    public void clearDisplayDelta() {
        if (this.displayDelta != null) {
            int sampleSize = 1 << this.scale;

            Vec3i remainder = new Vec3i(this.displayDelta.getX() % sampleSize, 0, this.displayDelta.getZ() % sampleSize);
            Vec3i delta = this.displayDelta.subtract(remainder);
            BlockPos newDisplayedCenter = this.displayedCenter.offset(delta);

            if (this.locator != null) {
                SectionPos startChunk = SectionPos.of(this.displayedCenter);
                SectionPos endChunk = SectionPos.of(newDisplayedCenter);
                this.locator.move(endChunk.getX() - startChunk.getX(), endChunk.getZ() - startChunk.getZ());
            }

            this.displayDelta = remainder;
            this.displayedCenter = newDisplayedCenter;
        } else {
            this.displayDelta = Vec3i.ZERO;
        }
    }

    public void updateDimension(ResourceKey<Level> dimension) {
        if (!this.dimension.equals(dimension)) {
            this.dimension = dimension;
            this.displayDelta = null;
            this.targets.clear();
            this.targetId = null;
            this.setDirty();
        }
    }

}
