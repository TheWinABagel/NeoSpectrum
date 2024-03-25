package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.patchouli.PatchouliHelper;
import de.dafuqs.spectrum.recipe.spirit_instiller.SpiritInstillerRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.List;

public class PageSpiritInstillerCrafting extends PageGatedRecipeSingle<SpiritInstillerRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/spirit_instiller.png");
	private static final ItemStack ITEM_BOWL_STACK = SpectrumBlocks.ITEM_BOWL_CALCITE.asItem().getDefaultInstance();
	
	public PageSpiritInstillerCrafting() {
		super(SpectrumRecipeTypes.SPIRIT_INSTILLING);
	}
	
	@Override
	protected ItemStack getRecipeOutput(Level world, SpiritInstillerRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull SpiritInstillerRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		drawContext.blit(BACKGROUND_TEXTURE, recipeX - 2, recipeY - 2, 0, 0, 104, 97, 128, 256);
		
		parent.drawCenteredStringNoShadow(drawContext, getTitle().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		// the ingredients
		List<IngredientStack> ingredients = recipe.getIngredientStacks();
		PatchouliHelper.renderIngredientStack(drawContext, parent, recipeX + 3, recipeY + 8, mouseX, mouseY, ingredients.get(1)); // left
		PatchouliHelper.renderIngredientStack(drawContext, parent, recipeX + 23, recipeY + 11, mouseX, mouseY, ingredients.get(0)); // center
		PatchouliHelper.renderIngredientStack(drawContext, parent, recipeX + 44, recipeY + 8, mouseX, mouseY, ingredients.get(2)); // right
		
		// spirit instiller
		parent.renderItemStack(drawContext, recipeX + 23, recipeY + 25, mouseX, mouseY, recipe.getToastSymbol());
		
		// item bowls
		parent.renderItemStack(drawContext, recipeX + 3, recipeY + 25, mouseX, mouseY, ITEM_BOWL_STACK);
		parent.renderItemStack(drawContext, recipeX + 44, recipeY + 25, mouseX, mouseY, ITEM_BOWL_STACK);
		
		// the output
		parent.renderItemStack(drawContext, recipeX + 79, recipeY + 8, mouseX, mouseY, recipe.getResultItem(world.registryAccess()));
	}
	
	@Override
	protected int getRecipeHeight() {
		return 58;
	}
	
}