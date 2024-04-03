package de.dafuqs.spectrum.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.dafuqs.revelationary.RevelationRegistry;
import de.dafuqs.revelationary.advancement_criteria.AdvancementCountCriterion;
import de.dafuqs.revelationary.advancement_criteria.AdvancementGottenCriterion;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.color.ColorRegistry;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import de.dafuqs.spectrum.blocks.PlacedItemBlock;
import de.dafuqs.spectrum.blocks.gemstone.SpectrumBuddingBlock;
import de.dafuqs.spectrum.enchantments.SpectrumEnchantment;
import de.dafuqs.spectrum.items.PigmentItem;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.mixin.accessors.LootTableAccessor;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrushingRecipe;
import de.dafuqs.spectrum.recipe.enchanter.EnchanterRecipe;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import de.dafuqs.spectrum.recipe.pedestal.BuiltinGemstoneColor;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipe;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SanityCommand {

	private static final List<ResourceLocation> ADVANCEMENT_GATING_WARNING_WHITELIST = List.of(
			SpectrumCommon.locate("find_preservation_ruins"),                    // does not have a prerequisite
			SpectrumCommon.locate("fail_to_glitch_into_preservation_ruin"),        // does not have a prerequisite
			SpectrumCommon.locate("midgame/craft_blacklisted_memory_success"),    // its parent is 2 parents in
			SpectrumCommon.locate("lategame/collect_myceylon")                    // its parent is 2 parents in
	);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("spectrum_sanity")
			.requires((source) -> source.hasPermission(2))
				.executes((context) -> execute(context.getSource()))
		);
	}

	private static int execute(CommandSourceStack source) {
		SpectrumCommon.logInfo("##### SANITY CHECK START ######");

		// All blocks that do not have a mineable tag
		for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
			ResourceKey<Block> registryKey = entry.getKey();
			if (registryKey.location().getNamespace().equals(SpectrumCommon.MOD_ID)) {
				BlockState blockState = entry.getValue().defaultBlockState();

				// unbreakable or instabreak blocks do not need to have an entry
				if (blockState.getBlock().defaultDestroyTime() <= 0) {
					continue;
				}

				if (!blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)
						&& !blockState.is(BlockTags.MINEABLE_WITH_AXE)
						&& !blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)
						&& !blockState.is(BlockTags.MINEABLE_WITH_HOE)//todoforge shears and sword minable from blockstate?
//						&& !blockState.is(FabricMineableTags.SHEARS_MINEABLE)
//						&& !blockState.is(FabricMineableTags.SWORD_MINEABLE)
						&& !blockState.is(SpectrumBlockTags.EXEMPT_FROM_MINEABLE_DEBUG_CHECK)) {
					SpectrumCommon.logWarning("[SANITY: Mineable Tags] Block " + registryKey.location() + " is not contained in a any vanilla mineable tag.");
				}
			}
		}

		// All blocks without a loot table
		for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
			ResourceKey<Block> registryKey = entry.getKey();
			if (registryKey.location().getNamespace().equals(SpectrumCommon.MOD_ID)) {
				Block block = entry.getValue();
				
				if (block instanceof PlacedItemBlock) {
					continue; // that one always drops itself via code
				}
				if (block instanceof SpectrumBuddingBlock) {
					continue; // does not have any drop by default
				}
				
				BlockState blockState = entry.getValue().defaultBlockState();
				ResourceLocation lootTableID = block.getLootTable();
				
				// unbreakable blocks do not need to have a loot table
				if (blockState.getBlock().defaultDestroyTime() <= -1) {
					continue;
				}
				
				if (!blockState.is(SpectrumBlockTags.EXEMPT_FROM_LOOT_TABLE_DEBUG_CHECK)) {
					if (lootTableID.equals(BuiltInLootTables.EMPTY) || lootTableID.getPath().equals("blocks/air")) {
						SpectrumCommon.logWarning("[SANITY: Loot Tables] Block " + registryKey.location() + " has a non-existent loot table");
					} else {
						LootTable lootTable = source.getLevel().getServer().getLootData().getLootTable(lootTableID);
						List<LootPool> lootPools = ((LootTableAccessor) lootTable).getPools();
						if (lootPools.isEmpty()) {
							SpectrumCommon.logWarning("[SANITY: Loot Tables] Block " + registryKey.location() + " has an empty loot table");
						}
					}
				}
			}
		}

		// Statistic: Build an empty hashmap of hashmaps for counting used gem colors for each tier
		// This info can be used to balance usage a bit
		HashMap<PedestalRecipeTier, HashMap<GemstoneColor, Integer>> usedColorsForEachTier = new HashMap<>();
		for (PedestalRecipeTier pedestalRecipeTier : PedestalRecipeTier.values()) {
			HashMap<GemstoneColor, Integer> colorMap = new HashMap<>();
			for (GemstoneColor gemstoneColor : BuiltinGemstoneColor.values()) {
				colorMap.put(gemstoneColor, 0);
			}
			usedColorsForEachTier.put(pedestalRecipeTier, colorMap);
		}

		MinecraftServer minecraftServer = source.getLevel().getServer();
		RecipeManager recipeManager = minecraftServer.getRecipeManager();
		ServerAdvancementManager advancementLoader = minecraftServer.getAdvancements();

		// Pedestal recipes that use gemstone powder not available at that tier yet
		for (PedestalRecipe pedestalRecipe : recipeManager.getAllRecipesFor(SpectrumRecipeTypes.PEDESTAL)) {
			/* There are some recipes that use advanced ingredients by design
			   despite being of a low tier, like black colored lamps.
			   While the player does not have access to that yet it is no problem at all
			   To exclude those recipes in these warnings there is a boolean flag in the recipe jsons
			*/
			if (pedestalRecipe.getTier() == PedestalRecipeTier.BASIC || pedestalRecipe.getTier() == PedestalRecipeTier.SIMPLE) {
				if (pedestalRecipe.getPowderInputs().getOrDefault(BuiltinGemstoneColor.BLACK, 0) > 0) {
					SpectrumCommon.logWarning("[SANITY: Pedestal Recipe Ingredients] Pedestal recipe '" + pedestalRecipe.getId() + "' of tier '" + pedestalRecipe.getTier() + "' is using onyx powder as input! Players will not have access to Onyx at that tier");
				}
			}
			if (pedestalRecipe.getTier() != PedestalRecipeTier.COMPLEX) {
				if (pedestalRecipe.getPowderInputs().getOrDefault(BuiltinGemstoneColor.WHITE, 0) > 0) {
					SpectrumCommon.logWarning("[SANITY: Pedestal Recipe Ingredients] Pedestal recipe '" + pedestalRecipe.getId() + "' of tier '" + pedestalRecipe.getTier() + "' is using moonstone powder as input! Players will not have access to Moonstone at that tier");
				}
			}
			for (Map.Entry<GemstoneColor, Integer> powderInput : pedestalRecipe.getPowderInputs().entrySet()) {
				usedColorsForEachTier.get(pedestalRecipe.getTier()).put(powderInput.getKey(), usedColorsForEachTier.get(pedestalRecipe.getTier()).get(powderInput.getKey()) + powderInput.getValue());
			}
		}
		// recipe groups without localisation
		Set<String> recipeGroups = new HashSet<>();
		recipeManager.getRecipeIds().forEach(identifier -> {
			Optional<? extends Recipe<?>> recipe = recipeManager.byKey(identifier);
			if (recipe.isPresent()) {
				if (recipe.get() instanceof GatedSpectrumRecipe gatedSpectrumRecipe) {
					String group = gatedSpectrumRecipe.getGroup();
					if (group == null) {
						SpectrumCommon.logWarning("Recipe with null group found! :" + gatedSpectrumRecipe.getId());
					} else if (!group.isEmpty()) {
						recipeGroups.add(group);
					}
				}
			}
		});
		for (String recipeGroup : recipeGroups) {
			if (!Language.getInstance().has("recipeGroup.spectrum." + recipeGroup)) {
				SpectrumCommon.logWarning("[SANITY: Recipe Group Lang] Recipe group " + recipeGroup + " is not localized.");
			}
		}

		// Impossible to unlock recipes
		testRecipeUnlocks(SpectrumRecipeTypes.PEDESTAL, "Pedestal", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.ANVIL_CRUSHING, "Anvil Crushing", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.FUSION_SHRINE, "Fusion Shrine", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.ENCHANTER, "Enchanting", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.ENCHANTMENT_UPGRADE, "Enchantment Upgrade", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.POTION_WORKSHOP_BREWING, "Potion Workshop Brewing", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.POTION_WORKSHOP_CRAFTING, "Potion Workshop Reagent", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.POTION_WORKSHOP_REACTING, "Potion Workshop Crafting", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING, "Midnight Solution Converting", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.SPIRIT_INSTILLING, "Spirit Instilling", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.INK_CONVERTING, "Ink Converting", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.CRYSTALLARIEUM, "Crystallarieum", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.CINDERHEARTH, "Cinderhearth", recipeManager, advancementLoader);
		testRecipeUnlocks(SpectrumRecipeTypes.TITRATION_BARREL, "Titration Barrel", recipeManager, advancementLoader);
		
		RegistryAccess registryManager = source.registryAccess();
		testIngredientsAndOutputInColorRegistry(SpectrumRecipeTypes.FUSION_SHRINE, "Fusion Shrine", recipeManager, registryManager);
		testIngredientsAndOutputInColorRegistry(SpectrumRecipeTypes.ENCHANTER, "Enchanting", recipeManager, registryManager);
		testIngredientsAndOutputInColorRegistry(SpectrumRecipeTypes.ENCHANTMENT_UPGRADE, "Enchantment Upgrade", recipeManager, registryManager);
		testIngredientsAndOutputInColorRegistry(SpectrumRecipeTypes.SPIRIT_INSTILLING, "Spirit Instiller", recipeManager, registryManager);
		
		
		// Impossible to unlock block cloaks
		for (Map.Entry<ResourceLocation, List<BlockState>> cloaks : RevelationRegistry.getBlockStateEntries().entrySet()) {
			if (advancementLoader.getAdvancement(cloaks.getKey()) == null) {
				SpectrumCommon.logWarning("[SANITY: Block Cloaks] Advancement '" + cloaks.getKey().toString() + "' for block / item cloaking does not exist. Registered cloaks: " + cloaks.getValue().size());
			}
		}
		
		for (Advancement advancement : advancementLoader.getAllAdvancements()) {
			for (Criterion criterion : advancement.getCriteria().values()) {
				CriterionTriggerInstance conditions = criterion.getTrigger();

				// "has advancement" criteria with nonexistent advancements
				if (conditions instanceof AdvancementGottenCriterion.Conditions hasAdvancementConditions) {
					ResourceLocation advancementIdentifier = hasAdvancementConditions.getAdvancementIdentifier();
					Advancement advancementCriterionAdvancement = advancementLoader.getAdvancement(advancementIdentifier);
					if (advancementCriterionAdvancement == null) {
						SpectrumCommon.logWarning("[SANITY: Has_Advancement Criteria] Advancement '" + advancement.getId() + "' references advancement '" + advancementIdentifier + "' that does not exist");
					}
					// "advancement count" criteria with nonexistent advancements
				} else if (conditions instanceof AdvancementCountCriterion.Conditions hasAdvancementConditions) {
					for (ResourceLocation advancementIdentifier : hasAdvancementConditions.getAdvancementIdentifiers()) {
						Advancement advancementCriterionAdvancement = advancementLoader.getAdvancement(advancementIdentifier);
						if (advancementCriterionAdvancement == null) {
							SpectrumCommon.logWarning("[SANITY: Advancement_Count Criteria] Advancement '" + advancement.getId() + "' references advancement '" + advancementIdentifier + "' that does not exist");
						}
					}
				}
			}
		}

		// advancements that dont require parent
		for (Advancement advancement : advancementLoader.getAllAdvancements()) {
			String path = advancement.getId().getPath();
			if (advancement.getId().getNamespace().equals(SpectrumCommon.MOD_ID) && !path.startsWith("hidden") && !path.startsWith("progression") && !path.startsWith("milestones") && advancement.getParent() != null) {
				ResourceLocation previousAdvancementIdentifier = null;
				for (String[] requirement : advancement.getRequirements()) {
					if (requirement.length > 0 && requirement[0].equals("gotten_previous")) {
						CriterionTriggerInstance conditions = advancement.getCriteria().get("gotten_previous").getTrigger();
						if (conditions instanceof AdvancementGottenCriterion.Conditions advancementConditions) {
							previousAdvancementIdentifier = advancementConditions.getAdvancementIdentifier();
							break;
						} else {
							SpectrumCommon.logWarning("[SANITY: Advancement Gating] Advancement '" + advancement.getId() + "' has a \"gotten_previous\" requirement, but its not revelationary:advancement_gotten");
						}
					}
				}
				if (!ADVANCEMENT_GATING_WARNING_WHITELIST.contains(advancement.getId())) {
					if (previousAdvancementIdentifier == null) {
						SpectrumCommon.logWarning("[SANITY: Advancement Gating] Advancement '" + advancement.getId() + "' does not have its parent set as requirement");
					} else {
						Advancement parent = advancement.getParent();
						if (parent.getId().equals(previousAdvancementIdentifier)) {
							continue;
						}
						if (parent.getParent() != null && parent.getParent().getId().equals(previousAdvancementIdentifier)) {
							continue; // "collect stuff" advancements with its 2nd parent being the requirement
						}
						SpectrumCommon.logWarning("[SANITY: Advancement Gating] Advancement '" + advancement.getId() + "' has its \"gotten_previous\" advancement set to something else than their parent. Intended?");
					}
				}
			}
		}

		// Pedestal Recipes in wrong data folder
		for (PedestalRecipe recipe : recipeManager.getAllRecipesFor(SpectrumRecipeTypes.PEDESTAL)) {
			ResourceLocation id = recipe.getId();
			if (id.getPath().startsWith("mod_integration/") || id.getPath().contains("/glass/") || id.getPath().contains("/saplings/") || id.getPath().contains("/detectors/") || id.getPath().contains("/gem_lamps/") || id.getPath().contains("/decostones/")
					|| id.getPath().contains("/runes/") || id.getPath().contains("/pastel_network/") || id.getPath().contains("/gemstone_chimes/") || id.getPath().contains("/pastel_network/") || id.getPath().contains("/player_only_glass/")) {
				continue;
			}

			if (recipe.getTier() == PedestalRecipeTier.BASIC && !id.getPath().contains("/tier1/")) {
				SpectrumCommon.logWarning("[SANITY: Pedestal Recipes] BASIC recipe not in the correct tier folder: '" + id + "'");
			} else if (recipe.getTier() == PedestalRecipeTier.SIMPLE && !id.getPath().contains("/tier2/")) {
				SpectrumCommon.logWarning("[SANITY: Pedestal Recipes] SIMPLE recipe not in the correct tier folder: '" + id + "'");
			} else if (recipe.getTier() == PedestalRecipeTier.ADVANCED && !id.getPath().contains("/tier3/")) {
				SpectrumCommon.logWarning("[SANITY: Pedestal Recipes] ADVANCED recipe not in the correct tier folder: '" + id + "'");
			} else if (recipe.getTier() == PedestalRecipeTier.COMPLEX && !id.getPath().contains("/tier4/")) {
				SpectrumCommon.logWarning("[SANITY: Pedestal Recipes] COMPLEX recipe not in the correct tier folder: '" + id + "'");
			}
		}

		// Item Crushing recipes with nonexistent sounds
		for (AnvilCrushingRecipe anvilCrushingRecipe : recipeManager.getAllRecipesFor(SpectrumRecipeTypes.ANVIL_CRUSHING)) {
			SoundEvent soundEvent = anvilCrushingRecipe.getSoundEvent();
			if (soundEvent == null) {
				SpectrumCommon.logWarning("[SANITY: Item Crushing] Recipe '" + anvilCrushingRecipe.getId() + "' has a nonexistent sound set");
			}
		}

		// Enchantments with nonexistent unlock enchantment
		for (Map.Entry<ResourceKey<Enchantment>, Enchantment> enchantment : BuiltInRegistries.ENCHANTMENT.entrySet()) {
			if (enchantment.getValue() instanceof SpectrumEnchantment spectrumEnchantment) {
				ResourceLocation advancementIdentifier = spectrumEnchantment.getUnlockAdvancementIdentifier();
				Advancement advancementCriterionAdvancement = advancementLoader.getAdvancement(advancementIdentifier);
				if (advancementCriterionAdvancement == null) {
					SpectrumCommon.logWarning("[SANITY: Enchantments] Enchantment '" + enchantment.getKey().location() + "' references advancement '" + advancementIdentifier + "' that does not exist");
				}
			}
		}

		// ExtendedEnchantables with enchantability <= 0 (unable to be enchanted) or not set to be enchantable
		for (Map.Entry<ResourceKey<Item>, Item> item : BuiltInRegistries.ITEM.entrySet()) {
			Item i = item.getValue();
			if (i instanceof ExtendedEnchantable) {
				if (!new ItemStack(i).isEnchantable()) {
					SpectrumCommon.logWarning("[SANITY: Enchantability] Item '" + item.getKey().location() + "' is not set to be enchantable.");
				}
				if (i.getEnchantmentValue() < 1) {
					SpectrumCommon.logWarning("[SANITY: Enchantability] Item '" + item.getKey().location() + "' is ExtendedEnchantable, but has enchantability of < 1");
				}
			}
		}

		// Enchantments without recipe
		Map<Enchantment, DyeColor> craftingColors = new HashMap<>();
		Map<Enchantment, DyeColor> upgradeColors = new HashMap<>();
		for (EnchanterRecipe recipe : recipeManager.getAllRecipesFor(SpectrumRecipeTypes.ENCHANTER)) {
			ItemStack output = recipe.getResultItem(source.registryAccess());
			if (output.getItem() == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(output);
				if (enchantments.size() > 0) {
					for (Ingredient ingredient : recipe.getIngredients()) {
						for (ItemStack matchingStack : ingredient.getItems()) {
							if (matchingStack.getItem() instanceof PigmentItem pigmentItem) {
								craftingColors.put(enchantments.keySet().stream().toList().get(0), pigmentItem.getColor());
							}
						}
					}
				}
			}
		}
		for (EnchantmentUpgradeRecipe recipe : recipeManager.getAllRecipesFor(SpectrumRecipeTypes.ENCHANTMENT_UPGRADE)) {
			ItemStack output = recipe.getResultItem(source.registryAccess());
			if (output.getItem() == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(output);
				if (enchantments.size() > 0 && recipe.getRequiredItem() instanceof PigmentItem pigmentItem) {
					upgradeColors.put(enchantments.keySet().stream().toList().get(0), pigmentItem.getColor());
				}
			}
		}
		for (Map.Entry<ResourceKey<Enchantment>, Enchantment> entry : BuiltInRegistries.ENCHANTMENT.entrySet()) {
			Enchantment enchantment = entry.getValue();
			if (!craftingColors.containsKey(enchantment)) {
				SpectrumCommon.logWarning("[SANITY: Enchantment Recipes] Enchantment '" + entry.getKey().location() + "' does not have a crafting recipe");
			}
			if (!upgradeColors.containsKey(enchantment) && enchantment.getMaxLevel() > 1) {
				SpectrumCommon.logWarning("[SANITY: Enchantment Recipes] Enchantment '" + entry.getKey().location() + "' does not have a upgrading recipe");
			}
			if (craftingColors.containsKey(enchantment) && upgradeColors.containsKey(enchantment) && craftingColors.get(enchantment) != upgradeColors.get(enchantment)) {
				SpectrumCommon.logWarning("[SANITY: Enchantment Recipes] Enchantment recipes for '" + entry.getKey().location() + "' use different pigments");
			}
		}
		for (Map.Entry<ResourceKey<Enchantment>, Enchantment> entry : BuiltInRegistries.ENCHANTMENT.entrySet()) {
			Enchantment enchantment = entry.getValue();
			if (entry.getKey().location().getNamespace().equals(SpectrumCommon.MOD_ID) && !SpectrumEnchantmentTags.isIn(SpectrumEnchantmentTags.SPECTRUM_ENCHANTMENT, enchantment)) {
				SpectrumCommon.logWarning("[SANITY: Enchantment Tags] Enchantment '" + entry.getKey().location() + "' is missing in the spectrum:enchantments tag");
			}
		}

		// Trinkets that have an invalid equip advancement and thus can't be equipped
		for (Map.Entry<ResourceKey<Item>, Item> item : BuiltInRegistries.ITEM.entrySet()) {
			if (item.getValue() instanceof SpectrumTrinketItem trinketItem) {
				ResourceLocation advancementIdentifier = trinketItem.getUnlockIdentifier();
				Advancement advancementCriterionAdvancement = advancementLoader.getAdvancement(advancementIdentifier);
				if (advancementCriterionAdvancement == null) {
					SpectrumCommon.logWarning("[SANITY: Trinkets] Trinket '" + item.getKey().location() + "' references advancement '" + advancementIdentifier + "' that does not exist");
				}
			}
		}
		//todoforge fractal port

		// items / blocks missing in the creative tab (will also omit them from most recipe viewers)
