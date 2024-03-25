package de.dafuqs.spectrum.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FlexLanternBlock extends DiagonalBlock implements SimpleWaterloggedBlock {
	
	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	public static final BooleanProperty ALT = BooleanProperty.create("alt");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape SHAPE_STANDING, SHAPE_STANDING_ALT, SHAPE_HANGING, SHAPE_HANGING_ALT;
	
	public FlexLanternBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(HANGING, false).setValue(ALT, false).setValue(WATERLOGGED, false));
	}
	
	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		var player = ctx.getPlayer();
		var state = super.getStateForPlacement(ctx);
		
		if (state != null) {
			if (player != null) {
				state = state.setValue(ALT, player.isShiftKeyDown());
			}
			if (ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER) {
				state = state.setValue(WATERLOGGED, true);
			}
			
			state = state.setValue(HANGING, ctx.getClickedFace() == Direction.DOWN);
		}
		
		return state;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		var alt = state.getValue(ALT);
		
		if (state.getValue(HANGING)) {
			return alt ? SHAPE_HANGING_ALT : SHAPE_HANGING;
		} else {
			return alt ? SHAPE_STANDING_ALT : SHAPE_STANDING;
		}
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = state.getValue(HANGING) ? Direction.UP : Direction.DOWN;
		return Block.canSupportCenter(world, pos.relative(direction), direction.getOpposite());
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HANGING, ALT, WATERLOGGED);
	}
	
	static {
		SHAPE_STANDING = Block.box(4, 0, 4, 12, 13, 12);
		SHAPE_STANDING_ALT = Block.box(4, 0, 4, 12, 16, 12);
		SHAPE_HANGING = Block.box(4, 4, 4, 12, 16, 12);
		SHAPE_HANGING_ALT = Block.box(4, 7, 4, 12, 16, 12);
	}
}
