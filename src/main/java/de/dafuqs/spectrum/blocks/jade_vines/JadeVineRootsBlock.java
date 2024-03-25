package de.dafuqs.spectrum.blocks.jade_vines;

import de.dafuqs.spectrum.api.interaction.NaturesStaffTriggered;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JadeVineRootsBlock extends BaseEntityBlock implements JadeVine, NaturesStaffTriggered {

    public static final BooleanProperty DEAD = JadeVine.DEAD;

    public JadeVineRootsBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(DEAD, false));
    }

    public static boolean canBePlantedOn(BlockState blockState) {
        return blockState.is(BlockTags.WOODEN_FENCES);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (!state.getValue(DEAD)) {
            JadeVine.spawnParticlesClient(world, pos);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return SpectrumItems.GERMINATED_JADE_VINE_BULB.getDefaultInstance();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        if (oldState.getBlock() instanceof FenceBlock) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof JadeVineRootsBlockEntity jadeVineRootsBlockEntity) {
                jadeVineRootsBlockEntity.setFenceBlockState(oldState.getBlock().defaultBlockState());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.is(this)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof JadeVineRootsBlockEntity jadeVineRootsBlockEntity) {
                world.setBlockAndUpdate(pos, jadeVineRootsBlockEntity.getFenceBlockState());
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader world, BlockPos pos) {
        return canBePlantedOn(world.getBlockState(pos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(DEAD);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new JadeVineRootsBlockEntity(pos, state);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(DEAD);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);

        if (!world.isClientSide && isRandomlyTicking(state)) {
            // die in sunlight, or then the bulb / plant was destroyed
            int age = getAge(world, pos, state);
            if (JadeVine.isExposedToSunlight(world, pos) || age < 0) {
                exposedToSunlight(world, pos);
            } else if (canGrow(world, pos)) {
                if (world.random.nextBoolean() && tryGrowUpwards(state, world, pos)) {
                    rememberGrownTime(world, pos);
                    world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.5F, 0.9F + 0.2F * world.random.nextFloat() * 0.2F);
                } else if (tryGrowDownwards(state, world, pos)) {
                    rememberGrownTime(world, pos);
                    world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.5F, 0.9F + 0.2F * world.random.nextFloat() * 0.2F);
                } else {
                    int targetAge = age;
                    if (age == BlockStateProperties.MAX_AGE_7 - 1) {
                        // only reach full bloom on full moon nights
                        if (world.getMoonPhase() == 0) { // 0 = full moon
                            targetAge = BlockStateProperties.MAX_AGE_7;
                        }
                    } else if (age == BlockStateProperties.MAX_AGE_7) {
                        // 2 days after full moon: revert to petal stage
                        if (world.getMoonPhase() > 2) {
                            targetAge = BlockStateProperties.MAX_AGE_7 - 1;
                        }
                    } else {
                        targetAge = age + 1;
                    }
                    if (targetAge != age) {
                        boolean couldGrow = setPlantToAge(world, pos, targetAge);
                        if (couldGrow) {
                            world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.5F, 0.9F + 0.2F * world.random.nextFloat() * 0.2F);
                        }
                    }
                    rememberGrownTime(world, pos);
                }
            }
        }
    }

    private void exposedToSunlight(ServerLevel world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(getLowestRootsPos(world, pos));
        if (blockEntity instanceof JadeVineRootsBlockEntity jadeVineRootsBlockEntity) {
            if (jadeVineRootsBlockEntity.wasExposedToSunlight()) {
                setDead(world, pos);
                world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.5F, 0.9F + 0.2F * world.random.nextFloat() * 0.2F);
            } else {
                jadeVineRootsBlockEntity.setExposedToSunlight(true);
            }
        }
    }

    boolean setPlantToAge(@NotNull ServerLevel world, @NotNull BlockPos blockPos, int age) {
        setToAge(world, blockPos, age);

        boolean anyGrown = false;

        // all upper roots
        int i = 1;
        while (true) {
            BlockPos upPos = blockPos.above(i);
            BlockState upState = world.getBlockState(upPos);
            if (upState.getBlock() instanceof JadeVineRootsBlock jadeVineRootsBlock) {
                if (jadeVineRootsBlock.setToAge(world, upPos, age)) {
                    anyGrown = true;
                    JadeVine.spawnParticlesServer(world, upPos, 8);
                }
            } else {
                break;
            }
            i++;
        }

        // all lower roots
        i = 1;
        while (true) {
            BlockPos downPos = blockPos.below(i);
            BlockState downState = world.getBlockState(downPos);
            if (downState.getBlock() instanceof JadeVineRootsBlock jadeVineRootsBlock) {
                if (jadeVineRootsBlock.setToAge(world, downPos, age)) {
                    anyGrown = true;
                    JadeVine.spawnParticlesServer(world, downPos, 8);
                }
            } else {
                break;
            }
            i++;
        }

        // bulb / plant
        BlockPos plantPos = blockPos.below(i);
        BlockState plantState = world.getBlockState(plantPos);
        Block plantBlock = plantState.getBlock();
        if (plantBlock instanceof JadeVinePlantBlock jadeVinePlantBlock) {
            if (jadeVinePlantBlock.setToAge(world, plantPos, age) && jadeVinePlantBlock.setToAge(world, plantPos.below(), age) && jadeVinePlantBlock.setToAge(world, plantPos.below(2), age)) {
                anyGrown = true;
                JadeVine.spawnParticlesServer(world, plantPos, 16);
                JadeVine.spawnParticlesServer(world, plantPos.below(), 16);
                JadeVine.spawnParticlesServer(world, plantPos.below(2), 16);
            }
        } else if (plantBlock instanceof JadeVineBulbBlock jadeVineBulbBlock) {
            if (jadeVineBulbBlock.setToAge(world, plantPos, age)) {
                anyGrown = true;
                JadeVine.spawnParticlesServer(world, plantPos, 16);
            }
        } else if (plantState.isAir() && age > 0) {
            // plant was destroyed? => grow a new bulb
            world.setBlockAndUpdate(plantPos, SpectrumBlocks.JADE_VINE_BULB.defaultBlockState());
            anyGrown = true;
            JadeVine.spawnParticlesServer(world, plantPos, 16);
        }

        return anyGrown;
    }

    // -1 means the plant is not valid anymore and should die off
    // (like the bulb being removed and only roots left)
    int getAge(Level world, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(DEAD)) {
            return 0;
        } else {
            BlockPos lowestRootsPos = getLowestRootsPos(world, blockPos);
            BlockState plantState = world.getBlockState(lowestRootsPos.below());
            Block plantBlock = plantState.getBlock();
            if (plantBlock instanceof JadeVinePlantBlock) {
                return plantState.getValue(JadeVinePlantBlock.AGE);
            } else if (plantBlock instanceof JadeVineBulbBlock) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    boolean canGrow(@NotNull Level world, @NotNull BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(getLowestRootsPos(world, blockPos));
        if (blockEntity instanceof JadeVineRootsBlockEntity jadeVineRootsBlockEntity) {
            return world.getBrightness(LightLayer.SKY, blockPos) > 8 && jadeVineRootsBlockEntity.isLaterNight(world);
        }
        return false;
    }

    boolean tryGrowUpwards(@NotNull BlockState blockState, @NotNull Level world, @NotNull BlockPos blockPos) {
        blockPos = blockPos.above();
        while (world.getBlockState(blockPos).getBlock() instanceof JadeVineRootsBlock) {
            // search up until no jade vines roots are hit anymore
            blockPos = blockPos.above();
        }

        BlockState targetState = world.getBlockState(blockPos);
        if (canBePlantedOn(targetState)) {
            world.setBlockAndUpdate(blockPos, blockState);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof JadeVineRootsBlockEntity jadeVineRootsBlockEntity) {
                jadeVineRootsBlockEntity.setFenceBlockState(targetState.getBlock().defaultBlockState());
            }
            return true;
        }
        return false;
    }

    boolean tryGrowDownwards(@NotNull BlockState blockState, @NotNull Level world, @NotNull BlockPos blockPos) {
        blockPos = blockPos.below();
        while (world.getBlockState(blockPos).getBlock() instanceof JadeVineRootsBlock) {
            // search down until no jade vines roots are hit anymore
            blockPos = blockPos.below();
        }

        BlockState targetState = world.getBlockState(blockPos);
        if (targetState.getBlock() instanceof JadeVineBulbBlock) {
            // is there room to grow the whole plant?
            if (world.getBlockState(blockPos.below()).isAir() && world.getBlockState(blockPos.below(2)).isAir()) {
                world.setBlockAndUpdate(blockPos, SpectrumBlocks.JADE_VINES.defaultBlockState().setValue(JadeVinePlantBlock.PART, JadeVinePlantBlock.JadeVinesPlantPart.BASE));
                world.setBlockAndUpdate(blockPos.below(), SpectrumBlocks.JADE_VINES.defaultBlockState().setValue(JadeVinePlantBlock.PART, JadeVinePlantBlock.JadeVinesPlantPart.MIDDLE));
                world.setBlockAndUpdate(blockPos.below(2), SpectrumBlocks.JADE_VINES.defaultBlockState().setValue(JadeVinePlantBlock.PART, JadeVinePlantBlock.JadeVinesPlantPart.TIP));
                return true;
            }
        } else if (targetState.isAir()) {
            world.setBlockAndUpdate(blockPos, SpectrumBlocks.JADE_VINE_BULB.defaultBlockState());
            return true;
        } else if (canBePlantedOn(targetState)) {
            world.setBlockAndUpdate(blockPos, SpectrumBlocks.JADE_VINE_ROOTS.defaultBlockState());

            long lastGrowTime = -1;
            BlockEntity currentBlockEntity = world.getBlockEntity(blockPos.above());
            if (currentBlockEntity instanceof JadeVineRootsBlockEntity rootsBlockEntity) {
                lastGrowTime = rootsBlockEntity.getLastGrownTime();
            }

            BlockEntity newBlockEntity = world.getBlockEntity(blockPos);
            if (newBlockEntity instanceof JadeVineRootsBlockEntity rootsBlockEntity) {
                rootsBlockEntity.setFenceBlockState(targetState.getBlock().defaultBlockState());
                if (lastGrowTime > 0) {
                    rootsBlockEntity.setLastGrownTime(lastGrowTime);
                } else {
                    rootsBlockEntity.setLastGrownTime(world.getGameTime());
                }
            }
            return true;
        }
        return false;
    }

    void setDead(@NotNull ServerLevel world, @NotNull BlockPos blockPos) {
        setPlantToAge(world, blockPos, 0);
    }

    void rememberGrownTime(@NotNull Level world, @NotNull BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(getLowestRootsPos(world, blockPos));
        if (blockEntity instanceof JadeVineRootsBlockEntity jadeVineRootsBlockEntity) {
            jadeVineRootsBlockEntity.setLastGrownTime(world.getDayTime());
        }
    }

    // each root saves and renders the stick these roots are growing on,
    // the lowest root in a stack is considered the "main" one, also keeping track
    // when the plant has grown last
    // => search for the lowest upper state in this column
    public BlockPos getLowestRootsPos(@NotNull Level world, @NotNull BlockPos blockPos) {
        int i = 0;
        do {
            if (world.getBlockState(blockPos.below(i + 1)).getBlock() instanceof JadeVineRootsBlock) {
                i++;
            } else {
                break;
            }
        } while (blockPos.getY() - i >= world.getMinBuildHeight());
        return blockPos.below(i);
    }

    @Override
    public boolean setToAge(@NotNull Level world, BlockPos blockPos, int age) {
        BlockState currentState = world.getBlockState(blockPos);
        boolean dead = currentState.getValue(DEAD);
        if (age == 0 && !dead) {
            world.setBlockAndUpdate(blockPos, currentState.setValue(DEAD, true));
            return true;
        } else if (age > 0 && dead) {
            world.setBlockAndUpdate(blockPos, currentState.setValue(DEAD, false));
            return true;
        }
        return false;
    }

    @Override
    public boolean canUseNaturesStaff(Level world, BlockPos pos, BlockState state) {
        return state.getValue(DEAD);
    }

    @Override
    public boolean onNaturesStaffUse(Level world, BlockPos pos, BlockState state, Player player) {
        setPlantToAge((ServerLevel) world, pos, 1);
        JadeVine.spawnParticlesServer((ServerLevel) world, pos, 16);
        return false;
    }
}
