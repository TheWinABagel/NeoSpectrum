package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ConsumedTeaWithSconeCriterion extends SimpleCriterionTrigger<ConsumedTeaWithSconeCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("consumed_tea_with_scone");
	
	public static Conditions create(ItemPredicate teaItemPredicate, ItemPredicate sconeItemPredicate) {
		return new Conditions(ContextAwarePredicate.ANY, teaItemPredicate, sconeItemPredicate);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext deserializer) {
		ItemPredicate teaItemPredicate = ItemPredicate.fromJson(jsonObject.get("tea_items"));
		ItemPredicate sconeItemPredicate = ItemPredicate.fromJson(jsonObject.get("scone_items"));
		return new Conditions(extended, teaItemPredicate, sconeItemPredicate);
	}
	
	public void trigger(ServerPlayer player, ItemStack teaStack, ItemStack sconeStack) {
		this.trigger(player, (conditions) -> conditions.matches(teaStack, sconeStack));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate teaItemPredicate;
		private final ItemPredicate sconeItemPredicate;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate teaItemPredicate, ItemPredicate sconeItemPredicate) {
			super(ID, player);
			this.teaItemPredicate = teaItemPredicate;
			this.sconeItemPredicate = sconeItemPredicate;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("tea_items", this.teaItemPredicate.toString());
			jsonObject.addProperty("scone_items", this.sconeItemPredicate.toString());
			return jsonObject;
		}
		
		public boolean matches(ItemStack teaStack, ItemStack sconeStack) {
			return teaItemPredicate.matches(teaStack) && sconeItemPredicate.matches(sconeStack);
		}
		
	}
	
}