package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.entity.entity.SpectrumFishingBobberEntity;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHookPredicate.class)
public abstract class FishingHookPredicateMixin {

	@Inject(method = "test(Lnet/minecraft/entity/Entity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;)Z", at = @At(value = "HEAD"), cancellable = true)
	public void spectrum$test(Entity entity, ServerLevel world, Vec3 pos, CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this != FishingHookPredicate.ANY && entity instanceof SpectrumFishingBobberEntity spectrumFishingBobberEntity && spectrumFishingBobberEntity.isInTheOpen()) {
			cir.setReturnValue(true);
		}
	}
	
}
