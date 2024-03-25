package de.dafuqs.spectrum.blocks.block_flooder;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class BlockFlooderBlockEntity extends BlockEntity {
	
	private Entity owner;
	private UUID ownerUUID;
	
	private BlockPos sourcePos;
	private BlockState targetBlockState = Blocks.AIR.defaultBlockState();
	
	public BlockFlooderBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.BLOCK_FLOODER, pos, state);
	}
	
	public UUID getOwnerUUID() {
		return ownerUUID;
	}
	
	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}
	
	public Entity getOwner() {
		if (this.owner == null) {
			this.owner = PlayerOwned.getPlayerEntityIfOnline(this.ownerUUID);
		}
		return this.owner;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		if (nbt.contains("SourcePositionX") && nbt.contains("SourcePositionY") && nbt.contains("SourcePositionZ")) {
			this.sourcePos = new BlockPos(nbt.getInt("SourcePositionX"), nbt.getInt("SourcePositionY"), nbt.getInt("SourcePositionZ"));
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		if (this.sourcePos != null) {
			nbt.putInt("SourcePositionX", this.sourcePos.getX());
			nbt.putInt("SourcePositionY", this.sourcePos.getY());
			nbt.putInt("SourcePositionZ", this.sourcePos.getZ());
		}
	}
	
	
	public BlockPos getSourcePos() {
		if (this.sourcePos == null) {
			this.sourcePos = this.worldPosition;
		}
		return this.sourcePos;
	}
	
	public void setSourcePos(BlockPos sourcePos) {
		this.sourcePos = sourcePos;
	}
	
	public BlockState getTargetBlockState() {
		return targetBlockState;
	}
	
	public void setTargetBlockState(BlockState targetBlockState) {
		this.targetBlockState = targetBlockState;
	}
	
}
