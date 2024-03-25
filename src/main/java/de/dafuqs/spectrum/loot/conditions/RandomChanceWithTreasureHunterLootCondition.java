package de.dafuqs.spectrum.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.dafuqs.spectrum.loot.SpectrumLootConditionTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class RandomChanceWithTreasureHunterLootCondition implements LootItemCondition {
	
	private final float chance;
	private final ItemStack advancementTriggerItemStack;
	
	private RandomChanceWithTreasureHunterLootCondition(float chance, Item item) {
		this.chance = chance;
		this.advancementTriggerItemStack = new ItemStack(item);
	}
	
	public static Builder builder(float chance, Item advancementTriggerItem) {
		return () -> new RandomChanceWithTreasureHunterLootCondition(chance, advancementTriggerItem);
	}
	
	@Override
	public LootItemConditionType getType() {
		return SpectrumLootConditionTypes.RANDOM_CHANCE_WITH_TREASURE_HUNTER;
	}
	
	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
	}
	
	@Override
	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
		int treasureHunterLevel = 0;
		if (entity instanceof Player playerEntity) {
			if (!SpectrumEnchantments.TREASURE_HUNTER.canEntityUse(playerEntity)) {
				return false;
			}
			treasureHunterLevel = EnchantmentHelper.getEnchantmentLevel(SpectrumEnchantments.TREASURE_HUNTER, (LivingEntity) entity);
		}
		
		if (treasureHunterLevel == 0) {
			// No Treasure Hunter => no drop
			return false;
		} else {
			boolean success = lootContext.getRandom().nextFloat() < this.chance * treasureHunterLevel;
			if (success) {
				Entity killerEntity = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
				if (killerEntity instanceof ServerPlayer serverPlayerEntity) {
					SpectrumAdvancementCriteria.TREASURE_HUNTER_DROP.trigger(serverPlayerEntity, advancementTriggerItemStack);
				}
			}
			return success;
		}
	}
	
	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<RandomChanceWithTreasureHunterLootCondition> {
		
		public Serializer() {
		}
		
		@Override
		public void toJson(JsonObject jsonObject, RandomChanceWithTreasureHunterLootCondition randomChanceWithLootingLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", randomChanceWithLootingLootCondition.chance);
			jsonObject.addProperty("advancement_trigger_itemstack", BuiltInRegistries.ITEM.getKey(randomChanceWithLootingLootCondition.advancementTriggerItemStack.getItem()).toString());
		}
		
		@Override
		public RandomChanceWithTreasureHunterLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new RandomChanceWithTreasureHunterLootCondition(
					GsonHelper.getAsFloat(jsonObject, "chance"),
					BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(jsonObject, "advancement_trigger_itemstack")))
			);
		}
	}
}
