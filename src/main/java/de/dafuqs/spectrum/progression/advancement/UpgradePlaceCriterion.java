package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class UpgradePlaceCriterion extends SimpleCriterionTrigger<UpgradePlaceCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("upgrade_place");

	public static UpgradePlaceCriterion.Conditions create(BlockPredicate blockPredicate, MinMaxBounds.Ints countRange, MinMaxBounds.Ints speedRange, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints efficiencyRange, MinMaxBounds.Ints yieldRange) {
		return new UpgradePlaceCriterion.Conditions(ContextAwarePredicate.ANY, blockPredicate, countRange, speedRange, experienceRange, efficiencyRange, yieldRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public UpgradePlaceCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		BlockPredicate blockPredicate = BlockPredicate.fromJson(jsonObject.get("block"));
		MinMaxBounds.Ints countRange = MinMaxBounds.Ints.fromJson(jsonObject.get("count"));
		MinMaxBounds.Ints speedRange = MinMaxBounds.Ints.fromJson(jsonObject.get("speed_mod"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("experience_mod"));
		MinMaxBounds.Ints efficiencyRange = MinMaxBounds.Ints.fromJson(jsonObject.get("efficiency_mod"));
		MinMaxBounds.Ints yieldRange = MinMaxBounds.Ints.fromJson(jsonObject.get("yield_mod"));
		return new UpgradePlaceCriterion.Conditions(extended, blockPredicate, countRange, speedRange, experienceRange, efficiencyRange, yieldRange);
	}

	public void trigger(ServerPlayer player, ServerLevel world, BlockPos pos, int upgradeCount, Map<Upgradeable.UpgradeType, Integer> upgradeModifiers) {
		this.trigger(player, (conditions) -> conditions.matches(world, pos, upgradeCount, upgradeModifiers));
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final BlockPredicate blockPredicate;
		private final MinMaxBounds.Ints countRange;
		private final MinMaxBounds.Ints speedRange;
		private final MinMaxBounds.Ints experienceRange;
		private final MinMaxBounds.Ints efficiencyRange;
		private final MinMaxBounds.Ints yieldRange;

		public Conditions(ContextAwarePredicate player, BlockPredicate blockPredicate, MinMaxBounds.Ints countRange, MinMaxBounds.Ints speedRange, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints efficiencyRange, MinMaxBounds.Ints yieldRange) {
			super(ID, player);
			this.blockPredicate = blockPredicate;
			this.countRange = countRange;
			this.speedRange = speedRange;
			this.experienceRange = experienceRange;
			this.efficiencyRange = efficiencyRange;
			this.yieldRange = yieldRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("block", this.blockPredicate.serializeToJson());
			jsonObject.add("count", this.countRange.serializeToJson());
			jsonObject.add("speed_mod", this.speedRange.serializeToJson());
			jsonObject.add("experience_mod", this.experienceRange.serializeToJson());
			jsonObject.add("efficiency_mod", this.efficiencyRange.serializeToJson());
			jsonObject.add("yield_mod", this.yieldRange.serializeToJson());
			return jsonObject;
		}

		public boolean matches(ServerLevel world, BlockPos pos, int upgradeCount, Map<Upgradeable.UpgradeType, Integer> upgradeModifiers) {
			return this.blockPredicate.matches(world, pos)
					&& this.countRange.matches(upgradeCount)
					&& this.speedRange.matches(upgradeModifiers.get(Upgradeable.UpgradeType.SPEED))
					&& this.experienceRange.matches(upgradeModifiers.get(Upgradeable.UpgradeType.EXPERIENCE))
					&& this.efficiencyRange.matches(upgradeModifiers.get(Upgradeable.UpgradeType.EFFICIENCY))
					&& this.yieldRange.matches(upgradeModifiers.get(Upgradeable.UpgradeType.YIELD));
		}
	}
	
}