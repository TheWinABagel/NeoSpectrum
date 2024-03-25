package de.dafuqs.spectrum.blocks.pastel_network.network;

import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import de.dafuqs.spectrum.helpers.InWorldInteractionHelper;
import de.dafuqs.spectrum.helpers.SchedulerMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PastelTransmission implements SchedulerMap.Callback {

    private @Nullable PastelNetwork network;
    private final List<BlockPos> nodePositions;
    private final ItemVariant variant;
    private final long amount;
    
    public PastelTransmission(List<BlockPos> nodePositions, ItemVariant variant, long amount) {
        this.nodePositions = nodePositions;
        this.variant = variant;
        this.amount = amount;
    }

    public void setNetwork(@NotNull PastelNetwork network) {
        this.network = network;
    }

    public @Nullable PastelNetwork getNetwork() {
        return this.network;
    }

    public List<BlockPos> getNodePositions() {
        return nodePositions;
    }

    public ItemVariant getVariant() {
        return this.variant;
    }

    public long getAmount() {
        return this.amount;
    }

    public BlockPos getStartPos() {
        return this.nodePositions.get(0);
    }

    @Override
    public void trigger() {
        arriveAtDestination();
    }

    private void arriveAtDestination() {
        if (nodePositions.size() == 0) {
            return;
        }

        BlockPos destinationPos = nodePositions.get(nodePositions.size() - 1);
        PastelNodeBlockEntity destinationNode = this.network.getNodeAt(destinationPos);
        Level world = this.network.getWorld();
        if (!world.isClientSide) {
            int inserted = 0;
            if (destinationNode != null) {
                Storage<ItemVariant> destinationStorage = destinationNode.getConnectedStorage();
                if (destinationStorage != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (destinationStorage.supportsInsertion()) {
                            inserted = (int) destinationStorage.insert(variant, amount, transaction);
                            destinationNode.addItemCountUnderway(-inserted);
                            transaction.commit();
                        }
                    }
                }
            }
            if (inserted != amount) {
                InWorldInteractionHelper.scatter(world, destinationPos.getX() + 0.5, destinationPos.getY() + 0.5, destinationPos.getZ() + 0.5, variant, amount - inserted);
            }
        }
    }

    public CompoundTag toNbt() {
        CompoundTag compound = new CompoundTag();
        compound.put("Variant", this.variant.toNbt());
        compound.putLong("Amount", this.amount);
        ListTag posList = new ListTag();
        for (BlockPos pos : nodePositions) {
            CompoundTag posCompound = new CompoundTag();
            posCompound.putInt("X", pos.getX());
            posCompound.putInt("Y", pos.getY());
            posCompound.putInt("Z", pos.getZ());
            posList.add(posCompound);
        }
        compound.put("NodePositions", posList);
        return compound;
    }

    public static PastelTransmission fromNbt(CompoundTag nbt) {
        ItemVariant variant = ItemVariant.fromNbt(nbt.getCompound("Variant"));
        long amount = nbt.getLong("Amount");

        List<BlockPos> posList = new ArrayList<>();
        for (Tag e : nbt.getList("NodePositions", Tag.TAG_COMPOUND)) {
            CompoundTag compound = (CompoundTag) e;
            BlockPos blockPos = new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
            posList.add(blockPos);
        }

        return new PastelTransmission(posList, variant, amount);
    }

    public static void writeToBuf(FriendlyByteBuf buf, PastelTransmission transfer) {
        buf.writeInt(transfer.nodePositions.size());
        for (BlockPos pos : transfer.nodePositions) {
            buf.writeBlockPos(pos);
        }
        transfer.variant.toPacket(buf);
        buf.writeLong(transfer.amount);
    }

    public static PastelTransmission fromPacket(FriendlyByteBuf buf) {
        int posCount = buf.readInt();
        List<BlockPos> posList = new ArrayList<>();
        for (int i = 0; i < posCount; i++) {
            posList.add(buf.readBlockPos());
        }
        ItemVariant variant = ItemVariant.fromPacket(buf);
        long amount = buf.readLong();
        return new PastelTransmission(posList, variant, amount);
    }

}
