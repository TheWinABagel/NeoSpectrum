package de.dafuqs.spectrum.blocks.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TilledShaleClayBlock extends ImmutableFarmlandBlock {
	public TilledShaleClayBlock(Properties settings, BlockState bareState) {
		super(settings, bareState);
	}

	@Override
	public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		entity.causeFallDamage(fallDistance, 2.0F, world.damageSources().fall());
	}

}
