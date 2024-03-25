package de.dafuqs.spectrum.blocks.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnderGlassBlock extends Block {
	
	public static final EnumProperty<TransparencyState> TRANSPARENCY_STATE = EnumProperty.create("transparency_state", TransparencyState.class);
	
	public EnderGlassBlock(Properties settings) {
		super(settings);
		registerDefaultState(getStateDefinition().any().setValue(TRANSPARENCY_STATE, TransparencyState.SOLID));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(TRANSPARENCY_STATE);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
		return (state.getValue(EnderGlassBlock.TRANSPARENCY_STATE) != TransparencyState.SOLID) && stateFrom.is(this) || super.skipRendering(state, stateFrom, direction);
	}
	
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		switch (state.getValue(TRANSPARENCY_STATE)) {
			case SOLID -> {
				return 0.0F;
			}
			case TRANSLUCENT -> {
				return 0.5F;
			}
			default -> {
				return 1.0F;
			}
		}
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return (state.getValue(TRANSPARENCY_STATE) == TransparencyState.NO_COLLISION);
	}
	
	@Override
	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if ((state.getValue(TRANSPARENCY_STATE) == TransparencyState.NO_COLLISION)) {
			return Shapes.empty();
		} else {
			return state.getShape(world, pos);
		}
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return !(state.getValue(TRANSPARENCY_STATE) == TransparencyState.SOLID);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
		if ((state.getValue(TRANSPARENCY_STATE) == TransparencyState.SOLID)) {
			return world.getMaxLightLevel();
		} else {
			return super.getLightBlock(state, world, pos);
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if ((state.getValue(TRANSPARENCY_STATE) == TransparencyState.NO_COLLISION)) {
			return Shapes.block();
		} else {
			return super.getShape(state, world, pos, context);
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		int power = ctx.getLevel().getBestNeighborSignal(ctx.getClickedPos());
		return this.defaultBlockState().setValue(TRANSPARENCY_STATE, getStateForRedstonePower(power));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClientSide) {
			BlockState fromPosBlockState = world.getBlockState(fromPos);
			if (fromPosBlockState.getBlock() instanceof EnderGlassBlock) {
				TransparencyState sourceTransparencyState = fromPosBlockState.getValue(TRANSPARENCY_STATE);
				
				if (sourceTransparencyState != state.getValue(TRANSPARENCY_STATE)) {
					world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(TRANSPARENCY_STATE, sourceTransparencyState));
				}
			} else {
				if (fromPosBlockState.isAir() || fromPosBlockState.isSignalSource()) {
					setTransparencyStateBasedOnRedstone(world, pos, state);
				}
			}
		}
		super.neighborChanged(state, world, pos, block, fromPos, notify);
	}
	
	private void setTransparencyStateBasedOnRedstone(Level world, BlockPos blockPos, BlockState currentState) {
		int powerAtPos = world.getBestNeighborSignal(blockPos);
		TransparencyState targetTransparencyState = getStateForRedstonePower(powerAtPos);
		
		if (currentState.getValue(TRANSPARENCY_STATE) != targetTransparencyState) {
			world.setBlockAndUpdate(blockPos, currentState.setValue(TRANSPARENCY_STATE, targetTransparencyState));
		}
		
	}
	
	private TransparencyState getStateForRedstonePower(int power) {
		if (power == 15) {
			return TransparencyState.NO_COLLISION;
		} else if (power == 0) {
			return TransparencyState.SOLID;
		} else {
			return TransparencyState.TRANSLUCENT;
		}
	}
	
	public enum TransparencyState implements StringRepresentable {
		SOLID("solid"),
		TRANSLUCENT("translucent"),
		NO_COLLISION("no_collision");
		
		private final String name;
		
		TransparencyState(String name) {
			this.name = name;
		}
		
		public String toString() {
			return this.name;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
	
}
