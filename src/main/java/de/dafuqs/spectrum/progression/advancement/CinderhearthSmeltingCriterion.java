package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CinderhearthSmeltingCriterion extends SimpleCriterionTrigger<CinderhearthSmeltingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("cinderhearth_smelting");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public CinderhearthSmeltingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate input = ItemPredicate.fromJson(jsonObject.get("input"));
		ItemPredicate output = ItemPredicate.fromJson(jsonObject.get("output"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("gained_experience"));
		MinMaxBounds.Ints speedMultiplierRange = MinMaxBounds.Ints.fromJson(jsonObject.get("speed_multiplier"));
		MinMaxBounds.Ints yieldMultiplierRange = MinMaxBounds.Ints.fromJson(jsonObject.get("yield_multiplier"));
		MinMaxBounds.Ints efficiencyMultiplierRange = MinMaxBounds.Ints.fromJson(jsonObject.get("efficiency_multiplier"));
		MinMaxBounds.Ints experienceMultiplierRange = MinMaxBounds.Ints.fromJson(jsonObject.get("experience_multiplier"));
		
		return new CinderhearthSmeltingCriterion.Conditions(extended, input, output, experienceRange, speedMultiplierRange, yieldMultiplierRange, efficiencyMultiplierRange, experienceMultiplierRange);
	}

	public void trigger(ServerPlayer player, ItemStack input, List<ItemStack> outputs, int experience, Upgradeable.UpgradeHolder upgrades) {
		this.trigger(player, (conditions) -> conditions.matches(input, outputs, experience, upgrades));
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {

		private final ItemPredicate input;
		private final ItemPredicate output;
		private final MinMaxBounds.Ints experienceRange;
		private final MinMaxBounds.Ints speedMultiplierRange;
		private final MinMaxBounds.Ints yieldMultiplierRange;
		private final MinMaxBounds.Ints efficiencyMultiplierRange;
		private final MinMaxBounds.Ints experienceMultiplierRange;

		public Conditions(ContextAwarePredicate player, ItemPredicate input, ItemPredicate output, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints speedMultiplierRange, MinMaxBounds.Ints yieldMultiplierRange, MinMaxBounds.Ints efficiencyMultiplierRange, MinMaxBounds.Ints experienceMultiplierRange) {
			super(ID, player);
			this.input = input;
			this.output = output;
			this.experienceRange = experienceRange;
			this.speedMultiplierRange = speedMultiplierRange;
			this.yieldMultiplierRange = yieldMultiplierRange;
			this.efficiencyMultiplierRange = efficiencyMultiplierRange;
			this.experienceMultiplierRange = experienceMultiplierRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("input", this.input.serializeToJson());
			jsonObject.add("output", this.output.serializeToJson());
			jsonObject.add("gained_experience", this.experienceRange.serializeToJson());
			jsonObject.add("speed_multiplier", this.speedMultiplierRange.serializeToJson());
			jsonObject.add("yield_multiplier", this.yieldMultiplierRange.serializeToJson());
			jsonObject.add("efficiency_multiplier", this.efficiencyMultiplierRange.serializeToJson());
			jsonObject.add("experience_multiplier", this.experienceMultiplierRange.serializeToJson());
			return jsonObject;
		}

		public boolean matches(ItemStack input, List<ItemStack> outputs, int experience, Upgradeable.UpgradeHolder upgrades) {
			if (!this.input.matches(input)) {
				return false;
			}
			if (!this.experienceRange.matches(experience)) {
				return false;
			}
			if (!this.speedMultiplierRange.matches(upgrades.getRawValue(Upgradeable.UpgradeType.SPEED))) {
				return false;
			}
			if (!this.yieldMultiplierRange.matches(upgrades.getRawValue(Upgradeable.UpgradeType.YIELD))) {
				return false;
			}
			if (!this.efficiencyMultiplierRange.matches(upgrades.getRawValue(Upgradeable.UpgradeType.EFFICIENCY))) {
				return false;
			}
			if (!this.experienceMultiplierRange.matches(upgrades.getRawValue(Upgradeable.UpgradeType.EXPERIENCE))) {
				return false;
			}
			if (this.output == ItemPredicate.ANY) {
				return true; // empty output predicate
			}
			for (ItemStack output : outputs) {
				if (this.output.matches(output)) {
					return true;
				}
			}
			return false;
		}

	}
	
}
