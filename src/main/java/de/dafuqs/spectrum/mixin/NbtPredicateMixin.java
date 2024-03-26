package de.dafuqs.spectrum.mixin;

import com.google.gson.JsonElement;
import de.dafuqs.spectrum.helpers.NbtHelper;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(NbtPredicate.class)
public class NbtPredicateMixin {
	@Inject(
		method = "fromJson",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/GsonHelper;convertToString(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;",
			ordinal = 0),
		cancellable = true)
	private static void fromJson(@Nullable JsonElement json, CallbackInfoReturnable<NbtPredicate> info) {
		Optional<CompoundTag> nbt = NbtHelper.getNbtCompound(json);
		info.setReturnValue(new NbtPredicate(nbt.isPresent() ? nbt.get() : null));
	}
}
