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

public class PotionWorkshopCraftingCriterion extends SimpleCriterionTrigger<PotionWorkshopCraftingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("crafted_with_potion_workshop");
	
	public static PotionWorkshopCraftingCriterion.Conditions create(ItemPredicate[] item) {
		return new PotionWorkshopCraftingCriterion.Conditions(ContextAwarePredicate.ANY, item);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public PotionWorkshopCraftingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate[] itemPredicates = ItemPredicate.fromJsonArray(jsonObject.get("items"));
		return new PotionWorkshopCraftingCriterion.Conditions(extended, itemPredicates);
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate[] itemPredicates;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate[] itemPredicates) {
			super(ID, player);
			this.itemPredicates = itemPredicates;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("items", Arrays.toString(this.itemPredicates));
			return jsonObject;
		}
		
		public boolean matches(ItemStack itemStack) {
			List<ItemPredicate> list = new ObjectArrayList<>(this.itemPredicates);
			if (list.isEmpty()) {
				return true;
			} else {
				if (!itemStack.isEmpty()) {
					list.removeIf((itemPredicate) -> itemPredicate.matches(itemStack));
				}
				return list.isEmpty();
			}
		}
	}
	
}
