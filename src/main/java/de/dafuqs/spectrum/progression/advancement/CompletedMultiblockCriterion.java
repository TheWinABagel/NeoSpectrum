package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import vazkii.patchouli.api.IMultiblock;

public class CompletedMultiblockCriterion extends SimpleCriterionTrigger<CompletedMultiblockCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("completed_multiblock");
	
	public static CompletedMultiblockCriterion.Conditions create(ResourceLocation id) {
		return new CompletedMultiblockCriterion.Conditions(ContextAwarePredicate.ANY, id);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public CompletedMultiblockCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(jsonObject, "multiblock_identifier"));
		return new CompletedMultiblockCriterion.Conditions(extended, identifier);
	}
	
	public void trigger(ServerPlayer player, IMultiblock iMultiblock) {
		this.trigger(player, (conditions) -> conditions.matches(iMultiblock));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ResourceLocation identifier;
		
		public Conditions(ContextAwarePredicate player, ResourceLocation identifier) {
			super(ID, player);
			this.identifier = identifier;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("multiblock_identifier", this.identifier.toString());
			return jsonObject;
		}
		
		public boolean matches(IMultiblock iMultiblock) {
			return iMultiblock.getID().equals(identifier);
		}
	}
	
}
