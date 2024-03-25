package de.dafuqs.spectrum.status_effects;

import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DeadlyPoisonStatusEffect extends SpectrumStatusEffect {
	
	public DeadlyPoisonStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		entity.hurt(SpectrumDamageTypes.deadlyPoison(entity.level()), 1.0F);
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int i = 25 >> amplifier;
		if (i > 0) {
			return duration % i == 0;
		} else {
			return true;
		}
	}
	
}
