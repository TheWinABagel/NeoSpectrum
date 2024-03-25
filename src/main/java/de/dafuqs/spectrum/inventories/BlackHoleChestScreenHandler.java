package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.api.block.FilterConfigurable;
import de.dafuqs.spectrum.blocks.chests.BlackHoleChestBlockEntity;
import de.dafuqs.spectrum.inventories.slots.ShadowSlot;
import de.dafuqs.spectrum.inventories.slots.StackFilterSlot;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlackHoleChestScreenHandler extends AbstractContainerMenu {
	
	protected static final int ROWS = 3;
	
	protected final Level world;
	private final Container inventory;
	protected BlackHoleChestBlockEntity blackHoleChestBlockEntity;
	protected Container filterInventory;
	
	public BlackHoleChestScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf packetByteBuf) {
		this(syncId, playerInventory, packetByteBuf.readBlockPos(), FilterConfigurable.getFilterInventoryFromPacket(packetByteBuf));
	}
	
	private BlackHoleChestScreenHandler(int syncId, Inventory playerInventory, BlockPos readBlockPos, Container filterInventory) {
		this(SpectrumScreenHandlerTypes.BLACK_HOLE_CHEST, syncId, playerInventory, new SimpleContainer(BlackHoleChestBlockEntity.INVENTORY_SIZE), filterInventory);
		BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(readBlockPos);
		if (blockEntity instanceof BlackHoleChestBlockEntity blackHoleChestBlockEntity) {
			this.blackHoleChestBlockEntity = blackHoleChestBlockEntity;
		}
	}

	public BlackHoleChestScreenHandler(int syncId, Inventory playerInventory, BlackHoleChestBlockEntity blackHoleChestBlockEntity) {
		this(SpectrumScreenHandlerTypes.BLACK_HOLE_CHEST, syncId, playerInventory, blackHoleChestBlockEntity, FilterConfigurable.getFilterInventoryFromItems(blackHoleChestBlockEntity.getItemFilters()));
		this.blackHoleChestBlockEntity = blackHoleChestBlockEntity;
		this.filterInventory = FilterConfigurable.getFilterInventoryFromItems(blackHoleChestBlockEntity.getItemFilters());
	}

	protected BlackHoleChestScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory, Container filterInventory) {
		super(type, syncId);
		this.inventory = inventory;
		this.world = playerInventory.player.level();
		this.filterInventory = filterInventory;

		checkContainerSize(inventory, BlackHoleChestBlockEntity.INVENTORY_SIZE);
		inventory.startOpen(playerInventory.player);

		int i = (ROWS - 4) * 18;
		
		// sucking chest slots
		int j;
		int k;
		for (j = 0; j < ROWS; ++j) {
			for (k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 26 + 16 + j * 18));
			}
		}
		
		// player inventory slots
		for (j = 0; j < 3; ++j) {
			for (k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 112 + 19 + j * 18 + i));
			}
		}
		
		// player hotbar
		for (j = 0; j < 9; ++j) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 170 + 19 + i));
		}
		
		// experience provider slot
		this.addSlot(new StackFilterSlot(inventory, BlackHoleChestBlockEntity.EXPERIENCE_STORAGE_PROVIDER_ITEM_SLOT, 152, 18, SpectrumItems.KNOWLEDGE_GEM));
		
		// filter slots
		for (k = 0; k < BlackHoleChestBlockEntity.ITEM_FILTER_SLOT_COUNT; ++k) {
			this.addSlot(new SuckingChestFilterSlot(filterInventory, k, 8 + k * 23, 18));
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
			if (index < ROWS * 9) {
				if (!this.moveItemStackTo(itemStack2, ROWS * 9, this.slots.size() - 6, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemStack2, 0, ROWS * 9, false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemStack2.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
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

	public BlackHoleChestBlockEntity getBlockEntity() {
		return this.blackHoleChestBlockEntity;
	}

	protected class SuckingChestFilterSlot extends ShadowSlot {

		public SuckingChestFilterSlot(Container inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean onClicked(ItemStack heldStack, ClickAction type, Player player) {
			if (blackHoleChestBlockEntity != null) {
				blackHoleChestBlockEntity.setFilterItem(getContainerSlot(), heldStack.getItem());
			}
			return super.onClicked(heldStack, type, player);
		}
	}
	
}
