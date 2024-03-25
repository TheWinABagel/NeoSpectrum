package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.blocks.chests.CompactingChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompactingChestScreenHandler extends AbstractContainerMenu {

	private final Container inventory;
	protected final int ROWS = 3;
	protected CompactingChestBlockEntity compactingChestBlockEntity;
	protected AutoCompactingInventory.AutoCraftingMode currentCraftingMode;
	
	public CompactingChestScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf packetByteBuf) {
		this(syncId, playerInventory, packetByteBuf.readBlockPos(), packetByteBuf.readInt());
	}
	
	public CompactingChestScreenHandler(int syncId, Inventory playerInventory, BlockPos readBlockPos, int currentCraftingMode) {
		this(SpectrumScreenHandlerTypes.COMPACTING_CHEST, syncId, playerInventory);
		
		BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(readBlockPos);
		if (blockEntity instanceof CompactingChestBlockEntity compactingChestBlockEntity) {
			this.compactingChestBlockEntity = compactingChestBlockEntity;
		}
		this.currentCraftingMode = AutoCompactingInventory.AutoCraftingMode.values()[currentCraftingMode];
	}
	
	public CompactingChestScreenHandler(int syncId, Inventory playerInventory, CompactingChestBlockEntity compactingChestBlockEntity) {
		this(SpectrumScreenHandlerTypes.COMPACTING_CHEST, syncId, playerInventory, compactingChestBlockEntity);
		this.compactingChestBlockEntity = compactingChestBlockEntity;
	}
	
	protected CompactingChestScreenHandler(MenuType<?> type, int i, Inventory playerInventory) {
		this(type, i, playerInventory, new SimpleContainer(27));
	}
	
	public CompactingChestScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
		this(SpectrumScreenHandlerTypes.COMPACTING_CHEST, syncId, playerInventory, inventory);
	}
	
	protected CompactingChestScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory) {
		super(type, syncId);
		this.inventory = inventory;
		
		checkContainerSize(inventory, 27);
		inventory.startOpen(playerInventory.player);
		
		int i = (ROWS - 4) * 18;
		
		int j;
		int k;
		for (j = 0; j < ROWS; ++j) {
			for (k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 26 + j * 18));
			}
		}
		
		for (j = 0; j < 3; ++j) {
			for (k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 112 + j * 18 + i));
			}
		}
		
		for (j = 0; j < 9; ++j) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 170 + i));
		}
	}
	
	@Override
	public boolean stillValid(Player player) {
		return this.inventory.stillValid(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemStack2 = slot.getItem();
			itemStack = itemStack2.copy();
			if (index < this.ROWS * 9) {
				if (!this.moveItemStackTo(itemStack2, this.ROWS * 9, this.slots.size(), true)) {
					if (inventory instanceof CompactingChestBlockEntity compactingChestBlockEntity) {
						compactingChestBlockEntity.inventoryChanged();
					}
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemStack2, 0, this.ROWS * 9, false)) {
				if (inventory instanceof CompactingChestBlockEntity compactingChestBlockEntity) {
					compactingChestBlockEntity.inventoryChanged();
				}
				return ItemStack.EMPTY;
			}
			
			if (itemStack2.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		
		if (inventory instanceof CompactingChestBlockEntity compactingChestBlockEntity) {
			compactingChestBlockEntity.inventoryChanged();
		}
		return itemStack;
	}
	
	public Container getInventory() {
		return this.inventory;
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.inventory.stopOpen(player);
	}
	
	public CompactingChestBlockEntity getBlockEntity() {
		return this.compactingChestBlockEntity;
	}
	
	public AutoCompactingInventory.AutoCraftingMode getCurrentCraftingMode() {
		return currentCraftingMode;
	}
	
}
