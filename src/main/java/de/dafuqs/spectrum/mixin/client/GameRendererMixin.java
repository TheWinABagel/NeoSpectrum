package de.dafuqs.spectrum.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.dafuqs.spectrum.registries.SpectrumDimensions;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @ModifyReturnValue(method = "getNightVisionStrength(Lnet/minecraft/entity/LivingEntity;F)F", at = @At("RETURN"))
    private static float spectrum$nerfNightVisionInDimension(float original, LivingEntity entity, float tickDelta) {
		if (SpectrumDimensions.DIMENSION_KEY.equals(entity.level().dimension())) {
			return original / 6F;
		}
        return original;
    }

}
