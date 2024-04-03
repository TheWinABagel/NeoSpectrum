package de.dafuqs.spectrum.registries;

//import de.dafuqs.fractal.api.ItemSubGroup;
//import de.dafuqs.fractal.interfaces.ItemGroupParent;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.api.item_group.ItemGroupIDs;
import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.compat.ae2.AE2Compat;
import de.dafuqs.spectrum.compat.gobber.GobberCompat;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.items.food.beverages.VariantBeverageItem;
import de.dafuqs.spectrum.recipe.titration_barrel.ITitrationBarrelRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class SpectrumItemGroups { //todoforge IMPORTANT port fractal

//
//	@SubscribeEvent
//	public void onRegister(RegisterEvent e) {
//		e.register();
//	}
//
//	public static final RegistryObject<CreativeModeTab> TAB = RegistryObject.create()
//
//	public static final CreativeModeTab MAIN = FabricItemGroup.builder()
//			.icon(() -> new ItemStack(SpectrumBlocks.PEDESTAL_ALL_BASIC))
//			.displayItems((displayContext, entries) -> {
//				entries.accept(SpectrumBlocks.PEDESTAL_ALL_BASIC, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
//				ItemGroupParent parent = (ItemGroupParent) SpectrumItemGroups.MAIN;
//				for (ItemSubGroup subGroup : parent.fractal$getChildren()) {
//					entries.acceptAll(subGroup.getSearchTabDisplayItems(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
//				}
//			})
//			.hideTitle()
//			.title(Component.translatable("itemGroup.spectrum"))
//			.build();
//
//	public static void register() {
//		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ItemGroupIDs.MAIN_GROUP_ID, MAIN);
//	}
//
//	public static final ItemSubGroup EQUIPMENT = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_EQUIPMENT, Component.translatable("itemGroup.spectrum.equipment"))
//			.backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE)
//			.entries((displayContext, entries) -> {
//				addEquipmentEntry(SpectrumItems.GUIDEBOOK, entries);
//				addEquipmentEntry(SpectrumItems.PAINTBRUSH, entries);
//				addEquipmentEntry(SpectrumItems.BOTTLE_OF_FADING, entries);
//				addEquipmentEntry(SpectrumItems.BOTTLE_OF_FAILING, entries);
//				addEquipmentEntry(SpectrumItems.BOTTLE_OF_RUIN, entries);
//				addEquipmentEntry(SpectrumItems.BOTTLE_OF_FORFEITURE, entries);
//				addEquipmentEntry(SpectrumItems.BOTTLE_OF_DECAY_AWAY, entries);
//				addEquipmentEntry(SpectrumItems.MULTITOOL, entries);
//				addEquipmentEntry(SpectrumItems.TENDER_PICKAXE, entries);
//				addEquipmentEntry(SpectrumItems.LUCKY_PICKAXE, entries);
//				addEquipmentEntry(SpectrumItems.RAZOR_FALCHION, entries);
//				addEquipmentEntry(SpectrumItems.OBLIVION_PICKAXE, entries);
//				addEquipmentEntry(SpectrumItems.RESONANT_PICKAXE, entries);
//				addEquipmentEntry(SpectrumItems.DRAGONRENDING_PICKAXE, entries);
//				addEquipmentEntry(SpectrumItems.LAGOON_ROD, entries);
//				addEquipmentEntry(SpectrumItems.MOLTEN_ROD, entries);
//				addEquipmentEntry(SpectrumItems.FETCHLING_HELMET, entries);
//				addEquipmentEntry(SpectrumItems.FEROCIOUS_CHESTPLATE, entries);
//				addEquipmentEntry(SpectrumItems.SYLPH_LEGGINGS, entries);
//				addEquipmentEntry(SpectrumItems.OREAD_BOOTS, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_PICKAXE, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_AXE, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_SHOVEL, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_SWORD, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_HOE, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_BOW, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_CROSSBOW, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_SHEARS, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_FISHING_ROD, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_HELMET, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_CHESTPLATE, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_LEGGINGS, entries);
//				addEquipmentEntry(SpectrumItems.BEDROCK_BOOTS, entries);
//				addEquipmentEntry(SpectrumItems.MALACHITE_WORKSTAFF, entries);
//				addEquipmentEntry(SpectrumItems.MALACHITE_ULTRA_GREATSWORD, entries);
//				addEquipmentEntry(SpectrumItems.MALACHITE_CROSSBOW, entries);
//				addEquipmentEntry(SpectrumItems.MALACHITE_BIDENT, entries);
//				addEquipmentEntry(SpectrumItems.GLASS_CREST_WORKSTAFF, entries);
//				addEquipmentEntry(SpectrumItems.GLASS_CREST_ULTRA_GREATSWORD, entries);
//				addEquipmentEntry(SpectrumItems.FEROCIOUS_GLASS_CREST_BIDENT, entries);
//				addEquipmentEntry(SpectrumItems.FRACTAL_GLASS_CREST_BIDENT, entries);
//				addEquipmentEntry(SpectrumItems.GLASS_CREST_CROSSBOW, entries);
//				addEquipmentEntry(SpectrumItems.MALACHITE_GLASS_ARROW, entries);
//				addEquipmentEntry(SpectrumItems.TOPAZ_GLASS_ARROW, entries);
//				addEquipmentEntry(SpectrumItems.AMETHYST_GLASS_ARROW, entries);
//				addEquipmentEntry(SpectrumItems.CITRINE_GLASS_ARROW, entries);
//				addEquipmentEntry(SpectrumItems.ONYX_GLASS_ARROW, entries);
//				addEquipmentEntry(SpectrumItems.MOONSTONE_GLASS_ARROW, entries);
//				addEquipmentEntry(SpectrumItems.AZURITE_GLASS_AMPOULE, entries);
//				addEquipmentEntry(SpectrumItems.MALACHITE_GLASS_AMPOULE, entries);
//				addEquipmentEntry(SpectrumItems.BLOODSTONE_GLASS_AMPOULE, entries);
//				addEquipmentEntry(SpectrumItems.DREAMFLAYER, entries);
//				addEquipmentEntry(SpectrumItems.NIGHTFALLS_BLADE, entries);
//				addEquipmentEntry(SpectrumItems.DRACONIC_TWINSWORD, entries);
//				addEquipmentEntry(SpectrumItems.DRAGON_TALON, entries);
//				addEquipmentEntry(SpectrumItems.OMNI_ACCELERATOR, entries);
//				addEquipmentEntry(SpectrumItems.FANCIFUL_STONE_RING, entries);
//				addEquipmentEntry(SpectrumItems.FANCIFUL_BELT, entries);
//				addEquipmentEntry(SpectrumItems.FANCIFUL_PENDANT, entries);
//				addEquipmentEntry(SpectrumItems.FANCIFUL_CIRCLET, entries);
//				addEquipmentEntry(SpectrumItems.FANCIFUL_GLOVES, entries);
//				addEquipmentEntry(SpectrumItems.FANCIFUL_BISMUTH_RING, entries);
//				addEquipmentEntry(SpectrumItems.GLOW_VISION_GOGGLES, entries);
//				addEquipmentEntry(SpectrumItems.JEOPARDANT, entries);
//				addEquipmentEntry(SpectrumItems.SEVEN_LEAGUE_BOOTS, entries);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.SEVEN_LEAGUE_BOOTS));
//				entries.accept(SpectrumItems.COTTON_CLOUD_BOOTS);
//				entries.accept(SpectrumItems.RADIANCE_PIN);
//				entries.accept(SpectrumItems.TOTEM_PENDANT);
//				entries.accept(SpectrumItems.TAKE_OFF_BELT);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.TAKE_OFF_BELT));
//				entries.accept(SpectrumItems.AZURE_DIKE_BELT);
//				entries.accept(SpectrumItems.AZURE_DIKE_RING);
//				entries.accept(SpectrumItems.SHIELDGRASP_AMULET);
//				entries.accept(SpectrumItems.SHIELDGRASP_AMULET.getFullStack());
//				entries.accept(SpectrumItems.HEARTSINGERS_REWARD);
//				entries.accept(SpectrumItems.HEARTSINGERS_REWARD.getFullStack());
//				entries.accept(SpectrumItems.GLOVES_OF_DAWNS_GRASP);
//				entries.accept(SpectrumItems.GLOVES_OF_DAWNS_GRASP.getFullStack());
//				entries.accept(SpectrumItems.RING_OF_PURSUIT);
//				entries.accept(SpectrumItems.RING_OF_PURSUIT.getFullStack());
//				entries.accept(SpectrumItems.GLEAMING_PIN);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.GLEAMING_PIN));
//				entries.accept(SpectrumItems.LESSER_POTION_PENDANT);
//				entries.accept(SpectrumItems.GREATER_POTION_PENDANT);
//				entries.accept(SpectrumItems.ASHEN_CIRCLET);
//				entries.accept(SpectrumItems.WEEPING_CIRCLET);
//				entries.accept(SpectrumItems.PUFF_CIRCLET);
//				entries.accept(SpectrumItems.WHISPY_CIRCLET);
//				entries.accept(SpectrumItems.CIRCLET_OF_ARROGANCE);
//				entries.accept(SpectrumItems.NEAT_RING);
//				entries.accept(SpectrumItems.CRAFTING_TABLET);
//				entries.accept(SpectrumItems.BOTTOMLESS_BUNDLE);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.BOTTOMLESS_BUNDLE));
//
//				entries.accept(SpectrumItems.KNOWLEDGE_GEM);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.KNOWLEDGE_GEM));
//
//				ItemStack knowledgeGemStack = SpectrumItems.KNOWLEDGE_GEM.getDefaultInstance();
//				ExperienceStorageItem.addStoredExperience(knowledgeGemStack, SpectrumItems.KNOWLEDGE_GEM.getMaxStoredExperience(knowledgeGemStack));
//				entries.accept(knowledgeGemStack);
//
//				ItemStack enchantedKnowledgeGemStack = SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.KNOWLEDGE_GEM);
//				ExperienceStorageItem.addStoredExperience(enchantedKnowledgeGemStack, SpectrumItems.KNOWLEDGE_GEM.getMaxStoredExperience(enchantedKnowledgeGemStack));
//				entries.accept(enchantedKnowledgeGemStack);
//
//				entries.accept(SpectrumItems.CELESTIAL_POCKETWATCH);
//				entries.accept(SpectrumItems.GILDED_BOOK);
//				entries.accept(SpectrumItems.ENCHANTMENT_CANVAS);
//				entries.accept(SpectrumItems.EVERPROMISE_RIBBON);
//				entries.accept(SpectrumItems.BAG_OF_HOLDING);
//				entries.accept(SpectrumItems.RADIANCE_STAFF);
//				entries.accept(SpectrumItems.NATURES_STAFF);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.NATURES_STAFF));
//				entries.accept(SpectrumItems.STAFF_OF_REMEMBRANCE);
//				entries.accept(SpectrumItems.CONSTRUCTORS_STAFF);
//				entries.accept(SpectrumItems.EXCHANGING_STAFF);
//				entries.accept(SpectrumEnchantmentHelper.addOrUpgradeEnchantment(SpectrumItems.EXCHANGING_STAFF.getDefaultInstance(), Enchantments.BLOCK_FORTUNE, 3, false, false).getB());
//				entries.accept(SpectrumEnchantmentHelper.addOrUpgradeEnchantment(SpectrumItems.EXCHANGING_STAFF.getDefaultInstance(), Enchantments.SILK_TOUCH, 1, false, false).getB());
//				entries.accept(SpectrumEnchantmentHelper.addOrUpgradeEnchantment(SpectrumItems.EXCHANGING_STAFF.getDefaultInstance(), SpectrumEnchantments.RESONANCE, 1, false, false).getB());
//				entries.accept(SpectrumItems.BLOCK_FLOODER);
//				entries.accept(SpectrumItems.ENDER_SPLICE);
//				entries.accept(SpectrumEnchantmentHelper.getMaxEnchantedStack(SpectrumItems.ENDER_SPLICE));
//				entries.accept(SpectrumItems.PERTURBED_EYE);
//				entries.accept(SpectrumBlocks.PARAMETRIC_MINING_DEVICE);
//				entries.accept(SpectrumBlocks.THREAT_CONFLUX);
//				entries.accept(SpectrumItems.PIPE_BOMB);
//				entries.accept(SpectrumItems.CRESCENT_CLOCK);
//				entries.accept(SpectrumItems.MYSTERIOUS_LOCKET);
//				entries.accept(SpectrumItems.MYSTERIOUS_COMPASS);
//			}).build();
//
//	public static final ItemSubGroup FUNCTIONAL = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_FUNCTIONAL, Component.translatable("itemGroup.spectrum.functional")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE)
//			.entries((displayContext, entries) -> {
//				entries.accept(SpectrumBlocks.PEDESTAL_BASIC_TOPAZ);
//				entries.accept(SpectrumBlocks.PEDESTAL_BASIC_AMETHYST);
//				entries.accept(SpectrumBlocks.PEDESTAL_BASIC_CITRINE);
//				entries.accept(SpectrumBlocks.PEDESTAL_ALL_BASIC);
//				entries.accept(SpectrumBlocks.PEDESTAL_ONYX);
//				entries.accept(SpectrumBlocks.PEDESTAL_MOONSTONE);
//				entries.accept(SpectrumBlocks.FUSION_SHRINE_BASALT);
//				entries.accept(SpectrumBlocks.FUSION_SHRINE_CALCITE);
//				entries.accept(SpectrumBlocks.ENCHANTER);
//				entries.accept(SpectrumBlocks.ITEM_BOWL_BASALT);
//				entries.accept(SpectrumBlocks.ITEM_BOWL_CALCITE);
//				entries.accept(SpectrumBlocks.ITEM_ROUNDEL);
//				entries.accept(SpectrumBlocks.POTION_WORKSHOP);
//				entries.accept(SpectrumBlocks.SPIRIT_INSTILLER);
//				entries.accept(SpectrumBlocks.CRYSTALLARIEUM);
//				entries.accept(SpectrumBlocks.CINDERHEARTH);
//				entries.accept(SpectrumBlocks.CRYSTAL_APOTHECARY);
//				entries.accept(SpectrumBlocks.COLOR_PICKER);
//
//				entries.accept(SpectrumBlocks.UPGRADE_SPEED);
//				entries.accept(SpectrumBlocks.UPGRADE_SPEED2);
//				entries.accept(SpectrumBlocks.UPGRADE_SPEED3);
//				entries.accept(SpectrumBlocks.UPGRADE_EFFICIENCY);
//				entries.accept(SpectrumBlocks.UPGRADE_EFFICIENCY2);
//				entries.accept(SpectrumBlocks.UPGRADE_YIELD);
//				entries.accept(SpectrumBlocks.UPGRADE_YIELD2);
//				entries.accept(SpectrumBlocks.UPGRADE_EXPERIENCE);
//				entries.accept(SpectrumBlocks.UPGRADE_EXPERIENCE2);
//
//				entries.accept(SpectrumBlocks.CONNECTION_NODE);
//				entries.accept(SpectrumBlocks.PROVIDER_NODE);
//				entries.accept(SpectrumBlocks.STORAGE_NODE);
//				entries.accept(SpectrumBlocks.SENDER_NODE);
//				entries.accept(SpectrumBlocks.GATHER_NODE);
//
//				entries.accept(SpectrumBlocks.LIGHT_LEVEL_DETECTOR);
//				entries.accept(SpectrumBlocks.WEATHER_DETECTOR);
//				entries.accept(SpectrumBlocks.ITEM_DETECTOR);
//				entries.accept(SpectrumBlocks.PLAYER_DETECTOR);
//				entries.accept(SpectrumBlocks.ENTITY_DETECTOR);
//				entries.accept(SpectrumBlocks.REDSTONE_TIMER);
//				entries.accept(SpectrumBlocks.REDSTONE_CALCULATOR);
//				entries.accept(SpectrumBlocks.REDSTONE_TRANSCEIVER);
//				entries.accept(SpectrumBlocks.REDSTONE_SAND);
//				entries.accept(SpectrumBlocks.ENDER_GLASS);
//				entries.accept(SpectrumBlocks.BLOCK_DETECTOR);
//				entries.accept(SpectrumBlocks.BLOCK_PLACER);
//				entries.accept(SpectrumBlocks.BLOCK_BREAKER);
//
//				entries.accept(SpectrumBlocks.HEARTBOUND_CHEST);
//				entries.accept(SpectrumBlocks.COMPACTING_CHEST);
//				entries.accept(SpectrumBlocks.RESTOCKING_CHEST);
//				entries.accept(SpectrumBlocks.BLACK_HOLE_CHEST);
//
//				entries.accept(SpectrumBlocks.ENDER_HOPPER);
//				entries.accept(SpectrumBlocks.ENDER_DROPPER);
//
//				entries.accept(SpectrumBlocks.PARTICLE_SPAWNER);
//
//				entries.accept(SpectrumBlocks.GLISTERING_MELON);
//				entries.accept(SpectrumBlocks.LAVA_SPONGE);
//				entries.accept(SpectrumBlocks.WET_LAVA_SPONGE);
//				entries.accept(SpectrumBlocks.ETHEREAL_PLATFORM);
//				entries.accept(SpectrumBlocks.UNIVERSE_SPYHOLE);
//				entries.accept(SpectrumBlocks.PRESENT);
//				entries.accept(SpectrumBlocks.TITRATION_BARREL);
//
//				entries.accept(SpectrumBlocks.INCANDESCENT_AMALGAM);
//				entries.accept(SpectrumBlocks.BEDROCK_ANVIL);
//				entries.accept(SpectrumBlocks.CRACKED_END_PORTAL_FRAME);
//
//				entries.accept(SpectrumBlocks.SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.TINTED_SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.RADIANT_SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.TOPAZ_SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.AMETHYST_SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.CITRINE_SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.ONYX_SEMI_PERMEABLE_GLASS);
//				entries.accept(SpectrumBlocks.MOONSTONE_SEMI_PERMEABLE_GLASS);
//
//				entries.accept(SpectrumBlocks.AXOLOTL_IDOL);
//				entries.accept(SpectrumBlocks.BAT_IDOL);
//				entries.accept(SpectrumBlocks.BEE_IDOL);
//				entries.accept(SpectrumBlocks.BLAZE_IDOL);
//				entries.accept(SpectrumBlocks.CAT_IDOL);
//				entries.accept(SpectrumBlocks.CHICKEN_IDOL);
//				entries.accept(SpectrumBlocks.COW_IDOL);
//				entries.accept(SpectrumBlocks.CREEPER_IDOL);
//				entries.accept(SpectrumBlocks.ENDER_DRAGON_IDOL);
//				entries.accept(SpectrumBlocks.ENDERMAN_IDOL);
//				entries.accept(SpectrumBlocks.ENDERMITE_IDOL);
//				entries.accept(SpectrumBlocks.EVOKER_IDOL);
//				entries.accept(SpectrumBlocks.FISH_IDOL);
//				entries.accept(SpectrumBlocks.FOX_IDOL);
//				entries.accept(SpectrumBlocks.GHAST_IDOL);
//				entries.accept(SpectrumBlocks.GLOW_SQUID_IDOL);
//				entries.accept(SpectrumBlocks.GOAT_IDOL);
//				entries.accept(SpectrumBlocks.GUARDIAN_IDOL);
//				entries.accept(SpectrumBlocks.HORSE_IDOL);
//				entries.accept(SpectrumBlocks.ILLUSIONER_IDOL);
//				entries.accept(SpectrumBlocks.OCELOT_IDOL);
//				entries.accept(SpectrumBlocks.PARROT_IDOL);
//				entries.accept(SpectrumBlocks.PHANTOM_IDOL);
//				entries.accept(SpectrumBlocks.PIG_IDOL);
//				entries.accept(SpectrumBlocks.PIGLIN_IDOL);
//				entries.accept(SpectrumBlocks.POLAR_BEAR_IDOL);
//				entries.accept(SpectrumBlocks.PUFFERFISH_IDOL);
//				entries.accept(SpectrumBlocks.RABBIT_IDOL);
//				entries.accept(SpectrumBlocks.SHEEP_IDOL);
//				entries.accept(SpectrumBlocks.SHULKER_IDOL);
//				entries.accept(SpectrumBlocks.SILVERFISH_IDOL);
//				entries.accept(SpectrumBlocks.SKELETON_IDOL);
//				entries.accept(SpectrumBlocks.SLIME_IDOL);
//				entries.accept(SpectrumBlocks.SNOW_GOLEM_IDOL);
//				entries.accept(SpectrumBlocks.SPIDER_IDOL);
//				entries.accept(SpectrumBlocks.SQUID_IDOL);
//				entries.accept(SpectrumBlocks.STRAY_IDOL);
//				entries.accept(SpectrumBlocks.STRIDER_IDOL);
//				entries.accept(SpectrumBlocks.TURTLE_IDOL);
//				entries.accept(SpectrumBlocks.WITCH_IDOL);
//				entries.accept(SpectrumBlocks.WITHER_IDOL);
//				entries.accept(SpectrumBlocks.WITHER_SKELETON_IDOL);
//				entries.accept(SpectrumBlocks.ZOMBIE_IDOL);
//			}).build();
//
//	public static final ItemSubGroup CUISINE = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_CUISINE, Component.translatable("itemGroup.spectrum.cuisine")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumItems.IMBRIFER_COOKBOOK);
//		entries.accept(SpectrumItems.IMPERIAL_COOKBOOK);
//		entries.accept(SpectrumItems.MELOCHITES_COOKBOOK_VOL_1);
//		entries.accept(SpectrumItems.MELOCHITES_COOKBOOK_VOL_2);
//		entries.accept(SpectrumItems.BREWERS_HANDBOOK);
//
//		entries.accept(SpectrumBlocks.SUGAR_STICK);
//		entries.accept(SpectrumBlocks.TOPAZ_SUGAR_STICK);
//		entries.accept(SpectrumBlocks.AMETHYST_SUGAR_STICK);
//		entries.accept(SpectrumBlocks.CITRINE_SUGAR_STICK);
//		entries.accept(SpectrumBlocks.ONYX_SUGAR_STICK);
//		entries.accept(SpectrumBlocks.MOONSTONE_SUGAR_STICK);
//		entries.accept(SpectrumItems.ROCK_CANDY);
//		entries.accept(SpectrumItems.TOPAZ_ROCK_CANDY);
//		entries.accept(SpectrumItems.AMETHYST_ROCK_CANDY);
//		entries.accept(SpectrumItems.CITRINE_ROCK_CANDY);
//		entries.accept(SpectrumItems.ONYX_ROCK_CANDY);
//		entries.accept(SpectrumItems.MOONSTONE_ROCK_CANDY);
//
//		entries.accept(SpectrumItems.TRIPLE_MEAT_POT_PIE);
//		entries.accept(SpectrumItems.KIMCHI);
//		entries.accept(SpectrumItems.CLOTTED_CREAM);
//		entries.accept(SpectrumItems.FRESH_CHOCOLATE);
//		entries.accept(SpectrumItems.BODACIOUS_BERRY_BAR);
//		entries.accept(SpectrumItems.HOT_CHOCOLATE);
//		entries.accept(SpectrumItems.RESTORATION_TEA);
//		entries.accept(SpectrumItems.GLISTERING_JELLY_TEA);
//		entries.accept(SpectrumItems.DEMON_TEA);
//		entries.accept(SpectrumItems.ENCHANTED_GOLDEN_CARROT);
//		entries.accept(SpectrumItems.JADE_JELLY);
//		entries.accept(SpectrumItems.JARAMEL);
//		entries.accept(SpectrumItems.MOONSTRUCK_NECTAR);
//		entries.accept(SpectrumItems.GLASS_PEACH);
//		entries.accept(SpectrumItems.BLOODBOIL_SYRUP);
//		entries.accept(SpectrumItems.SCONE);
//		entries.accept(SpectrumItems.STAR_CANDY);
//		entries.accept(SpectrumItems.PURPLE_STAR_CANDY);
//		entries.accept(SpectrumItems.LUCKY_ROLL);
//		entries.accept(SpectrumItems.HONEY_PASTRY);
//		entries.accept(SpectrumItems.JARAMEL_TART);
//		entries.accept(SpectrumItems.SALTED_JARAMEL_TART);
//		entries.accept(SpectrumItems.ASHEN_TART);
//		entries.accept(SpectrumItems.WEEPING_TART);
//		entries.accept(SpectrumItems.WHISPY_TART);
//		entries.accept(SpectrumItems.PUFF_TART);
//		entries.accept(SpectrumItems.JARAMEL_TRIFLE);
//		entries.accept(SpectrumItems.SALTED_JARAMEL_TRIFLE);
//		entries.accept(SpectrumItems.MONSTER_TRIFLE);
//		entries.accept(SpectrumItems.DEMON_TRIFLE);
//		entries.accept(SpectrumItems.MYCEYLON);
//		entries.accept(SpectrumItems.MYCEYLON_APPLE_PIE);
//		entries.accept(SpectrumItems.MYCEYLON_PUMPKIN_PIE);
//		entries.accept(SpectrumItems.MYCEYLON_COOKIE);
//		entries.accept(SpectrumItems.ALOE_LEAF);
//		entries.accept(SpectrumItems.SAWBLADE_HOLLY_BERRY);
//		entries.accept(SpectrumItems.PRICKLY_BAYLEAF);
//		entries.accept(SpectrumItems.TRIPLE_MEAT_POT_STEW);
//		entries.accept(SpectrumItems.DRAGONBONE_BROTH);
//		entries.accept(SpectrumItems.BAGNUN);
//		entries.accept(SpectrumItems.BANYASH);
//		entries.accept(SpectrumItems.BERLINER);
//		entries.accept(SpectrumItems.BRISTLE_MEAD);
//		entries.accept(SpectrumItems.CHAUVE_SOURIS_AU_VIN);
//		entries.accept(SpectrumItems.CRAWFISH);
//		entries.accept(SpectrumItems.CRAWFISH_COCKTAIL);
//		entries.accept(SpectrumItems.CREAM_PASTRY);
//		entries.accept(SpectrumItems.FADED_KOI);
//		entries.accept(SpectrumItems.FISHCAKE);
//		entries.accept(SpectrumItems.LIZARD_MEAT);
//		entries.accept(SpectrumItems.GOLDEN_BRISTLE_TEA);
//		entries.accept(SpectrumItems.HARE_ROAST);
//		entries.accept(SpectrumItems.JUNKET);
//		entries.accept(SpectrumItems.KOI);
//		entries.accept(SpectrumItems.MEATLOAF);
//		entries.accept(SpectrumItems.MEATLOAF_SANDWICH);
//		entries.accept(SpectrumItems.MELLOW_SHALLOT_SOUP);
//		entries.accept(SpectrumItems.PEACHES_FLAMBE);
//		entries.accept(SpectrumItems.PEACH_CREAM);
//		entries.accept(SpectrumItems.PEACH_JAM);
//		entries.accept(SpectrumItems.RABBIT_CREAM_PIE);
//		entries.accept(SpectrumItems.SEDATIVES);
//		entries.accept(SpectrumItems.SLUSHSLIDE);
//		entries.accept(SpectrumItems.SURSTROMMING);
//		entries.accept(SpectrumItems.MORCHELLA);
//		entries.accept(SpectrumItems.NECTERED_VIOGNIER);
//		entries.accept(SpectrumItems.FREIGEIST);
//
//		// adding all beverages from recipes
//		if (SpectrumCommon.minecraftServer != null) {
//			for (ITitrationBarrelRecipe recipe : SpectrumCommon.minecraftServer.getRecipeManager().getAllRecipesFor(SpectrumRecipeTypes.TITRATION_BARREL)) {
//				ItemStack output = recipe.getResultItem(SpectrumCommon.minecraftServer.registryAccess()).copy();
//				if (output.getItem() instanceof VariantBeverageItem) {
//					output.setCount(1);
//					entries.accept(output);
//				}
//			}
//		}
//
//		entries.accept(SpectrumItems.PURE_ALCOHOL);
//		entries.accept(SpectrumItems.REPRISE);
//		entries.accept(SpectrumItems.SUSPICIOUS_BREW);
//		entries.accept(SpectrumItems.JADE_WINE);
//		entries.accept(SpectrumItems.CHRYSOCOLLA);
//		entries.accept(SpectrumItems.AQUA_REGIA);
//	}).build();
//
//	public static final ItemSubGroup RESOURCES = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_RESOURCES, Component.translatable("itemGroup.spectrum.resources")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumItems.TOPAZ_SHARD);
//		entries.accept(Items.AMETHYST_SHARD);
//		entries.accept(SpectrumItems.CITRINE_SHARD);
//		entries.accept(SpectrumItems.ONYX_SHARD);
//		entries.accept(SpectrumItems.MOONSTONE_SHARD);
//
//		entries.accept(SpectrumBlocks.TOPAZ_BLOCK);
//		entries.accept(Blocks.AMETHYST_BLOCK);
//		entries.accept(SpectrumBlocks.CITRINE_BLOCK);
//		entries.accept(SpectrumBlocks.ONYX_BLOCK);
//		entries.accept(SpectrumBlocks.MOONSTONE_BLOCK);
//
//		entries.accept(SpectrumItems.TOPAZ_POWDER);
//		entries.accept(SpectrumItems.AMETHYST_POWDER);
//		entries.accept(SpectrumItems.CITRINE_POWDER);
//		entries.accept(SpectrumItems.ONYX_POWDER);
//		entries.accept(SpectrumItems.MOONSTONE_POWDER);
//
//		entries.accept(SpectrumBlocks.TOPAZ_POWDER_BLOCK);
//		entries.accept(SpectrumBlocks.AMETHYST_POWDER_BLOCK);
//		entries.accept(SpectrumBlocks.CITRINE_POWDER_BLOCK);
//		entries.accept(SpectrumBlocks.ONYX_POWDER_BLOCK);
//		entries.accept(SpectrumBlocks.MOONSTONE_POWDER_BLOCK);
//
//		entries.accept(SpectrumBlocks.BUDDING_TOPAZ);
//		entries.accept(Blocks.BUDDING_AMETHYST);
//		entries.accept(SpectrumBlocks.BUDDING_CITRINE);
//		entries.accept(SpectrumBlocks.BUDDING_ONYX);
//		entries.accept(SpectrumBlocks.BUDDING_MOONSTONE);
//
//		entries.accept(SpectrumBlocks.SMALL_TOPAZ_BUD);
//		entries.accept(SpectrumBlocks.MEDIUM_TOPAZ_BUD);
//		entries.accept(SpectrumBlocks.LARGE_TOPAZ_BUD);
//		entries.accept(SpectrumBlocks.TOPAZ_CLUSTER);
//
//		entries.accept(Blocks.SMALL_AMETHYST_BUD);
//		entries.accept(Blocks.MEDIUM_AMETHYST_BUD);
//		entries.accept(Blocks.LARGE_AMETHYST_BUD);
//		entries.accept(Blocks.AMETHYST_CLUSTER);
//
//		entries.accept(SpectrumBlocks.SMALL_CITRINE_BUD);
//		entries.accept(SpectrumBlocks.MEDIUM_CITRINE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_CITRINE_BUD);
//		entries.accept(SpectrumBlocks.CITRINE_CLUSTER);
//
//		entries.accept(SpectrumBlocks.SMALL_ONYX_BUD);
//		entries.accept(SpectrumBlocks.MEDIUM_ONYX_BUD);
//		entries.accept(SpectrumBlocks.LARGE_ONYX_BUD);
//		entries.accept(SpectrumBlocks.ONYX_CLUSTER);
//
//		entries.accept(SpectrumBlocks.SMALL_MOONSTONE_BUD);
//		entries.accept(SpectrumBlocks.MEDIUM_MOONSTONE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_MOONSTONE_BUD);
//		entries.accept(SpectrumBlocks.MOONSTONE_CLUSTER);
//
//		entries.accept(SpectrumBlocks.TOPAZ_ORE);
//		entries.accept(SpectrumBlocks.AMETHYST_ORE);
//		entries.accept(SpectrumBlocks.CITRINE_ORE);
//		entries.accept(SpectrumBlocks.ONYX_ORE);
//		entries.accept(SpectrumBlocks.MOONSTONE_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_TOPAZ_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_AMETHYST_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_CITRINE_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_ONYX_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_MOONSTONE_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_TOPAZ_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_AMETHYST_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_CITRINE_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_ONYX_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_MOONSTONE_ORE);
//		entries.accept(SpectrumBlocks.SHIMMERSTONE_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_SHIMMERSTONE_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_SHIMMERSTONE_ORE);
//		entries.accept(SpectrumBlocks.AZURITE_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_AZURITE_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_AZURITE_ORE);
//		entries.accept(SpectrumBlocks.STRATINE_ORE);
//		entries.accept(SpectrumBlocks.PALTAERIA_ORE);
//
//		entries.accept(SpectrumBlocks.BLACKSLAG_COAL_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_COPPER_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_IRON_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_GOLD_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_DIAMOND_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_REDSTONE_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_LAPIS_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_EMERALD_ORE);
//
//		entries.accept(SpectrumItems.BISMUTH_FLAKE);
//		entries.accept(SpectrumBlocks.SMALL_BISMUTH_BUD);
//		entries.accept(SpectrumBlocks.LARGE_BISMUTH_BUD);
//		entries.accept(SpectrumBlocks.BISMUTH_CLUSTER);
//		entries.accept(SpectrumItems.BISMUTH_CRYSTAL);
//
//		entries.accept(SpectrumBlocks.MALACHITE_ORE);
//		entries.accept(SpectrumBlocks.DEEPSLATE_MALACHITE_ORE);
//		entries.accept(SpectrumBlocks.BLACKSLAG_MALACHITE_ORE);
//		entries.accept(SpectrumItems.RAW_MALACHITE);
//		entries.accept(SpectrumBlocks.SMALL_MALACHITE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_MALACHITE_BUD);
//		entries.accept(SpectrumBlocks.MALACHITE_CLUSTER);
//		entries.accept(SpectrumItems.REFINED_MALACHITE);
//
//		entries.accept(SpectrumItems.RAW_AZURITE);
//		entries.accept(SpectrumBlocks.SMALL_AZURITE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_AZURITE_BUD);
//		entries.accept(SpectrumBlocks.AZURITE_CLUSTER);
//		entries.accept(SpectrumItems.REFINED_AZURITE);
//
//		entries.accept(SpectrumItems.RAW_BLOODSTONE);
//		entries.accept(SpectrumBlocks.SMALL_BLOODSTONE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_BLOODSTONE_BUD);
//		entries.accept(SpectrumBlocks.BLOODSTONE_CLUSTER);
//		entries.accept(SpectrumItems.REFINED_BLOODSTONE);
//
//		entries.accept(SpectrumItems.FROSTBITE_ESSENCE);
//		entries.accept(SpectrumBlocks.FROSTBITE_CRYSTAL);
//		entries.accept(SpectrumItems.INCANDESCENT_ESSENCE);
//		entries.accept(SpectrumBlocks.BLAZING_CRYSTAL);
//
//		entries.accept(SpectrumBlocks.CLOVER);
//		entries.accept(SpectrumBlocks.FOUR_LEAF_CLOVER);
//		entries.accept(SpectrumItems.BLOOD_ORCHID_PETAL);
//		entries.accept(SpectrumBlocks.BLOOD_ORCHID);
//		entries.accept(SpectrumBlocks.QUITOXIC_REEDS);
//		entries.accept(SpectrumItems.QUITOXIC_POWDER);
//
//		entries.accept(SpectrumItems.AMARANTH_GRAINS);
//		entries.accept(SpectrumBlocks.AMARANTH_BUSHEL);
//		entries.accept(SpectrumItems.GLISTERING_MELON_SEEDS);
//
//		entries.accept(SpectrumBlocks.GLISTERING_SHOOTING_STAR);
//		entries.accept(SpectrumBlocks.FIERY_SHOOTING_STAR);
//		entries.accept(SpectrumBlocks.COLORFUL_SHOOTING_STAR);
//		entries.accept(SpectrumBlocks.PRISTINE_SHOOTING_STAR);
//		entries.accept(SpectrumBlocks.GEMSTONE_SHOOTING_STAR);
//		entries.accept(SpectrumItems.STARDUST);
//		entries.accept(SpectrumBlocks.STARDUST_BLOCK);
//		entries.accept(SpectrumItems.STAR_FRAGMENT);
//
//		entries.accept(SpectrumBlocks.RADIATING_ENDER);
//
//		entries.accept(SpectrumItems.WHITE_PIGMENT);
//		entries.accept(SpectrumItems.ORANGE_PIGMENT);
//		entries.accept(SpectrumItems.MAGENTA_PIGMENT);
//		entries.accept(SpectrumItems.LIGHT_BLUE_PIGMENT);
//		entries.accept(SpectrumItems.YELLOW_PIGMENT);
//		entries.accept(SpectrumItems.LIME_PIGMENT);
//		entries.accept(SpectrumItems.PINK_PIGMENT);
//		entries.accept(SpectrumItems.GRAY_PIGMENT);
//		entries.accept(SpectrumItems.LIGHT_GRAY_PIGMENT);
//		entries.accept(SpectrumItems.CYAN_PIGMENT);
//		entries.accept(SpectrumItems.PURPLE_PIGMENT);
//		entries.accept(SpectrumItems.BLUE_PIGMENT);
//		entries.accept(SpectrumItems.BROWN_PIGMENT);
//		entries.accept(SpectrumItems.GREEN_PIGMENT);
//		entries.accept(SpectrumItems.RED_PIGMENT);
//		entries.accept(SpectrumItems.BLACK_PIGMENT);
//
//		entries.accept(SpectrumItems.VEGETAL);
//		entries.accept(SpectrumItems.NEOLITH);
//		entries.accept(SpectrumItems.BEDROCK_DUST);
//		entries.accept(SpectrumItems.MIDNIGHT_ABERRATION);
//		entries.accept(SpectrumItems.MIDNIGHT_ABERRATION.getStableStack());
//		entries.accept(SpectrumItems.MIDNIGHT_CHIP);
//
//		entries.accept(SpectrumItems.SHIMMERSTONE_GEM);
//		entries.accept(SpectrumItems.PALTAERIA_FRAGMENTS);
//		entries.accept(SpectrumItems.PALTAERIA_GEM);
//		entries.accept(SpectrumItems.STRATINE_FRAGMENTS);
//		entries.accept(SpectrumItems.STRATINE_GEM);
//
//		entries.accept(SpectrumItems.HIBERNATING_JADE_VINE_BULB);
//		entries.accept(SpectrumItems.GERMINATED_JADE_VINE_BULB);
//		entries.accept(SpectrumItems.JADE_VINE_PETALS);
//		entries.accept(SpectrumItems.NEPHRITE_BLOSSOM_BULB);
//		entries.accept(SpectrumItems.JADEITE_LOTUS_BULB);
//		entries.accept(SpectrumItems.JADEITE_PETALS);
//
//		entries.accept(SpectrumItems.MERMAIDS_GEM);
//		entries.accept(SpectrumItems.STORM_STONE);
//		entries.accept(SpectrumItems.DOOMBLOOM_SEED);
//		entries.accept(SpectrumItems.EFFULGENT_FEATHER);
//		entries.accept(SpectrumItems.DRAGONBONE_CHUNK);
//		entries.accept(SpectrumItems.BONE_ASH);
//		entries.accept(SpectrumItems.DOWNSTONE_FRAGMENTS);
//		entries.accept(SpectrumItems.RESONANCE_SHARD);
//		entries.accept(SpectrumItems.MOONSTONE_CORE);
//
//		entries.accept(SpectrumItems.LIQUID_CRYSTAL_BUCKET);
//		entries.accept(SpectrumItems.MUD_BUCKET);
//		entries.accept(SpectrumItems.MIDNIGHT_SOLUTION_BUCKET);
//		entries.accept(SpectrumItems.DRAGONROT_BUCKET);
//	}).build();
//
//	public static final ItemSubGroup PURE_RESOURCES = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_PURE_RESOURCES, Component.translatable("itemGroup.spectrum.pure_resources")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumItems.PURE_COAL);
//		entries.accept(SpectrumBlocks.SMALL_COAL_BUD);
//		entries.accept(SpectrumBlocks.LARGE_COAL_BUD);
//		entries.accept(SpectrumBlocks.COAL_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_COAL_BLOCK);
//		entries.accept(SpectrumItems.PURE_COPPER);
//		entries.accept(SpectrumBlocks.SMALL_COPPER_BUD);
//		entries.accept(SpectrumBlocks.LARGE_COPPER_BUD);
//		entries.accept(SpectrumBlocks.COPPER_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_COPPER_BLOCK);
//		entries.accept(SpectrumItems.PURE_IRON);
//		entries.accept(SpectrumBlocks.SMALL_IRON_BUD);
//		entries.accept(SpectrumBlocks.LARGE_IRON_BUD);
//		entries.accept(SpectrumBlocks.IRON_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_IRON_BLOCK);
//		entries.accept(SpectrumItems.PURE_GOLD);
//		entries.accept(SpectrumBlocks.SMALL_GOLD_BUD);
//		entries.accept(SpectrumBlocks.LARGE_GOLD_BUD);
//		entries.accept(SpectrumBlocks.GOLD_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_GOLD_BLOCK);
//		entries.accept(SpectrumItems.PURE_LAPIS);
//		entries.accept(SpectrumBlocks.SMALL_LAPIS_BUD);
//		entries.accept(SpectrumBlocks.LARGE_LAPIS_BUD);
//		entries.accept(SpectrumBlocks.LAPIS_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_LAPIS_BLOCK);
//		entries.accept(SpectrumItems.PURE_REDSTONE);
//		entries.accept(SpectrumBlocks.SMALL_REDSTONE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_REDSTONE_BUD);
//		entries.accept(SpectrumBlocks.REDSTONE_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_REDSTONE_BLOCK);
//		entries.accept(SpectrumItems.PURE_DIAMOND);
//		entries.accept(SpectrumBlocks.SMALL_DIAMOND_BUD);
//		entries.accept(SpectrumBlocks.LARGE_DIAMOND_BUD);
//		entries.accept(SpectrumBlocks.DIAMOND_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_DIAMOND_BLOCK);
//		entries.accept(SpectrumItems.PURE_EMERALD);
//		entries.accept(SpectrumBlocks.SMALL_EMERALD_BUD);
//		entries.accept(SpectrumBlocks.LARGE_EMERALD_BUD);
//		entries.accept(SpectrumBlocks.EMERALD_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_EMERALD_BLOCK);
//
//		entries.accept(SpectrumItems.PURE_PRISMARINE);
//		entries.accept(SpectrumBlocks.SMALL_PRISMARINE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_PRISMARINE_BUD);
//		entries.accept(SpectrumBlocks.PRISMARINE_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_PRISMARINE_BLOCK);
//
//		entries.accept(SpectrumItems.PURE_QUARTZ);
//		entries.accept(SpectrumBlocks.SMALL_QUARTZ_BUD);
//		entries.accept(SpectrumBlocks.LARGE_QUARTZ_BUD);
//		entries.accept(SpectrumBlocks.QUARTZ_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_QUARTZ_BLOCK);
//		entries.accept(SpectrumItems.PURE_GLOWSTONE);
//		entries.accept(SpectrumBlocks.SMALL_GLOWSTONE_BUD);
//		entries.accept(SpectrumBlocks.LARGE_GLOWSTONE_BUD);
//		entries.accept(SpectrumBlocks.GLOWSTONE_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_GLOWSTONE_BLOCK);
//		entries.accept(SpectrumItems.PURE_NETHERITE_SCRAP);
//		entries.accept(SpectrumBlocks.SMALL_NETHERITE_SCRAP_BUD);
//		entries.accept(SpectrumBlocks.LARGE_NETHERITE_SCRAP_BUD);
//		entries.accept(SpectrumBlocks.NETHERITE_SCRAP_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_NETHERITE_SCRAP_BLOCK);
//
//		entries.accept(SpectrumItems.PURE_ECHO);
//		entries.accept(SpectrumBlocks.SMALL_ECHO_BUD);
//		entries.accept(SpectrumBlocks.LARGE_ECHO_BUD);
//		entries.accept(SpectrumBlocks.ECHO_CLUSTER);
//		entries.accept(SpectrumBlocks.PURE_ECHO_BLOCK);
//
//		if (SpectrumIntegrationPacks.isIntegrationPackActive(SpectrumIntegrationPacks.AE2_ID)) {
//			entries.accept(AE2Compat.PURE_CERTUS_QUARTZ);
//			entries.accept(AE2Compat.SMALL_CERTUS_QUARTZ_BUD);
//			entries.accept(AE2Compat.LARGE_CERTUS_QUARTZ_BUD);
//			entries.accept(AE2Compat.CERTUS_QUARTZ_CLUSTER);
//			entries.accept(AE2Compat.PURE_CERTUS_QUARTZ_BLOCK);
//
//			entries.accept(AE2Compat.PURE_FLUIX);
//			entries.accept(AE2Compat.SMALL_FLUIX_BUD);
//			entries.accept(AE2Compat.LARGE_FLUIX_BUD);
//			entries.accept(AE2Compat.FLUIX_CLUSTER);
//			entries.accept(AE2Compat.PURE_FLUIX_BLOCK);
//		}
//
//		if (SpectrumIntegrationPacks.isIntegrationPackActive(SpectrumIntegrationPacks.GOBBER_ID)) {
//			entries.accept(GobberCompat.PURE_GLOBETTE);
//			entries.accept(GobberCompat.SMALL_GLOBETTE_BUD);
//			entries.accept(GobberCompat.LARGE_GLOBETTE_BUD);
//			entries.accept(GobberCompat.GLOBETTE_CLUSTER);
//			entries.accept(GobberCompat.PURE_GLOBETTE_BLOCK);
//
//			entries.accept(GobberCompat.PURE_GLOBETTE_NETHER);
//			entries.accept(GobberCompat.SMALL_GLOBETTE_NETHER_BUD);
//			entries.accept(GobberCompat.LARGE_GLOBETTE_NETHER_BUD);
//			entries.accept(GobberCompat.GLOBETTE_NETHER_CLUSTER);
//			entries.accept(GobberCompat.PURE_GLOBETTE_NETHER_BLOCK);
//
//			entries.accept(GobberCompat.PURE_GLOBETTE_END);
//			entries.accept(GobberCompat.SMALL_GLOBETTE_END_BUD);
//			entries.accept(GobberCompat.LARGE_GLOBETTE_END_BUD);
//			entries.accept(GobberCompat.GLOBETTE_END_CLUSTER);
//			entries.accept(GobberCompat.PURE_GLOBETTE_END_BLOCK);
//		}
//
//	}).build();
//
//	public static final ItemSubGroup BLOCKS = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_BLOCKS, Component.translatable("itemGroup.spectrum.blocks")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumBlocks.SMOOTH_BASALT_SLAB);
//		entries.accept(SpectrumBlocks.SMOOTH_BASALT_WALL);
//		entries.accept(SpectrumBlocks.SMOOTH_BASALT_STAIRS);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_PILLAR);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_CREST);
//		entries.accept(SpectrumBlocks.CHISELED_POLISHED_BASALT);
//		entries.accept(SpectrumBlocks.NOTCHED_POLISHED_BASALT);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_SLAB);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_WALL);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_STAIRS);
//		entries.accept(SpectrumBlocks.BASALT_BRICKS);
//		entries.accept(SpectrumBlocks.BASALT_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.BASALT_BRICK_WALL);
//		entries.accept(SpectrumBlocks.BASALT_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.CRACKED_BASALT_BRICKS);
//		entries.accept(SpectrumBlocks.BASALT_TILES);
//		entries.accept(SpectrumBlocks.BASALT_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.BASALT_TILE_SLAB);
//		entries.accept(SpectrumBlocks.BASALT_TILE_WALL);
//		entries.accept(SpectrumBlocks.PLANED_BASALT);
//		entries.accept(SpectrumBlocks.CRACKED_BASALT_TILES);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_BUTTON);
//		entries.accept(SpectrumBlocks.POLISHED_BASALT_PRESSURE_PLATE);
//
//		entries.accept(SpectrumBlocks.CALCITE_SLAB);
//		entries.accept(SpectrumBlocks.CALCITE_WALL);
//		entries.accept(SpectrumBlocks.CALCITE_STAIRS);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_PILLAR);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_CREST);
//		entries.accept(SpectrumBlocks.CHISELED_POLISHED_CALCITE);
//		entries.accept(SpectrumBlocks.NOTCHED_POLISHED_CALCITE);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_SLAB);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_WALL);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_STAIRS);
//		entries.accept(SpectrumBlocks.CALCITE_BRICKS);
//		entries.accept(SpectrumBlocks.CALCITE_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.CALCITE_BRICK_WALL);
//		entries.accept(SpectrumBlocks.CALCITE_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.CRACKED_CALCITE_BRICKS);
//		entries.accept(SpectrumBlocks.CALCITE_TILES);
//		entries.accept(SpectrumBlocks.CALCITE_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.CALCITE_TILE_SLAB);
//		entries.accept(SpectrumBlocks.CALCITE_TILE_WALL);
//		entries.accept(SpectrumBlocks.PLANED_CALCITE);
//		entries.accept(SpectrumBlocks.CRACKED_CALCITE_TILES);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_BUTTON);
//		entries.accept(SpectrumBlocks.POLISHED_CALCITE_PRESSURE_PLATE);
//
//		entries.accept(SpectrumBlocks.BLACKSLAG);
//		entries.accept(SpectrumBlocks.BLACKSLAG_SLAB);
//		entries.accept(SpectrumBlocks.BLACKSLAG_WALL);
//		entries.accept(SpectrumBlocks.BLACKSLAG_STAIRS);
//		entries.accept(SpectrumBlocks.COBBLED_BLACKSLAG);
//		entries.accept(SpectrumBlocks.COBBLED_BLACKSLAG_STAIRS);
//		entries.accept(SpectrumBlocks.COBBLED_BLACKSLAG_SLAB);
//		entries.accept(SpectrumBlocks.COBBLED_BLACKSLAG_WALL);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG_STAIRS);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG_SLAB);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG_WALL);
//		entries.accept(SpectrumBlocks.BLACKSLAG_TILES);
//		entries.accept(SpectrumBlocks.BLACKSLAG_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.BLACKSLAG_TILE_SLAB);
//		entries.accept(SpectrumBlocks.BLACKSLAG_TILE_WALL);
//		entries.accept(SpectrumBlocks.CRACKED_BLACKSLAG_TILES);
//		entries.accept(SpectrumBlocks.BLACKSLAG_BRICKS);
//		entries.accept(SpectrumBlocks.BLACKSLAG_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.BLACKSLAG_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.BLACKSLAG_BRICK_WALL);
//		entries.accept(SpectrumBlocks.CRACKED_BLACKSLAG_BRICKS);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG_PILLAR);
//		entries.accept(SpectrumBlocks.CHISELED_POLISHED_BLACKSLAG);
//		entries.accept(SpectrumBlocks.ANCIENT_CHISELED_POLISHED_BLACKSLAG);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG_BUTTON);
//		entries.accept(SpectrumBlocks.POLISHED_BLACKSLAG_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.INFESTED_BLACKSLAG);
//		entries.accept(SpectrumBlocks.SHALE_CLAY);
//		entries.accept(SpectrumBlocks.TILLED_SHALE_CLAY);
//		entries.accept(SpectrumBlocks.POLISHED_SHALE_CLAY);
//		entries.accept(SpectrumBlocks.EXPOSED_POLISHED_SHALE_CLAY);
//		entries.accept(SpectrumBlocks.WEATHERED_POLISHED_SHALE_CLAY);
//		entries.accept(SpectrumBlocks.POLISHED_SHALE_CLAY_STAIRS);
//		entries.accept(SpectrumBlocks.POLISHED_SHALE_CLAY_SLAB);
//		entries.accept(SpectrumBlocks.EXPOSED_POLISHED_SHALE_CLAY_STAIRS);
//		entries.accept(SpectrumBlocks.EXPOSED_POLISHED_SHALE_CLAY_SLAB);
//		entries.accept(SpectrumBlocks.WEATHERED_POLISHED_SHALE_CLAY_STAIRS);
//		entries.accept(SpectrumBlocks.WEATHERED_POLISHED_SHALE_CLAY_SLAB);
//		entries.accept(SpectrumBlocks.SHALE_CLAY_BRICKS);
//		entries.accept(SpectrumBlocks.EXPOSED_SHALE_CLAY_BRICKS);
//		entries.accept(SpectrumBlocks.WEATHERED_SHALE_CLAY_BRICKS);
//		entries.accept(SpectrumBlocks.SHALE_CLAY_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.SHALE_CLAY_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.EXPOSED_SHALE_CLAY_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.EXPOSED_SHALE_CLAY_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.WEATHERED_SHALE_CLAY_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.WEATHERED_SHALE_CLAY_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.SHALE_CLAY_TILES);
//		entries.accept(SpectrumBlocks.EXPOSED_SHALE_CLAY_TILES);
//		entries.accept(SpectrumBlocks.WEATHERED_SHALE_CLAY_TILES);
//		entries.accept(SpectrumBlocks.SHALE_CLAY_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.SHALE_CLAY_TILE_SLAB);
//		entries.accept(SpectrumBlocks.EXPOSED_SHALE_CLAY_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.EXPOSED_SHALE_CLAY_TILE_SLAB);
//		entries.accept(SpectrumBlocks.WEATHERED_SHALE_CLAY_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.WEATHERED_SHALE_CLAY_TILE_SLAB);
//		entries.accept(SpectrumBlocks.POLISHED_BONE_ASH);
//		entries.accept(SpectrumBlocks.POLISHED_BONE_ASH_SLAB);
//		entries.accept(SpectrumBlocks.POLISHED_BONE_ASH_STAIRS);
//		entries.accept(SpectrumBlocks.POLISHED_BONE_ASH_WALL);
//		entries.accept(SpectrumBlocks.BONE_ASH_BRICKS);
//		entries.accept(SpectrumBlocks.BONE_ASH_BRICK_SLAB);
//		entries.accept(SpectrumBlocks.BONE_ASH_BRICK_STAIRS);
//		entries.accept(SpectrumBlocks.BONE_ASH_BRICK_WALL);
//		entries.accept(SpectrumBlocks.BONE_ASH_TILES);
//		entries.accept(SpectrumBlocks.BONE_ASH_TILE_SLAB);
//		entries.accept(SpectrumBlocks.BONE_ASH_TILE_STAIRS);
//		entries.accept(SpectrumBlocks.BONE_ASH_TILE_WALL);
//		entries.accept(SpectrumBlocks.POLISHED_BONE_ASH_PILLAR);
//		entries.accept(SpectrumBlocks.BONE_ASH_SHINGLES);
//		entries.accept(SpectrumBlocks.BLACK_MATERIA);
//		entries.accept(SpectrumBlocks.SLUSH);
//		entries.accept(SpectrumBlocks.TILLED_SLUSH);
//		entries.accept(SpectrumBlocks.BLACK_SLUDGE);
//
//		entries.accept(SpectrumItems.PYRITE_CHUNK);
//		entries.accept(SpectrumBlocks.PYRITE);
//		entries.accept(SpectrumBlocks.PYRITE_PILE);
//		entries.accept(SpectrumBlocks.PYRITE_TILE);
//		entries.accept(SpectrumBlocks.PYRITE_PLATING);
//		entries.accept(SpectrumBlocks.PYRITE_TUBING);
//		entries.accept(SpectrumBlocks.PYRITE_RELIEF);
//		entries.accept(SpectrumBlocks.PYRITE_STACK);
//		entries.accept(SpectrumBlocks.PYRITE_PANNELING);
//		entries.accept(SpectrumBlocks.PYRITE_VENT);
//		entries.accept(SpectrumBlocks.PYRITE_RIPPER);
//
//		entries.accept(SpectrumBlocks.DRAGONBONE);
//		entries.accept(SpectrumBlocks.CRACKED_DRAGONBONE);
//		entries.accept(SpectrumBlocks.SAWBLADE_GRASS);
//		entries.accept(SpectrumBlocks.OVERGROWN_BLACKSLAG);
//		entries.accept(SpectrumBlocks.SHIMMEL);
//		entries.accept(SpectrumBlocks.ROTTEN_GROUND);
//		entries.accept(SpectrumBlocks.SLATE_NOXSHROOM);
//		entries.accept(SpectrumBlocks.SLATE_NOXCAP_BLOCK);
//		entries.accept(SpectrumBlocks.SLATE_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.STRIPPED_SLATE_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.SLATE_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.STRIPPED_SLATE_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.SLATE_NOXCAP_GILLS);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_PLANKS);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_STAIRS);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_SLAB);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_FENCE);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_FENCE_GATE);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_DOOR);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_TRAPDOOR);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_BUTTON);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_BEAM);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_AMPHORA);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_LANTERN);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_LIGHT);
//		entries.accept(SpectrumBlocks.SLATE_NOXWOOD_LAMP);
//		entries.accept(SpectrumBlocks.EBONY_NOXSHROOM);
//		entries.accept(SpectrumBlocks.EBONY_NOXCAP_BLOCK);
//		entries.accept(SpectrumBlocks.EBONY_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.STRIPPED_EBONY_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.EBONY_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.STRIPPED_EBONY_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.EBONY_NOXCAP_GILLS);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_PLANKS);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_STAIRS);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_SLAB);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_FENCE);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_FENCE_GATE);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_DOOR);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_TRAPDOOR);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_BUTTON);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_BEAM);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_AMPHORA);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_LANTERN);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_LIGHT);
//		entries.accept(SpectrumBlocks.EBONY_NOXWOOD_LAMP);
//		entries.accept(SpectrumBlocks.IVORY_NOXSHROOM);
//		entries.accept(SpectrumBlocks.IVORY_NOXCAP_BLOCK);
//		entries.accept(SpectrumBlocks.IVORY_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.STRIPPED_IVORY_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.IVORY_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.STRIPPED_IVORY_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.IVORY_NOXCAP_GILLS);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_PLANKS);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_STAIRS);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_SLAB);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_FENCE);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_FENCE_GATE);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_DOOR);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_TRAPDOOR);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_BUTTON);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_BEAM);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_AMPHORA);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_LANTERN);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_LIGHT);
//		entries.accept(SpectrumBlocks.IVORY_NOXWOOD_LAMP);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXSHROOM);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXCAP_BLOCK);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.STRIPPED_CHESTNUT_NOXCAP_STEM);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.STRIPPED_CHESTNUT_NOXCAP_HYPHAE);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXCAP_GILLS);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_PLANKS);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_STAIRS);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_SLAB);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_FENCE);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_FENCE_GATE);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_DOOR);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_TRAPDOOR);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_BUTTON);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_BEAM);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_AMPHORA);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_LANTERN);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_LIGHT);
//		entries.accept(SpectrumBlocks.CHESTNUT_NOXWOOD_LAMP);
//		entries.accept(SpectrumBlocks.SMALL_RED_DRAGONJAG);
//		entries.accept(SpectrumBlocks.SMALL_YELLOW_DRAGONJAG);
//		entries.accept(SpectrumBlocks.SMALL_PINK_DRAGONJAG);
//		entries.accept(SpectrumBlocks.SMALL_PURPLE_DRAGONJAG);
//		entries.accept(SpectrumBlocks.SMALL_BLACK_DRAGONJAG);
//		entries.accept(SpectrumBlocks.BRISTLE_SPROUTS);
//		entries.accept(SpectrumBlocks.DOOMBLOOM);
//		entries.accept(SpectrumBlocks.SNAPPING_IVY);
//		entries.accept(SpectrumBlocks.HUMMINGSTONE);
//		entries.accept(SpectrumBlocks.HUMMINGSTONE_GLASS);
//		entries.accept(SpectrumBlocks.CLEAR_HUMMINGSTONE_GLASS);
//		entries.accept(SpectrumBlocks.NEPHRITE_BLOSSOM_STEM);
//		entries.accept(SpectrumBlocks.NEPHRITE_BLOSSOM_LEAVES);
//		entries.accept(SpectrumBlocks.JADEITE_LOTUS_STEM);
//		entries.accept(SpectrumBlocks.JADEITE_LOTUS_FLOWER);
//	}).build();
//
//	public static final ItemSubGroup DECORATION = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_DECORATION, Component.translatable("itemGroup.spectrum.decoration")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumBlocks.TOPAZ_STORAGE_BLOCK);
//		entries.accept(SpectrumBlocks.AMETHYST_STORAGE_BLOCK);
//		entries.accept(SpectrumBlocks.CITRINE_STORAGE_BLOCK);
//		entries.accept(SpectrumBlocks.ONYX_STORAGE_BLOCK);
//		entries.accept(SpectrumBlocks.MOONSTONE_STORAGE_BLOCK);
//
//		entries.accept(SpectrumBlocks.VEGETAL_BLOCK);
//		entries.accept(SpectrumBlocks.NEOLITH_BLOCK);
//		entries.accept(SpectrumBlocks.BEDROCK_STORAGE_BLOCK);
//
//		entries.accept(SpectrumBlocks.SHIMMERSTONE_BLOCK);
//		entries.accept(SpectrumBlocks.AZURITE_BLOCK);
//		entries.accept(SpectrumBlocks.MALACHITE_BLOCK);
//		entries.accept(SpectrumBlocks.BLOODSTONE_BLOCK);
//		entries.accept(SpectrumBlocks.BISMUTH_BLOCK);
//
//		entries.accept(SpectrumBlocks.STRATINE_FRAGMENT_BLOCK);
//		entries.accept(SpectrumBlocks.PALTAERIA_FRAGMENT_BLOCK);
//		entries.accept(SpectrumBlocks.HOVER_BLOCK);
//
//		entries.accept(SpectrumBlocks.TOPAZ_CALCITE_LAMP);
//		entries.accept(SpectrumBlocks.AMETHYST_CALCITE_LAMP);
//		entries.accept(SpectrumBlocks.CITRINE_CALCITE_LAMP);
//		entries.accept(SpectrumBlocks.ONYX_CALCITE_LAMP);
//		entries.accept(SpectrumBlocks.MOONSTONE_CALCITE_LAMP);
//		entries.accept(SpectrumBlocks.TOPAZ_BASALT_LAMP);
//		entries.accept(SpectrumBlocks.AMETHYST_BASALT_LAMP);
//		entries.accept(SpectrumBlocks.CITRINE_BASALT_LAMP);
//		entries.accept(SpectrumBlocks.ONYX_BASALT_LAMP);
//		entries.accept(SpectrumBlocks.MOONSTONE_BASALT_LAMP);
//
//		entries.accept(SpectrumBlocks.BASALT_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.CALCITE_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.STONE_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.GRANITE_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.DIORITE_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.ANDESITE_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.DEEPSLATE_SHIMMERSTONE_LIGHT);
//		entries.accept(SpectrumBlocks.BLACKSLAG_SHIMMERSTONE_LIGHT);
//
//		entries.accept(SpectrumBlocks.TOPAZ_CHISELED_BASALT);
//		entries.accept(SpectrumBlocks.AMETHYST_CHISELED_BASALT);
//		entries.accept(SpectrumBlocks.CITRINE_CHISELED_BASALT);
//		entries.accept(SpectrumBlocks.ONYX_CHISELED_BASALT);
//		entries.accept(SpectrumBlocks.MOONSTONE_CHISELED_BASALT);
//		entries.accept(SpectrumBlocks.TOPAZ_CHISELED_CALCITE);
//		entries.accept(SpectrumBlocks.AMETHYST_CHISELED_CALCITE);
//		entries.accept(SpectrumBlocks.CITRINE_CHISELED_CALCITE);
//		entries.accept(SpectrumBlocks.ONYX_CHISELED_CALCITE);
//		entries.accept(SpectrumBlocks.MOONSTONE_CHISELED_CALCITE);
//
//		entries.accept(SpectrumBlocks.TOPAZ_GLASS);
//		entries.accept(SpectrumBlocks.AMETHYST_GLASS);
//		entries.accept(SpectrumBlocks.CITRINE_GLASS);
//		entries.accept(SpectrumBlocks.ONYX_GLASS);
//		entries.accept(SpectrumBlocks.MOONSTONE_GLASS);
//		entries.accept(SpectrumBlocks.RADIANT_GLASS);
//
//		entries.accept(SpectrumBlocks.TOPAZ_CHIME);
//		entries.accept(SpectrumBlocks.AMETHYST_CHIME);
//		entries.accept(SpectrumBlocks.CITRINE_CHIME);
//		entries.accept(SpectrumBlocks.ONYX_CHIME);
//		entries.accept(SpectrumBlocks.MOONSTONE_CHIME);
//
//		entries.accept(SpectrumBlocks.AMETHYST_DECOSTONE);
//		entries.accept(SpectrumBlocks.TOPAZ_DECOSTONE);
//		entries.accept(SpectrumBlocks.CITRINE_DECOSTONE);
//		entries.accept(SpectrumBlocks.ONYX_DECOSTONE);
//		entries.accept(SpectrumBlocks.MOONSTONE_DECOSTONE);
//
//		entries.accept(SpectrumBlocks.JADE_VINE_PETAL_BLOCK);
//		entries.accept(SpectrumBlocks.JADE_VINE_PETAL_CARPET);
//
//		entries.accept(SpectrumBlocks.JADEITE_PETAL_BLOCK);
//		entries.accept(SpectrumBlocks.JADEITE_PETAL_CARPET);
//
//		entries.accept(SpectrumBlocks.EFFULGENT_BLOCK);
//		entries.accept(SpectrumBlocks.EFFULGENT_CUSHION);
//		entries.accept(SpectrumBlocks.EFFULGENT_CARPET);
//
//		entries.accept(SpectrumBlocks.WHITE_BLOCK);
//		entries.accept(SpectrumBlocks.ORANGE_BLOCK);
//		entries.accept(SpectrumBlocks.MAGENTA_BLOCK);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_BLOCK);
//		entries.accept(SpectrumBlocks.YELLOW_BLOCK);
//		entries.accept(SpectrumBlocks.LIME_BLOCK);
//		entries.accept(SpectrumBlocks.PINK_BLOCK);
//		entries.accept(SpectrumBlocks.GRAY_BLOCK);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_BLOCK);
//		entries.accept(SpectrumBlocks.CYAN_BLOCK);
//		entries.accept(SpectrumBlocks.PURPLE_BLOCK);
//		entries.accept(SpectrumBlocks.BLUE_BLOCK);
//		entries.accept(SpectrumBlocks.BROWN_BLOCK);
//		entries.accept(SpectrumBlocks.GREEN_BLOCK);
//		entries.accept(SpectrumBlocks.RED_BLOCK);
//		entries.accept(SpectrumBlocks.BLACK_BLOCK);
//		entries.accept(SpectrumBlocks.WHITE_LAMP);
//		entries.accept(SpectrumBlocks.ORANGE_LAMP);
//		entries.accept(SpectrumBlocks.MAGENTA_LAMP);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_LAMP);
//		entries.accept(SpectrumBlocks.YELLOW_LAMP);
//		entries.accept(SpectrumBlocks.LIME_LAMP);
//		entries.accept(SpectrumBlocks.PINK_LAMP);
//		entries.accept(SpectrumBlocks.GRAY_LAMP);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_LAMP);
//		entries.accept(SpectrumBlocks.CYAN_LAMP);
//		entries.accept(SpectrumBlocks.PURPLE_LAMP);
//		entries.accept(SpectrumBlocks.BLUE_LAMP);
//		entries.accept(SpectrumBlocks.BROWN_LAMP);
//		entries.accept(SpectrumBlocks.GREEN_LAMP);
//		entries.accept(SpectrumBlocks.RED_LAMP);
//		entries.accept(SpectrumBlocks.BLACK_LAMP);
//		entries.accept(SpectrumBlocks.WHITE_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.ORANGE_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.MAGENTA_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.YELLOW_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.LIME_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.PINK_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.GRAY_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.CYAN_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.PURPLE_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.BLUE_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.BROWN_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.GREEN_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.RED_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.BLACK_GLOWBLOCK);
//		entries.accept(SpectrumBlocks.WHITE_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.ORANGE_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.MAGENTA_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.YELLOW_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.LIME_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.PINK_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.GRAY_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.CYAN_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.PURPLE_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.BLUE_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.BROWN_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.GREEN_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.RED_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.BLACK_SPORE_BLOSSOM);
//		entries.accept(SpectrumBlocks.RESONANT_LILY);
//		entries.accept(SpectrumItems.PHANTOM_FRAME);
//		entries.accept(SpectrumItems.GLOW_PHANTOM_FRAME);
//		entries.accept(SpectrumItems.LOGO_BANNER_PATTERN);
//		entries.accept(SpectrumItems.AMETHYST_SHARD_BANNER_PATTERN);
//		entries.accept(SpectrumItems.AMETHYST_CLUSTER_BANNER_PATTERN);
//		entries.accept(SpectrumItems.MUSIC_DISC_SPECTRUM_THEME);
//		entries.accept(SpectrumItems.MUSIC_DISC_DIMENSION_THEME);
//		entries.accept(SpectrumItems.MUSIC_DISC_EVERREFLECTIVE);
//	}).build();
//
//	public static final ItemSubGroup COLORED_WOOD = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_COLORED_WOOD, Component.translatable("itemGroup.spectrum.colored_wood")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumBlocks.WHITE_LOG);
//		entries.accept(SpectrumBlocks.ORANGE_LOG);
//		entries.accept(SpectrumBlocks.MAGENTA_LOG);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_LOG);
//		entries.accept(SpectrumBlocks.YELLOW_LOG);
//		entries.accept(SpectrumBlocks.LIME_LOG);
//		entries.accept(SpectrumBlocks.PINK_LOG);
//		entries.accept(SpectrumBlocks.GRAY_LOG);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_LOG);
//		entries.accept(SpectrumBlocks.CYAN_LOG);
//		entries.accept(SpectrumBlocks.PURPLE_LOG);
//		entries.accept(SpectrumBlocks.BLUE_LOG);
//		entries.accept(SpectrumBlocks.BROWN_LOG);
//		entries.accept(SpectrumBlocks.GREEN_LOG);
//		entries.accept(SpectrumBlocks.RED_LOG);
//		entries.accept(SpectrumBlocks.BLACK_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_WHITE_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_ORANGE_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_MAGENTA_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_LIGHT_BLUE_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_YELLOW_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_LIME_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_PINK_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_GRAY_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_LIGHT_GRAY_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_CYAN_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_PURPLE_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_BLUE_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_BROWN_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_GREEN_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_RED_LOG);
//		entries.accept(SpectrumBlocks.STRIPPED_BLACK_LOG);
//		entries.accept(SpectrumBlocks.WHITE_WOOD);
//		entries.accept(SpectrumBlocks.ORANGE_WOOD);
//		entries.accept(SpectrumBlocks.MAGENTA_WOOD);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_WOOD);
//		entries.accept(SpectrumBlocks.YELLOW_WOOD);
//		entries.accept(SpectrumBlocks.LIME_WOOD);
//		entries.accept(SpectrumBlocks.PINK_WOOD);
//		entries.accept(SpectrumBlocks.GRAY_WOOD);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_WOOD);
//		entries.accept(SpectrumBlocks.CYAN_WOOD);
//		entries.accept(SpectrumBlocks.PURPLE_WOOD);
//		entries.accept(SpectrumBlocks.BLUE_WOOD);
//		entries.accept(SpectrumBlocks.BROWN_WOOD);
//		entries.accept(SpectrumBlocks.GREEN_WOOD);
//		entries.accept(SpectrumBlocks.RED_WOOD);
//		entries.accept(SpectrumBlocks.BLACK_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_WHITE_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_ORANGE_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_MAGENTA_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_LIGHT_BLUE_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_YELLOW_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_LIME_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_PINK_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_GRAY_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_LIGHT_GRAY_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_CYAN_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_PURPLE_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_BLUE_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_BROWN_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_GREEN_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_RED_WOOD);
//		entries.accept(SpectrumBlocks.STRIPPED_BLACK_WOOD);
//		entries.accept(SpectrumBlocks.WHITE_LEAVES);
//		entries.accept(SpectrumBlocks.ORANGE_LEAVES);
//		entries.accept(SpectrumBlocks.MAGENTA_LEAVES);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_LEAVES);
//		entries.accept(SpectrumBlocks.YELLOW_LEAVES);
//		entries.accept(SpectrumBlocks.LIME_LEAVES);
//		entries.accept(SpectrumBlocks.PINK_LEAVES);
//		entries.accept(SpectrumBlocks.GRAY_LEAVES);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_LEAVES);
//		entries.accept(SpectrumBlocks.CYAN_LEAVES);
//		entries.accept(SpectrumBlocks.PURPLE_LEAVES);
//		entries.accept(SpectrumBlocks.BLUE_LEAVES);
//		entries.accept(SpectrumBlocks.BROWN_LEAVES);
//		entries.accept(SpectrumBlocks.GREEN_LEAVES);
//		entries.accept(SpectrumBlocks.RED_LEAVES);
//		entries.accept(SpectrumBlocks.BLACK_LEAVES);
//		entries.accept(SpectrumBlocks.WHITE_SAPLING);
//		entries.accept(SpectrumBlocks.ORANGE_SAPLING);
//		entries.accept(SpectrumBlocks.MAGENTA_SAPLING);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_SAPLING);
//		entries.accept(SpectrumBlocks.YELLOW_SAPLING);
//		entries.accept(SpectrumBlocks.LIME_SAPLING);
//		entries.accept(SpectrumBlocks.PINK_SAPLING);
//		entries.accept(SpectrumBlocks.GRAY_SAPLING);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_SAPLING);
//		entries.accept(SpectrumBlocks.CYAN_SAPLING);
//		entries.accept(SpectrumBlocks.PURPLE_SAPLING);
//		entries.accept(SpectrumBlocks.BLUE_SAPLING);
//		entries.accept(SpectrumBlocks.BROWN_SAPLING);
//		entries.accept(SpectrumBlocks.GREEN_SAPLING);
//		entries.accept(SpectrumBlocks.RED_SAPLING);
//		entries.accept(SpectrumBlocks.BLACK_SAPLING);
//		entries.accept(SpectrumBlocks.WHITE_PLANKS);
//		entries.accept(SpectrumBlocks.ORANGE_PLANKS);
//		entries.accept(SpectrumBlocks.MAGENTA_PLANKS);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_PLANKS);
//		entries.accept(SpectrumBlocks.YELLOW_PLANKS);
//		entries.accept(SpectrumBlocks.LIME_PLANKS);
//		entries.accept(SpectrumBlocks.PINK_PLANKS);
//		entries.accept(SpectrumBlocks.GRAY_PLANKS);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_PLANKS);
//		entries.accept(SpectrumBlocks.CYAN_PLANKS);
//		entries.accept(SpectrumBlocks.PURPLE_PLANKS);
//		entries.accept(SpectrumBlocks.BLUE_PLANKS);
//		entries.accept(SpectrumBlocks.BROWN_PLANKS);
//		entries.accept(SpectrumBlocks.GREEN_PLANKS);
//		entries.accept(SpectrumBlocks.RED_PLANKS);
//		entries.accept(SpectrumBlocks.BLACK_PLANKS);
//		entries.accept(SpectrumBlocks.WHITE_STAIRS);
//		entries.accept(SpectrumBlocks.ORANGE_STAIRS);
//		entries.accept(SpectrumBlocks.MAGENTA_STAIRS);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_STAIRS);
//		entries.accept(SpectrumBlocks.YELLOW_STAIRS);
//		entries.accept(SpectrumBlocks.LIME_STAIRS);
//		entries.accept(SpectrumBlocks.PINK_STAIRS);
//		entries.accept(SpectrumBlocks.GRAY_STAIRS);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_STAIRS);
//		entries.accept(SpectrumBlocks.CYAN_STAIRS);
//		entries.accept(SpectrumBlocks.PURPLE_STAIRS);
//		entries.accept(SpectrumBlocks.BLUE_STAIRS);
//		entries.accept(SpectrumBlocks.BROWN_STAIRS);
//		entries.accept(SpectrumBlocks.GREEN_STAIRS);
//		entries.accept(SpectrumBlocks.RED_STAIRS);
//		entries.accept(SpectrumBlocks.BLACK_STAIRS);
//		entries.accept(SpectrumBlocks.WHITE_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.ORANGE_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.MAGENTA_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.YELLOW_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.LIME_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.PINK_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.GRAY_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.CYAN_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.PURPLE_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.BLUE_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.BROWN_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.GREEN_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.RED_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.BLACK_PRESSURE_PLATE);
//		entries.accept(SpectrumBlocks.WHITE_FENCE);
//		entries.accept(SpectrumBlocks.ORANGE_FENCE);
//		entries.accept(SpectrumBlocks.MAGENTA_FENCE);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_FENCE);
//		entries.accept(SpectrumBlocks.YELLOW_FENCE);
//		entries.accept(SpectrumBlocks.LIME_FENCE);
//		entries.accept(SpectrumBlocks.PINK_FENCE);
//		entries.accept(SpectrumBlocks.GRAY_FENCE);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_FENCE);
//		entries.accept(SpectrumBlocks.CYAN_FENCE);
//		entries.accept(SpectrumBlocks.PURPLE_FENCE);
//		entries.accept(SpectrumBlocks.BLUE_FENCE);
//		entries.accept(SpectrumBlocks.BROWN_FENCE);
//		entries.accept(SpectrumBlocks.GREEN_FENCE);
//		entries.accept(SpectrumBlocks.RED_FENCE);
//		entries.accept(SpectrumBlocks.BLACK_FENCE);
//		entries.accept(SpectrumBlocks.WHITE_FENCE_GATE);
//		entries.accept(SpectrumBlocks.ORANGE_FENCE_GATE);
//		entries.accept(SpectrumBlocks.MAGENTA_FENCE_GATE);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_FENCE_GATE);
//		entries.accept(SpectrumBlocks.YELLOW_FENCE_GATE);
//		entries.accept(SpectrumBlocks.LIME_FENCE_GATE);
//		entries.accept(SpectrumBlocks.PINK_FENCE_GATE);
//		entries.accept(SpectrumBlocks.GRAY_FENCE_GATE);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_FENCE_GATE);
//		entries.accept(SpectrumBlocks.CYAN_FENCE_GATE);
//		entries.accept(SpectrumBlocks.PURPLE_FENCE_GATE);
//		entries.accept(SpectrumBlocks.BLUE_FENCE_GATE);
//		entries.accept(SpectrumBlocks.BROWN_FENCE_GATE);
//		entries.accept(SpectrumBlocks.GREEN_FENCE_GATE);
//		entries.accept(SpectrumBlocks.RED_FENCE_GATE);
//		entries.accept(SpectrumBlocks.BLACK_FENCE_GATE);
//		entries.accept(SpectrumBlocks.WHITE_BUTTON);
//		entries.accept(SpectrumBlocks.ORANGE_BUTTON);
//		entries.accept(SpectrumBlocks.MAGENTA_BUTTON);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_BUTTON);
//		entries.accept(SpectrumBlocks.YELLOW_BUTTON);
//		entries.accept(SpectrumBlocks.LIME_BUTTON);
//		entries.accept(SpectrumBlocks.PINK_BUTTON);
//		entries.accept(SpectrumBlocks.GRAY_BUTTON);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_BUTTON);
//		entries.accept(SpectrumBlocks.CYAN_BUTTON);
//		entries.accept(SpectrumBlocks.PURPLE_BUTTON);
//		entries.accept(SpectrumBlocks.BLUE_BUTTON);
//		entries.accept(SpectrumBlocks.BROWN_BUTTON);
//		entries.accept(SpectrumBlocks.GREEN_BUTTON);
//		entries.accept(SpectrumBlocks.RED_BUTTON);
//		entries.accept(SpectrumBlocks.BLACK_BUTTON);
//		entries.accept(SpectrumBlocks.WHITE_SLAB);
//		entries.accept(SpectrumBlocks.ORANGE_SLAB);
//		entries.accept(SpectrumBlocks.MAGENTA_SLAB);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_SLAB);
//		entries.accept(SpectrumBlocks.YELLOW_SLAB);
//		entries.accept(SpectrumBlocks.LIME_SLAB);
//		entries.accept(SpectrumBlocks.PINK_SLAB);
//		entries.accept(SpectrumBlocks.GRAY_SLAB);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_SLAB);
//		entries.accept(SpectrumBlocks.CYAN_SLAB);
//		entries.accept(SpectrumBlocks.PURPLE_SLAB);
//		entries.accept(SpectrumBlocks.BLUE_SLAB);
//		entries.accept(SpectrumBlocks.BROWN_SLAB);
//		entries.accept(SpectrumBlocks.GREEN_SLAB);
//		entries.accept(SpectrumBlocks.RED_SLAB);
//		entries.accept(SpectrumBlocks.BLACK_SLAB);
//	}).build();
//
//	public static final ItemSubGroup MOB_HEADS = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_MOB_HEADS, Component.translatable("itemGroup.spectrum.mob_heads")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		for (Block skullBlock : SpectrumBlocks.MOB_HEADS.values()) {
//			entries.accept(skullBlock.asItem());
//		}
//	}).build();
//
//	public static final ItemSubGroup CREATURES = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_CREATURES, Component.translatable("itemGroup.spectrum.creatures")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumItems.EGG_LAYING_WOOLY_PIG_SPAWN_EGG);
//		entries.accept(SpectrumItems.PRESERVATION_TURRET_SPAWN_EGG);
//		entries.accept(SpectrumItems.KINDLING_SPAWN_EGG);
//		entries.accept(SpectrumItems.LIZARD_SPAWN_EGG);
//		entries.accept(SpectrumItems.ERASER_SPAWN_EGG);
//		entries.accept(SpectrumItems.BUCKET_OF_ERASER);
//		MemoryItem.appendEntries(entries);
//	}).build();
//
//	public static final ItemSubGroup ENERGY = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_ENERGY, Component.translatable("itemGroup.spectrum.energy")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumItems.INK_FLASK);
//		for (InkColor color : InkColor.all()) {
//			entries.accept(SpectrumItems.INK_FLASK.getFullStack(color));
//		}
//		entries.accept(SpectrumItems.INK_ASSORTMENT);
//		entries.accept(SpectrumItems.INK_ASSORTMENT.getFullStack());
//		entries.accept(SpectrumItems.PIGMENT_PALETTE);
//		entries.accept(SpectrumItems.PIGMENT_PALETTE.getFullStack());
//		entries.accept(SpectrumItems.ARTISTS_PALETTE);
//		entries.accept(SpectrumItems.ARTISTS_PALETTE.getFullStack());
//	}).build();
//
//	public static final ItemSubGroup CREATIVE = new ItemSubGroup.Builder(MAIN, ItemGroupIDs.SUBTAB_CREATIVE, Component.translatable("itemGroup.spectrum.creative")).backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE).entries((displayContext, entries) -> {
//		entries.accept(SpectrumItems.PEDESTAL_TIER_1_STRUCTURE_PLACER);
//		entries.accept(SpectrumItems.PEDESTAL_TIER_2_STRUCTURE_PLACER);
//		entries.accept(SpectrumItems.PEDESTAL_TIER_3_STRUCTURE_PLACER);
//		entries.accept(SpectrumItems.FUSION_SHRINE_STRUCTURE_PLACER);
//		entries.accept(SpectrumItems.ENCHANTER_STRUCTURE_PLACER);
//		entries.accept(SpectrumItems.SPIRIT_INSTILLER_STRUCTURE_PLACER);
//		entries.accept(SpectrumItems.CINDERHEARTH_STRUCTURE_PLACER);
//
//		entries.accept(SpectrumBlocks.CREATIVE_PARTICLE_SPAWNER);
//		entries.accept(SpectrumItems.CREATIVE_INK_ASSORTMENT);
//
//		entries.accept(SpectrumBlocks.DOWNSTONE);
//		entries.accept(SpectrumBlocks.PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.PRESERVATION_STAIRS);
//		entries.accept(SpectrumBlocks.PRESERVATION_SLAB);
//		entries.accept(SpectrumBlocks.PRESERVATION_BRICKS);
//		entries.accept(SpectrumBlocks.SHIMMERING_PRESERVATION_BRICKS);
//		entries.accept(SpectrumBlocks.POWDER_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.DIKE_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.PRESERVATION_GLASS);
//		entries.accept(SpectrumBlocks.TINTED_PRESERVATION_GLASS);
//		entries.accept(SpectrumBlocks.PRESERVATION_ROUNDEL);
//		entries.accept(SpectrumBlocks.PRESERVATION_BLOCK_DETECTOR);
//		entries.accept(SpectrumBlocks.DIKE_GATE_FOUNTAIN);
//		entries.accept(SpectrumBlocks.DIKE_GATE);
//		entries.accept(SpectrumBlocks.PRESERVATION_CONTROLLER);
//		entries.accept(SpectrumBlocks.BLACK_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.BLUE_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.BROWN_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.CYAN_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.GRAY_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.GREEN_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.LIGHT_BLUE_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.LIGHT_GRAY_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.LIME_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.MAGENTA_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.ORANGE_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.PINK_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.PURPLE_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.RED_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.WHITE_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.YELLOW_CHISELED_PRESERVATION_STONE);
//		entries.accept(SpectrumBlocks.INVISIBLE_WALL);
//		entries.accept(SpectrumBlocks.COURIER_STATUE);
//		entries.accept(SpectrumBlocks.PRESERVATION_CHEST);
//
//		entries.accept(SpectrumItems.DIVINATION_HEART);
//
//		entries.accept(SpectrumItems.ARTISANS_ATLAS);
//
//		//entries.add(SpectrumItems.FRIGID_STARDUST);
//		//entries.add(SpectrumItems.SPECTRAL_SHARD);
//		//entries.add(SpectrumBlocks.SPECTRAL_SHARD_BLOCK);
//		//entries.add(SpectrumBlocks.SPECTRAL_SHARD_STORAGE_BLOCK);
//	}).build();
//
//	public static void addEquipmentEntry(Item item, CreativeModeTab.Output entries) {
//		if (item instanceof Preenchanted preenchanted) {
//			entries.accept(preenchanted.getDefaultEnchantedStack(item));
//		} else {
//			entries.accept(item);
//		}
//	}
}
