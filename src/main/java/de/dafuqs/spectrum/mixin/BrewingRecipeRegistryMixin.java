package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.registries.SpectrumPotions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public abstract class BrewingRecipeRegistryMixin {
	
	@Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
	private static void spectrum$disallowPigmentPotionInBrewingStand(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		Potion potion = PotionUtils.getPotion(input);
		if (potion == SpectrumPotions.PIGMENT_POTION) {
			cir.setReturnValue(false);
		}
	}
	
}
