package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.LoomScreenHandler$5")
public abstract class LoomContainerPatternSlotMixin extends Slot {
	private LoomContainerPatternSlotMixin() {
		super(null, 0, 0, 0);
	}
	
	@Inject(method = "canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
	private void checkBppLoomPatternItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (stack.getItem() instanceof LoomPatternProvider) {
			info.setReturnValue(true);
		}
	}
}
