package de.dafuqs.spectrum.blocks.pastel_network.network;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeType;
import de.dafuqs.spectrum.helpers.SchedulerMap;
import de.dafuqs.spectrum.helpers.TickLooper;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServerPastelNetwork extends PastelNetwork {

	// new transfers are checked for every 10 ticks
	private final TickLooper transferLooper = new TickLooper(10);

	protected final SchedulerMap<PastelTransmission> transmissions = new SchedulerMap<>();
	protected final PastelTransmissionLogic transmissionLogic;

	public ServerPastelNetwork(Level world, @Nullable UUID uuid) {
		super(world, uuid);
		this.transmissionLogic = new PastelTransmissionLogic(this);
	}

	public void incorporate(PastelNetwork networkToIncorporate) {
        for (Map.Entry<PastelNodeType, Set<PastelNodeBlockEntity>> nodesToIncorporate : networkToIncorporate.getNodes().entrySet()) {
            PastelNodeType type = nodesToIncorporate.getKey();
            for (PastelNodeBlockEntity nodeToIncorporate : nodesToIncorporate.getValue()) {
                this.nodes.get(type).add(nodeToIncorporate);
                nodeToIncorporate.setNetwork(this);
            }
        }
		this.graph = null;
		this.transmissionLogic.invalidateCache();
	}
	
	@Override
	public void addNode(PastelNodeBlockEntity node) {
		super.addNode(node);
		this.transmissionLogic.invalidateCache();
	}
	
	@Override
	public boolean removeNode(PastelNodeBlockEntity node, NodeRemovalReason reason) {
		boolean result = super.removeNode(node, reason);
		this.transmissionLogic.invalidateCache();
		return result;
	}
	
	@Override
	public void tick() {
		this.transmissions.tick();

		this.transferLooper.tick();
		if (this.transferLooper.reachedCap()) {
			this.transferLooper.reset();
			try {
				this.transmissionLogic.tick();
			} catch (Exception e) {
				// hmmmmmm. Block getting unloaded / new one placed while logic is running?
			}
		}
	}
	
	@Override
	public void addTransmission(PastelTransmission transmission, int travelTime) {
		transmission.setNetwork(this);
		this.transmissions.put(transmission, travelTime);
	}
	
	public CompoundTag toNbt() {
		CompoundTag compound = new CompoundTag();
		compound.putUUID("UUID", this.uuid);
		compound.putString("World", this.getWorld().dimension().location().toString());
		compound.put("Looper", this.transferLooper.toNbt());
		
		ListTag transmissionList = new ListTag();
        for (Map.Entry<PastelTransmission, Integer> transmission : this.transmissions) {
            CompoundTag transmissionCompound = new CompoundTag();
            transmissionCompound.putInt("Delay", transmission.getValue());
            transmissionCompound.put("Transmission", transmission.getKey().toNbt());
            transmissionList.add(transmissionCompound);
        }
        compound.put("Transmissions", transmissionList);

        return compound;
    }

    public static ServerPastelNetwork fromNbt(CompoundTag compound) {
		Level world = SpectrumCommon.minecraftServer.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(compound.getString("World"))));
		UUID uuid = compound.getUUID("UUID");

		ServerPastelNetwork network = new ServerPastelNetwork(world, uuid);
		if (compound.contains("Looper", Tag.TAG_COMPOUND)) {
			network.transferLooper.readNbt(compound.getCompound("Looper"));
		}

		for (Tag e : compound.getList("Transmissions", Tag.TAG_COMPOUND)) {
			CompoundTag t = (CompoundTag) e;
			int delay = t.getInt("Delay");
			PastelTransmission transmission = PastelTransmission.fromNbt(t.getCompound("Transmission"));
			network.addTransmission(transmission, delay);
		}
		return network;
	}

}
