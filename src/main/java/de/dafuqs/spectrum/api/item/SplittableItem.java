package de.dafuqs.spectrum.api.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface SplittableItem {

    ItemStack getResult(ServerPlayer player, ItemStack parent);

    boolean canSplit(ServerPlayer player, InteractionHand activeHand, ItemStack stack);

    default void sign(ServerPlayer player, ItemStack stack) {
        stack.getOrCreateTag().putLong("pairSignature", player.level().getGameTime() + player.getUUID().getMostSignificantBits());
    }

    SoundEvent getSplitSound();
}
