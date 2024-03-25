package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class HummingstoneHymnCriterion extends SimpleCriterionTrigger<HummingstoneHymnCriterion.Conditions> {

	static final ResourceLocation ID = SpectrumCommon.locate("hummingstone_hymn");

	public static HummingstoneHymnCriterion.Conditions create(LocationPredicate location) {
		return new HummingstoneHymnCriterion.Conditions(ContextAwarePredicate.ANY, location);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public HummingstoneHymnCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		return new HummingstoneHymnCriterion.Conditions(extended, locationPredicate);
	}

	public void trigger(ServerPlayer player, ServerLevel world, BlockPos pos) {
		this.trigger(player, (conditions) -> conditions.matches(world, pos));
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final LocationPredicate location;

		public Conditions(ContextAwarePredicate player, LocationPredicate location) {
			super(ID, player);
			this.location = location;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("location", this.location.serializeToJson());
			return jsonObject;
		}

		public boolean matches(ServerLevel world, BlockPos pos) {
			return this.location.matches(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5);
		}
	}

}
