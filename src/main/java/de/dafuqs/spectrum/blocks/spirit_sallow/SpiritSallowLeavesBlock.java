package de.dafuqs.spectrum.blocks.spirit_sallow;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SpiritSallowLeavesBlock extends LeavesBlock {
	
	public SpiritSallowLeavesBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		
		if (random.nextBoolean() /*!state.get(LeavesBlock.PERSISTENT) && state.get(LeavesBlock.DISTANCE) > 1 && world.getBlockState(pos.up()).isAir()*/) {
			double startX = pos.getX() + random.nextFloat();
			double startY = pos.getY() + 1.01;
			double startZ = pos.getZ() + random.nextFloat();
			
			double velocityX = 0.02 - random.nextFloat() * 0.04;
			double velocityY = 0.005 + random.nextFloat() * 0.01;
			double velocityZ = 0.02 - random.nextFloat() * 0.04;
			
			world.addParticle(SpectrumParticleTypes.SPIRIT_SALLOW, startX, startY, startZ, velocityX, velocityY, velocityZ);
		}
		
	}
	
	
}
