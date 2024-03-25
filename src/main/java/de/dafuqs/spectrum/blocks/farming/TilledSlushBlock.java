package de.dafuqs.spectrum.blocks.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class TilledSlushBlock extends ExtraTickFarmlandBlock {
	
	public TilledSlushBlock(Properties settings, BlockState bareState) {
		super(settings, bareState);
		this.registerDefaultState(defaultBlockState().setValue(MOISTURE, 7));
	}
	
	@Override
	protected boolean isNearWater(LevelReader world, BlockPos pos) {
		return true;
	}
	
}
