package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.blocks.chests.RestockingChestBlockEntity;
import de.dafuqs.spectrum.inventories.slots.ExtractOnlySlot;
import de.dafuqs.spectrum.inventories.slots.StackFilterSlot;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RestockingChestScreenHandler extends AbstractContainerMenu {
	
	protected final Level world;
	private final Container inventory;
	
	public RestockingChestScreenHandler(int syncId, Inventory playerInventory) {
		this(SpectrumScreenHandlerTypes.RESTOCKING_CHEST, syncId, playerInventory);
	}
	
	protected RestockingChestScreenHandler(MenuType<?> type, int i, Inventory playerInventory) {
		this(type, i, playerInventory, new SimpleContainer(RestockingChestBlockEntity.INVENTORY_SIZE));
	}
	
	public RestockingChestScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
		this(SpectrumScreenHandlerTypes.RESTOCKING_CHEST, syncId, playerInventory, inventory);
	}
	
	protected RestockingChestScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory) {
		super(type, syncId);
		this.inventory = inventory;
		this.world = playerInventory.player.level();
		
		checkContainerSize(inventory, RestockingChestBlockEntity.INVENTORY_SIZE);
		inventory.startOpen(playerInventory.player);
		
		// chest inventory
		int l;
		for (l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inventory, l * 9 + k, 8 + k * 18, 67 + l * 18));
			}
		}
		
		// crafting tablet slots
		for (int j = 0; j < 4; j++) {
			int slotId = RestockingChestBlockEntity.RECIPE_SLOTS[j];
			this.addSlot(new StackFilterSlot(inventory, slotId, 26 + j * 36, 18, SpectrumItems.CRAFTING_TABLET));
		}
		
		// crafting result slots
		for (int j = 0; j < 4; j++) {
			int slotId = RestockingChestBlockEntity.RESULT_SLOTS[j];
			this.addSlot(new ExtractOnlySlot(inventory, slotId, 26 + j * 36, 42));
		}
		
		// player inventory
		for (l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 138 + l * 18));
			}
		}
		
		// player hotbar
		for (l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 196));
		}
	}
	
	@Override
	public boolean stillValid(Player player) {
		return this.inventory.stillValid(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack clickedStackCopy = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		if (slot.hasItem()) {
			ItemStack clickedStack = slot.getItem();
			clickedStackCopy = clickedStack.copy();
			
			if (index < RestockingChestBlockEntity.INVENTORY_SIZE) {
				// => player inv
				if (!this.moveItemStackTo(clickedStack, 35, 71, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index > RestockingChestBlockEntity.INVENTORY_SIZE && clickedStackCopy.is(SpectrumItems.CRAFTING_TABLET)) {
				if (!this.moveItemStackTo(clickedStack, RestockingChestBlockEntity.RECIPE_SLOTS[0], RestockingChestBlockEntity.RECIPE_SLOTS[RestockingChestBlockEntity.RECIPE_SLOTS.length - 1] + 1, false)) {
					return ItemStack.EMPTY;
				}
			}
			
			// chest => inventory
			if (!this.moveItemStackTo(clickedStack, 0, RestockingChestBlockEntity.CHEST_SLOTS.length - 1, false)) {
				return ItemStack.EMPTY;
			}
			
			if (clickedStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (clickedStack.getCount() == clickedStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, clickedStack);
		}
		
		
		return clickedStackCopy;
	}
	
	public Container getInventory() {
		return this.inventory;
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.inventory.stopOpen(player);
	}
	
}
