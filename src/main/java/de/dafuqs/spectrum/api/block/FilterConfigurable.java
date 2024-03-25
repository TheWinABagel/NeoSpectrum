package de.dafuqs.spectrum.api.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public interface FilterConfigurable {

    List<Item> getItemFilters();

    void setFilterItem(int slot, Item item);

    default void writeFilterNbt(CompoundTag tag, List<Item> filterItems) {
        for (int i = 0; i < filterItems.size(); i++) {
			tag.putString("Filter" + i, BuiltInRegistries.ITEM.getKey(filterItems.get(i)).toString());
        }
    }

    default void readFilterNbt(CompoundTag tag, List<Item> filterItems) {
        for (int i = 0; i < filterItems.size(); i++) {
            if (tag.contains("Filter" + i, Tag.TAG_STRING)) {
				filterItems.set(i, BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString("Filter" + i))));
            }
        }
    }

    static Container getFilterInventoryFromPacket(FriendlyByteBuf packetByteBuf) {
        int size = packetByteBuf.readInt();
        Container inventory = new SimpleContainer(size);
        for (int i = 0; i < size; i++) {
			inventory.setItem(i, BuiltInRegistries.ITEM.get(packetByteBuf.readResourceLocation()).getDefaultInstance());
        }
        return inventory;
    }

    static Container getFilterInventoryFromItems(List<Item> items) {
        Container inventory = new SimpleContainer(items.size());
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i).getDefaultInstance());
        }
        return inventory;
    }

    static void writeScreenOpeningData(FriendlyByteBuf buf, List<Item> filterItems) {
        buf.writeInt(filterItems.size());
        for (Item filterItem : filterItems) {
			buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(filterItem));
        }
    }

    default boolean hasEmptyFilter() {
        for (Item item : getItemFilters()) {
            if (item != Items.AIR) {
                return false;
            }
        }
        return true;
    }

}
