package de.dafuqs.spectrum.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.loot.SpectrumLootFunctionTypes;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DyeRandomlyLootFunction extends LootItemConditionalFunction {
	
	final List<Integer> colors;
	
	DyeRandomlyLootFunction(LootItemCondition[] conditions, Collection<Integer> colors) {
		super(conditions);
		this.colors = ImmutableList.copyOf(colors);
	}
	
	@Override
	public LootItemFunctionType getType() {
		return SpectrumLootFunctionTypes.DYE_RANDOMLY;
	}
	
	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		
		if (stack.getItem() instanceof DyeableLeatherItem dyeableItem) {
			RandomSource random = context.getRandom();
			int color = this.colors.isEmpty() ? ColorHelper.getRandomColor(random.nextInt()) : this.colors.get(random.nextInt(this.colors.size()));
			dyeableItem.setColor(stack, color);
		}
		
		return stack;
	}
	
	public static de.dafuqs.spectrum.loot.functions.DyeRandomlyLootFunction.Builder create() {
		return new de.dafuqs.spectrum.loot.functions.DyeRandomlyLootFunction.Builder();
	}
	
	public static LootItemConditionalFunction.Builder<?> builder() {
		return simpleBuilder((conditions) -> new DyeRandomlyLootFunction(conditions, ImmutableList.of()));
	}
	
	public static class Builder extends LootItemConditionalFunction.Builder<DyeRandomlyLootFunction.Builder> {
		private final Set<Integer> colors = Sets.newHashSet();
		
		@Override
		protected DyeRandomlyLootFunction.Builder getThis() {
			return this;
		}
		
		public DyeRandomlyLootFunction.Builder add(Integer color) {
			this.colors.add(color);
			return this;
		}
		
		@Override
		public LootItemFunction build() {
			return new DyeRandomlyLootFunction(this.getConditions(), this.colors);
		}
	}
	
	public static class Serializer extends LootItemConditionalFunction.Serializer<DyeRandomlyLootFunction> {
		
		@Override
		public void toJson(JsonObject jsonObject, DyeRandomlyLootFunction lootFunction, JsonSerializationContext jsonSerializationContext) {
			super.serialize(jsonObject, lootFunction, jsonSerializationContext);
			if (!lootFunction.colors.isEmpty()) {
				JsonArray jsonArray = new JsonArray();
				for (Integer color : lootFunction.colors) {
					jsonArray.add(new JsonPrimitive(color));
				}
				jsonObject.add("colors", jsonArray);
			}
		}
		
		@Override
		public DyeRandomlyLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
			List<Integer> colors = Lists.newArrayList();
			if (jsonObject.has("colors")) {
				JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "colors");
				for (JsonElement jsonElement : jsonArray) {
					if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
						if (jsonPrimitive.isNumber()) {
							colors.add(jsonElement.getAsInt());
						} else if (jsonPrimitive.isString()) {
							String hex = jsonPrimitive.getAsString();
							if (hex.startsWith("#")) {
								colors.add(Integer.parseInt(hex.substring(1), 16));
							}
						}
					}
				}
			}
			
			return new DyeRandomlyLootFunction(lootConditions, colors);
		}
	}
}
