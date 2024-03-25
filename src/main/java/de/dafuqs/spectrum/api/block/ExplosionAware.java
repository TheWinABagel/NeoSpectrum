package de.dafuqs.spectrum.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface ExplosionAware {

	/**
	 * Alternate Method of the vanilla onDestroyedByExplosion() logic
	 * called before the block is set to air and therefore having state information
	 * and still intact block entity.
	 */
	default void beforeDestroyedByExplosion(Level world, BlockPos pos, BlockState state, Explosion explosion) {

	}

	/**
	 * The block to place when the block is exploded.
	 * Unless you want to use a custom block, return Blocks.AIR.getDefaultState() (vanilla default)
	 * @return the state to replace the block with
	 */
	default BlockState getStateForExplosion(Level world, BlockPos blockPos, BlockState stateAtPos) {
		return Blocks.AIR.defaultBlockState();
	}

}
