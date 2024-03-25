package de.dafuqs.spectrum.blocks.pastel_network.network;

import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// Persisted together with the overworld
// resetting the overworld will also reset all networks
public class ServerPastelNetworkManager extends SavedData implements PastelNetworkManager {
	
	private static final String PERSISTENT_STATE_ID = "spectrum_pastel_network_manager";
	
	private final List<ServerPastelNetwork> networks = new ArrayList<>();
	
	public ServerPastelNetworkManager() {
		super();
	}
	
	@Override
	public boolean isDirty() {
		return true;
	}
	
	public static ServerPastelNetworkManager get(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(ServerPastelNetworkManager::fromNbt, ServerPastelNetworkManager::new, PERSISTENT_STATE_ID);
	}
	
	@Override
	public CompoundTag save(CompoundTag nbt) {
		ListTag networkList = new ListTag();
		for (ServerPastelNetwork network : this.networks) {
			CompoundTag compound = network.toNbt();
			networkList.add(compound);
		}
		nbt.put("Networks", networkList);
		return nbt;
	}
	
	public static ServerPastelNetworkManager fromNbt(CompoundTag nbt) {
		ServerPastelNetworkManager manager = new ServerPastelNetworkManager();
		for (Tag element : nbt.getList("Networks", Tag.TAG_COMPOUND)) {
			manager.networks.add(ServerPastelNetwork.fromNbt((CompoundTag) element));
		}
		return manager;
	}
	
	private ServerPastelNetwork createNetwork(Level world, @Nullable UUID uuid) {
		ServerPastelNetwork network = new ServerPastelNetwork(world, uuid);
		this.networks.add(network);
		return network;
	}
	
	public void tick() {
		// using a for here instead of foreach
		// to prevent ConcurrentModificationExceptions
		for (int i = 0; i < this.networks.size(); i++) {
			this.networks.get(i).tick();
		}
	}
	
	@Override
	public PastelNetwork joinNetwork(PastelNodeBlockEntity node, @Nullable UUID uuid) {
		if (uuid == null) {
			for (ServerPastelNetwork network : this.networks) {
				if (network.canConnect(node)) {
					network.addNode(node);
					checkNetworkMergesForNewNode(network, node);
					return network;
				}
			}
		} else {
			//noinspection ForLoopReplaceableByForEach
			for (int i = 0; i < this.networks.size(); i++) {
				PastelNetwork network = this.networks.get(i);
				if (network.getUUID().equals(uuid)) {
					network.addNode(node);
					return network;
				}
			}
		}
		
		ServerPastelNetwork network = createNetwork(node.getLevel(), uuid);
		network.addNode(node);
		return network;
	}
	
	@Override
	public void removeNode(PastelNodeBlockEntity node, NodeRemovalReason reason) {
		ServerPastelNetwork network = (ServerPastelNetwork) node.getNetwork();
		if (network != null) {
			network.removeNode(node, reason);
			
			if (network.hasNodes()) {
				// check if the removed node split the network into subnetworks
				checkForNetworkSplit(network);
			} else if (reason == NodeRemovalReason.BROKEN || reason == NodeRemovalReason.MOVED) {
				this.networks.remove(network);
			}
		}
	}
	
	private void checkForNetworkSplit(ServerPastelNetwork network) {
		ConnectivityInspector<PastelNodeBlockEntity, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(network.getGraph());
		List<Set<PastelNodeBlockEntity>> connectedSets = connectivityInspector.connectedSets();
		if (connectedSets.size() != 1) {
			for (int i = 1; i < connectedSets.size(); i++) {
				Set<PastelNodeBlockEntity> disconnectedNodes = connectedSets.get(i);
				PastelNetwork newNetwork = createNetwork(network.world, null);
				for (PastelNodeBlockEntity disconnectedNode : disconnectedNodes) {
					network.nodes.get(disconnectedNode.getNodeType()).remove(disconnectedNode);
					network.getGraph().removeVertex(disconnectedNode);
					newNetwork.addNode(disconnectedNode);
					disconnectedNode.setNetwork(newNetwork);
				}
			}
		}
	}
	
	private void checkNetworkMergesForNewNode(ServerPastelNetwork network, PastelNodeBlockEntity newNode) {
		int biggestNetworkNodeCount = network.getNodeCount();
		
		ServerPastelNetwork biggestNetwork = network;
		List<ServerPastelNetwork> smallerNetworks = new ArrayList<>();
		
		for (ServerPastelNetwork currentNetwork : this.networks) {
			if (currentNetwork == network) {
				continue;
			}
			if (currentNetwork.canConnect(newNode)) {
				if (currentNetwork.getNodeCount() > biggestNetworkNodeCount) {
					smallerNetworks.add(biggestNetwork);
					biggestNetwork = currentNetwork;
				} else {
					smallerNetworks.add(currentNetwork);
				}
				break;
			}
		}
		
		if (smallerNetworks.size() == 0) {
			return;
		}
		
		for (ServerPastelNetwork smallerNetwork : smallerNetworks) {
			biggestNetwork.incorporate(smallerNetwork);
			this.networks.remove(smallerNetwork);
		}
	}
	
}
