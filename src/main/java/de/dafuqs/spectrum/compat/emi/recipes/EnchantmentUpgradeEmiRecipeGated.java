package de.dafuqs.spectrum.compat.emi.recipes;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntity;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.client.Minecraft;

public class EnchantmentUpgradeEmiRecipeGated extends EnchanterEmiRecipeGated {
	
	protected boolean requiresOverEnchanting;
	
	public EnchantmentUpgradeEmiRecipeGated(EmiRecipeCategory category, EnchantmentUpgradeRecipe recipe) {
		super(category, recipe);
		this.requiresOverEnchanting = recipe.requiresUnlockedOverEnchanting();
	}
	
	@Override
	public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		if (!super.isUnlocked()) {
			return false;
		}
		if (requiresOverEnchanting) {
			return AdvancementHelper.hasAdvancement(client.player, EnchanterBlockEntity.OVERENCHANTING_ADVANCEMENT_IDENTIFIER);
		}
		return true;
	}
	
}
