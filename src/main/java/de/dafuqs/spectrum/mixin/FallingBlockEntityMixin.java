package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrusher;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
	
	/**
	 * By default, falling blocks only damage living entities
	 * This mixin runs a second check if we are dealing anvil damage and if yes, triggers anvil crushing
	 */
	@Inject(method = "causeFallDamage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void spectrum$processAnvilCrushing(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, int hurtDistance, Predicate<Entity> predicate, DamageSource damageSource2, float fallHurt) {
		if (damageSource2.is(DamageTypes.FALLING_ANVIL)) {
			FallingBlockEntity thisEntity = (FallingBlockEntity) (Object) this;
			thisEntity.level().getEntities(EntityTypeTest.forClass(ItemEntity.class), thisEntity.getBoundingBox(), Entity::isAlive)
					.forEach((entity) -> AnvilCrusher.crush(entity, fallHurt));
		}
	}
	
}
