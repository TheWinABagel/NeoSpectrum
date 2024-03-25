package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.pastel_network.network.ServerPastelNetwork;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PastelNetworkCreatingCriterion extends SimpleCriterionTrigger<PastelNetworkCreatingCriterion.Conditions> {

	static final ResourceLocation ID = SpectrumCommon.locate("pastel_network_creation");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public PastelNetworkCreatingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext advancementEntityPredicateDeserializer) {
		MinMaxBounds.Ints totalNodes = MinMaxBounds.Ints.fromJson(jsonObject.get("total_nodes"));
		MinMaxBounds.Ints connectionNodes = MinMaxBounds.Ints.fromJson(jsonObject.get("connection_nodes"));
		MinMaxBounds.Ints providerNodes = MinMaxBounds.Ints.fromJson(jsonObject.get("provider_nodes"));
		MinMaxBounds.Ints storageNodes = MinMaxBounds.Ints.fromJson(jsonObject.get("storage_nodes"));
		MinMaxBounds.Ints senderNodes = MinMaxBounds.Ints.fromJson(jsonObject.get("sender_nodes"));
		MinMaxBounds.Ints gatherNodes = MinMaxBounds.Ints.fromJson(jsonObject.get("gather_nodes"));
		
		return new PastelNetworkCreatingCriterion.Conditions(predicate, totalNodes, connectionNodes, providerNodes, storageNodes, senderNodes, gatherNodes);
	}

	public void trigger(ServerPlayer player, ServerPastelNetwork network) {
		this.trigger(player, (conditions) -> conditions.matches(network.getNodes(PastelNodeType.CONNECTION).size(), network.getNodes(PastelNodeType.PROVIDER).size(),
				network.getNodes(PastelNodeType.STORAGE).size(), network.getNodes(PastelNodeType.SENDER).size(), network.getNodes(PastelNodeType.GATHER).size()));
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {

		private final MinMaxBounds.Ints totalNodes;
		private final MinMaxBounds.Ints connectionNodes;
		private final MinMaxBounds.Ints providerNodes;
		private final MinMaxBounds.Ints storageNodes;
		private final MinMaxBounds.Ints senderNodes;
		private final MinMaxBounds.Ints gatherNodes;

		public Conditions(ContextAwarePredicate playerPredicate, MinMaxBounds.Ints totalNodes, MinMaxBounds.Ints connectionNodes, MinMaxBounds.Ints providerNodes, MinMaxBounds.Ints storageNodes, MinMaxBounds.Ints senderNodes, MinMaxBounds.Ints gatherNodes) {
			super(PastelNetworkCreatingCriterion.ID, playerPredicate);
			this.totalNodes = totalNodes;
			this.connectionNodes = connectionNodes;
			this.providerNodes = providerNodes;
			this.storageNodes = storageNodes;
			this.senderNodes = senderNodes;
			this.gatherNodes = gatherNodes;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("total_nodes", this.totalNodes.serializeToJson());
			jsonObject.add("connection_nodes", this.connectionNodes.serializeToJson());
			jsonObject.add("provider_nodes", this.providerNodes.serializeToJson());
			jsonObject.add("storage_nodes", this.storageNodes.serializeToJson());
			jsonObject.add("sender_nodes", this.senderNodes.serializeToJson());
			jsonObject.add("gather_nodes", this.gatherNodes.serializeToJson());
			return jsonObject;
		}

		public boolean matches(int connectionNodes, int providerNodes, int storageNodes, int senderNodes, int gatherNodes) {
			return this.totalNodes.matches(connectionNodes + providerNodes + storageNodes + senderNodes + gatherNodes) && this.connectionNodes.matches(connectionNodes) && this.providerNodes.matches(providerNodes) && this.storageNodes.matches(storageNodes) && this.senderNodes.matches(senderNodes) && this.gatherNodes.matches(gatherNodes);
		}

	}

}
