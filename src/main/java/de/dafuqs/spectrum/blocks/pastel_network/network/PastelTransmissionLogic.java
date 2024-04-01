package de.dafuqs.spectrum.blocks.pastel_network.network;

import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeType;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class PastelTransmissionLogic {
	
	private enum TransferMode {
		PUSH,
		PULL,
		PUSH_PULL
	}
	
	public static final int MAX_TRANSFER_AMOUNT = 1;
	public static final int TRANSFER_TICKS_PER_NODE = 30;
	private final ServerPastelNetwork network;
	
	private DijkstraShortestPath<PastelNodeBlockEntity, DefaultEdge> dijkstra;
	private Map<PastelNodeBlockEntity, Map<PastelNodeBlockEntity, GraphPath<PastelNodeBlockEntity, DefaultEdge>>> pathCache = new HashMap<>();
	
	
	public PastelTransmissionLogic(ServerPastelNetwork network) {
		this.network = network;
	}
	
	public void invalidateCache() {
		this.dijkstra = null;
		this.pathCache = new HashMap<>();
	}
	
	public @Nullable GraphPath<PastelNodeBlockEntity, DefaultEdge> getPath(Graph<PastelNodeBlockEntity, DefaultEdge> graph, PastelNodeBlockEntity source, PastelNodeBlockEntity destination) {
		if (this.dijkstra == null) {
			this.dijkstra = new DijkstraShortestPath<>(graph);
		}
		
		// cache hit?
		Map<PastelNodeBlockEntity, GraphPath<PastelNodeBlockEntity, DefaultEdge>> e = this.pathCache.getOrDefault(source, null);
		if (e != null) {
			if (e.containsKey(destination)) {
				return e.get(destination);
			}
		}
		
		// calculate and cache
		ShortestPathAlgorithm.SingleSourcePaths<PastelNodeBlockEntity, DefaultEdge> paths = this.dijkstra.getPaths(source);
		GraphPath<PastelNodeBlockEntity, DefaultEdge> path = paths.getPath(destination);
		if (this.pathCache.containsKey(source)) {
			this.pathCache.get(source).put(destination, path);
		} else {
			Map<PastelNodeBlockEntity, GraphPath<PastelNodeBlockEntity, DefaultEdge>> newMap = new HashMap<>();
			newMap.put(destination, path);
			this.pathCache.put(source, newMap);
		}
		
		return path;
	}
	
	public void tick() {
		transferBetween(PastelNodeType.SENDER, PastelNodeType.GATHER, TransferMode.PUSH_PULL);
		transferBetween(PastelNodeType.PROVIDER, PastelNodeType.GATHER, TransferMode.PULL);
		transferBetween(PastelNodeType.STORAGE, PastelNodeType.GATHER, TransferMode.PULL);
		transferBetween(PastelNodeType.SENDER, PastelNodeType.STORAGE, TransferMode.PUSH);
	}
	
	private void transferBetween(PastelNodeType sourceType, PastelNodeType destinationType, TransferMode transferMode) {
		for (PastelNodeBlockEntity sourceNode : this.network.getNodes(sourceType)) {
			if (!sourceNode.canTransfer()) {
				continue;
			}
			
			Optional<IItemHandler> sourceStorage = sourceNode.getConnectedStorage();
			if (sourceStorage.isPresent()) {
				tryTransferToType(sourceNode, sourceStorage.get(), destinationType, transferMode);
			}
		}
	}
	
	private void tryTransferToType(PastelNodeBlockEntity sourceNode, IItemHandler sourceStorage, PastelNodeType type, TransferMode transferMode) {
		for (PastelNodeBlockEntity destinationNode : this.network.getNodes(type)) {
			if (!destinationNode.canTransfer()) {
				continue;
			}

			Optional<IItemHandler> destinationStorage = destinationNode.getConnectedStorage();
			if (destinationStorage.isPresent()) {
				boolean success = transferBetween(sourceNode, sourceStorage, destinationNode, destinationStorage.get(), transferMode);
				if (success && transferMode != TransferMode.PULL) {
					return;
				}
			}
		}
	}
	//todoforge probably broken
	private boolean transferBetween(PastelNodeBlockEntity sourceNode, IItemHandler sourceStorage, PastelNodeBlockEntity destinationNode, IItemHandler destinationStorage, TransferMode transferMode) {
		Predicate<ItemStack> filter = sourceNode.getTransferFilterTo(destinationNode);

			//for each slot
			for (int slot = 0; slot < sourceStorage.getSlots(); slot++) {

				// Current resource
				ItemStack storedResource = sourceStorage.getStackInSlot(slot);
				//if resource is empty or if it fails the filter, check next slot
				if (storedResource.isEmpty() || !filter.test(storedResource)) {
					continue;
				}

				int transferrableAmount = MAX_TRANSFER_AMOUNT;
				int itemCountUnderway = (int) destinationNode.getItemCountUnderway();
				//simulate insertion to see if insertion is possible
				transferrableAmount = destinationStorage.insertItem(slot, storedResource.copyWithCount(storedResource.getCount() + itemCountUnderway), true).getCount();
				transferrableAmount = transferrableAmount - itemCountUnderway; // prevention to not overfill the container (send more transfers when the existing ones would fill it already)

				if (transferrableAmount <= 0) {
					continue;
				}
				//extract from container
				transferrableAmount = sourceStorage.extractItem(slot, transferrableAmount, false).getCount();
				if (transferrableAmount <= 0) {
					continue;
				}

				Optional<PastelTransmission> optionalTransmission = createTransmissionOnValidPath(sourceNode, destinationNode, storedResource, transferrableAmount);
				if (optionalTransmission.isPresent()) {
					PastelTransmission transmission = optionalTransmission.get();
					int verticesCount = transmission.getNodePositions().size() - 1;
					int travelTime = TRANSFER_TICKS_PER_NODE * verticesCount;
					this.network.addTransmission(transmission, travelTime);
					SpectrumS2CPacketSender.sendPastelTransmissionParticle(this.network, travelTime, transmission);

					if (transferMode == TransferMode.PULL) {
						destinationNode.markTransferred();
					} else if (transferMode == TransferMode.PUSH) {
						sourceNode.markTransferred();
					} else {
						destinationNode.markTransferred();
						sourceNode.markTransferred();
					}

					destinationNode.addItemCountUnderway(transferrableAmount);
//					transaction.commit();
					return true;
				}
			}
//			transaction.abort();

		return false;
	}

//	private boolean transferBetween(PastelNodeBlockEntity sourceNode, IItemHandler sourceStorage, PastelNodeBlockEntity destinationNode, IItemHandler destinationStorage, TransferMode transferMode) {
//		Predicate<ItemStack> filter = sourceNode.getTransferFilterTo(destinationNode);
//
//		try (Transaction transaction = Transaction.openOuter()) {
//			for (StorageView<ItemVariant> view : sourceStorage) {
//				if (view.isResourceBlank()) {
//					continue;
//				}
//
//				ItemVariant storedResource = view.getResource(); // Current resource
//				if (storedResource.isBlank() || !filter.test(storedResource)) {
//					continue;
//				}
//
//				long storedAmount = view.getAmount();
//				if (storedAmount <= 0) {
//					continue;
//				}
//
//				long transferrableAmount = MAX_TRANSFER_AMOUNT;
//				long itemCountUnderway = destinationNode.getItemCountUnderway();
//				transferrableAmount = (int) StorageUtil.simulateInsert(destinationStorage, storedResource, transferrableAmount + itemCountUnderway, transaction);
//				transferrableAmount = transferrableAmount - itemCountUnderway; // prevention to not overfill the container (send more transfers when the existing ones would fill it already)
//
//				if (transferrableAmount <= 0) {
//					continue;
//				}
//
//				transferrableAmount = sourceStorage.extract(storedResource, transferrableAmount, transaction);
//				if (transferrableAmount <= 0) {
//					continue;
//				}
//
//				Optional<PastelTransmission> optionalTransmission = createTransmissionOnValidPath(sourceNode, destinationNode, storedResource, transferrableAmount);
//				if (optionalTransmission.isPresent()) {
//					PastelTransmission transmission = optionalTransmission.get();
//					int verticesCount = transmission.getNodePositions().size() - 1;
//					int travelTime = TRANSFER_TICKS_PER_NODE * verticesCount;
//					this.network.addTransmission(transmission, travelTime);
//					SpectrumS2CPacketSender.sendPastelTransmissionParticle(this.network, travelTime, transmission);
//
//					if (transferMode == TransferMode.PULL) {
//						destinationNode.markTransferred();
//					} else if (transferMode == TransferMode.PUSH) {
//						sourceNode.markTransferred();
//					} else {
//						destinationNode.markTransferred();
//						sourceNode.markTransferred();
//					}
//
//					destinationNode.addItemCountUnderway(transferrableAmount);
//					transaction.commit();
//					return true;
//				}
//			}
//			transaction.abort();
//		}
//		return false;
//	}
	
	public Optional<PastelTransmission> createTransmissionOnValidPath(PastelNodeBlockEntity source, PastelNodeBlockEntity destination, ItemStack variant, long amount) {
		GraphPath<PastelNodeBlockEntity, DefaultEdge> graphPath = getPath(this.network.getGraph(), source, destination);
		if (graphPath != null) {
			List<BlockPos> vertexPositions = new ArrayList<>();
			for (PastelNodeBlockEntity vertex : graphPath.getVertexList()) {
				vertexPositions.add(vertex.getBlockPos());
			}
			return Optional.of(new PastelTransmission(vertexPositions, variant, amount));
		}
		return Optional.empty();
	}
	
}
