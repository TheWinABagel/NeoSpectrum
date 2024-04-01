package de.dafuqs.spectrum.blocks.pastel_network.network;

import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import de.dafuqs.spectrum.helpers.InWorldInteractionHelper;
import de.dafuqs.spectrum.helpers.SchedulerMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PastelTransmission implements SchedulerMap.Callback {

    private @Nullable PastelNetwork network;
    private final List<BlockPos> nodePositions;
    private final ItemStack variant;
    private final long amount;
    
    public PastelTransmission(List<BlockPos> nodePositions, ItemStack variant, long amount) {
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

    public ItemStack getVariant() {
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
    //todoforge item handler hell
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
                Optional<IItemHandler> destinationStorage = destinationNode.getConnectedStorage();
                destinationStorage.ifPresent(iItemHandler -> {
                    //is this how this works?
                    ItemHandlerHelper.insertItem(iItemHandler, variant, false);
                });
            }
            if (inserted != amount) {
                InWorldInteractionHelper.scatter(world, destinationPos.getX() + 0.5, destinationPos.getY() + 0.5, destinationPos.getZ() + 0.5, variant, amount - inserted);
            }
        }
    }

    public CompoundTag toNbt() {
        CompoundTag compound = new CompoundTag();
        compound.put("Variant", this.variant.serializeNBT());
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
        ItemStack variant = ItemStack.of(nbt.getCompound("Variant"));
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
        buf.writeItemStack(transfer.variant, false);
//        transfer.variant.toPacket(buf);
        buf.writeLong(transfer.amount);
    }

    public static PastelTransmission fromPacket(FriendlyByteBuf buf) {
        int posCount = buf.readInt();
        List<BlockPos> posList = new ArrayList<>();
        for (int i = 0; i < posCount; i++) {
            posList.add(buf.readBlockPos());
        }
        ItemStack variant = buf.readItem();
        long amount = buf.readLong();
        return new PastelTransmission(posList, variant, amount);
    }

}
