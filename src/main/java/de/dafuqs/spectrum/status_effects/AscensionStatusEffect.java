package de.dafuqs.spectrum.status_effects;

import de.dafuqs.spectrum.helpers.ParticleHelper;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class AscensionStatusEffect extends SpectrumStatusEffect {
	
	public static final int MUSIC_DURATION_TICKS = 288 * 20;
	public static final int MUSIC_INTRO_TICKS = 56 * 20; // 56 seconds
	
	public AscensionStatusEffect(MobEffectCategory statusEffectCategory, int color) {
		super(statusEffectCategory, color);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		Level world = entity.level();
		if (world.isClientSide) {
			ParticleHelper.playParticleWithPatternAndVelocityClient(entity.level(), entity.position(), SpectrumParticleTypes.WHITE_SPARKLE_RISING, VectorPattern.EIGHT, 0.2);
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 4 == 0;
	}
	
	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.addAttributeModifiers(entity, attributes, amplifier);
		if (entity instanceof ServerPlayer player) {
			SpectrumS2CPacketSender.playAscensionAppliedEffects(player);
		}
	}
	
	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.removeAttributeModifiers(entity, attributes, amplifier);
		entity.addEffect(new MobEffectInstance(SpectrumStatusEffects.DIVINITY, MUSIC_DURATION_TICKS - MUSIC_INTRO_TICKS, DivinityStatusEffect.ASCENSION_AMPLIFIER));
	}
	
}