package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface StatusEffectInstanceAccessor {
	
	@Accessor(value = "duration")
	void setDuration(int newDuration);
	
}