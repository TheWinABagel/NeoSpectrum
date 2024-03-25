package de.dafuqs.spectrum.blocks.decay;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.DeeperDownPortalBlock;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumDimensions;
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

public class RuinBlock extends DecayBlock {
	
	public RuinBlock(Properties settings) {
		super(settings, SpectrumCommon.CONFIG.RuinDecayTickRate, SpectrumCommon.CONFIG.RuinCanDestroyBlockEntities, 3, 5F);
		registerDefaultState(getStateDefinition().any().setValue(CONVERSION, Conversion.NONE));
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, placer, itemStack);
		
		if (!world.isClientSide) {
			world.playSound(null, pos, SpectrumSoundEvents.RUIN_PLACED, SoundSource.BLOCKS, 0.5F, 1.0F);
		} else {
			RandomSource random = world.getRandom();
			world.addParticle(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F), 0.05, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F));
			world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F), 0.05, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F));
			
			for (int i = 0; i < 40; i++) {
				world.addParticle(SpectrumParticleTypes.DECAY_PLACE, pos.getX() - 0.5 + random.nextFloat() * 2, pos.getY() + random.nextFloat(), pos.getZ() - 0.5 + random.nextFloat() * 2, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F), 0.05, ((-1.0F + random.nextFloat() * 2.0F) / 12.0F));
			}
		}
	}
	
	@Override
	protected @Nullable BlockState getSpreadState(BlockState stateToSpreadFrom, BlockState stateToSpreadTo, Level world, BlockPos stateToSpreadToPos) {
		if (stateToSpreadTo.getCollisionShape(world, stateToSpreadToPos).isEmpty() || stateToSpreadTo.is(SpectrumBlockTags.RUIN_SAFE)) {
			return null;
		}
		
		if (stateToSpreadTo.is(SpectrumBlockTags.RUIN_SPECIAL_CONVERSIONS)) {
			return this.defaultBlockState().setValue(CONVERSION, Conversion.SPECIAL);
		} else if (stateToSpreadTo.is(SpectrumBlockTags.RUIN_CONVERSIONS)) {
			return this.defaultBlockState().setValue(CONVERSION, Conversion.DEFAULT);
		}
		return stateToSpreadFrom.setValue(CONVERSION, Conversion.NONE);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		super.onRemove(state, world, pos, newState, moved);
		
		if (state.getValue(RuinBlock.CONVERSION) != Conversion.NONE && newState.isAir()) {
			if (world.dimension() == Level.OVERWORLD && pos.getY() == world.getMinBuildHeight()) {
				world.setBlock(pos, SpectrumBlocks.DEEPER_DOWN_PORTAL.defaultBlockState().setValue(DeeperDownPortalBlock.FACING_UP, false), 3);
			} else if (world.dimension() == SpectrumDimensions.DIMENSION_KEY && pos.getY() == world.getMaxBuildHeight() - 1) { // highest layer cannot be built on
				world.setBlock(pos, SpectrumBlocks.DEEPER_DOWN_PORTAL.defaultBlockState().setValue(DeeperDownPortalBlock.FACING_UP, true), 3);
			}
		}
	}
	
}
