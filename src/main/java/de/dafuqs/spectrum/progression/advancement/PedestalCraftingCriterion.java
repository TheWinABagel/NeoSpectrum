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

public class PedestalCraftingCriterion extends SimpleCriterionTrigger<PedestalCraftingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("crafted_with_pedestal");
	
	public static PedestalCraftingCriterion.Conditions create(ItemPredicate[] item, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints durationTicks) {
		return new PedestalCraftingCriterion.Conditions(ContextAwarePredicate.ANY, item, experienceRange, durationTicks);
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
		ItemPredicate[] itemPredicates = ItemPredicate.fromJsonArray(jsonObject.get("items"));
		MinMaxBounds.Ints experienceRange = MinMaxBounds.Ints.fromJson(jsonObject.get("gained_experience"));
		MinMaxBounds.Ints craftingDurationTicksRange = MinMaxBounds.Ints.fromJson(jsonObject.get("crafting_duration_ticks"));
		return new PedestalCraftingCriterion.Conditions(playerPredicate, itemPredicates, experienceRange, craftingDurationTicksRange);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	public void trigger(ServerPlayer player, ItemStack itemStack, int experience, int durationTicks) {
		this.trigger(player, (conditions) -> conditions.matches(itemStack, experience, durationTicks));
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate[] itemPredicates;
		private final MinMaxBounds.Ints experienceRange;
		private final MinMaxBounds.Ints craftingDurationTicksRange;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate[] itemPredicates, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints craftingDurationTicksRange) {
			this(ID, player, itemPredicates, experienceRange, craftingDurationTicksRange);
		}
		
		public Conditions(ResourceLocation id, ContextAwarePredicate player, ItemPredicate[] itemPredicates, MinMaxBounds.Ints experienceRange, MinMaxBounds.Ints craftingDurationTicksRange) {
			super(id, player);
			this.itemPredicates = itemPredicates;
			this.experienceRange = experienceRange;
			this.craftingDurationTicksRange = craftingDurationTicksRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.addProperty("items", Arrays.toString(this.itemPredicates));
			jsonObject.add("gained_experience", this.experienceRange.serializeToJson());
			jsonObject.add("crafting_duration_ticks", this.experienceRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack itemStack, int experience, int durationTicks) {
			if (this.experienceRange.matches(experience) && this.craftingDurationTicksRange.matches(durationTicks)) {
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
