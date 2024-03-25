package de.dafuqs.spectrum.blocks.crystallarieum;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystallarieumGrowableBlock extends Block {
	
	public enum GrowthStage {
		SMALL(3, 4),
		MEDIUM(4, 3),
		LARGE(5, 3),
		CLUSTER(7, 3);
		
		final VoxelShape shape;
		
		GrowthStage(int height, int xzOffset) {
			this.shape = Block.box(xzOffset, 0.0D, xzOffset, (16 - xzOffset), height, (16 - xzOffset));
		}
		
		public VoxelShape getShape() {
			return this.shape;
		}
		
	}
	
	public final GrowthStage growthStage;
	
	public CrystallarieumGrowableBlock(BlockBehaviour.Properties settings, GrowthStage growthStage) {
		super(settings);
		this.growthStage = growthStage;
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockPos = pos.relative(Direction.DOWN);
		return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, Direction.DOWN);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return this.growthStage.getShape();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
}
