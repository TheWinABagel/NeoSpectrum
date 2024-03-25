package de.dafuqs.spectrum.blocks.ender;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.block.PlayerOwnedWithName;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class EnderDropperBlockEntity extends BlockEntity implements PlayerOwnedWithName {
	
	private UUID ownerUUID;
	private String ownerName;
	
	public EnderDropperBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.ENDER_DROPPER, blockPos, blockState);
	}
	
	protected Component getContainerName() {
		if (hasOwner()) {
			return Component.translatable("block.spectrum.ender_dropper.owner", this.ownerName);
		} else {
			return Component.translatable("block.spectrum.ender_dropper");
		}
	}
	
	public int chooseNonEmptySlot() {
		if (this.hasOwner()) {
			Player playerEntity = getOwnerIfOnline();
			if (playerEntity == null) {
				return -1; // player not online => no drop
			} else {
				int i = -1;
				int j = 1;
				
				PlayerEnderChestContainer enderInventory = playerEntity.getEnderChestInventory();
				for (int k = 0; k < enderInventory.getContainerSize(); ++k) {
					if (!(enderInventory.getItem(k)).isEmpty() && level.random.nextInt(j++) == 0) {
						i = k;
					}
				}
				
				return i;
			}
		} else {
			return -1; // no owner
		}
	}
	
	public ItemStack getStack(int slot) {
		Player playerEntity = getOwnerIfOnline();
		if (playerEntity != null) {
			PlayerEnderChestContainer enderInventory = playerEntity.getEnderChestInventory();
			return enderInventory.getItem(slot);
		}
		return ItemStack.EMPTY;
	}
	
	public void setStack(int slot, ItemStack itemStack) {
		Player playerEntity = getOwnerIfOnline();
		if (playerEntity != null) {
			PlayerEnderChestContainer enderInventory = playerEntity.getEnderChestInventory();
			enderInventory.setItem(slot, itemStack);
		}
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public String getOwnerName() {
		return this.ownerName;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		this.ownerName = playerEntity.getName().getString();
		setChanged();
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		this.ownerName = PlayerOwned.readOwnerName(nbt);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		PlayerOwned.writeOwnerName(nbt, this.ownerName);
	}
	
}