//		Collection<ItemStack> itemGroupStacks = SpectrumItemGroups.MAIN.getSearchTabDisplayItems();
		Collection<ItemStack> itemGroupStacks = Collections.emptyList();
		for (Map.Entry<ResourceKey<Item>, Item> item : BuiltInRegistries.ITEM.entrySet()) {
			
			if (item.getKey().location().getNamespace().equals(SpectrumCommon.MOD_ID) && !item.getValue().builtInRegistryHolder().is(SpectrumItemTags.COMING_SOON_TOOLTIP)) {
				boolean found = false;
				for(ItemStack stack : itemGroupStacks) {
					if(stack.is(item.getValue())) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					SpectrumCommon.logWarning("[SANITY: ItemGroups] Item '" + item.getKey().location() + "' is missing from the Spectrum item group.");
				}
			}
		}
		

		SpectrumCommon.logInfo("##### SANITY CHECK FINISHED ######");

		SpectrumCommon.logInfo("##### SANITY CHECK PEDESTAL RECIPE STATISTICS ######");
		for (PedestalRecipeTier pedestalRecipeTier : PedestalRecipeTier.values()) {
			HashMap<GemstoneColor, Integer> entry = usedColorsForEachTier.get(pedestalRecipeTier);
			SpectrumCommon.logInfo("[SANITY: Pedestal Recipe Gemstone Usages] Gemstone Powder for tier " + StringUtils.leftPad(pedestalRecipeTier.toString(), 8) +
					": C:" + StringUtils.leftPad(entry.get(BuiltinGemstoneColor.CYAN).toString(), 3) +
					" M:" + StringUtils.leftPad(entry.get(BuiltinGemstoneColor.MAGENTA).toString(), 3) +
					" Y:" + StringUtils.leftPad(entry.get(BuiltinGemstoneColor.YELLOW).toString(), 3) +
					" K:" + StringUtils.leftPad(entry.get(BuiltinGemstoneColor.BLACK).toString(), 3) +
					" W:" + StringUtils.leftPad(entry.get(BuiltinGemstoneColor.WHITE).toString(), 3));
		}

		if (source.getEntity() instanceof ServerPlayer serverPlayerEntity) {
			serverPlayerEntity.displayClientMessage(Component.translatable("commands.spectrum.progression_sanity.success"), false);
		}

		return 0;
	}

	private static <R extends GatedRecipe> void testRecipeUnlocks(RecipeType<R> recipeType, String name, RecipeManager recipeManager, ServerAdvancementManager advancementLoader) {
		for (GatedRecipe recipe : recipeManager.getAllRecipesFor(recipeType)) {
			ResourceLocation advancementIdentifier = recipe.getRequiredAdvancementIdentifier();
			if (advancementIdentifier != null && advancementLoader.getAdvancement(advancementIdentifier) == null) {
				SpectrumCommon.logWarning("[SANITY: " + name + " Recipe Unlocks] Advancement '" + recipe.getRequiredAdvancementIdentifier() + "' in recipe '" + recipe.getId() + "' does not exist");
			}
		}
	}
	
	private static <R extends GatedRecipe> void testIngredientsAndOutputInColorRegistry(RecipeType<R> recipeType, String name, RecipeManager recipeManager, RegistryAccess registryManager) {
		for (GatedRecipe recipe : recipeManager.getAllRecipesFor(recipeType)) {
			for (Ingredient inputIngredient : recipe.getIngredients()) {
				for (ItemStack matchingItemStack : inputIngredient.getItems()) {
					if (ColorRegistry.ITEM_COLORS.getMapping(matchingItemStack.getItem()).isEmpty()) {
						SpectrumCommon.logWarning("[SANITY: " + name + " Recipe] Input '" + BuiltInRegistries.ITEM.getKey(matchingItemStack.getItem()) + "' in recipe '" + recipe.getId() + "', does not exist in the item color registry. Add it for nice effects!");
					}
				}
			}
			Item outputItem = recipe.getResultItem(registryManager).getItem();
			if (outputItem != null && outputItem != Items.AIR && ColorRegistry.ITEM_COLORS.getMapping(outputItem).isEmpty()) {
				SpectrumCommon.logWarning("[SANITY: " + name + " Recipe] Output '" + BuiltInRegistries.ITEM.getKey(outputItem) + "' in recipe '" + recipe.getId() + "', does not exist in the item color registry. Add it for nice effects!");
			}
		}
	}

}
