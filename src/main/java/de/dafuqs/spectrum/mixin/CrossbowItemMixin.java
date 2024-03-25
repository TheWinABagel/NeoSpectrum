package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
	
	@Inject(method = "getSpeed(Lnet/minecraft/item/ItemStack;)F", at = @At("RETURN"), cancellable = true)
	private static void getSpeed(ItemStack stack, CallbackInfoReturnable<Float> cir) {
		int sniperLevel = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.SNIPER, stack);
		if (sniperLevel > 0) {
			cir.setReturnValue(cir.getReturnValue() + 1.0F * sniperLevel);
		}
	}
	
}
