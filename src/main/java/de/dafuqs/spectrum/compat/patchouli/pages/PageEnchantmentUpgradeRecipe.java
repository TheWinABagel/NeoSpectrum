package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.items.magic_items.KnowledgeGemItem;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.ArrayList;
import java.util.List;

public class PageEnchantmentUpgradeRecipe extends PageGatedRecipeSingle<EnchantmentUpgradeRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/enchanter_crafting.png");
	
	public PageEnchantmentUpgradeRecipe() {
		super(SpectrumRecipeTypes.ENCHANTMENT_UPGRADE);
	}
	
	@Override
	protected ItemStack getRecipeOutput(Level world, EnchantmentUpgradeRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull EnchantmentUpgradeRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		drawContext.blit(BACKGROUND_TEXTURE, recipeX, recipeY, 0, 0, 100, 80, 256, 256);
		
		parent.drawCenteredStringNoShadow(drawContext, getTitle().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		// the ingredients
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		
		int ingredientX = recipeX - 3;
		
		// surrounding input slots
		List<ItemStack> inputStacks = new ArrayList<>();
		int requiredItemCountSplit = recipe.getRequiredItemCount() / 8;
		int requiredItemCountModulo = recipe.getRequiredItemCount() % 8;
		for (int i = 0; i < 8; i++) {
			int addAmount = i < requiredItemCountModulo ? 1 : 0;
			inputStacks.add(new ItemStack(recipe.getRequiredItem(), requiredItemCountSplit + addAmount));
		}
		
		parent.renderItemStack(drawContext, ingredientX + 16, recipeY, mouseX, mouseY, inputStacks.get(0));
		parent.renderItemStack(drawContext, ingredientX + 40, recipeY, mouseX, mouseY, inputStacks.get(1));
		parent.renderItemStack(drawContext, ingredientX + 56, recipeY + 16, mouseX, mouseY, inputStacks.get(2));
		parent.renderItemStack(drawContext, ingredientX + 56, recipeY + 40, mouseX, mouseY, inputStacks.get(3));
		parent.renderItemStack(drawContext, ingredientX + 40, recipeY + 56, mouseX, mouseY, inputStacks.get(4));
		parent.renderItemStack(drawContext, ingredientX + 16, recipeY + 56, mouseX, mouseY, inputStacks.get(5));
		parent.renderItemStack(drawContext, ingredientX, recipeY + 40, mouseX, mouseY, inputStacks.get(6));
		parent.renderItemStack(drawContext, ingredientX, recipeY + 16, mouseX, mouseY, inputStacks.get(7));
		
		// center input slot
		parent.renderIngredient(drawContext, ingredientX + 28, recipeY + 28, mouseX, mouseY, ingredients.get(0));
		
		// Knowledge Gem and Enchanter
		ItemStack knowledgeDropStackWithXP = KnowledgeGemItem.getKnowledgeDropStackWithXP(recipe.getRequiredExperience(), true);
		parent.renderItemStack(drawContext, recipeX + 81, recipeY + 9, mouseX, mouseY, knowledgeDropStackWithXP);
		parent.renderItemStack(drawContext, recipeX + 81, recipeY + 46, mouseX, mouseY, SpectrumBlocks.ENCHANTER.asItem().getDefaultInstance());
		
		// the output
		parent.renderItemStack(drawContext, recipeX + 81, recipeY + 31, mouseX, mouseY, recipe.getResultItem(world.registryAccess()));
	}
	
	@Override
	protected int getRecipeHeight() {
		return 94;
	}
	
}