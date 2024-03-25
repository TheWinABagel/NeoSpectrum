package de.dafuqs.spectrum.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import de.dafuqs.spectrum.helpers.NbtHelper;
import de.dafuqs.spectrum.loot.SpectrumLootFunctionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MergeNbtRandomlyLootFunction extends LootItemConditionalFunction {
	
	final List<CompoundTag> nbts;
	
	MergeNbtRandomlyLootFunction(LootItemCondition[] conditions, Collection<CompoundTag> nbts) {
		super(conditions);
		this.nbts = ImmutableList.copyOf(nbts);
	}
	
	@Override
	public LootItemFunctionType getType() {
		return SpectrumLootFunctionTypes.SET_NBT_RANDOMLY;
	}
	
	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		if (this.nbts.isEmpty()) {
			return stack;
		}
		
		CompoundTag compound = this.nbts.get(context.getRandom().nextInt(this.nbts.size()));
		stack.getOrCreateTag().merge(compound);
		return stack;
	}
	
	public static de.dafuqs.spectrum.loot.functions.MergeNbtRandomlyLootFunction.Builder create() {
		return new de.dafuqs.spectrum.loot.functions.MergeNbtRandomlyLootFunction.Builder();
	}
	
	public static LootItemConditionalFunction.Builder<?> builder() {
		return simpleBuilder((conditions) -> new MergeNbtRandomlyLootFunction(conditions, ImmutableList.of()));
	}
	
	public static class Builder extends LootItemConditionalFunction.Builder<MergeNbtRandomlyLootFunction.Builder> {
		private final Set<CompoundTag> nbts = Sets.newHashSet();
		
		@Override
		protected MergeNbtRandomlyLootFunction.Builder getThis() {
			return this;
		}
		
		public MergeNbtRandomlyLootFunction.Builder add(CompoundTag nbt) {
			this.nbts.add(nbt);
			return this;
		}
		
		@Override
		public LootItemFunction build() {
			return new MergeNbtRandomlyLootFunction(this.getConditions(), this.nbts);
		}
	}
	
	public static class Serializer extends LootItemConditionalFunction.Serializer<MergeNbtRandomlyLootFunction> {
		
		@Override
		public void serialize(JsonObject jsonObject, MergeNbtRandomlyLootFunction lootFunction, JsonSerializationContext jsonSerializationContext) {
			super.serialize(jsonObject, lootFunction, jsonSerializationContext);
			if (!lootFunction.nbts.isEmpty()) {
				JsonArray jsonArray = new JsonArray();
				for (CompoundTag nbt : lootFunction.nbts) {
					jsonArray.add(new JsonPrimitive(nbt.toString()));
				}
				jsonObject.add("tags", jsonArray);
			}
		}
		
		@Override
		public MergeNbtRandomlyLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
			List<CompoundTag> nbts = Lists.newArrayList();
			if (jsonObject.has("tags")) {
				JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "tags");
				for (JsonElement jsonElement : jsonArray) {
					Optional<CompoundTag> nbt = NbtHelper.getNbtCompound(jsonElement);
					if (nbt.isPresent()) {
						nbts.add(nbt.get());
					}
				}
			}
			
			return new MergeNbtRandomlyLootFunction(lootConditions, nbts);
		}
	}
	
}
