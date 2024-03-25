package de.dafuqs.spectrum.status_effects;

import de.dafuqs.spectrum.items.trinkets.WhispyCircletItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class ImmunityStatusEffect extends MobEffect {
	
	public ImmunityStatusEffect(MobEffectCategory statusEffectCategory, int color) {
		super(statusEffectCategory, color);
	}
	
	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.addAttributeModifiers(entity, attributes, amplifier);
		WhispyCircletItem.removeNegativeStatusEffects(entity);
	}
	
}