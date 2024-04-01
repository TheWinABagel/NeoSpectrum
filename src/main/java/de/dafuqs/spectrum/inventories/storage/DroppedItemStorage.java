package de.dafuqs.spectrum.inventories.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DroppedItemStorage extends ItemStackHandler {
    public DroppedItemStorage(Item item, CompoundTag nbt) {
        super(1);
        this.stacks.set(0, new ItemStack(item, 1, nbt));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return 1;
    }
}