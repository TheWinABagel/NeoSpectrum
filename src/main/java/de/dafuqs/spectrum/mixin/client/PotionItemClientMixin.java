package de.dafuqs.spectrum.mixin.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

@OnlyIn(Dist.CLIENT)
@Mixin({PotionItem.class, LingeringPotionItem.class, TippedArrowItem.class})
public abstract class PotionItemClientMixin {
	
	@Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
	private void spectrum$makePotionUnidentifiable(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context, CallbackInfo ci) {
		CompoundTag nbtCompound = stack.getTag();
		if (nbtCompound != null && nbtCompound.contains("spectrum_unidentifiable", Tag.TAG_BYTE) && nbtCompound.getBoolean("spectrum_unidentifiable")) {
			tooltip.add(Component.translatable("item.spectrum.potion.tooltip.unidentifiable"));
			ci.cancel();
		}
	}
	
}
