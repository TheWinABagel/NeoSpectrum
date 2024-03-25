package de.dafuqs.spectrum.blocks.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class ExtraTickFarmlandBlock extends SpectrumFarmlandBlock {
	
	public ExtraTickFarmlandBlock(Properties settings, BlockState bareState) {
		super(settings.randomTicks(), bareState);
	}

	/**
	 * If there is a crop block on top of this block: tick it, too
	 * => the crop grows faster
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		BlockPos topPos = pos.above();
		BlockState topBlockState = world.getBlockState(topPos);
		if (shouldMaintainFarmland(world, pos)) {
			topBlockState.getBlock().randomTick(topBlockState, world, topPos, random);
		}
		
		super.randomTick(state, world, pos, random);
	}
	
}
