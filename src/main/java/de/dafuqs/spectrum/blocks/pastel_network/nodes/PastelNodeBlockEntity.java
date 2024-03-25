package de.dafuqs.spectrum.blocks.pastel_network.nodes;

import de.dafuqs.spectrum.api.block.FilterConfigurable;
import de.dafuqs.spectrum.blocks.pastel_network.Pastel;
import de.dafuqs.spectrum.blocks.pastel_network.network.NodeRemovalReason;
import de.dafuqs.spectrum.blocks.pastel_network.network.PastelNetwork;
import de.dafuqs.spectrum.inventories.FilteringScreenHandler;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class PastelNodeBlockEntity extends BlockEntity implements FilterConfigurable, ExtendedScreenHandlerFactory {
	
	public static final int ITEM_FILTER_COUNT = 5;
	public static final int RANGE = 12;
	protected PastelNetwork network;
	protected @Nullable UUID networkUUIDToMerge = null;
	protected long lastTransferTick = 0;
	protected final long cachedRedstonePowerTick = 0;
	protected boolean cachedNoRedstonePower = true;
	
	protected long itemCountUnderway = 0;
	
	protected BlockApiCache<Storage<ItemVariant>, Direction> connectedStorageCache = null;
	protected Direction cachedDirection = null;

    private final List<Item> filterItems;

    public PastelNodeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpectrumBlockEntities.PASTEL_NODE, blockPos, blockState);
        this.filterItems = NonNullList.withSize(ITEM_FILTER_COUNT, Items.AIR);
    }

    public @Nullable Storage<ItemVariant> getConnectedStorage() {
        if (connectedStorageCache == null) {
            BlockState state = this.getBlockState();
            if (!(state.getBlock() instanceof PastelNodeBlock)) {
                return null;
            }
            cachedDirection = state.getValue(PastelNodeBlock.FACING);
            connectedStorageCache = BlockApiCache.create(ItemStorage.SIDED, (ServerLevel) level, this.getBlockPos().relative(cachedDirection.getOpposite()));
        }
        return connectedStorageCache.find(cachedDirection);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        if (!world.isClientSide) {
            if (this.networkUUIDToMerge != null) {
                this.network = Pastel.getServerInstance().joinNetwork(this, this.networkUUIDToMerge);
                this.networkUUIDToMerge = null;
            } else if (this.network == null) {
                this.network = Pastel.getServerInstance().joinNetwork(this, null);
            }
        }
    }

    public boolean canTransfer() {
        long time = this.getLevel().getGameTime();
        if (time > this.cachedRedstonePowerTick) {
            this.cachedNoRedstonePower = level.getBestNeighborSignal(this.worldPosition) == 0;
        }
        return this.getLevel().getGameTime() > lastTransferTick && this.cachedNoRedstonePower;
    }

    public void markTransferred() {
        this.lastTransferTick = level.getGameTime();
        this.setChanged();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Network")) {
            UUID networkUUID = nbt.getUUID("Network");
            if (this.getLevel() == null) {
                this.networkUUIDToMerge = networkUUID;
            } else {
                this.network = Pastel.getInstance(level.isClientSide).joinNetwork(this, networkUUID);
            }
        }
        if (nbt.contains("LastTransferTick", Tag.TAG_LONG)) {
            this.lastTransferTick = nbt.getLong("LastTransferTick");
        }
        if (nbt.contains("ItemCountUnderway", Tag.TAG_LONG)) {
            this.itemCountUnderway = nbt.getLong("ItemCountUnderway");
        }
        if (this.getNodeType().usesFilters()) {
            readFilterNbt(nbt, this.filterItems);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.network != null) {
            nbt.putUUID("Network", this.network.getUUID());
        }
        nbt.putLong("LastTransferTick", this.lastTransferTick);
        nbt.putLong("ItemCountUnderway", this.itemCountUnderway);
        if (this.getNodeType().usesFilters()) {
            writeFilterNbt(nbt, this.filterItems);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbtCompound = new CompoundTag();
        this.saveAdditional(nbtCompound);
        return nbtCompound;
    }

    // triggered when the chunk is unloaded, or the world quit
    @Override
    public void setRemoved() {
        super.setRemoved();
        Pastel.getInstance(level.isClientSide).removeNode(this, NodeRemovalReason.UNLOADED);
    }

    public void onBroken() {
        Pastel.getInstance(level.isClientSide).removeNode(this, NodeRemovalReason.BROKEN);
    }

    public boolean canConnect(PastelNodeBlockEntity node) {
        return this.worldPosition.closerThan(node.worldPosition, RANGE);
    }

    public PastelNetwork getNetwork() {
        return this.network;
    }

    public PastelNodeType getNodeType() {
        if (this.getBlockState().getBlock() instanceof PastelNodeBlock pastelNodeBlock) {
            return pastelNodeBlock.pastelNodeType;
        }
        return PastelNodeType.CONNECTION;
    }

    public void setNetwork(PastelNetwork network) {
        this.network = network;
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            updateInClientWorld();
            this.setChanged();
        }
    }
    
    public long getItemCountUnderway() {
        return this.itemCountUnderway;
    }
    
    public void addItemCountUnderway(long count) {
        this.itemCountUnderway += count;
        this.itemCountUnderway = Math.max(0, this.itemCountUnderway);
        this.setChanged();
    }

    // interaction methods
    public void updateInClientWorld() {
        ((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
    }

    @Override
    public List<Item> getItemFilters() {
        return this.filterItems;
    }

    @Override
    public void setFilterItem(int slot, Item item) {
        this.filterItems.set(slot, item);
    }

    public Predicate<ItemVariant> getTransferFilterTo(PastelNodeBlockEntity other) {
        if (this.getNodeType().usesFilters() && !this.hasEmptyFilter()) {
            if (other.getNodeType().usesFilters() && !other.hasEmptyFilter()) {
                // unionize both filters
                return itemVariant -> filterItems.contains(itemVariant.getItem()) && other.filterItems.contains(itemVariant.getItem());
            } else {
                return itemVariant -> filterItems.contains(itemVariant.getItem());
            }
        } else if (other.getNodeType().usesFilters() && !other.hasEmptyFilter()) {
            return itemVariant -> other.filterItems.contains(itemVariant.getItem());
        } else {
            return itemVariant -> true;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.spectrum.pastel_node");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new FilteringScreenHandler(syncId, inv, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        FilterConfigurable.writeScreenOpeningData(buf, filterItems);
    }

    public boolean equals(Object obj) {
        return obj instanceof PastelNodeBlockEntity blockEntity && this.worldPosition.equals(blockEntity.worldPosition);
    }
    
    public int hashCode() {
        return this.worldPosition.hashCode();
    }
}
