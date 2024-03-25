package de.dafuqs.spectrum.blocks.decay;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FadingBlock extends DecayBlock {
	
	public FadingBlock(Properties settings) {
		super(settings, SpectrumCommon.CONFIG.FadingDecayTickRate, SpectrumCommon.CONFIG.FadingCanDestroyBlockEntities, 1, 1F);
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, placer, itemStack);
		
		if (!world.isClientSide) {
			world.playSound(null, pos, SpectrumSoundEvents.FADING_PLACED, SoundSource.BLOCKS, 0.5F, 1.0F);
		} else {
			RandomSource random = world.getRandom();
			world.addParticle(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F), 0.05, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F));
			
			for (int i = 0; i < 10; i++) {
				world.addParticle(SpectrumParticleTypes.DECAY_PLACE, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(), ((-1.0F + random.nextFloat() * 2.0F) / 12.0F), 0.05, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F));
			}
		}
	}
	
	@Override
	protected @Nullable BlockState getSpreadState(BlockState stateToSpreadFrom, BlockState stateToSpreadTo, Level world, BlockPos stateToSpreadToPos) {
		if (stateToSpreadTo.is(SpectrumBlockTags.FADING_SPECIAL_CONVERSIONS)) {
			return stateToSpreadFrom.setValue(CONVERSION, Conversion.SPECIAL);
		} else if (stateToSpreadTo.is(SpectrumBlockTags.FADING_CONVERSIONS)) {
			return stateToSpreadFrom.setValue(CONVERSION, Conversion.DEFAULT);
		}
		return null;
	}
	
}
