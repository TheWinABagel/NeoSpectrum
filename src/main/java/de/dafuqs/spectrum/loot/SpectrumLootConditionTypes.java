package de.dafuqs.spectrum.loot;

import de.dafuqs.spectrum.loot.conditions.RandomChanceWithTreasureHunterLootCondition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class SpectrumLootConditionTypes {

    public static LootItemConditionType RANDOM_CHANCE_WITH_TREASURE_HUNTER;

    private static LootItemConditionType register(String id, Serializer<? extends LootItemCondition> serializer) {
		return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(id), new LootItemConditionType(serializer));
    }

    public static void register() {
        RANDOM_CHANCE_WITH_TREASURE_HUNTER = register("random_chance_with_treasure_hunter", new RandomChanceWithTreasureHunterLootCondition.Serializer());
	}
	
}
