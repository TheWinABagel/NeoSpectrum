package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SlimeSizingCriterion extends SimpleCriterionTrigger<SlimeSizingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("slime_sizing");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public SlimeSizingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext advancementEntityPredicateDeserializer) {
		MinMaxBounds.Ints sizeRange = MinMaxBounds.Ints.fromJson(jsonObject.get("size"));
		
		return new SlimeSizingCriterion.Conditions(predicate, sizeRange);
	}
	
	public void trigger(ServerPlayer player, int size) {
		this.trigger(player, (conditions) -> conditions.matches(size));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final MinMaxBounds.Ints sizeRange;
		
		public Conditions(ContextAwarePredicate player, MinMaxBounds.Ints sizeRange) {
			super(SlimeSizingCriterion.ID, player);
			this.sizeRange = sizeRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("size", this.sizeRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(int size) {
			return this.sizeRange.matches(size);
		}
	}
	
}
