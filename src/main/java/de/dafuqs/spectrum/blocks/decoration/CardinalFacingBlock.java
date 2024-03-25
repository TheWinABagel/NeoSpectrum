package de.dafuqs.spectrum.blocks.decoration;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CardinalFacingBlock extends Block {
	
	public static final BooleanProperty CARDINAL_FACING = BooleanProperty.create("cardinal_facing");
	
	public CardinalFacingBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(CARDINAL_FACING, false));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction facing = ctx.getHorizontalDirection();
		boolean facingVertical = facing.equals(Direction.EAST) || facing.equals(Direction.WEST);
		return this.defaultBlockState().setValue(CARDINAL_FACING, facingVertical);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(CARDINAL_FACING);
	}
	
	
}
