package de.dafuqs.spectrum.inventories;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Vanilla does autocrafting, too!
 * See SheepEntity::createDyeMixingCraftingInventory
 */
public class AutoCraftingInventory extends AutoInventory {
	
	List<ItemStack> inputInventory;
	
	public AutoCraftingInventory(int width, int height) {
		this(width, height, 0);
	}
	
	public AutoCraftingInventory(int width, int height, int additionalSlots) {
		super(width, height);
		inputInventory = NonNullList.withSize(width * height + additionalSlots, ItemStack.EMPTY);
	}
	
	public void setInputInventory(List<ItemStack> inputInventory) {
		this.inputInventory = inputInventory;
	}
	
	public void setInputInventory(Container inventory, int fromSlot, int toSlot) {
		this.inputInventory = NonNullList.withSize(toSlot - fromSlot, ItemStack.EMPTY);
		for (int i = fromSlot; i < toSlot; i++) {
			this.inputInventory.set(i, inventory.getItem(i));
		}
	}
	
	@Override
	public int getContainerSize() {
		return inputInventory.size();
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public ItemStack getItem(int slot) {
		return inputInventory.get(slot);
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return false;
	}
	
	@Override
	public void clearContent() {
	}
	
	@Override
	public int getHeight() {
		return 3;
	}
	
	@Override
	public int getWidth() {
		return 3;
	}
	
}
