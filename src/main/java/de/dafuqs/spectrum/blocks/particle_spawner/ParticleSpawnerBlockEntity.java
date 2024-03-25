package de.dafuqs.spectrum.blocks.particle_spawner;

import de.dafuqs.spectrum.inventories.ParticleSpawnerScreenHandler;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class ParticleSpawnerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
	
	protected ParticleSpawnerConfiguration configuration;
	protected boolean initialized = false;
	
	public ParticleSpawnerBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(SpectrumBlockEntities.PARTICLE_SPAWNER, blockPos, blockState);
	}
	
	public ParticleSpawnerBlockEntity(BlockEntityType<ParticleSpawnerBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		
		this.configuration = new ParticleSpawnerConfiguration(
				SpectrumParticleTypes.SHOOTING_STAR,
				new Vector3i(80, 40, 0),
				false,
				10.0F,
				new Vector3f(0.0F, 1.0F, 0.0F),
				new Vector3f(0, 0.0F, 0),
				new Vector3f(0.0F, 0.1F, 0),
				new Vector3f(0.1F, 0.1F, 0.1F),
				1.0F,
				0.2F,
				20,
				10,
				0.02F,
				true);
	}
	
	public static void clientTick(Level world, BlockPos pos, BlockState state, ParticleSpawnerBlockEntity blockEntity) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof AbstractParticleSpawnerBlock particleSpawnerBlock && particleSpawnerBlock.shouldSpawnParticles(world, pos)) {
			blockEntity.configuration.spawnParticles(world, pos);
		}
	}
	
	// Called when the chunk is first loaded to initialize this be
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		this.saveAdditional(nbtCompound);
		return nbtCompound;
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	public void updateInClientWorld() {
		if (level != null) {
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), Block.UPDATE_INVISIBLE);
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("particle_config", this.configuration.toNbt());
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.initialized = false;
		if (tag.contains("particle_config", Tag.TAG_COMPOUND)) {
			this.configuration = ParticleSpawnerConfiguration.fromNbt(tag.getCompound("particle_config"));
			this.initialized = true;
		}
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
		return new ParticleSpawnerScreenHandler(syncId, inv, this);
	}
	
	@Override
	public Component getDisplayName() {
		return Component.translatable("block.spectrum.particle_spawner");
	}
	
	public void applySettings(ParticleSpawnerConfiguration configuration) {
		this.configuration = configuration;
		this.initialized = true;
		
		this.updateInClientWorld();
		this.setChanged();
	}
	
	@Override
	public void writeScreenOpeningData(ServerPlayer player, @NotNull FriendlyByteBuf buf) {
		buf.writeBlockPos(this.worldPosition);
	}
	
	public ParticleSpawnerConfiguration getConfiguration() {
		return configuration;
	}
	
}
