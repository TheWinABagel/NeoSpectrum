package de.dafuqs.spectrum.api.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public interface MergeableItem {

    ItemStack getResult(ServerPlayer player, ItemStack firstHalf, ItemStack secondHalf);

    boolean canMerge(ServerPlayer player, ItemStack parent, ItemStack other);

    default boolean verify(ItemStack parent, ItemStack other) {
        if (!EnchantmentHelper.getEnchantments(parent).equals(EnchantmentHelper.getEnchantments(other))) {
            return false;
        }

        var parNbt = parent.getOrCreateTag();
        var otherNbt = other.getOrCreateTag();
        if (parNbt.contains("pairSignature") && otherNbt.contains("pairSignature"))
            return parNbt.getLong("pairSignature") == otherNbt.getLong("pairSignature");
        return false;
    }

    SoundEvent getMergeSound();
}
