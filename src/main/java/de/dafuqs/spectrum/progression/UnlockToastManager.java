package de.dafuqs.spectrum.progression;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import de.dafuqs.spectrum.items.magic_items.PaintbrushItem;
import de.dafuqs.spectrum.progression.toast.MessageToast;
import de.dafuqs.spectrum.progression.toast.UnlockedRecipeToast;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipe;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class UnlockToastManager {
	// Advancement Identifier + Recipe Type => Recipe
	public static final Map<ResourceLocation, Map<RecipeType<?>, List<GatedRecipe>>> gatedRecipes = new HashMap<>();
	
	public static final HashMap<ResourceLocation, Tuple<ItemStack, String>> messageToasts = new HashMap<>() {{
		put(SpectrumCommon.locate("milestones/unlock_shooting_stars"), new Tuple<>(Items.SPYGLASS.getDefaultInstance(), "shooting_stars_unlocked"));
		put(SpectrumCommon.locate("milestones/unlock_overenchanting_with_enchanter"), new Tuple<>(SpectrumBlocks.ENCHANTER.asItem().getDefaultInstance(), "overchanting_unlocked"));
		put(SpectrumCommon.locate("milestones/unlock_conflicted_enchanting_with_enchanter"), new Tuple<>(SpectrumBlocks.ENCHANTER.asItem().getDefaultInstance(), "enchant_conflicting_enchantments_unlocked"));
		put(SpectrumCommon.locate("milestones/unlock_fourth_potion_workshop_reagent_slot"), new Tuple<>(SpectrumBlocks.POTION_WORKSHOP.asItem().getDefaultInstance(), "fourth_potion_reagent_unlocked"));
		put(SpectrumCommon.locate("midgame/spectrum_midgame"), new Tuple<>(SpectrumBlocks.PEDESTAL_ONYX.asItem().getDefaultInstance(), "second_advancement_tree_unlocked"));
		put(SpectrumCommon.locate("lategame/spectrum_lategame"), new Tuple<>(SpectrumBlocks.PEDESTAL_MOONSTONE.asItem().getDefaultInstance(), "third_advancement_tree_unlocked"));
		put(PaintbrushItem.UNLOCK_COLORING_ADVANCEMENT_ID, new Tuple<>(SpectrumItems.PAINTBRUSH.getDefaultInstance(), "block_coloring_unlocked"));
		put(PaintbrushItem.UNLOCK_INK_SLINGING_ADVANCEMENT_ID, new Tuple<>(SpectrumItems.PAINTBRUSH.getDefaultInstance(), "paint_flinging_unlocked"));
	}};
	
	public static void registerGatedRecipe(RecipeType<?> recipeType, GatedRecipe gatedRecipe) {
		ResourceLocation requiredAdvancementIdentifier = gatedRecipe.getRequiredAdvancementIdentifier();
		
		if (gatedRecipes.containsKey(requiredAdvancementIdentifier)) {
			Map<RecipeType<?>, List<GatedRecipe>> recipeTypeListMap = gatedRecipes.get(requiredAdvancementIdentifier);
			if (recipeTypeListMap.containsKey(recipeType)) {
				List<GatedRecipe> existingList = recipeTypeListMap.get(recipeType);
				if (!existingList.contains(gatedRecipe)) {
					existingList.add(gatedRecipe);
				}
			} else {
				List<GatedRecipe> newList = new ArrayList<>();
				newList.add(gatedRecipe);
				recipeTypeListMap.put(recipeType, newList);
			}
		} else {
			Map<RecipeType<?>, List<GatedRecipe>> recipeTypeListMap = new HashMap<>();
			List<GatedRecipe> newList = new ArrayList<>();
			newList.add(gatedRecipe);
			recipeTypeListMap.put(recipeType, newList);
			gatedRecipes.put(requiredAdvancementIdentifier, recipeTypeListMap);
		}
	}
	
	public static void processAdvancements(Set<ResourceLocation> doneAdvancements) {
		Minecraft client = Minecraft.getInstance();
		RegistryAccess registryManager = client.level.registryAccess();
		
		int unlockedRecipeCount = 0;
		HashMap<RecipeType<?>, List<GatedRecipe>> unlockedRecipesByType = new HashMap<>();
		List<Tuple<ItemStack, String>> specialToasts = new ArrayList<>();
		
		for (ResourceLocation doneAdvancement : doneAdvancements) {
			if (gatedRecipes.containsKey(doneAdvancement)) {
				Map<RecipeType<?>, List<GatedRecipe>> recipesGatedByAdvancement = gatedRecipes.get(doneAdvancement);
				
				for (Map.Entry<RecipeType<?>, List<GatedRecipe>> recipesByType : recipesGatedByAdvancement.entrySet()) {
					List<GatedRecipe> newRecipes;
					if (unlockedRecipesByType.containsKey(recipesByType.getKey())) {
						newRecipes = unlockedRecipesByType.get(recipesByType.getKey());
					} else {
						newRecipes = new ArrayList<>();
					}
					
					for (GatedRecipe unlockedRecipe : recipesByType.getValue()) {
						if (unlockedRecipe.canPlayerCraft(client.player)) {
							if (!newRecipes.contains((unlockedRecipe))) {
								newRecipes.add(unlockedRecipe);
								unlockedRecipeCount++;
							}
						}
					}
					unlockedRecipesByType.put(recipesByType.getKey(), newRecipes);
				}
			}
			
			Optional<PedestalRecipeTier> newlyUnlockedRecipeTier = PedestalRecipeTier.hasJustUnlockedANewRecipeTier(doneAdvancement);
			if (newlyUnlockedRecipeTier.isPresent()) {
				List<GatedRecipe> unlockedPedestalRecipes;
				if (unlockedRecipesByType.containsKey(SpectrumRecipeTypes.PEDESTAL)) {
					unlockedPedestalRecipes = unlockedRecipesByType.get(SpectrumRecipeTypes.PEDESTAL);
				} else {
					unlockedPedestalRecipes = new ArrayList<>();
				}
				List<GatedRecipe> pedestalRecipes = new ArrayList<>();
				for (Map<RecipeType<?>, List<GatedRecipe>> recipesByType : gatedRecipes.values()) {
					if (recipesByType.containsKey(SpectrumRecipeTypes.PEDESTAL)) {
						pedestalRecipes.addAll(recipesByType.get(SpectrumRecipeTypes.PEDESTAL));
					}
				}
				
				for (PedestalRecipe alreadyUnlockedRecipe : getRecipesForTierWithAllConditionsMet(newlyUnlockedRecipeTier.get(), pedestalRecipes)) {
					if (!unlockedPedestalRecipes.contains(alreadyUnlockedRecipe)) {
						unlockedPedestalRecipes.add(alreadyUnlockedRecipe);
					}
				}
			}
			
			if (UnlockToastManager.messageToasts.containsKey(doneAdvancement)) {
				specialToasts.add(UnlockToastManager.messageToasts.get(doneAdvancement));
			}
		}
		
		if (unlockedRecipeCount > 50) {
			// the player unlocked a LOT of recipes at the same time (via command?)
			// => show a single toast. Nobody's going to remember all that stuff.
			// At that point it would be overwhelming / annoying
			List<ItemStack> allStacks = new ArrayList<>();
			for (List<GatedRecipe> recipes : unlockedRecipesByType.values()) {
				for (GatedRecipe recipe : recipes) {
					allStacks.add(recipe.getResultItem(client.level.registryAccess()));
				}
			}
            UnlockedRecipeToast.showLotsOfRecipesToast(Minecraft.getInstance(), allStacks);
		} else {
			for (List<GatedRecipe> unlockedRecipeList : unlockedRecipesByType.values()) {
				showGroupedRecipeUnlockToasts(registryManager, unlockedRecipeList);
			}
		}
		
		for (Tuple<ItemStack, String> messageToast : specialToasts) {
			MessageToast.showMessageToast(Minecraft.getInstance(), messageToast.getA(), messageToast.getB());
		}
	}
	
	private static void showGroupedRecipeUnlockToasts(RegistryAccess registryManager, List<GatedRecipe> unlockedRecipes) {
		if (unlockedRecipes.isEmpty()) {
			return;
		}
		
		
		Component singleText = unlockedRecipes.get(0).getSingleUnlockToastString();
		Component multipleText = unlockedRecipes.get(0).getMultipleUnlockToastString();
		
		List<ItemStack> singleRecipes = new ArrayList<>();
		HashMap<String, List<ItemStack>> groupedRecipes = new HashMap<>();

		for (GatedRecipe recipe : unlockedRecipes) {
			if (!recipe.getResultItem(registryManager).isEmpty()) { // weather recipes
				if (recipe.getGroup() == null) {
					SpectrumCommon.logWarning("Found a recipe with null group: " + recipe.getId().toString() + " Please report this. If you are Dafuqs and you are reading this: you messed up big time.");
				}
				
				if (recipe.getGroup().isEmpty()) {
					singleRecipes.add(recipe.getResultItem(registryManager));
				} else {
					if (groupedRecipes.containsKey(recipe.getGroup())) {
						groupedRecipes.get(recipe.getGroup()).add(recipe.getResultItem(registryManager));
					} else {
						List<ItemStack> newList = new ArrayList<>();
						newList.add(recipe.getResultItem(registryManager));
						groupedRecipes.put(recipe.getGroup(), newList);
					}
				}
			}
		}

		// show grouped recipes
		if (!groupedRecipes.isEmpty()) {
			for (Map.Entry<String, List<ItemStack>> group : groupedRecipes.entrySet()) {
				List<ItemStack> groupedList = group.getValue();
				if (groupedList.size() == 1) {
					UnlockedRecipeToast.showRecipeToast(Minecraft.getInstance(), groupedList.get(0), singleText);
				} else {
					UnlockedRecipeToast.showRecipeGroupToast(Minecraft.getInstance(), group.getKey(), groupedList, multipleText);
				}
			}
		}

		// show singular recipes
		for (ItemStack singleStack : singleRecipes) {
			UnlockedRecipeToast.showRecipeToast(Minecraft.getInstance(), singleStack, singleText);
		}
	}
	
	/**
	 * When the player upgraded their pedestal and built the new structure
	 * show toasts for all recipes that he already meets the requirements for
	 *
	 * @param pedestalRecipeTier The new pedestal recipe tier the player unlocked
	 */
	private static @NotNull List<PedestalRecipe> getRecipesForTierWithAllConditionsMet(PedestalRecipeTier pedestalRecipeTier, List<GatedRecipe> pedestalRecipes) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		
		List<PedestalRecipe> alreadyUnlockedRecipesAtNewTier = new ArrayList<>();
		for (GatedRecipe recipe : pedestalRecipes) {
			PedestalRecipe pedestalRecipe = (PedestalRecipe) recipe;
			if (pedestalRecipe.getTier() == pedestalRecipeTier && !alreadyUnlockedRecipesAtNewTier.contains(recipe) && recipe.canPlayerCraft(player)) {
				alreadyUnlockedRecipesAtNewTier.add(pedestalRecipe);
			}
		}
		return alreadyUnlockedRecipesAtNewTier;
	}
	
}
