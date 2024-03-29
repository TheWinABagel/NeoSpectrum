package de.dafuqs.spectrum.mixin.client;

import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.registries.SpectrumDimensions;
import de.dafuqs.spectrum.registries.SpectrumMusicType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Inject(method = "getSituationalMusic", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;dimension()Lnet/minecraft/resources/ResourceKey;"), cancellable = true)
	public void spectrum$getMusicType(CallbackInfoReturnable<Music> cir) {
		if (player.level().dimension() == SpectrumDimensions.DIMENSION_KEY) {
			if (Support.hasPlayerFinishedMod(player)) {
				cir.setReturnValue(SpectrumMusicType.SPECTRUM_THEME);
			} else {
				cir.setReturnValue(SpectrumMusicType.DEEPER_DOWN_THEME);
			}
		}
	}
	
}