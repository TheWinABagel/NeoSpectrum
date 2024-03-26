package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.animal.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MushroomCow.class)
public interface MooshroomEntityAccessor {
	
	@Accessor("effect")
	MobEffect getStewEffect();
	
	@Accessor("effect")
	void setStewEffect(MobEffect statusEffect);
	
	@Accessor("effectDuration")
	int getStewEffectDuration();
	
	@Accessor("effectDuration")
	void setStewEffectDuration(int stewEffectDuration);
	
}