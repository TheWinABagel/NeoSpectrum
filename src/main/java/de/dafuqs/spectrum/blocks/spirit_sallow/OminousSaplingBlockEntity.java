package de.dafuqs.spectrum.blocks.spirit_sallow;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class OminousSaplingBlockEntity extends BlockEntity implements PlayerOwned {
	
	public UUID ownerUUID;
	
	public OminousSaplingBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.OMINOUS_SAPLING, blockPos, blockState);
	}
	
	public OminousSaplingBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		setChanged();
	}
	
	// Serialize the BlockEntity
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		
		if (this.ownerUUID != null) {
			tag.putUUID("OwnerUUID", this.ownerUUID);
		}
	}
	
	// Deserialize the BlockEntity
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		
		if (tag.contains("OwnerUUID")) {
			this.ownerUUID = tag.getUUID("OwnerUUID");
		} else {
			this.ownerUUID = null;
		}
	}
	
}
