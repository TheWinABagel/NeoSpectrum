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

public class FusionShrineCraftingCriterion extends SimpleCriterionTrigger<FusionShrineCraftingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("crafted_with_fusion_shrine");
	
	public static FusionShrineCraftingCriterion.Conditions create(ItemPredicate[] item, MinMaxBounds.Ints experienceRange) {
		return new FusionShrineCraftingCriterion.Conditions(ContextAwarePredicate.ANY, item, experienceRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public FusionShrineCraftingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate[] itemPredicates = ItemPredicate.fromJsonArray(jsonObject.get("items"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("gained_experience"));
		return new FusionShrineCraftingCriterion.Conditions(extended, itemPredicates, experienceRange);
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack, int experience) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack, experience));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate[] itemPredicates;
		private final MinMaxBounds.Ints experienceRange;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate[] itemPredicates, MinMaxBounds.Ints experienceRange) {
			super(ID, player);
			this.itemPredicates = itemPredicates;
			this.experienceRange = experienceRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("items", Arrays.toString(this.itemPredicates));
			jsonObject.add("gained_experience", this.experienceRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack itemStack, int experience) {
			if (this.experienceRange.matches(experience)) {
				List<ItemPredicate> list = new ObjectArrayList<>(this.itemPredicates);
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
