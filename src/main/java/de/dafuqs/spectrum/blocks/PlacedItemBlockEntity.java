package de.dafuqs.spectrum.blocks;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlacedItemBlockEntity extends BlockEntity implements PlayerOwned {
	
	protected ItemStack stack = ItemStack.EMPTY;
	protected UUID ownerUUID;
	
	public PlacedItemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public PlacedItemBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.PLACED_ITEM, pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.stack = ItemStack.of(nbt.getCompound("stack"));
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("stack", this.stack.save(new CompoundTag()));
		
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
	}
	
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.setChanged();
	}
	
	public ItemStack getStack() {
		return this.stack;
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(@NotNull Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
}
