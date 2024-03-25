package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.FluidIngredient;
import de.dafuqs.spectrum.compat.patchouli.PatchouliHelper;
import de.dafuqs.spectrum.recipe.titration_barrel.ITitrationBarrelRecipe;
import de.dafuqs.spectrum.recipe.titration_barrel.TitrationBarrelRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.BookTextRenderer;

import java.util.List;

public class PageTitrationBarrelFermenting extends PageGatedRecipeDouble<ITitrationBarrelRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/titration_barrel.png");
	
	private transient BookTextRenderer textRenderer;
	private transient BookTextRenderer textRenderer2;
	
	public PageTitrationBarrelFermenting() {
		super(SpectrumRecipeTypes.TITRATION_BARREL);
	}
	
	@Override
	protected ItemStack getRecipeOutput(Level world, ITitrationBarrelRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull ITitrationBarrelRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		RenderSystem.enableBlend();
		drawContext.blit(BACKGROUND_TEXTURE, recipeX - 2, recipeY - 2, 0, 0, 100, 32, 128, 256);
		
		FluidIngredient fluid = recipe.getFluidInput();
		boolean usesFluid = fluid != FluidIngredient.EMPTY;
		IngredientStack bucketStack = IngredientStack.EMPTY;
		if (usesFluid) {
			bucketStack = IngredientStack.of(recipe.getFluidInput().into());
		}
		
		// the ingredients
		List<IngredientStack> ingredients = recipe.getIngredientStacks();
		int ingredientSize = ingredients.size();
		int ingredientSizeWithFluid = usesFluid ? ingredientSize + 1 : ingredientSize;
		int startX = recipeX + Math.max(-5, 15 - ingredientSizeWithFluid * 10);
		int startY = recipeY + (ingredientSizeWithFluid > 3 ? 0 : 10);
		for (int i = 0; i < ingredientSizeWithFluid; i++) {
			IngredientStack currentIngredient = i == ingredientSize ? bucketStack : ingredients.get(i);
			int yOffset;
			int xOffset;
			if (i < 3) {
				xOffset = i * 18;
				yOffset = 0;
			} else {
				xOffset = (i - 3) * 18;
				yOffset = 18;
			}
			PatchouliHelper.renderIngredientStack(drawContext, parent, startX + xOffset, startY + yOffset, mouseX, mouseY, currentIngredient);
		}
		
		// the titration barrel / tapping ingredient
		if (recipe.getTappingItem() == Items.AIR) {
			parent.renderItemStack(drawContext, recipeX + 54, recipeY + 20, mouseX, mouseY, recipe.getToastSymbol());
		} else {
			parent.renderItemStack(drawContext, recipeX + 50, recipeY + 20, mouseX, mouseY, recipe.getToastSymbol());
			parent.renderItemStack(drawContext, recipeX + 60, recipeY + 20, mouseX, mouseY, recipe.getTappingItem().getDefaultInstance());
		}
		
		// the output
		parent.renderItemStack(drawContext, recipeX + 78, recipeY + 10, mouseX, mouseY, recipe.getResultItem(world.registryAccess()));
		
		// the duration
		if (second) {
			if (textRenderer2 == null) {
				MutableComponent text = TitrationBarrelRecipe.getDurationText(recipe.getMinFermentationTimeHours(), recipe.getFermentationData());
				textRenderer2 = new BookTextRenderer(parent, text, 0, recipeY + 40);
			}
			textRenderer2.render(drawContext, mouseX, mouseY);
		} else {
			if (textRenderer == null) {
				MutableComponent text = TitrationBarrelRecipe.getDurationText(recipe.getMinFermentationTimeHours(), recipe.getFermentationData());
				textRenderer = new BookTextRenderer(parent, text, 0, recipeY + 40);
			}
			textRenderer.render(drawContext, mouseX, mouseY);
		}
	}
	
	@Override
	protected int getRecipeHeight() {
		return 56;
	}
	
}