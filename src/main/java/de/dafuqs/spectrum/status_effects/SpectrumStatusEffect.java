package de.dafuqs.spectrum.status_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SpectrumStatusEffect extends MobEffect {
	
	public SpectrumStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	// no unused super() calls (performance)
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return false;
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
	
	}
	
	@Override
	public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
	
	}
	
	
}
