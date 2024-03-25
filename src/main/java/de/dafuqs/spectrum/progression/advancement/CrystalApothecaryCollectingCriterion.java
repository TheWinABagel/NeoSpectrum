package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrystalApothecaryCollectingCriterion extends SimpleCriterionTrigger<CrystalApothecaryCollectingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("collect_using_crystal_apothecary");
	
	public static CrystalApothecaryCollectingCriterion.Conditions create(ItemPredicate item) {
		return new CrystalApothecaryCollectingCriterion.Conditions(ContextAwarePredicate.ANY, item);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public CrystalApothecaryCollectingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new CrystalApothecaryCollectingCriterion.Conditions(extended, itemPredicate);
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate itemPredicate;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate itemPredicate) {
			super(ID, player);
			this.itemPredicate = itemPredicate;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("item", this.itemPredicate.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack stack) {
			return this.itemPredicate.matches(stack);
		}
	}
	
}
