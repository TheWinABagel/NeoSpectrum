package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class ConfirmationButtonPressedCriterion extends SimpleCriterionTrigger<ConfirmationButtonPressedCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("confirmation_button_pressed");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public ConfirmationButtonPressedCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext advancementEntityPredicateDeserializer) {
		String confirmation = GsonHelper.getAsString(jsonObject, "confirmation");
		
		return new ConfirmationButtonPressedCriterion.Conditions(predicate, confirmation);
	}
	
	public void trigger(ServerPlayer player, String confirmation) {
		this.trigger(player, (conditions) -> conditions.matches(confirmation));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final String confirmation;
		
		public Conditions(ContextAwarePredicate player, String confirmation) {
			super(ConfirmationButtonPressedCriterion.ID, player);
			this.confirmation = confirmation;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("confirmation", new JsonPrimitive(this.confirmation));
			return jsonObject;
		}
		
		public boolean matches(String confirmation) {
			return this.confirmation.equals(confirmation);
		}
	}
	
}
