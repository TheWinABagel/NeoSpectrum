package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrushingRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PageAnvilCrushing extends PageGatedRecipeSingle<AnvilCrushingRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/container/anvil_crushing.png");
	
	public PageAnvilCrushing() {
		super(SpectrumRecipeTypes.ANVIL_CRUSHING);
	}
	
	@Override
	protected ItemStack getRecipeOutput(Level world, AnvilCrushingRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull AnvilCrushingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		drawContext.blit(BACKGROUND_TEXTURE, recipeX, recipeY + 4, 0, 0, 84, 48, 256, 256);
		
		parent.drawCenteredStringNoShadow(drawContext, getTitle().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		// the ingredients
		Ingredient ingredient = recipe.getIngredients().get(0);
		parent.renderIngredient(drawContext, recipeX + 16, recipeY + 35, mouseX, mouseY, ingredient);
		
		// the anvil
		parent.renderItemStack(drawContext, recipeX + 16, recipeY + 15, mouseX, mouseY, recipe.getToastSymbol());
		
		// the output
		parent.renderItemStack(drawContext, recipeX + 64, recipeY + 29, mouseX, mouseY, recipe.getResultItem(world.registryAccess()));
	}
	
	@Override
	protected int getRecipeHeight() {
		return 73;
	}
	
}