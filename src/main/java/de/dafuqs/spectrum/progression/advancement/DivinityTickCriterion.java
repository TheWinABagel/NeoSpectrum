package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DivinityTickCriterion extends SimpleCriterionTrigger<DivinityTickCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("divinity_tick");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public DivinityTickCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext advancementEntityPredicateDeserializer) {
		MinMaxBounds.Doubles healthRange = MinMaxBounds.Doubles.fromJson(jsonObject.get("health"));

		Boolean isAlive = null;
		JsonElement isAliveElement = jsonObject.get("is_alive");
		if (isAliveElement != null && isAliveElement.isJsonPrimitive() && isAliveElement.getAsJsonPrimitive().isBoolean()) {
			isAlive = isAliveElement.getAsBoolean();
		}
		
		return new DivinityTickCriterion.Conditions(predicate, healthRange, isAlive);
	}
	
	public void trigger(ServerPlayer player) {
		this.trigger(player, (conditions) -> conditions.matches(player.isAlive(), player.getHealth()));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {

		private final Boolean isAlive;
		private final MinMaxBounds.Doubles healthRange;
		
		public Conditions(ContextAwarePredicate player, MinMaxBounds.Doubles healthRange, Boolean isAlive) {
			super(DivinityTickCriterion.ID, player);
			this.isAlive = isAlive;
			this.healthRange = healthRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			if (this.isAlive != null) {
				jsonObject.addProperty("is_alive", this.isAlive);
			}
			jsonObject.add("health", this.healthRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(boolean isPlayerAlive, float health) {
			return (this.isAlive == null || this.isAlive == isPlayerAlive)
					&& this.healthRange.matches(health);
		}
	}
	
}
