package de.dafuqs.spectrum.compat.patchouli.pages;

import com.google.gson.annotations.SerializedName;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

/**
 * Like PageGatedRecipeDouble, but only displays a single recipe
 */
public abstract class PageGatedRecipe<T extends GatedRecipe> extends PageWithText implements GatedPatchouliPage {
	
	private final RecipeType<T> recipeType;
	
	@SerializedName("recipe")
	ResourceLocation recipeId;
	String title;
	
	protected transient T recipe;
	protected transient Component titleText;
	
	public PageGatedRecipe(RecipeType<T> recipeType) {
		this.recipeType = recipeType;
	}
	
	@SuppressWarnings({"unchecked"})
	private @Nullable T getRecipe(ResourceLocation id) {
		Minecraft client = Minecraft.getInstance();
		if (client.level == null) {
			return null;
		}
		RecipeManager manager = client.level.getRecipeManager();
		return (T) manager.byKey(id).filter(recipe -> recipe.getType() == recipeType).orElse(null);
	}
	
	protected T loadRecipe(BookContentsBuilder builder, BookEntry entry, ResourceLocation identifier) {
		Minecraft client = Minecraft.getInstance();
		if (identifier == null || client.level == null) {
			return null;
		}
		T recipe = getRecipe(identifier);
		if (recipe != null) {
			entry.addRelevantStack(builder, recipe.getResultItem(client.level.registryAccess()), pageNum);
			return recipe;
		}
		PatchouliAPI.LOGGER.warn("Recipe {} (of type {}) not found", identifier, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
		return null;
	}
	
	@Override
	public boolean isPageUnlocked() {
		Minecraft client = Minecraft.getInstance();
		if (!super.isPageUnlocked() || recipe == null) {
			return false;
		}
		return recipe.canPlayerCraft(client.player);
	}
	
	@Override
	public void build(Level world, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(world, entry, builder, pageNum);
		
		recipe = loadRecipe(builder, entry, recipeId);
		
		boolean customTitle = title != null && !title.isEmpty();
		titleText = !customTitle ? getRecipeOutput(world, recipe).getHoverName() : i18nText(title);
		
		GatedPatchouliPage.runSanityCheck(entry.getId(), pageNum, advancement, recipe);
	}
	
	@Override
	public int getTextHeight() {
		return getY() + getRecipeHeight() - 13;
	}
	
	@Override
	public boolean shouldRenderText() {
		return getTextHeight() + 10 < GuiBook.PAGE_HEIGHT;
	}
	
	protected abstract ItemStack getRecipeOutput(Level world, T recipe);
	
	protected abstract int getRecipeHeight();
	
	protected int getX() {
		return GuiBook.PAGE_WIDTH / 2 - 49;
	}
	
	protected int getY() {
		return 4;
	}
	
	protected Component getTitle() {
		return titleText;
	}
	
}
