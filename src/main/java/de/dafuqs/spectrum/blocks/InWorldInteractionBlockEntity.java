package de.dafuqs.spectrum.blocks;

import de.dafuqs.spectrum.api.block.ImplementedInventory;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class InWorldInteractionBlockEntity extends BlockEntity implements ImplementedInventory {
	
	private final int inventorySize;
	protected NonNullList<ItemStack> items;
	@Nullable
	protected ResourceLocation lootTableId;
	protected long lootTableSeed;
	
	public InWorldInteractionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize) {
		super(type, pos, state);
		this.inventorySize = inventorySize;
		this.items = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
	}
	
	// interaction methods
	public void updateInClientWorld() {
		((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}
	
	// Called when the chunk is first loaded to initialize this be
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtCompound = new CompoundTag();
		this.saveAdditional(nbtCompound);
		return nbtCompound;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.items = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, items);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, items);
	}
	
	protected boolean deserializeLootTable(CompoundTag nbt) {
		if (nbt.contains("LootTable", Tag.TAG_STRING)) {
			this.lootTableId = new ResourceLocation(nbt.getString("LootTable"));
			this.lootTableSeed = nbt.getLong("LootTableSeed");
			return true;
		}
		
		return false;
	}

	protected boolean serializeLootTable(CompoundTag nbt) {
		if (this.lootTableId == null) {
			return false;
		}
		
		nbt.putString("LootTable", this.lootTableId.toString());
		if (this.lootTableSeed != 0L) {
			nbt.putLong("LootTableSeed", this.lootTableSeed);
		}
		
		return true;
	}

	public void checkLootInteraction(@Nullable Player player) {
		var world = this.getLevel();
		if (world != null && this.lootTableId != null && world.getServer() != null) {
			LootTable lootTable = world.getServer().getLootData().getLootTable(this.lootTableId);
			if (player instanceof ServerPlayer serverPlayerEntity) {
				CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayerEntity, this.lootTableId);
			}
			
			this.lootTableId = null;
			var builder = new LootParams.Builder((ServerLevel) world)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition));
			if (player != null) {
				builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
			}

			if (lootTableSeed != 0) {
				lootTable.fill(this, builder.create(LootContextParamSets.CHEST), lootTableSeed);
			} else {
				lootTable.fill(this, builder.create(LootContextParamSets.CHEST), this.getLevel().getRandom().nextLong());
			}

			this.setChanged();
		}
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public NonNullList<ItemStack> getItems() {
		return items;
	}
	
	@Override
	public void inventoryChanged() {
		this.setChanged();
		if (level != null && !level.isClientSide) {
			updateInClientWorld();
		}
	}
	
}
