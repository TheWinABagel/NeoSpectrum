package de.dafuqs.spectrum.status_effects;

import de.dafuqs.spectrum.api.status_effect.StackableStatusEffect;
import de.dafuqs.spectrum.registries.SpectrumStatusEffectTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class EffectProlongingStatusEffect extends SpectrumStatusEffect implements StackableStatusEffect {
	
	public static final float ADDITIONAL_EFFECT_DURATION_MODIFIER_PER_LEVEL = 0.25F;
	
	public EffectProlongingStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
	
	}
	
	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
	
	}
	
	public static boolean canBeExtended(MobEffect statusEffect) {
		return !SpectrumStatusEffectTags.isIn(SpectrumStatusEffectTags.NO_DURATION_EXTENSION, statusEffect);
	}
	
	public static int getExtendedDuration(int originalDuration, int prolongingAmplifier) {
		return (int) (originalDuration * (1 + ADDITIONAL_EFFECT_DURATION_MODIFIER_PER_LEVEL * prolongingAmplifier));
	}
	
}
