package de.dafuqs.spectrum.status_effects;

import de.dafuqs.spectrum.api.status_effect.StackableStatusEffect;
import de.dafuqs.spectrum.cca.LastKillComponent;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FrenzyStatusEffect extends SpectrumStatusEffect implements StackableStatusEffect {
	
	public static final String ATTACK_SPEED_UUID_STRING = "7ee7c082-1134-4dc5-b0f9-dab92723f560";
	public static final double ATTACK_SPEED_PER_STAGE = 0.1D;
	
	public static final String MOVEMENT_SPEED_UUID_STRING = "a215d081-48a9-4d6c-bdff-a153d4838324";
	public static final double MOVEMENT_SPEED_PER_STAGE = 0.1D;
	
	public static final String ATTACK_DAMAGE_UUID_STRING = "061a2c27-eae8-4643-a0c0-0f0d195bc9b1";
	public static final double ATTACK_DAMAGE_PER_STAGE = 0.5D;
	
	public static final String KNOCKBACK_RESISTANCE_UUID_STRING = "b9d38c3a-75b5-462f-a624-eec9b987a5e2";
	public static final double KNOCKBACK_RESISTANCE_PER_STAGE = 0.25D;
	
	public static final long REQUIRE_KILL_EVERY_X_TICKS = 200;
	
	public FrenzyStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		if (!SpectrumStatusEffects.effectsAreGettingStacked && !entity.hasEffect(this)) {
			super.addAttributeModifiers(entity, attributes, amplifier);
		}
	}
	
	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		if (!SpectrumStatusEffects.effectsAreGettingStacked) {
			super.removeAttributeModifiers(entity, attributes, amplifier);
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		long lastKillTick = LastKillComponent.getLastKillTick(entity);
		long worldTime = entity.level().getGameTime();
		long lastKillTickDifference = worldTime - lastKillTick;
		boolean scoredKillInTime = lastKillTick >= 0 && lastKillTickDifference < REQUIRE_KILL_EVERY_X_TICKS;
		
		if (!scoredKillInTime && lastKillTickDifference % REQUIRE_KILL_EVERY_X_TICKS == 0) {
			updateAttributes(entity, amplifier, -1);
		}
	}
	
	public void onKill(LivingEntity livingEntity, int amplifier) {
		updateAttributes(livingEntity, amplifier, 1);
	}
	
	public void updateAttributes(@NotNull LivingEntity entity, int amplifier, int increase) {
		AttributeMap attributes = entity.getAttributes();
		if (attributes != null) {
			for (Map.Entry<Attribute, AttributeModifier> attributeEntry : this.getAttributeModifiers().entrySet()) {
				AttributeInstance entityInstance = attributes.getInstance(attributeEntry.getKey());
				if (entityInstance != null) {
					AttributeModifier baseAttributeValue = attributeEntry.getValue();
					AttributeModifier appliedModifier = entityInstance.getModifier(baseAttributeValue.getId());
					double newBaseValue = appliedModifier == null ? baseAttributeValue.getAmount() : appliedModifier.getAmount();
					double newValue = this.adjustModifierAmount(newBaseValue, attributeEntry.getValue().getAmount(), amplifier, increase);
					entityInstance.removeModifier(baseAttributeValue);
					entityInstance.addPermanentModifier(new AttributeModifier(baseAttributeValue.getId(), baseAttributeValue.getName(), newValue, baseAttributeValue.getOperation()));
					entityInstance.getValue();
				}
			}
		}
	}
	
	public double adjustModifierAmount(double existingValue, double additionalValue, int amplifier, int increase) {
		if (increase > 0) {
			return existingValue + additionalValue * (amplifier + increase);
		} else {
			return existingValue - additionalValue * (amplifier - increase);
		}
	}
	
}
