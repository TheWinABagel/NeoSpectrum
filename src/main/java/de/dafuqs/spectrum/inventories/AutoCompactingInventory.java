package de.dafuqs.spectrum.inventories;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AutoCompactingInventory extends AutoInventory {
	
	ItemStack inputItemStack;
	private AutoCraftingMode autoCraftingMode;
	
	public AutoCompactingInventory() {
		super(3, 3);
		this.inputItemStack = ItemStack.EMPTY;
		this.autoCraftingMode = AutoCraftingMode.ThreeXThree;
	}
	
	public void setCompacting(AutoCraftingMode autoCraftingMode, ItemStack itemStack) {
		this.autoCraftingMode = autoCraftingMode;
		this.inputItemStack = itemStack;
	}
	
	@Override
	public int getContainerSize() {
		return getSize() * getSize();
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public ItemStack getItem(int slot) {
		return inputItemStack;
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
		return getSize();
	}
	
	@Override
	public int getWidth() {
		return getSize();
	}
	
	private int getSize() {
		return switch (this.autoCraftingMode) {
			case OneXOne -> 1;
			case TwoXTwo -> 2;
			default -> 3;
		};
	}
	
	public enum AutoCraftingMode {
		OneXOne,
		TwoXTwo,
		ThreeXThree;
		
		public int getItemCount() {
			if (this == AutoCraftingMode.OneXOne) {
				return 1;
			} else if (this == AutoCraftingMode.TwoXTwo) {
				return 4;
			} else {
				return 9;
			}
		}
	}
	
}
