package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.blocks.PrimordialFireBlock;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
	
	@Shadow
	@Final
	private DamageSource damageSource;
	
	@Inject(method = "affectWorld(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractFireBlock;getState(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private void spectrum$modifyExplosion(boolean particles, CallbackInfo ci) {
		if (this.damageSource.is(SpectrumDamageTypes.INCANDESCENCE)) {
			PrimordialFireBlock.EXPLOSION_CAUSES_PRIMORDIAL_FIRE_FLAG = true;
		}
	}
	
}
