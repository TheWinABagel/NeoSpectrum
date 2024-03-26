package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.api.item.TranstargetItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

	@Shadow @Final public EnchantmentCategory category;

	@Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
	public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		var accepted = cir.getReturnValue();

		if (stack.getItem() instanceof TranstargetItem spoofer) {
			accepted = this.category == spoofer.getRealTarget();
		}

		if (!accepted) {
			if ((stack.getItem() instanceof ExtendedEnchantable extendedEnchantable && extendedEnchantable.acceptsEnchantment((Enchantment) (Object) this))) {
				cir.setReturnValue(true);
			}
		}
	}
	
}
