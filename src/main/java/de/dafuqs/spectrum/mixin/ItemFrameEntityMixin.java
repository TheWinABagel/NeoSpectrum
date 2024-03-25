package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.items.magic_items.CelestialPocketWatchItem;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public abstract class ItemFrameEntityMixin {
	
	@Shadow
	public abstract ItemStack getHeldItemStack();
	
	@Inject(method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setRotation(I)V"))
	public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (getHeldItemStack().is(SpectrumItems.CELESTIAL_POCKETWATCH) && (((ItemFrame) (Object) this).level() instanceof ServerLevel serverWorld)) {
			CelestialPocketWatchItem.tryAdvanceTime(serverWorld, (ServerPlayer) player);
		}
	}
	
}
