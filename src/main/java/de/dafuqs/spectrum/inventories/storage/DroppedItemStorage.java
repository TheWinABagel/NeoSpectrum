package de.dafuqs.spectrum.inventories.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public class DroppedItemStorage extends SingleItemStorage {
    public DroppedItemStorage(Item item, CompoundTag nbt) {
        this.variant = ItemVariant.of(item, nbt);
        this.amount = 1;
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return 1;
    }
}