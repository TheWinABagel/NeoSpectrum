package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PedestalRecipeCalculatedCriterion extends SimpleCriterionTrigger<PedestalCraftingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("pedestal_recipe_calculated");
	
	public static PedestalCraftingCriterion.Conditions create(ItemPredicate[] item, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints durationTicks) {
		return new PedestalCraftingCriterion.Conditions(ID, ContextAwarePredicate.ANY, item, experienceRange, durationTicks);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public PedestalCraftingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate[] itemPredicates = ItemPredicate.fromJsonArray(jsonObject.get("items"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("gained_experience"));
		MinMaxBounds.Ints craftingDurationTicksRange = MinMaxBounds.Ints.fromJson(jsonObject.get("crafting_duration_ticks"));
		return new PedestalCraftingCriterion.Conditions(ID, extended, itemPredicates, experienceRange, craftingDurationTicksRange);
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack, int experience, int durationTicks) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack, experience, durationTicks));
	}
	
}
