package de.dafuqs.spectrum.blocks.redstone;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class BlockBreakerBlockEntity extends BlockEntity implements PlayerOwned {
	
	private UUID ownerUUID;
	
	public BlockBreakerBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.BLOCK_BREAKER, pos, state);
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		this.setChanged();
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
	}
	
}
