package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class PreservationCheckCriterion extends SimpleCriterionTrigger<PreservationCheckCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("preservation_check");
	
	public static PreservationCheckCriterion.Conditions create(String checkName, boolean checkPassed) {
		return new PreservationCheckCriterion.Conditions(ContextAwarePredicate.ANY, checkName, checkPassed);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public PreservationCheckCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		String checkName = GsonHelper.getAsString(jsonObject, "check_name", "");
		boolean checkPassed = GsonHelper.getAsBoolean(jsonObject, "check_passed", true);
		return new PreservationCheckCriterion.Conditions(extended, checkName, checkPassed);
	}
	
	public void trigger(ServerPlayer player, String checkName, boolean checkPassed) {
		this.trigger(player, (conditions) -> conditions.matches(checkName, checkPassed));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final String checkName;
		private final boolean checkPassed;
		
		public Conditions(ContextAwarePredicate player, String checkName, boolean checkPassed) {
			super(ID, player);
			this.checkName = checkName;
			this.checkPassed = checkPassed;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("check_name", this.checkName);
			jsonObject.addProperty("check_passed", this.checkPassed);
			return jsonObject;
		}
		
		public boolean matches(String name, boolean checkPassed) {
			return this.checkPassed == checkPassed && (this.checkName.isEmpty() || this.checkName.equals(name));
		}
	}
	
}
