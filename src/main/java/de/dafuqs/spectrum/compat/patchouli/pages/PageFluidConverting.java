package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageFluidConverting<P extends FluidConvertingRecipe> extends PageGatedRecipeSingle<P> {
	
	public PageFluidConverting(RecipeType<P> recipeType) {
		super(recipeType);
	}
	
	public abstract ResourceLocation getBackgroundTexture();
	
	@Override
	protected ItemStack getRecipeOutput(Level world, FluidConvertingRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull FluidConvertingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		drawContext.blit(getBackgroundTexture(), recipeX - 2, recipeY - 2, 0, 0, 104, 97, 128, 256);
		
		parent.drawCenteredStringNoShadow(drawContext, getTitle().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		// fluid bucket
		parent.renderItemStack(drawContext, recipeX - 1, recipeY + 15, mouseX, mouseY, recipe.getToastSymbol());
		
		// the ingredients
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		parent.renderIngredient(drawContext, recipeX + 23, recipeY + 7, mouseX, mouseY, ingredients.get(0));
		
		// the output
		parent.renderItemStack(drawContext, recipeX + 75, recipeY + 7, mouseX, mouseY, recipe.getResultItem(world.registryAccess()));
	}
	
	@Override
	protected int getRecipeHeight() {
		return 50;
	}
	
}