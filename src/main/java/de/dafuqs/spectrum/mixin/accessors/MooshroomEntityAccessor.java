package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.animal.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MushroomCow.class)
public interface MooshroomEntityAccessor {
	
	@Accessor("stewEffect")
	MobEffect getStewEffect();
	
	@Accessor("stewEffect")
	void setStewEffect(MobEffect statusEffect);
	
	@Accessor("stewEffectDuration")
	int getStewEffectDuration();
	
	@Accessor("stewEffectDuration")
	void setStewEffectDuration(int stewEffectDuration);
	
}