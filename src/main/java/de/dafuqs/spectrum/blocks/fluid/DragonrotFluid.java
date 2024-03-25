package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumFluids;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
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

public abstract class DragonrotFluid extends SpectrumFluid {

	@Override
	public Fluid getSource() {
		return SpectrumFluids.DRAGONROT;
	}
	
	@Override
	public Fluid getFlowing() {
		return SpectrumFluids.FLOWING_DRAGONROT;
	}
	
	@Override
	public Item getBucket() {
		return SpectrumItems.DRAGONROT_BUCKET;
	}
	
	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		return SpectrumBlocks.DRAGONROT.defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(fluidState));
	}
	
	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == SpectrumFluids.DRAGONROT || fluid == SpectrumFluids.FLOWING_DRAGONROT;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(Level world, BlockPos pos, FluidState state, RandomSource random) {
		BlockPos topPos = pos.above();
		BlockState topState = world.getBlockState(topPos);
		if (topState.isAir() && !topState.isSolidRender(world, topPos)) {
			float soundRandom = random.nextFloat();
			if (soundRandom < 0.0003F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HONEY_DRINK, SoundSource.AMBIENT, random.nextFloat() * 0.65F + 0.25F, random.nextFloat() * 0.2F, false);
			} else if (soundRandom < 0.00048F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SpectrumSoundEvents.MUD_AMBIENT, SoundSource.AMBIENT, random.nextFloat() * 0.65F + 0.25F, random.nextFloat() * 0.2F, false);
			} else if (soundRandom < 0.0006F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.AMBIENT, random.nextFloat() * 0.4F + 0.25F, random.nextFloat() * 0.5F + 0.1F, false);
			} else if (soundRandom < 0.0008F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FROG_AMBIENT, SoundSource.AMBIENT, random.nextFloat() + 0.25F, random.nextFloat() * 0.3F + 0.01F, false);
			} else if (soundRandom < 0.001F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.SCULK_BLOCK_PLACE, SoundSource.AMBIENT, random.nextFloat() + 0.25F, random.nextFloat() * 0.4F + 0.2F, false);
			} else if (soundRandom < 0.0014F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HONEY_BLOCK_STEP, SoundSource.AMBIENT, random.nextFloat() * 2F, 0.1F, false);
			} else if (soundRandom < 0.00144F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.VILLAGER_DEATH, SoundSource.AMBIENT, random.nextFloat() * 0.334F + 0.1F, 1F, false);
			} else if (soundRandom < 0.00148F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PARROT_DEATH, SoundSource.AMBIENT, random.nextFloat() * 0.334F + 0.1F, 1F, false);
			} else if (soundRandom < 0.00152F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CAT_DEATH, SoundSource.AMBIENT, random.nextFloat() * 0.334F + 0.1F, 1F, false);
			} else if (soundRandom < 0.00156F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOLF_DEATH, SoundSource.AMBIENT, random.nextFloat() * 0.3F + 0.1F, 1F, false);
			} else if (soundRandom < 0.001564F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.AMBIENT, 2F, 0.1F, false);
			} else if (soundRandom < 0.001566F) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SpectrumSoundEvents.ENTITY_MONSTROSITY_AMBIENT, SoundSource.AMBIENT, random.nextFloat() * 0.65F + 0.25F, random.nextFloat(), false);
			}
		}
	}
	
	@Override
	protected int getSlopeFindDistance(LevelReader worldView) {
		return 3;
	}
	
	@Override
	protected int getDropOff(LevelReader worldView) {
		return 3;
	}
	
	@Override
	public int getTickDelay(LevelReader worldView) {
		return 40;
	}
	
	@Override
	public ParticleOptions getDripParticle() {
		return SpectrumParticleTypes.DRIPPING_DRAGONROT;
	}
	
	@Override
	public ParticleOptions getSplashParticle() {
		return SpectrumParticleTypes.DRAGONROT_SPLASH;
	}
	
	public static class Flowing extends DragonrotFluid {
		
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
	
	public static class Still extends DragonrotFluid {
		
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