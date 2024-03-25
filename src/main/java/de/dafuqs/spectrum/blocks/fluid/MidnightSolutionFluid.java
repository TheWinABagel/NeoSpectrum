package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.blocks.decay.BlackMateriaBlock;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumFluids;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class MidnightSolutionFluid extends SpectrumFluid {
	
	@Override
	public Fluid getSource() {
		return SpectrumFluids.MIDNIGHT_SOLUTION;
	}
	
	@Override
	public Fluid getFlowing() {
		return SpectrumFluids.FLOWING_MIDNIGHT_SOLUTION;
	}
	
	@Override
	public Item getBucket() {
		return SpectrumItems.MIDNIGHT_SOLUTION_BUCKET;
	}
	
	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		return SpectrumBlocks.MIDNIGHT_SOLUTION.defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(fluidState));
	}
	
	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == SpectrumFluids.MIDNIGHT_SOLUTION || fluid == SpectrumFluids.FLOWING_MIDNIGHT_SOLUTION;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(Level world, BlockPos pos, FluidState state, RandomSource random) {
		BlockPos topPos = pos.above();
		BlockState topState = world.getBlockState(topPos);
		if (topState.isAir() && !topState.isSolidRender(world, topPos) && random.nextInt(2000) == 0) {
			world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SpectrumSoundEvents.MIDNIGHT_SOLUTION_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}
	}
	
	@Override
	protected int getSlopeFindDistance(LevelReader worldView) {
		return 5;
	}
	
	@Override
	protected int getDropOff(LevelReader worldView) {
		return 1;
	}
	
	@Override
	public void tick(Level world, BlockPos pos, FluidState state) {
		super.tick(world, pos, state);
		
		if (state.getOwnHeight() < 1.0) {
			for (Direction direction : Direction.values()) {
				if (MidnightSolutionFluidBlock.tryConvertNeighbor(world, pos, pos.relative(direction))) {
					break;
				}
			}
		}
		
		boolean converted = BlackMateriaBlock.spreadBlackMateria(world, pos, world.random, MidnightSolutionFluidBlock.SPREAD_BLOCKSTATE);
		if (converted) {
			world.scheduleTick(pos, state.getType(), 400 + world.random.nextInt(800));
		}
	}
	
	@Override
	public int getTickDelay(LevelReader worldView) {
		return 12;
	}
	
	@Override
	public ParticleOptions getDripParticle() {
		return SpectrumParticleTypes.DRIPPING_MIDNIGHT_SOLUTION;
	}
	
	@Override
	public ParticleOptions getSplashParticle() {
		return SpectrumParticleTypes.MIDNIGHT_SOLUTION_SPLASH;
	}
	
	public static class Flowing extends MidnightSolutionFluid {
		
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}
		
		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}
		
		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}
		
	}
	
	public static class Still extends MidnightSolutionFluid {
		
		@Override
		public int getAmount(FluidState fluidState) {
			return 8;
		}
		
		@Override
		public boolean isSource(FluidState fluidState) {
			return true;
		}
		
	}
}