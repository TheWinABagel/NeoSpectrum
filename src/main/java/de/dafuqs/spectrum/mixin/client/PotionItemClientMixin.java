package de.dafuqs.spectrum.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin({PotionItem.class, LingeringPotionItem.class, TippedArrowItem.class})
public abstract class PotionItemClientMixin {
	
	@Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", at = @At("HEAD"), cancellable = true)
	private void spectrum$makePotionUnidentifiable(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context, CallbackInfo ci) {
		CompoundTag nbtCompound = stack.getTag();
		if (nbtCompound != null && nbtCompound.contains("spectrum_unidentifiable", Tag.TAG_BYTE) && nbtCompound.getBoolean("spectrum_unidentifiable")) {
			tooltip.add(Component.translatable("item.spectrum.potion.tooltip.unidentifiable"));
			ci.cancel();
		}
	}
	
}
