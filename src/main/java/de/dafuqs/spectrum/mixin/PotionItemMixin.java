package de.dafuqs.spectrum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

	@ModifyReturnValue(method = "getMaxUseTime(Lnet/minecraft/item/ItemStack;)I", at = @At("RETURN"))
	private int spectrum$modifyDrinkTime(int drinkTime, ItemStack stack) {
		CompoundTag compound = stack.getTag();
		if (compound != null && compound.contains("SpectrumAdditionalDrinkDuration", Tag.TAG_ANY_NUMERIC)) {
			int additionalDrinkDuration = compound.getInt("SpectrumAdditionalDrinkDuration");
			drinkTime += Math.max(4, drinkTime + additionalDrinkDuration);
		}
		return drinkTime;
	}

	@Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", at = @At("TAIL"))
	public void spectrum$appendTooltip(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context, CallbackInfo ci) {
		CompoundTag compound = stack.getTag();
		if (compound != null && compound.contains("SpectrumAdditionalDrinkDuration", Tag.TAG_ANY_NUMERIC)) {
			int additionalDrinkDuration = compound.getInt("SpectrumAdditionalDrinkDuration");
			if (additionalDrinkDuration > 0) {
				tooltip.add(Component.translatable("item.spectrum.potion.slower_to_drink"));
			} else if (additionalDrinkDuration < 0) {
				tooltip.add(Component.translatable("item.spectrum.potion.faster_to_drink"));
			}
		}
	}

}
