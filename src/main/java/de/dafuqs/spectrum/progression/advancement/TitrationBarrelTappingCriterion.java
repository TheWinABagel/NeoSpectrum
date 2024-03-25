package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TitrationBarrelTappingCriterion extends SimpleCriterionTrigger<TitrationBarrelTappingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("titration_barrel_tapping");
	
	public static TitrationBarrelTappingCriterion.Conditions create(ItemPredicate[] item, MinMaxBounds.Ints ingameDaysAgeRange, MinMaxBounds.Ints ingredientCountRange) {
		return new TitrationBarrelTappingCriterion.Conditions(ContextAwarePredicate.ANY, item, ingameDaysAgeRange, ingredientCountRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public TitrationBarrelTappingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate[] tappedItemPredicates = ItemPredicate.fromJsonArray(jsonObject.get("items"));
		MinMaxBounds.Ints ingameDaysAgeRange = MinMaxBounds.Ints.fromJson(jsonObject.get("age_ingame_days"));
		MinMaxBounds.Ints ingredientCountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("ingredient_count"));
		return new TitrationBarrelTappingCriterion.Conditions(extended, tappedItemPredicates, ingameDaysAgeRange, ingredientCountRange);
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack, int ingameDaysAge, int ingredientCount) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack, ingameDaysAge, ingredientCount));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate[] tappedItemPredicates;
		private final MinMaxBounds.Ints ingameDaysAgeRange;
		private final MinMaxBounds.Ints ingredientCountRange;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate[] tappedItemPredicates, MinMaxBounds.Ints ingameDaysAgeRange, MinMaxBounds.Ints ingredientCountRange) {
			super(ID, player);
			this.tappedItemPredicates = tappedItemPredicates;
			this.ingameDaysAgeRange = ingameDaysAgeRange;
			this.ingredientCountRange = ingredientCountRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("items", Arrays.toString(this.tappedItemPredicates));
			jsonObject.add("age_ingame_days", this.ingameDaysAgeRange.serializeToJson());
			jsonObject.add("ingredient_count", this.ingredientCountRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack itemStack, int experience, int ingredientCount) {
			if (this.ingameDaysAgeRange.matches(experience) && this.ingredientCountRange.matches(ingredientCount)) {
				List<ItemPredicate> list = new ObjectArrayList<>(this.tappedItemPredicates);
				if (list.isEmpty()) {
					return true;
				} else {
					if (!itemStack.isEmpty()) {
						list.removeIf((itemPredicate) -> itemPredicate.matches(itemStack));
					}
					return list.isEmpty();
				}
			} else {
				return false;
			}
		}
	}
	
}
