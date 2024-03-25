package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AloeBlock extends BushBlock implements BonemealableBlock {

    protected static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
    protected static final double GROW_CHANCE = 0.4;
    protected static final int MAX_LIGHT_LEVEL = 10;

    public AloeBlock(Properties settings) {
        super(settings);
    }

    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AGE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getRawBrightness(pos, 0) <= MAX_LIGHT_LEVEL && super.canSurvive(state, world, pos);
	}
	
	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
		return floor.is(SpectrumBlockTags.ALOE_PLANTABLE);
	}
	
	@Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
		return state.getValue(AGE) < BlockStateProperties.MAX_AGE_4;
	}

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return world.getRawBrightness(pos, 0) <= MAX_LIGHT_LEVEL && random.nextFloat() > GROW_CHANCE;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        if (canSurvive(state, world, pos)) {
            int age = state.getValue(AGE);
            if (age < BlockStateProperties.MAX_AGE_4) {
                world.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
                world.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);

                if (world.getBlockState(pos.below()).is(SpectrumBlockTags.ALOE_CONVERTED)) {
                    world.setBlockAndUpdate(pos.below(), Blocks.SAND.defaultBlockState());
                }
            }
        } else {
            Block.updateOrDestroy(state, Blocks.AIR.defaultBlockState(), world, pos, 10, 512);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        performBonemeal(world, random, pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        if (age > 1) {
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                world.setBlockAndUpdate(pos, state.setValue(AGE, age - 1));
                player.getInventory().placeItemBackInInventory(this.asItem().getDefaultInstance());
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 0.9F + world.random.nextFloat() * 0.2F);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

}
