package de.dafuqs.spectrum.status_effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NourishingStatusEffect extends SpectrumStatusEffect {
	
	public NourishingStatusEffect(MobEffectCategory statusEffectCategory, int color) {
		super(statusEffectCategory, color);
	}
	
	@Override
	public String getDescriptionId() {
		return MobEffects.SATURATION.getDescriptionId();
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		Level world = entity.level();
		if (!world.isClientSide && entity instanceof Player playerEntity) {
			playerEntity.getFoodData().eat(1, 0.25F);
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int i = 200 >> amplifier;
		if (i > 0) {
			return duration % i == 0;
		}
		return true;
	}
	
}