package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntity;
import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import de.dafuqs.spectrum.items.magic_items.KnowledgeGemItem;
import de.dafuqs.spectrum.recipe.enchanter.EnchanterRecipe;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentUpgradeDisplay extends EnchanterDisplay {
	
	protected final Enchantment enchantment;
	protected final int enchantmentDestinationLevel;
	
	protected final int requiredExperience;
	protected final int requiredItemCount;
	
	public EnchantmentUpgradeDisplay(@NotNull EnchantmentUpgradeRecipe recipe) {
		super(recipe, buildIngredients(recipe), recipe.getResultItem(BasicDisplay.registryAccess()));
		
		this.enchantment = recipe.getEnchantment();
		this.enchantmentDestinationLevel = recipe.getEnchantmentDestinationLevel();
		this.requiredItemCount = recipe.getRequiredItemCount();
		this.requiredExperience = recipe.getRequiredExperience();
	}
	
	private static List<EntryIngredient> buildIngredients(EnchantmentUpgradeRecipe recipe) {
		List<EntryIngredient> inputs = new ArrayList<>();
		inputs.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0))); // the center stack
		int requiredItemCountSplit = recipe.getRequiredItemCount() / 8;
		int requiredItemCountModulo = recipe.getRequiredItemCount() % 8;
		for (int i = 0; i < 8; i++) {
			int addAmount = i < requiredItemCountModulo ? 1 : 0;
			inputs.add(EntryIngredients.of(recipe.getRequiredItem(), requiredItemCountSplit + addAmount));
		}
		
		inputs.add(EntryIngredients.of(KnowledgeGemItem.getKnowledgeDropStackWithXP(recipe.getRequiredExperience(), true)));
		return inputs;
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.ENCHANTMENT_UPGRADE;
	}
	
	@Override
    public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		if (!AdvancementHelper.hasAdvancement(client.player, EnchanterRecipe.UNLOCK_IDENTIFIER) || !super.isUnlocked()) {
			return false;
		}
		if (enchantmentDestinationLevel > enchantment.getMaxLevel()) {
			return AdvancementHelper.hasAdvancement(client.player, EnchanterBlockEntity.OVERENCHANTING_ADVANCEMENT_IDENTIFIER);
		} else {
			return true;
		}
	}
	
}
