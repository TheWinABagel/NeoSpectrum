package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.api.entity.NonLivingAttackable;
import de.dafuqs.spectrum.api.item.MergeableItem;
import de.dafuqs.spectrum.api.item.SplittableItem;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {


    @Shadow public ServerPlayer player;

    @Inject(method = "onPlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0), cancellable = true)
    private void handleSwapInteractions(ServerboundPlayerActionPacket packet, CallbackInfo ci) {

        var mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        var offStack = player.getItemInHand(InteractionHand.OFF_HAND);
        var mainItem = mainStack.getItem();
        var offItem = offStack.getItem();

        if (mainItem instanceof SplittableItem splittable && splittable.canSplit(player, InteractionHand.MAIN_HAND, mainStack)) {
           splitItem(mainStack, splittable);
            ci.cancel();
        }
        else if (offItem instanceof SplittableItem splittable && splittable.canSplit(player, InteractionHand.OFF_HAND, offStack)) {
            splitItem(offStack, splittable);
            ci.cancel();
        }
        else if(mainItem instanceof MergeableItem mergeable && offItem instanceof MergeableItem && mergeable.canMerge(player, mainStack, offStack)) {
           mergeItems(mainStack, offStack, mergeable);
            ci.cancel();
        }
    }

    @Mixin(targets = "net/minecraft/server/network/ServerPlayNetworkHandler$1")
    static class NetworkEntityValidationMixin {

        @Final
        @Shadow(aliases = "field_28963")
        private ServerGamePacketListenerImpl this$0;

        @Final
        @Shadow(aliases = "field_28962")
        private Entity innerEntity;

        @Inject(method = "attack", at = @At(value = "HEAD"), cancellable = true)
        public void allowNonLivingEntityAttack(CallbackInfo ci) {
            if (innerEntity instanceof NonLivingAttackable) {
                this$0.player.attack(innerEntity);
                ci.cancel();
            }
        }
    }

    @Unique
    private void splitItem(ItemStack stack, SplittableItem splittable) {
        var split = splittable.getResult(player, stack);
        player.setItemInHand(InteractionHand.MAIN_HAND, split);
        player.setItemInHand(InteractionHand.OFF_HAND, split.copy());
        player.stopUsingItem();
        player.playNotifySound(splittable.getSplitSound(), SoundSource.PLAYERS, 1, 0.8F + player.getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private void mergeItems(ItemStack firstHalf, ItemStack secondHalf, MergeableItem mergeable) {
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.MAIN_HAND, mergeable.getResult(player, firstHalf, secondHalf));
        player.stopUsingItem();
        player.playNotifySound(mergeable.getMergeSound(), SoundSource.PLAYERS, 1, 0.8F + player.getRandom().nextFloat() * 0.4F);
    }
}
