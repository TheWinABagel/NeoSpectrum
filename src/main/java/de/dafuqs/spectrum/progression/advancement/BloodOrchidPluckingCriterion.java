package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BloodOrchidPluckingCriterion extends SimpleCriterionTrigger<BloodOrchidPluckingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("blood_orchid_plucking");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected Conditions createInstance(JsonObject obj, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
		return new BloodOrchidPluckingCriterion.Conditions(playerPredicate);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, Conditions::matches);
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		public Conditions(ContextAwarePredicate predicate) {
			super(BloodOrchidPluckingCriterion.ID, predicate);
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			return super.serializeToJson(predicateSerializer);
		}
		
		public boolean matches() {
			return true;
		}
	}
	
}
