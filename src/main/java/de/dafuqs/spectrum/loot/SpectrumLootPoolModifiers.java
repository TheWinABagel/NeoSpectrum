package de.dafuqs.spectrum.loot;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockType;
import de.dafuqs.spectrum.compat.gofish.GoFishCompat;
import de.dafuqs.spectrum.entity.type_specific_predicates.ShulkerPredicate;
import de.dafuqs.spectrum.loot.conditions.RandomChanceWithTreasureHunterLootCondition;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpectrumLootPoolModifiers {
	
	private static final Map<ResourceLocation, TreasureHunterDropDefinition> trophyHunterLootPools = new HashMap<>() {{
		// Additional vanilla head drops
		put(new ResourceLocation("entities/creeper"), new TreasureHunterDropDefinition(Items.CREEPER_HEAD, 0.02F));
		put(new ResourceLocation("entities/skeleton"), new TreasureHunterDropDefinition(Items.SKELETON_SKULL, 0.02F));
		put(new ResourceLocation("entities/wither_skeleton"), new TreasureHunterDropDefinition(Items.WITHER_SKELETON_SKULL, 0.1F));
		put(new ResourceLocation("entities/zombie"), new TreasureHunterDropDefinition(Items.ZOMBIE_HEAD, 0.02F));
		put(new ResourceLocation("entities/piglin"), new TreasureHunterDropDefinition(Items.PIGLIN_HEAD, 0.02F));
		put(new ResourceLocation("entities/ender_dragon"), new TreasureHunterDropDefinition(Items.DRAGON_HEAD, 0.35F)); // why not!
		
		// Spectrum head drops
		// ATTENTION: No specific enough loot tables exist for fox, axolotl, parrot and shulker variants.
		// Those are handled separately in setup()
		put(new ResourceLocation("entities/sheep/black"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_BLACK).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/blue"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_BLUE).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/brown"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_BROWN).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/cyan"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_CYAN).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/gray"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_GRAY).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/green"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_GREEN).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/light_blue"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_LIGHT_BLUE).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/light_gray"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_LIGHT_GRAY).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/lime"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_LIME).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/magenta"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_MAGENTA).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/orange"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_ORANGE).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/pink"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_PINK).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/purple"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_PURPLE).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/red"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_RED).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/white"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_WHITE).asItem(), 0.02F));
		put(new ResourceLocation("entities/sheep/yellow"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHEEP_YELLOW).asItem(), 0.02F));
		put(new ResourceLocation("entities/bat"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.BAT).asItem(), 0.02F));
		put(new ResourceLocation("entities/blaze"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.BLAZE).asItem(), 0.02F));
		put(new ResourceLocation("entities/cat"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.CAT).asItem(), 0.02F));
		put(new ResourceLocation("entities/cave_spider"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.CAVE_SPIDER).asItem(), 0.02F));
		put(new ResourceLocation("entities/chicken"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.CHICKEN).asItem(), 0.02F));
		put(new ResourceLocation("entities/cow"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.COW).asItem(), 0.02F));
		put(new ResourceLocation("entities/donkey"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.DONKEY).asItem(), 0.02F));
		put(new ResourceLocation("entities/drowned"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.DROWNED).asItem(), 0.02F));
		put(new ResourceLocation("entities/elder_guardian"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ELDER_GUARDIAN).asItem(), 0.02F));
		put(new ResourceLocation("entities/enderman"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ENDERMAN).asItem(), 0.02F));
		put(new ResourceLocation("entities/endermite"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ENDERMITE).asItem(), 0.02F));
		put(new ResourceLocation("entities/evoker"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.EVOKER).asItem(), 0.02F));
		put(new ResourceLocation("entities/ghast"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.GHAST).asItem(), 0.02F));
		put(new ResourceLocation("entities/guardian"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.GUARDIAN).asItem(), 0.02F));
		put(new ResourceLocation("entities/hoglin"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.HOGLIN).asItem(), 0.02F));
		put(new ResourceLocation("entities/horse"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.HORSE).asItem(), 0.02F));
		put(new ResourceLocation("entities/husk"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.HUSK).asItem(), 0.02F));
		put(new ResourceLocation("entities/illusioner"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ILLUSIONER).asItem(), 0.02F));
		put(new ResourceLocation("entities/iron_golem"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.IRON_GOLEM).asItem(), 0.02F));
		put(new ResourceLocation("entities/llama"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.LLAMA).asItem(), 0.02F));
		put(new ResourceLocation("entities/magma_cube"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.MAGMA_CUBE).asItem(), 0.02F));
		put(new ResourceLocation("entities/mule"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.MULE).asItem(), 0.02F));
		put(new ResourceLocation("entities/ocelot"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.OCELOT).asItem(), 0.02F));
		put(new ResourceLocation("entities/panda"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PANDA).asItem(), 0.02F));
		put(new ResourceLocation("entities/phantom"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PHANTOM).asItem(), 0.02F));
		put(new ResourceLocation("entities/pig"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PIG).asItem(), 0.02F));
		put(new ResourceLocation("entities/polar_bear"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.POLAR_BEAR).asItem(), 0.02F));
		put(new ResourceLocation("entities/pufferfish"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PUFFERFISH).asItem(), 0.02F));
		put(new ResourceLocation("entities/rabbit"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.RABBIT).asItem(), 0.02F));
		put(new ResourceLocation("entities/ravager"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.RAVAGER).asItem(), 0.02F));
		put(new ResourceLocation("entities/salmon"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SALMON).asItem(), 0.02F));
		put(new ResourceLocation("entities/silverfish"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SILVERFISH).asItem(), 0.02F));
		put(new ResourceLocation("entities/slime"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SLIME).asItem(), 0.02F));
		put(new ResourceLocation("entities/snow_golem"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SNOW_GOLEM).asItem(), 0.02F));
		put(new ResourceLocation("entities/spider"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SPIDER).asItem(), 0.02F));
		put(new ResourceLocation("entities/squid"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SQUID).asItem(), 0.02F));
		put(new ResourceLocation("entities/stray"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.STRAY).asItem(), 0.02F));
		put(new ResourceLocation("entities/strider"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.STRIDER).asItem(), 0.02F));
		put(new ResourceLocation("entities/trader_llama"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.TRADER_LLAMA).asItem(), 0.02F));
		put(new ResourceLocation("entities/turtle"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.TURTLE).asItem(), 0.02F));
		put(new ResourceLocation("entities/vex"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.VEX).asItem(), 0.02F));
		put(new ResourceLocation("entities/villager"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.VILLAGER).asItem(), 0.02F));
		put(new ResourceLocation("entities/vindicator"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.VINDICATOR).asItem(), 0.02F));
		put(new ResourceLocation("entities/wandering_trader"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.WANDERING_TRADER).asItem(), 0.02F));
		put(new ResourceLocation("entities/witch"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.WITCH).asItem(), 0.02F));
		put(new ResourceLocation("entities/wither"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.WITHER).asItem(), 0.15F)); // he has 3 heads, after all!
		put(new ResourceLocation("entities/wolf"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.WOLF).asItem(), 0.02F));
		put(new ResourceLocation("entities/zoglin"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ZOGLIN).asItem(), 0.02F));
		put(new ResourceLocation("entities/zombie_villager"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ZOMBIE_VILLAGER).asItem(), 0.02F));
		put(new ResourceLocation("entities/zombified_piglin"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ZOMBIFIED_PIGLIN).asItem(), 0.02F));
		put(new ResourceLocation("entities/bee"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.BEE).asItem(), 0.02F));
		put(new ResourceLocation("entities/tropical_fish"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.CLOWNFISH).asItem(), 0.02F));
		put(new ResourceLocation("entities/goat"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.GOAT).asItem(), 0.02F));
		put(new ResourceLocation("entities/glow_squid"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.GLOW_SQUID).asItem(), 0.02F));
		put(new ResourceLocation("entities/warden"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.WARDEN).asItem(), 0.2F));
		put(new ResourceLocation("entities/tadpole"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.TADPOLE).asItem(), 0.02F));
		put(new ResourceLocation("entities/allay"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ALLAY).asItem(), 0.02F));
		
		put(new ResourceLocation("spectrum:entities/egg_laying_wooly_pig"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.EGG_LAYING_WOOLY_PIG).asItem(), 0.1F));
		put(new ResourceLocation("spectrum:entities/kindling"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.KINDLING).asItem(), 0.1F));
		put(new ResourceLocation("spectrum:entities/preservation_turret"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PRESERVATION_TURRET).asItem(), 0.1F));
		put(new ResourceLocation("spectrum:entities/monstrosity"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.MONSTROSITY).asItem(), 0.1F));
		put(new ResourceLocation("spectrum:entities/lizard"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.LIZARD).asItem(), 0.1F));
		put(new ResourceLocation("spectrum:entities/eraser"), new TreasureHunterDropDefinition(SpectrumBlocks.getMobHead(SpectrumSkullBlockType.ERASER).asItem(), 0.1F));
	}};

	public static void setup() { //todoforge loot pool modifiers, move to GLM
//		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
//
//			// Treasure hunter pools
//			if (trophyHunterLootPools.containsKey(id)) {
//				TreasureHunterDropDefinition treasureHunterDropDefinition = trophyHunterLootPools.get(id);
//				tableBuilder.pool(getLootPool(treasureHunterDropDefinition));
//				// Some treasure hunter pools use custom loot conditions
//				// because vanillas are too generic (fox/snow fox both use "fox" loot table)
//			} else if (id.equals(new ResourceLocation("entities/fox"))) {
//				tableBuilder.pool(getFoxLootPool(Fox.Type.RED, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.FOX).asItem(), 0.02F));
//				tableBuilder.pool(getFoxLootPool(Fox.Type.SNOW, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.FOX_ARCTIC).asItem(), 0.02F));
//			} else if (id.equals(new ResourceLocation("entities/mooshroom"))) {
//				tableBuilder.pool(getMooshroomLootPool(MushroomCow.MushroomType.BROWN, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.MOOSHROOM_BROWN).asItem(), 0.02F));
//				tableBuilder.pool(getMooshroomLootPool(MushroomCow.MushroomType.RED, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.MOOSHROOM_RED).asItem(), 0.02F));
//			} else if (id.equals(new ResourceLocation("entities/shulker"))) {
//				tableBuilder.pool(getShulkerLootPool(null, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.BLACK, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_BLACK).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.BLUE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_BLUE).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.BROWN, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_BROWN).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.CYAN, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_CYAN).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.GRAY, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_GRAY).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.GREEN, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_GREEN).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.LIGHT_BLUE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_LIGHT_BLUE).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.LIGHT_GRAY, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_LIGHT_GRAY).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.LIME, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_LIME).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.MAGENTA, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_MAGENTA).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.ORANGE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_ORANGE).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.PINK, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_PINK).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.PURPLE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_PURPLE).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.RED, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_RED).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.WHITE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_WHITE).asItem(), 0.05F));
//				tableBuilder.pool(getShulkerLootPool(DyeColor.YELLOW, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.SHULKER_YELLOW).asItem(), 0.05F));
//			} else if (id.equals(new ResourceLocation("entities/axolotl"))) {
//				tableBuilder.pool(getAxolotlLootPool(Axolotl.Variant.BLUE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.AXOLOTL_BLUE).asItem(), 0.02F));
//				tableBuilder.pool(getAxolotlLootPool(Axolotl.Variant.CYAN, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.AXOLOTL_CYAN).asItem(), 0.02F));
//				tableBuilder.pool(getAxolotlLootPool(Axolotl.Variant.GOLD, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.AXOLOTL_GOLD).asItem(), 0.02F));
//				tableBuilder.pool(getAxolotlLootPool(Axolotl.Variant.LUCY, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.AXOLOTL_LEUCISTIC).asItem(), 0.02F));
//				tableBuilder.pool(getAxolotlLootPool(Axolotl.Variant.WILD, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.AXOLOTL_BROWN).asItem(), 0.02F));
//			} else if (id.equals(new ResourceLocation("entities/parrot"))) {
//				tableBuilder.pool(getParrotLootPool(Parrot.Variant.RED_BLUE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PARROT_RED).asItem(), 0.02F));
//				tableBuilder.pool(getParrotLootPool(Parrot.Variant.BLUE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PARROT_BLUE).asItem(), 0.02F));
//				tableBuilder.pool(getParrotLootPool(Parrot.Variant.GREEN, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PARROT_GREEN).asItem(), 0.02F));
//				tableBuilder.pool(getParrotLootPool(Parrot.Variant.YELLOW_BLUE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PARROT_CYAN).asItem(), 0.02F));
//				tableBuilder.pool(getParrotLootPool(Parrot.Variant.GRAY, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.PARROT_GRAY).asItem(), 0.02F));
//			} else if (id.equals(new ResourceLocation("entities/frog"))) {
//				tableBuilder.pool(getFrogLootPool(FrogVariant.TEMPERATE, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.FROG_TEMPERATE).asItem(), 0.02F));
//				tableBuilder.pool(getFrogLootPool(FrogVariant.COLD, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.FROG_COLD).asItem(), 0.02F));
//				tableBuilder.pool(getFrogLootPool(FrogVariant.WARM, SpectrumBlocks.getMobHead(SpectrumSkullBlockType.FROG_WARM).asItem(), 0.02F));
//			} else if (GoFishCompat.isLoaded()) {
//				//Go-Fish compat: fishing of crates & go-fish fishies
//				if (id.equals(SpectrumCommon.locate("gameplay/fishing/lava/fishing"))) {
//					tableBuilder.modifyPools(builder -> builder.with(LootTableReference.lootTableReference(GoFishCompat.NETHER_FISH_LOOT_TABLE_ID).setWeight(80).setQuality(-1).build()));
//					tableBuilder.modifyPools(builder -> builder.add(LootTableReference.lootTableReference(GoFishCompat.NETHER_CRATES_LOOT_TABLE_ID).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().subPredicate(FishingHookPredicate.inOpenWater(true)).build()))));
//				} else if (id.equals(SpectrumCommon.locate("gameplay/fishing/end/fishing"))) {
//					tableBuilder.modifyPools(builder -> builder.with(LootTableReference.lootTableReference(GoFishCompat.END_FISH_LOOT_TABLE_ID).setWeight(90).setQuality(-1).build()));
//					tableBuilder.modifyPools(builder -> builder.add(LootTableReference.lootTableReference(GoFishCompat.END_CRATES_LOOT_TABLE_ID).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().subPredicate(FishingHookPredicate.inOpenWater(true)).build()))));
//				} else if (id.equals(SpectrumCommon.locate("gameplay/fishing/deeper_down/fishing"))) {
//					tableBuilder.modifyPools(builder -> builder.add(LootTableReference.lootTableReference(GoFishCompat.DEFAULT_CRATES_LOOT_TABLE_ID).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().subPredicate(FishingHookPredicate.inOpenWater(true)).build()))));
//				} else if (id.equals(SpectrumCommon.locate("gameplay/fishing/mud/fishing"))) {
//					tableBuilder.modifyPools(builder -> builder.add(LootTableReference.lootTableReference(GoFishCompat.DEFAULT_CRATES_LOOT_TABLE_ID).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().subPredicate(FishingHookPredicate.inOpenWater(true)).build()))));
//				} else if (id.equals(SpectrumCommon.locate("gameplay/fishing/liquid_crystal/fishing"))) {
//					tableBuilder.modifyPools(builder -> builder.add(LootTableReference.lootTableReference(GoFishCompat.DEFAULT_CRATES_LOOT_TABLE_ID).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().subPredicate(FishingHookPredicate.inOpenWater(true)).build()))));
//				} else if (id.equals(SpectrumCommon.locate("gameplay/fishing/midnight_solution/fishing"))) {
//					tableBuilder.modifyPools(builder -> builder.add(LootTableReference.lootTableReference(GoFishCompat.DEFAULT_CRATES_LOOT_TABLE_ID).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().subPredicate(FishingHookPredicate.inOpenWater(true)).build()))));
//				}
//			}
//		});
	}
	
	private static LootPool getLootPool(TreasureHunterDropDefinition treasureHunterDropDefinition) {
		Item dropItem = treasureHunterDropDefinition.skullItem;
		float chance = treasureHunterDropDefinition.treasureHunterMultiplier;
		
		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, dropItem))
				.add(LootItem.lootTableItem(dropItem))
				.build();
	}
	
	private static LootPool getFoxLootPool(Fox.Type foxType, Item item, float chance) {
		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, item))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.Types.FOX.createPredicate(foxType)).build()))
				.add(LootItem.lootTableItem(item))
				.build();
	}
	
	private static LootPool getMooshroomLootPool(MushroomCow.MushroomType mooshroomType, Item item, float chance) {
		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, item))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.Types.MOOSHROOM.createPredicate(mooshroomType)).build()))
				.add(LootItem.lootTableItem(item))
				.build();
	}
	
	private static LootPool getShulkerLootPool(@Nullable DyeColor dyeColor, Item item, float chance) {
		Optional<DyeColor> c = dyeColor == null ? Optional.empty() : Optional.of(dyeColor);

		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, item))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(ShulkerPredicate.of(c.orElse(null))).build()))
				.add(LootItem.lootTableItem(item))
				.build();
	}

	private static LootPool getAxolotlLootPool(Axolotl.Variant variant, Item item, float chance) {
		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, item))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.Types.AXOLOTL.createPredicate(variant))))
				.add(LootItem.lootTableItem(item))
				.build();
	}

	private static LootPool getFrogLootPool(FrogVariant variant, Item item, float chance) {
		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, item))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.variant(variant)).build()))
				.add(LootItem.lootTableItem(item))
				.build();
	}

	private static LootPool getParrotLootPool(Parrot.Variant variant, Item item, float chance) {
		return new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1))
				.when(RandomChanceWithTreasureHunterLootCondition.builder(chance, item))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.Types.PARROT.createPredicate(variant)).build()))
				.add(LootItem.lootTableItem(item))
				.build();
	}

	private static class TreasureHunterDropDefinition {
		public final Item skullItem;
		public final float treasureHunterMultiplier;
		
		public TreasureHunterDropDefinition(Item skullItem, float trophyHunterChance) {
			this.skullItem = skullItem;
			this.treasureHunterMultiplier = trophyHunterChance;
		}
	}
	
	
}
