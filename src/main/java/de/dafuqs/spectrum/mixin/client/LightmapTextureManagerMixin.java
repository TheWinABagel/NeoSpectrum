package de.dafuqs.spectrum.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.dafuqs.spectrum.registries.SpectrumDimensions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
@Mixin(LightTexture.class)
public abstract class LightmapTextureManagerMixin {

	@ModifyReturnValue(method = "calculateDarknessScale", at = @At("RETURN"))
	private float spectrum$getDarkness(float original) {
		Minecraft client = Minecraft.getInstance();
		if (SpectrumDimensions.DIMENSION_KEY.equals(client.player.level().dimension())) {
			return Math.max(0.12F, original);
		}
		return original;
	}

}
