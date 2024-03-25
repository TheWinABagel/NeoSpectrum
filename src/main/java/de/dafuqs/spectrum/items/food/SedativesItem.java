package de.dafuqs.spectrum.items.food;

import de.dafuqs.spectrum.items.ItemWithTooltip;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SedativesItem extends ItemWithTooltip {
	
	public SedativesItem(Properties settings, String tooltip) {
		super(settings, tooltip);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		if (!world.isClientSide) { // TODO: do we need this? Frenzy is self-stacking
			var frenzy = user.getEffect(SpectrumStatusEffects.FRENZY);
			
			if (frenzy != null) {
				var level = frenzy.getAmplifier();
				var duration = frenzy.getDuration();
				
				if (world.getRandom().nextInt((int) (frenzy.getAmplifier() + Math.round(duration / 30.0) + 1)) == 0) {
					user.removeEffect(SpectrumStatusEffects.FRENZY);
					if (frenzy.getAmplifier() > 0) {
						user.addEffect(new MobEffectInstance(SpectrumStatusEffects.FRENZY, duration, level - 1, frenzy.isAmbient(), frenzy.isVisible(), frenzy.showIcon()));
					}
				}
				
			}
			
		}
		
		return super.finishUsingItem(stack, world, user);
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 48;
	}
}
