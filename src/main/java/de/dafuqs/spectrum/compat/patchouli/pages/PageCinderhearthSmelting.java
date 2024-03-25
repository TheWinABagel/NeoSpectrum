package de.dafuqs.spectrum.compat.patchouli.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.cinderhearth.CinderhearthRecipe;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.ArrayList;
import java.util.List;

public class PageCinderhearthSmelting extends PageGatedRecipeSingle<CinderhearthRecipe> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = SpectrumCommon.locate("textures/gui/patchouli/cinderhearth.png");
	
	final transient List<BookTextRenderer> chanceTextRenders = new ArrayList<>();
	
	public PageCinderhearthSmelting() {
		super(SpectrumRecipeTypes.CINDERHEARTH);
	}
	
	@Override
	protected ItemStack getRecipeOutput(Level world, CinderhearthRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		} else {
			return recipe.getResultItem(world.registryAccess());
		}
	}
	
	@Override
	protected void drawRecipe(GuiGraphics drawContext, Level world, @NotNull CinderhearthRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		
		List<Tuple<ItemStack, Float>> possibleOutputs = recipe.getOutputsWithChance(world.registryAccess());
		recipeX = Math.max(recipeX, recipeX + 26 - possibleOutputs.size() * 10);
		
		int backgroundTextureWidth = 34 + possibleOutputs.size() * 24;
		drawContext.blit(BACKGROUND_TEXTURE, recipeX - 1, recipeY - 2, 0, 0, backgroundTextureWidth, 45, 128, 128);
		
		parent.drawCenteredStringNoShadow(drawContext, titleText.getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		// the ingredient
		Ingredient ingredient = recipe.getIngredients().get(0);
		parent.renderIngredient(drawContext, recipeX + 2, recipeY + 7, mouseX, mouseY, ingredient);
		
		// cinderhearth
		parent.renderItemStack(drawContext, recipeX + 21, recipeY + 26, mouseX, mouseY, recipe.getToastSymbol());
		
		// outputs
		int chanceTextIndex = 0;
		for (int i = 0; i < possibleOutputs.size(); i++) {
			Tuple<ItemStack, Float> possibleOutput = possibleOutputs.get(i);
			int x = recipeX + 37 + i * 23;
			parent.renderItemStack(drawContext, x, recipeY + 6, mouseX, mouseY, possibleOutput.getA());
			
			if (possibleOutput.getB() < 1.0F) {
				if (chanceTextRenders.size() < chanceTextIndex + 1) {
					chanceTextRenders.add(new BookTextRenderer(parent, Component.literal((int) (possibleOutput.getB() * 100) + "%"), x, recipeY + 24));
				}
				chanceTextRenders.get(chanceTextIndex).render(drawContext, mouseX, mouseY);
				chanceTextIndex++;
			}
		}
	}
	
	@Override
	protected int getRecipeHeight() {
		return 58;
	}
	
}