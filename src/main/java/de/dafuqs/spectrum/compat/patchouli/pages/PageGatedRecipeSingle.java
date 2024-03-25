package de.dafuqs.spectrum.compat.patchouli.pages;

import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Like PageGatedRecipeDouble, but only displays a single recipe
 */
public abstract class PageGatedRecipeSingle<T extends GatedRecipe> extends PageGatedRecipe<T> {
	
	public PageGatedRecipeSingle(RecipeType<T> recipeType) {
		super(recipeType);
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta) {
		if (recipe != null) {
			Level world = Minecraft.getInstance().level;
			if (world == null) {
				return;
			}
			
			int recipeX = getX();
			int recipeY = getY();
			drawRecipe(drawContext, world, recipe, recipeX, recipeY, mouseX, mouseY);
		}
		super.render(drawContext, mouseX, mouseY, tickDelta);
	}
	
	protected abstract void drawRecipe(GuiGraphics drawContext, Level world, T recipe, int recipeX, int recipeY, int mouseX, int mouseY);
	
}
