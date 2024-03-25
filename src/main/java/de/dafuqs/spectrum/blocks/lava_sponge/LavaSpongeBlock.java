package de.dafuqs.spectrum.blocks.lava_sponge;

import com.google.common.collect.Lists;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Queue;

public class LavaSpongeBlock extends SpongeBlock {
	
	public LavaSpongeBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	protected void tryAbsorbWater(Level world, BlockPos pos) {
		if (this.absorbLava(world, pos)) {
			world.setBlock(pos, SpectrumBlocks.WET_LAVA_SPONGE.defaultBlockState(), 2);
			world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(Blocks.LAVA.defaultBlockState()));
		}
	}
	
	private boolean absorbLava(Level world, BlockPos pos) {
		Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
		queue.add(new Tuple<>(pos, 0));
		int i = 0;
		
		while (!queue.isEmpty()) {
			Tuple<BlockPos, Integer> pair = queue.poll();
			BlockPos blockPos = pair.getA();
			int j = pair.getB();
			
			for (Direction direction : Direction.values()) {
				BlockPos blockPos2 = blockPos.relative(direction);
				BlockState blockState = world.getBlockState(blockPos2);
				FluidState fluidState = world.getFluidState(blockPos2);
				if (fluidState.is(FluidTags.LAVA)) {
					if (blockState.getBlock() instanceof BucketPickup && !((BucketPickup) blockState.getBlock()).pickupBlock(world, blockPos2, blockState).isEmpty()) {
						++i;
						if (j < 6) {
							queue.add(new Tuple<>(blockPos2, j + 1));
						}
					} else if (blockState.getBlock() instanceof LiquidBlock) {
						world.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
						++i;
						if (j < 6) {
							queue.add(new Tuple<>(blockPos2, j + 1));
						}
					}
				}
			}
			
			if (i > 64) {
				break;
			}
		}
		
		return i > 0;
	}
	
}
