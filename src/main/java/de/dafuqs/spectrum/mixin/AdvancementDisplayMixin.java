package de.dafuqs.spectrum.mixin;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.helpers.NbtHelper;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(DisplayInfo.class)
public class AdvancementDisplayMixin {
	@Inject(
			method = "iconFromJson(Lcom/google/gson/JsonObject;)Lnet/minecraft/item/ItemStack;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/ItemConvertible;)V",
					ordinal = 0),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true)
	private static void iconFromJson(JsonObject json, CallbackInfoReturnable<ItemStack> info, Item item) {
		ItemStack itemStack = new ItemStack(item);
		Optional<CompoundTag> nbt = NbtHelper.getNbtCompound(json.get("nbt"));
		if (nbt.isPresent()) itemStack.setTag(nbt.get());
		info.setReturnValue(itemStack);
	}
}
