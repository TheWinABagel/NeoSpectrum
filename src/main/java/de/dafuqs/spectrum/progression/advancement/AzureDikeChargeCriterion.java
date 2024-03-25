package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AzureDikeChargeCriterion extends SimpleCriterionTrigger<AzureDikeChargeCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("azure_dike_charge_change");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate lootContextPredicate, DeserializationContext predicateDeserializer) {
		MinMaxBounds.Ints chargesRange = MinMaxBounds.Ints.fromJson(jsonObject.get("charges"));
		MinMaxBounds.Ints rechargeRateRange = MinMaxBounds.Ints.fromJson(jsonObject.get("recharge_rate"));
		MinMaxBounds.Ints changeRange = MinMaxBounds.Ints.fromJson(jsonObject.get("change"));

		return new AzureDikeChargeCriterion.Conditions(lootContextPredicate, chargesRange, rechargeRateRange, changeRange);
	}

	public void trigger(ServerPlayer player, int charges, int rechargeRate, int change) {
		this.trigger(player, (conditions) -> conditions.matches(charges, rechargeRate, change));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final MinMaxBounds.Ints chargesRange;
		private final MinMaxBounds.Ints rechargeRateRange;
		private final MinMaxBounds.Ints changeRange;
		
		public Conditions(ContextAwarePredicate predicate, MinMaxBounds.Ints chargesRange, MinMaxBounds.Ints rechargeRateRange, MinMaxBounds.Ints changeRange) {
			super(AzureDikeChargeCriterion.ID, predicate);
			this.chargesRange = chargesRange;
			this.rechargeRateRange = rechargeRateRange;
			this.changeRange = changeRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("charges", this.chargesRange.serializeToJson());
			jsonObject.add("recharge_rate", this.rechargeRateRange.serializeToJson());
			jsonObject.add("change", this.changeRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(int charges, int rechargeRate, int change) {
			return this.chargesRange.matches(charges) && this.rechargeRateRange.matches(rechargeRate) && this.changeRange.matches(change);
		}
	}
	
}
