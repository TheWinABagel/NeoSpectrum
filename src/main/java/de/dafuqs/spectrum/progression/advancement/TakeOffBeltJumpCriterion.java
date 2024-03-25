package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.items.trinkets.TakeOffBeltItem;
import de.dafuqs.spectrum.registries.SpectrumItems;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class TakeOffBeltJumpCriterion extends SimpleCriterionTrigger<TakeOffBeltJumpCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("take_off_belt_jump");
	
	public static TakeOffBeltJumpCriterion.Conditions create(ItemPredicate itemPredicate, MinMaxBounds.Ints chargesRange) {
		return new TakeOffBeltJumpCriterion.Conditions(ContextAwarePredicate.ANY, itemPredicate, chargesRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public TakeOffBeltJumpCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		MinMaxBounds.Ints chargesRange = MinMaxBounds.Ints.fromJson(jsonObject.get("charges"));
		return new TakeOffBeltJumpCriterion.Conditions(extended, itemPredicate, chargesRange);
	}
	
	public void trigger(ServerPlayer player) {
		this.trigger(player, (conditions) -> {
			Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
			if (component.isPresent()) {
				List<Tuple<SlotReference, ItemStack>> equipped = component.get().getEquipped(SpectrumItems.TAKE_OFF_BELT);
				if (!equipped.isEmpty()) {
					ItemStack firstBelt = equipped.get(0).getB();
					if (firstBelt != null) {
						int charge = TakeOffBeltItem.getCurrentCharge(player);
						if (charge > 0) {
							return conditions.matches(firstBelt, charge);
						}
					}
				}
			}
			return false;
		});
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate itemPredicate;
		private final MinMaxBounds.Ints chargesRange;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate itemPredicate, MinMaxBounds.Ints chargesRange) {
			super(ID, player);
			this.itemPredicate = itemPredicate;
			this.chargesRange = chargesRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("item", this.itemPredicate.toString());
			jsonObject.addProperty("charges", this.chargesRange.toString());
			return jsonObject;
		}
		
		public boolean matches(ItemStack beltStack, int charge) {
			return itemPredicate.matches(beltStack) && this.chargesRange.matches(charge);
		}
	}
	
}
