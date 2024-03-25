package de.dafuqs.spectrum.items.map;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.datafixers.util.Pair;
import de.dafuqs.spectrum.registries.SpectrumStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtisansAtlasItem extends MapItem {
    
    public static final int COOLDOWN_DURATION_TICKS = 20;
    
    public ArtisansAtlasItem(Properties settings) {
        super(settings);
    }
    
    private static void createAndSetState(ItemStack stack, ServerLevel world, int centerX, int centerZ, @Nullable StructureStart target, @Nullable ResourceLocation targetId) {
        CompoundTag nbt = stack.getOrCreateTag();
        
        int id;
        if (nbt.contains("map")) {
            id = nbt.getInt("map");
        } else {
            id = world.getFreeMapId();
            nbt.putInt("map", id);
        }

        ArtisansAtlasState state = new ArtisansAtlasState(centerX, centerZ, (byte) 1, true, true, false, world.dimension());

        state.setTargetId(targetId);
        if (targetId != null) {
            state.startLocator(world);
            if (target != null) {
                state.addTarget(world, target);
            }
        } else {
            state.cancelLocator();
        }

        world.setMapData(makeKey(id), state);
    }

    @Override
    public void update(Level world, Entity entity, MapItemSavedData state) {
        if (world.dimension() != state.dimension || !(entity instanceof Player playerEntity) || !(state instanceof ArtisansAtlasState atlasState)) {
            return;
        }

        int sampleSize = 1 << state.scale;
        MapItemSavedData.HoldingPlayer playerUpdateTracker = state.getHoldingPlayer(playerEntity);
        playerUpdateTracker.step++;

        Vec3i delta = atlasState.getDisplayDelta();
        if (delta == null) {
            // Delta is null when the state is first created, so update the whole thing
            delta = entity.blockPosition().subtract(atlasState.getDisplayedCenter());
            int deltaX = delta.getX() / sampleSize;
            int deltaZ = delta.getZ() / sampleSize;

            for (int x = 0; x <= 127; x++) {
                updateVerticalStrip(world, atlasState, deltaX, deltaZ, x, 0, 127);
            }

            atlasState.clearDisplayDelta();
            return;
        }

        // Re-render the part that's moved, and copy the rest
        int deltaX = delta.getX() / sampleSize;
        int deltaZ = delta.getZ() / sampleSize;

        if (deltaX < 0) {
            for (int x = 127; x >= -deltaX; x--) {
                updateOrCopyVerticalStrip(world, atlasState, deltaX, deltaZ, x, playerUpdateTracker.step);
            }
            for (int x = 0; x <= Math.min(127, -deltaX - 1); x++) {
                updateVerticalStrip(world, atlasState, deltaX, deltaZ, x, 0, 127);
            }
        } else {
            for (int x = 0; x <= 127 - deltaX; x++) {
                updateOrCopyVerticalStrip(world, atlasState, deltaX, deltaZ, x, playerUpdateTracker.step);
            }
            for (int x = Math.max(0, 127 - deltaX + 1); x <= 127; x++) {
                updateVerticalStrip(world, atlasState, deltaX, deltaZ, x, 0, 127);
            }
        }

        if (deltaX != 0 || deltaZ != 0) {
            atlasState.clearDisplayDelta();
        }
    }

    private void updateOrCopyVerticalStrip(Level world, ArtisansAtlasState state, int deltaX, int deltaZ, int x, int tick) {
        if (deltaX > 127 || deltaX < -127 || deltaZ > 127 || deltaZ < -127 || (x & 15) == (tick & 15)) {
            updateVerticalStrip(world, state, deltaX, deltaZ, x, 0, 127);
        } else if (deltaZ < 0) {
            copyVerticalStrip(state, deltaX, deltaZ, x, 127, -deltaZ);
            updateVerticalStrip(world, state, deltaX, deltaZ, x, 0, -deltaZ - 1);
        } else if (deltaZ > 0) {
            copyVerticalStrip(state, deltaX, deltaZ, x, 0, 127 - deltaZ);
            updateVerticalStrip(world, state, deltaX, deltaZ, x, 127 - deltaZ + 1, 127);
        } else if (deltaX != 0) {
            copyVerticalStrip(state, deltaX, deltaZ, x, 0, 127);
        }
    }

    private void copyVerticalStrip(ArtisansAtlasState state, int deltaX, int deltaZ, int x, int startZ, int endZ) {
        if (startZ > endZ) {
            for (int z = startZ; z >= endZ; z--) {
                state.setColor(x, z, state.colors[(x + deltaX) + (z + deltaZ) * 128]);
            }
        } else {
            for (int z = startZ; z <= endZ; z++) {
                state.setColor(x, z, state.colors[(x + deltaX) + (z + deltaZ) * 128]);
            }
        }
    }

    private void updateVerticalStrip(Level world, ArtisansAtlasState state, int deltaX, int deltaZ, int x, int startZ, int endZ) {
        double previousHeight = updateColor(world, state, deltaX, deltaZ, x, startZ - 1, 0, false);
        for (int z = startZ; z <= endZ; z++) {
            previousHeight = updateColor(world, state, deltaX, deltaZ, x, z, previousHeight, true);
        }
    }

    private double updateColor(Level world, ArtisansAtlasState state, int deltaX, int deltaZ, int x, int z, double previousHeight, boolean setColor) {
        int sampleSize = 1 << state.scale;
        int sampleArea = sampleSize * sampleSize;

        boolean hasCeiling = world.dimensionType().hasCeiling();

        int blockX = ((state.getDisplayedCenter().getX() >> state.scale) + deltaX + x - 64) * sampleSize;
        int blockZ = ((state.getDisplayedCenter().getZ() >> state.scale) + deltaZ + z - 64) * sampleSize;

        Multiset<MapColor> multiset = LinkedHashMultiset.create();
        LevelChunk chunk = world.getChunk(SectionPos.blockToSectionCoord(blockX), SectionPos.blockToSectionCoord(blockZ));
        if (chunk.isEmpty()) {
            return previousHeight;
        }

        int fluidDepth = 0;
        double height = 0.0;
        if (hasCeiling) {
            int hash = blockX + blockZ * 231871;
            hash = hash * hash * 31287121 + hash * 11;
            if ((hash >> 20 & 1) == 0) {
                multiset.add(Blocks.DIRT.defaultBlockState().getMapColor(world, BlockPos.ZERO), 10);
            } else {
                multiset.add(Blocks.STONE.defaultBlockState().getMapColor(world, BlockPos.ZERO), 100);
            }

            height = 100.0;
        } else {
            for(int sampleX = 0; sampleX < sampleSize; sampleX++) {
                for(int sampleZ = 0; sampleZ < sampleSize; sampleZ++) {
                    BlockPos.MutableBlockPos samplePos = new BlockPos.MutableBlockPos(blockX + sampleX, 0, blockZ + sampleZ);
                    int sampleY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, samplePos.getX(), samplePos.getZ()) + 1;

                    BlockState blockState;
                    if (sampleY <= world.getMinBuildHeight() + 1) {
                        blockState = Blocks.BEDROCK.defaultBlockState();
                    } else {
                        do {
                            sampleY--;
                            samplePos.setY(sampleY);
                            blockState = chunk.getBlockState(samplePos);
                        } while(blockState.getMapColor(world, samplePos) == MapColor.NONE && sampleY > world.getMinBuildHeight());

                        if (sampleY > world.getMinBuildHeight() && !blockState.getFluidState().isEmpty()) {
                            int fluidY = sampleY - 1;
                            BlockPos.MutableBlockPos fluidPos = samplePos.mutable();

                            BlockState fluidBlockState;
                            do {
                                fluidPos.setY(fluidY--);
                                fluidBlockState = chunk.getBlockState(fluidPos);
                                fluidDepth++;
                            } while(fluidY > world.getMinBuildHeight() && !fluidBlockState.getFluidState().isEmpty());

                            blockState = this.getCorrectStateForFluidBlock(world, blockState, samplePos);
                        }
                    }

                    state.checkBanners(world, samplePos.getX(), samplePos.getZ());
                    height += (double) sampleY / (double) sampleArea;
                    multiset.add(blockState.getMapColor(world, samplePos));
                }
            }
        }

        if (setColor) {
            fluidDepth /= sampleArea;

            int maxCount = 0;
            MapColor color = MapColor.NONE;
            for (Multiset.Entry<MapColor> entry : multiset.entrySet()) {
                if (entry.getCount() > maxCount) {
                    maxCount = entry.getCount();
                    color = entry.getElement();
                }
            }

            MapColor.Brightness brightness;

            int odd = ((blockX ^ blockZ) / sampleSize) & 1;
            if (color == MapColor.WATER) {
                double depth = (double) fluidDepth * 0.1 + (double) odd * 0.2;
                if (depth < 0.5) {
                    brightness = MapColor.Brightness.HIGH;
                } else if (depth > 0.9) {
                    brightness = MapColor.Brightness.LOW;
                } else {
                    brightness = MapColor.Brightness.NORMAL;
                }
            } else {
                double f = (height - previousHeight) * 4.0 / (double) (sampleSize + 4) + ((double) odd - 0.5) * 0.4;
                if (f > 0.6) {
                    brightness = MapColor.Brightness.HIGH;
                } else if (f < -0.6) {
                    brightness = MapColor.Brightness.LOW;
                } else {
                    brightness = MapColor.Brightness.NORMAL;
                }
            }

            state.setColor(x, z, color.getPackedId(brightness));
        }

        return height;
    }

    private BlockState getCorrectStateForFluidBlock(Level world, BlockState state, BlockPos pos) {
        FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && !state.isFaceSturdy(world, pos, Direction.UP) ? fluidState.createLegacyBlock() : state;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide) {
            MapItemSavedData state = getSavedData(stack, world);
            if (state instanceof ArtisansAtlasState atlasState) {
                atlasState.updateDimension(world.dimension());
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide() && context.getLevel() instanceof ServerLevel serverWorld && context.getPlayer() instanceof ServerPlayer serverPlayerEntity) {
            ItemStack stack = serverPlayerEntity.getItemInHand(context.getHand());
            if (serverPlayerEntity.isShiftKeyDown()) {
                Vec3 hitPos = context.getClickLocation();
                BlockPos blockPos = BlockPos.containing(hitPos.x(), hitPos.y(), hitPos.z());
                Pair<ResourceLocation, StructureStart> pair = ArtisansAtlasState.locateAnyStructureAtBlock(serverWorld, blockPos);
                if (pair != null) {
                    ResourceLocation structureId = pair.getFirst();
                    if (SpectrumStructureTags.isIn(serverWorld, structureId, SpectrumStructureTags.UNLOCATABLE)) {
                        serverPlayerEntity.displayClientMessage(Component.translatable("item.spectrum.artisans_atlas.unlocatable"), true);
                    } else {
                        serverPlayerEntity.displayClientMessage(Component.translatable("item.spectrum.artisans_atlas.set_structure").append(Component.translatable(structureId.toLanguageKey("structure"))), true);
                        createAndSetState(stack, serverWorld, (int) serverPlayerEntity.getX(), (int) serverPlayerEntity.getZ(), pair.getSecond(), pair.getFirst());
                    }
                }
    
                serverPlayerEntity.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_DURATION_TICKS);
            }
        }

        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
    
        if (!world.isClientSide() && world instanceof ServerLevel serverWorld && user instanceof ServerPlayer serverPlayerEntity) {
            if (user.isShiftKeyDown()) {
                createAndSetState(stack, serverWorld, (int) serverPlayerEntity.getX(), (int) serverPlayerEntity.getZ(), null, null);
            }
        }
    
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        
        MapItemSavedData state = getSavedData(stack, world);
        if (state instanceof ArtisansAtlasState atlasState) {
            ResourceLocation structureId = atlasState.getTargetId();
            if (structureId == null) {
                tooltip.add(Component.translatable("item.spectrum.artisans_atlas.empty"));
            } else {
                tooltip.add(Component.translatable("item.spectrum.artisans_atlas.locates_structure").append(Component.translatable(structureId.toLanguageKey("structure"))));
            }
        }
        
    }
    
}
