package de.dafuqs.spectrum.compat.patchouli.pages;

import com.google.gson.annotations.SerializedName;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageGatedRecipeDouble<T extends GatedRecipe> extends PageGatedRecipe<T> {
	
	@SerializedName("recipe2")
	ResourceLocation recipe2Id;
	
	protected transient T recipe2;
	
	public PageGatedRecipeDouble(RecipeType<T> recipeType) {
		super(recipeType);
	}
	
	protected abstract void drawRecipe(GuiGraphics drawContext, Level world, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
	
	@Override
	public void build(Level world, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		if (recipe == null && recipe2 != null) {
			recipe = recipe2;
			recipe2 = null;
		}
		
		super.build(world, entry, builder, pageNum);
		
		recipe2 = loadRecipe(builder, entry, recipe2Id);
		if (recipe2 != null) {
			GatedPatchouliPage.runSanityCheck(entry.getId(), pageNum, this.advancement, recipe2);
		}
	}
	
	@Override
	public int getTextHeight() {
		return getY() + getRecipeHeight() * (recipe2 == null ? 1 : 2);
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta) {
		super.render(drawContext, mouseX, mouseY, tickDelta);
		
		if (recipe != null) {
			Level world = Minecraft.getInstance().level;
			if (world == null) {
				return;
			}
			
			int recipeX = getX();
			int recipeY = getY();
			Component title = getTitle();
			parent.drawCenteredStringNoShadow(drawContext, title.getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
			
			drawRecipe(drawContext, world, recipe, recipeX, recipeY, mouseX, mouseY, false);
			if (recipe2 != null) {
				drawRecipe(drawContext, world, recipe2, recipeX, recipeY + getRecipeHeight(), mouseX, mouseY, true);
			}
		}
	}
	
	@Override
	public boolean isPageUnlocked() {
		Minecraft client = Minecraft.getInstance();
		if (!super.isPageUnlocked()) {
			return false;
		}
		Player player = client.player;
		return (recipe != null && recipe.canPlayerCraft(player)) || (recipe2 != null && recipe2.canPlayerCraft(player));
	}
	
}
