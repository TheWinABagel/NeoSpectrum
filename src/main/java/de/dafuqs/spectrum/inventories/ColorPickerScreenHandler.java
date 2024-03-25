package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.api.block.InkColorSelectedPacketReceiver;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntity;
import de.dafuqs.spectrum.inventories.slots.ColorPickerInputSlot;
import de.dafuqs.spectrum.inventories.slots.InkStorageSlot;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ColorPickerScreenHandler extends AbstractContainerMenu implements InkColorSelectedPacketReceiver {
	
	public static final int PLAYER_INVENTORY_START_X = 8;
	public static final int PLAYER_INVENTORY_START_Y = 84;
	
	protected final Level world;
	protected ColorPickerBlockEntity blockEntity;
	
	public final ServerPlayer player;
	
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		
		if (this.player != null && this.blockEntity.getInkDirty()) {
			SpectrumS2CPacketSender.updateBlockEntityInk(blockEntity.getBlockPos(), blockEntity.getEnergyStorage(), player);
		}
	}
	
	public ColorPickerScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
		this(syncId, playerInventory, buf.readBlockPos(), buf.readBoolean() ? InkColor.of(buf.readUtf()) : null);
	}
	
	public ColorPickerScreenHandler(int syncId, Inventory playerInventory, BlockPos readBlockPos, @Nullable InkColor selectedColor) {
		super(SpectrumScreenHandlerTypes.COLOR_PICKER, syncId);
		this.player = playerInventory.player instanceof ServerPlayer serverPlayerEntity ? serverPlayerEntity : null;
		this.world = playerInventory.player.level();
		BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(readBlockPos);
		if (blockEntity instanceof ColorPickerBlockEntity colorPickerBlockEntity) {
			this.blockEntity = colorPickerBlockEntity;
			this.blockEntity.setSelectedColor(selectedColor);
		} else {
			throw new IllegalArgumentException("GUI called with a position where no valid BlockEntity exists");
		}
		
		checkContainerSize(colorPickerBlockEntity, ColorPickerBlockEntity.INVENTORY_SIZE);
		colorPickerBlockEntity.startOpen(playerInventory.player);
		
		// color picker slots
		this.addSlot(new ColorPickerInputSlot(colorPickerBlockEntity, 0, 26, 33));
		this.addSlot(new InkStorageSlot(colorPickerBlockEntity, 1, 133, 33));
		
		// player inventory
		for (int j = 0; j < 3; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, PLAYER_INVENTORY_START_X + k * 18, PLAYER_INVENTORY_START_Y + j * 18));
			}
		}
		
		// player hotbar
		for (int j = 0; j < 9; ++j) {
			this.addSlot(new Slot(playerInventory, j, PLAYER_INVENTORY_START_X + j * 18, PLAYER_INVENTORY_START_Y + 58));
		}
		
		if (this.player != null) {
			SpectrumS2CPacketSender.updateBlockEntityInk(blockEntity.getBlockPos(), this.blockEntity.getEnergyStorage(), player);
		}
	}
	
	@Override
	public ColorPickerBlockEntity getBlockEntity() {
		return this.blockEntity;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return this.blockEntity.stillValid(player);
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.blockEntity.stopOpen(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemStack2 = slot.getItem();
			itemStack = itemStack2.copy();
			if (index < ColorPickerBlockEntity.INVENTORY_SIZE) {
				if (!this.moveItemStackTo(itemStack2, ColorPickerBlockEntity.INVENTORY_SIZE, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemStack2, 0, ColorPickerBlockEntity.INVENTORY_SIZE, false)) {
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
	
	@Override
	public void onInkColorSelectedPacket(@Nullable InkColor inkColor) {
		this.blockEntity.setSelectedColor(inkColor);
	}
	
}
