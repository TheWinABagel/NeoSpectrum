package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchanterEnchantingCriterion extends SimpleCriterionTrigger<EnchanterEnchantingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("enchanter_enchanting");
	
	public static EnchanterEnchantingCriterion.Conditions create(ItemPredicate itemPredicate, MinMaxBounds.Ints experienceRange) {
		return new EnchanterEnchantingCriterion.Conditions(ContextAwarePredicate.ANY, itemPredicate, experienceRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public EnchanterEnchantingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("spent_experience"));
		return new EnchanterEnchantingCriterion.Conditions(extended, itemPredicate, experienceRange);
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack, int spentExperience) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack, spentExperience));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate itemPredicate;
		private final MinMaxBounds.Ints experienceRange;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate itemPredicate, MinMaxBounds.Ints experienceRange) {
			super(ID, player);
			this.itemPredicate = itemPredicate;
			this.experienceRange = experienceRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("item", this.itemPredicate.serializeToJson());
			jsonObject.add("spent_experience", this.experienceRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack stack, int spentExperience) {
			if (!this.itemPredicate.matches(stack)) {
				return false;
			} else {
				return this.experienceRange.matches(spentExperience);
			}
		}
	}
	
}
