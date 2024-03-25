package de.dafuqs.spectrum.status_effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class LifeDrainStatusEffect extends SpectrumStatusEffect {
	
	public static final String ATTRIBUTE_UUID_STRING = "28f9e619-20bf-4b2c-9646-06fbf714c00c";
	public static final UUID ATTRIBUTE_UUID = UUID.fromString(ATTRIBUTE_UUID_STRING);
	
	public LifeDrainStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		AttributeInstance instance = entity.getAttribute(Attributes.MAX_HEALTH);
		if (instance != null) {
			AttributeModifier currentMod = instance.getModifier(ATTRIBUTE_UUID);
			if (currentMod != null) {
				instance.removeModifier(currentMod);
				AttributeModifier newModifier = new AttributeModifier(UUID.fromString(ATTRIBUTE_UUID_STRING), this::getDescriptionId, currentMod.getAmount() - 1, AttributeModifier.Operation.ADDITION);
				instance.addPermanentModifier(newModifier);
				instance.getValue(); // recalculate final value
				if (entity.getHealth() > entity.getMaxHealth()) {
					entity.setHealth(entity.getMaxHealth());
				}
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % Math.max(1, 40 - amplifier * 2) == 0;
	}
	
}