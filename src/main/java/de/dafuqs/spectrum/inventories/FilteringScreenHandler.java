package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.api.block.FilterConfigurable;
import de.dafuqs.spectrum.inventories.slots.ShadowSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FilteringScreenHandler extends AbstractContainerMenu {

	protected final Level world;
	protected FilterConfigurable filterConfigurable;
	protected final Container filterInventory;

	public FilteringScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf packetByteBuf) {
		this(SpectrumScreenHandlerTypes.FILTERING, syncId, playerInventory, FilterConfigurable.getFilterInventoryFromPacket(packetByteBuf));
	}

	public FilteringScreenHandler(int syncId, Inventory playerInventory, FilterConfigurable filterConfigurable) {
		this(SpectrumScreenHandlerTypes.FILTERING, syncId, playerInventory, FilterConfigurable.getFilterInventoryFromItems(filterConfigurable.getItemFilters()));
		this.filterConfigurable = filterConfigurable;
	}

	protected FilteringScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container filterInventory) {
		super(type, syncId);
		this.world = playerInventory.player.level();
		this.filterInventory = filterInventory;

		// filter slots
		int startX = (176 / 2) - (filterInventory.getContainerSize() + 1) * 9;
		for (int k = 0; k < filterInventory.getContainerSize(); ++k) {
			this.addSlot(new FilterSlot(filterInventory, k, startX + k * 23, 18));
		}

		// player inventory slots
		int i = 52;
		for (int j = 0; j < 3; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, j * 18 + i));
			}
		}
		// player hotbar
		for (int j = 0; j < 9; ++j) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 58 + i));
		}

	}
	
	
	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	public Container getInventory() {
		return null;
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
	}

	public FilterConfigurable getFilterConfigurable() {
		return this.filterConfigurable;
	}

	protected class FilterSlot extends ShadowSlot {

		public FilterSlot(Container inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean onClicked(ItemStack heldStack, ClickAction type, Player player) {
			if (!world.isClientSide && filterConfigurable != null) {
				filterConfigurable.setFilterItem(getContainerSlot(), heldStack.getItem());
			}
			return super.onClicked(heldStack, type, player);
		}
	}

}
