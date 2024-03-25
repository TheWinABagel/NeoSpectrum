package de.dafuqs.spectrum.status_effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class ScarredStatusEffect extends SpectrumStatusEffect {
	
	public ScarredStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.addAttributeModifiers(entity, attributes, amplifier);
		if (entity.isSprinting()) {
			entity.setSprinting(false);
		}
	}
	
	
}
