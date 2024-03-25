package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TreasureHunterDropCriterion extends SimpleCriterionTrigger<TreasureHunterDropCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("treasure_hunter_drop");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public TreasureHunterDropCriterion.Conditions createInstance(@NotNull JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate droppedItemPredicate = ItemPredicate.fromJson(jsonObject.get("dropped_item"));
		return new TreasureHunterDropCriterion.Conditions(extended, droppedItemPredicate);
	}
	
	public void trigger(ServerPlayer player, ItemStack droppedStack) {
		this.trigger(player, (conditions) -> conditions.matches(droppedStack));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final ItemPredicate droppedItemPredicate;
		
		public Conditions(ContextAwarePredicate player, @Nullable ItemPredicate droppedItemPredicate) {
			super(ID, player);
			this.droppedItemPredicate = droppedItemPredicate;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("dropped_item", this.droppedItemPredicate.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack droppedStack) {
			return this.droppedItemPredicate.matches(droppedStack);
		}
	}
	
}
