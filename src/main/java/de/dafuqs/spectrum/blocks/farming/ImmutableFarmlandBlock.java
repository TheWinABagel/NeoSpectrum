package de.dafuqs.spectrum.blocks.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class ImmutableFarmlandBlock extends SpectrumFarmlandBlock {

	public ImmutableFarmlandBlock(Properties settings, BlockState bareState) {
		super(settings, bareState);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return false;
	}

}
