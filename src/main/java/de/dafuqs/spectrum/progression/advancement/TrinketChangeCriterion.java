package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrinketChangeCriterion extends SimpleCriterionTrigger<TrinketChangeCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("trinket_change");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
		ItemPredicate[] itemPredicates = ItemPredicate.fromJsonArray(jsonObject.get("items"));
		MinMaxBounds.Ints totalCountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("total_count"));
		MinMaxBounds.Ints spectrumCountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("spectrum_count"));

		return new TrinketChangeCriterion.Conditions(playerPredicate, itemPredicates, totalCountRange, spectrumCountRange);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, (conditions) -> {
			Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(player);
			if (trinketComponent.isPresent()) {
				List<ItemStack> equippedStacks = new ArrayList<>();
				int spectrumStacks = 0;
				for (Tuple<SlotReference, ItemStack> t : trinketComponent.get().getAllEquipped()) {
					equippedStacks.add(t.getB());
					if (t.getB().getItem() instanceof SpectrumTrinketItem) {
						spectrumStacks++;
					}
				}
				return conditions.matches(equippedStacks, equippedStacks.size(), spectrumStacks);
			}
			return false;
		});
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		
		private final ItemPredicate[] itemPredicates;
		private final MinMaxBounds.Ints totalCountRange;
		private final MinMaxBounds.Ints spectrumCountRange;
		
		public Conditions(ContextAwarePredicate playerPredicate, ItemPredicate[] itemPredicates, MinMaxBounds.Ints totalCountRange, MinMaxBounds.Ints spectrumCountRange) {
			super(TrinketChangeCriterion.ID, playerPredicate);
			this.itemPredicates = itemPredicates;
			this.totalCountRange = totalCountRange;
			this.spectrumCountRange = spectrumCountRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			
			if (this.itemPredicates.length > 0) {
				JsonArray jsonObject2 = new JsonArray();
				for (ItemPredicate itemPredicate : this.itemPredicates) {
					jsonObject2.add(itemPredicate.serializeToJson());
				}
				
				jsonObject.add("items", jsonObject2);
			}
			jsonObject.add("total_count", this.totalCountRange.serializeToJson());
			jsonObject.add("spectrum_count", this.spectrumCountRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(List<ItemStack> trinketStacks, int totalCount, int spectrumCount) {
			if (this.totalCountRange.matches(totalCount) && this.spectrumCountRange.matches(spectrumCount)) {
				int i = this.itemPredicates.length;
				if (i == 0) {
					return true;
				} else {
					List<ItemPredicate> requiredTrinkets = new ObjectArrayList<>(this.itemPredicates);
					for (ItemStack trinketStack : trinketStacks) {
						if (requiredTrinkets.isEmpty()) {
							return true;
						}
						if (!trinketStack.isEmpty()) {
							requiredTrinkets.removeIf((item) -> item.matches(trinketStack));
						}
					}
					
					return requiredTrinkets.isEmpty();
				}
			}
			return false;
		}
	}
	
}
