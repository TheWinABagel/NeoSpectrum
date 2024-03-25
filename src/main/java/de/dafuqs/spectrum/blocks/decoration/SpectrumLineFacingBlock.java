package de.dafuqs.spectrum.blocks.decoration;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public class SpectrumLineFacingBlock extends SpectrumFacingBlock {
	
	public SpectrumLineFacingBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection());
	}
	
}
