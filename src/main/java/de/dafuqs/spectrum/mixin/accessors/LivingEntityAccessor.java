package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker
    void callHurtArmor(DamageSource pDamageSource, float pDamageAmount);

    @Invoker
    void callHurtCurrentlyUsedShield(float pDamageAmount);
}
