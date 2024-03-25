package de.dafuqs.spectrum.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A crop block that is two blocks tall.
 * This class is fully usable on its own, but it is recommended to extend it.
 */
public class TallCropBlock extends CropBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public final int lastSingleBlockAge;

    /**
     * @param lastSingleBlockAge The highest age for which this block is one block tall.
     */
    // For PL flax, lastSingleBlockAge is 3.
    public TallCropBlock(Properties settings, int lastSingleBlockAge) {
        super(settings);
        this.lastSingleBlockAge = lastSingleBlockAge;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random){
        this.tryGrow(state, world, pos, random, 25F);
    }

    @Override
    public void growCrops(Level world, BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = world.getBlockState(pos);
        }
        if (!state.is(this)){
            return;
        }
        int newAge = this.getAge(state) + this.getBonemealAgeIncrease(world);
        int maxAge = this.getMaxAge();
        if (newAge > maxAge) {
            newAge = maxAge;
        }

        if (newAge > this.lastSingleBlockAge && canGrowUp(world, pos, state, newAge)) {
            world.setBlock(pos, this.getStateForAge(newAge), Block.UPDATE_CLIENTS);
            world.setBlock(pos.above(), this.withAgeAndHalf(newAge, DoubleBlockHalf.UPPER), Block.UPDATE_CLIENTS);
        } else {
            world.setBlock(pos, this.getStateForAge(Math.min(newAge, lastSingleBlockAge)), Block.UPDATE_CLIENTS);
        }
    }

    private boolean canGrowUp(Level world, BlockPos pos, BlockState state, int age) {
        return world.getBlockState(pos.above()).is(this) || world.getBlockState(pos.above()).canBeReplaced();
    }

    /**
     * Tries to grow the crop. Call me in randomTick().
     * Will not do anything if the block state is upper.
     *
     * @param upperBound The inverse of the probability of the crop growing with zero moisture.
     *                   <br>E.g. If upperBound is 25F, the crop with no moisture will grow about 1 in every 25 attempts.
     *                   The more moisture, the more likely.
     */
    @SuppressWarnings("SameParameterValue")
    protected void tryGrow(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, float upperBound) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return;

        if (world.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                float moisture = getGrowthSpeed(this, world, pos);
                // More likely if there's more moisture
                if (random.nextInt((int) (upperBound / moisture) + 1) == 0) {
                    if (age >= Block.UPDATE_CLIENTS) {
                        if (world.getBlockState(pos.above()).is(this) || world.getBlockState(pos.above()).canBeReplaced()) {
                            world.setBlock(pos, this.getStateForAge(age + 1), Block.UPDATE_CLIENTS);
                            world.setBlock(pos.above(), this.withAgeAndHalf(age + 1, DoubleBlockHalf.UPPER), Block.UPDATE_CLIENTS);
                        }
                    } else {
                        world.setBlock(pos, this.getStateForAge(age + 1), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

    /**
     * Returns the bottom block state for the given age.
     */
    @Override
    public BlockState getStateForAge(int age) {
        return this.withAgeAndHalf(age, DoubleBlockHalf.LOWER);
    }
    
    public BlockState withAgeAndHalf(int age, DoubleBlockHalf half) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), age).setValue(HALF, half);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(AGE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            return this.mayPlaceOn(world.getBlockState(blockPos), world, blockPos);
        } else {
            BlockState blockState = world.getBlockState(pos.below());
            return blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER && blockState.getValue(AGE) > this.lastSingleBlockAge;
        }
    }

    protected DoubleBlockHalf getHalf(BlockState state) {
        return state.getValue(HALF);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (state.getValue(AGE) <= this.lastSingleBlockAge) {
                return super.getShape(state, world, pos, context);
            } else {
                // Fill in the bottom block if the plant is two-tall
                return Block.box(0, 0, 0,16, 16, 16);
            }
        } else {
            return super.getShape(this.getStateForAge(state.getValue(AGE) - this.lastSingleBlockAge - 1), world, pos, context);
        }
    }

    // below code is (mostly) copied from TallPlantBlock

    public static BlockState withWaterloggedState(LevelReader world, BlockPos pos, BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, world.isWaterAt(pos)) : state;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return (state.getValue(AGE) <= lastSingleBlockAge || neighborState.is(this) && neighborState.getValue(HALF) != doubleBlockHalf) ? state : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        return blockPos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockPos.above()).canBeReplaced(ctx) ? this.withAgeAndHalf(0, DoubleBlockHalf.LOWER) : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // Place the other half
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            world.setBlock(pos.below(), this.withAgeAndHalf(state.getValue(AGE), DoubleBlockHalf.LOWER), Block.UPDATE_ALL);
        } else {
            if (state.getValue(AGE) > this.lastSingleBlockAge) {
                world.setBlock(pos.above(), this.withAgeAndHalf(state.getValue(AGE), DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
            }
        }
    }

    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            // Break the other half
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropResources(state, world, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
    }
}
