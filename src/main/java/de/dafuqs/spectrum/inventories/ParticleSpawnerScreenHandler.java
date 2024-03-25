package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ParticleSpawnerScreenHandler extends AbstractContainerMenu {
	
	protected final Player player;
	protected ParticleSpawnerBlockEntity particleSpawnerBlockEntity;
	
	public ParticleSpawnerScreenHandler(int syncId, Inventory inventory) {
		super(SpectrumScreenHandlerTypes.PARTICLE_SPAWNER, syncId);
		this.player = inventory.player;
	}
	
	public ParticleSpawnerScreenHandler(int syncId, Inventory inv, ParticleSpawnerBlockEntity particleSpawnerBlockEntity) {
		this(syncId, inv);
		this.particleSpawnerBlockEntity = particleSpawnerBlockEntity;
	}
	
	public ParticleSpawnerScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf packetByteBuf) {
		this(syncId, playerInventory, packetByteBuf.readBlockPos());
	}
	
	public ParticleSpawnerScreenHandler(int syncId, Inventory playerInventory, BlockPos readBlockPos) {
		super(SpectrumScreenHandlerTypes.PARTICLE_SPAWNER, syncId);
		this.player = playerInventory.player;
		BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(readBlockPos);
		if (blockEntity instanceof ParticleSpawnerBlockEntity particleSpawnerBlockEntity) {
			this.particleSpawnerBlockEntity = particleSpawnerBlockEntity;
		} else {
			throw new IllegalArgumentException("Particle Spawner GUI called with a position where no ParticleSpawnerBlockEntity exists");
		}
	}
	
	public ParticleSpawnerBlockEntity getBlockEntity() {
		return this.particleSpawnerBlockEntity;
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return this.particleSpawnerBlockEntity != null && !this.particleSpawnerBlockEntity.isRemoved();
	}
	
}
