package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.patchouli.PatchouliHelper;
import de.dafuqs.spectrum.recipe.fusion_shrine.FusionShrineRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.List;

public class PageFusionShrine extends PageGatedRecipeSingle<FusionShrineRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/fusion_shrine.png");
	
	public PageFusionShrine() {
		super(SpectrumRecipeTypes.FUSION_SHRINE);
	}
	
	@Override
	protected ItemStack getRecipeOutput(Level world, FusionShrineRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull FusionShrineRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		drawContext.blit(BACKGROUND_TEXTURE, recipeX - 2, recipeY - 2, 0, 0, 104, 97, 128, 256);
		
		parent.drawCenteredStringNoShadow(drawContext, getTitle().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		// the ingredients
		List<IngredientStack> ingredients = recipe.getIngredientStacks();
		int startX = Math.max(-5, 30 - ingredients.size() * 8);
		for (int i = 0; i < ingredients.size(); i++) {
			PatchouliHelper.renderIngredientStack(drawContext, parent, recipeX + startX + i * 16, recipeY + 3, mouseX, mouseY, ingredients.get(i));
		}
		
		if (recipe.getFluidInput() != Fluids.EMPTY) {
			Item fluidBucketItem = recipe.getFluidInput().getBucket();
			if (fluidBucketItem != null) {
				// the shrine
				parent.renderItemStack(drawContext, recipeX + 14, recipeY + 31, mouseX, mouseY, recipe.getToastSymbol());
				
				// the fluid as a bucket
				ItemStack fluidBucketItemStack = new ItemStack(fluidBucketItem);
				parent.renderItemStack(drawContext, recipeX + 30, recipeY + 31, mouseX, mouseY, fluidBucketItemStack);
				
			}
		} else {
			// the shrine
			parent.renderItemStack(drawContext, recipeX + 22, recipeY + 31, mouseX, mouseY, recipe.getToastSymbol());
		}
		
		// the output
		parent.renderItemStack(drawContext, recipeX + 78, recipeY + 31, mouseX, mouseY, recipe.getResultItem(world.registryAccess()));
	}
	
	@Override
	protected int getRecipeHeight() {
		return 68;
	}
	
}