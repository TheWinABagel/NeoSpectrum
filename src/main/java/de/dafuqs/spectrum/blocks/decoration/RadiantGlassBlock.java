package de.dafuqs.spectrum.blocks.decoration;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RadiantGlassBlock extends GlassBlock {
	
	public RadiantGlassBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.is(this) || stateFrom.is(SpectrumBlocks.RADIANT_SEMI_PERMEABLE_GLASS)) {
			return true;
		}
		return super.skipRendering(state, stateFrom, direction);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}
	
}
