package de.dafuqs.spectrum.api.item;

import net.minecraft.world.item.ItemStack;

public interface SlotReservingItem {

    public static String NBT_STRING = "reserved";

    boolean isReservingSlot(ItemStack stack);

    void markReserved(ItemStack stack, boolean reserved);
}
