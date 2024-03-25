package de.dafuqs.spectrum.recipe;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GatedSpectrumRecipe implements GatedRecipe {
	
	public final ResourceLocation id;
	public final String group;
	public final boolean secret;
	public final ResourceLocation requiredAdvancementIdentifier;
	
	protected GatedSpectrumRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier) {
		this.id = id;
		this.group = group;
		this.secret = secret;
		this.requiredAdvancementIdentifier = requiredAdvancementIdentifier;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}
	
	@Override
	public String getGroup() {
		return this.group;
	}
	
	@Override
	public boolean isSecret() {
		return this.secret;
	}
	
	/**
	 * The advancement the player has to have for the recipe be craftable
	 *
	 * @return The advancement identifier. A null value means the player is always able to craft this recipe
	 */
	@Nullable
	@Override
	public ResourceLocation getRequiredAdvancementIdentifier() {
		return this.requiredAdvancementIdentifier;
	}
	
	@Override
	public abstract ResourceLocation getRecipeTypeUnlockIdentifier();
	
	@Override
	public boolean canPlayerCraft(Player playerEntity) {
		return AdvancementHelper.hasAdvancement(playerEntity, getRecipeTypeUnlockIdentifier()) && AdvancementHelper.hasAdvancement(playerEntity, this.requiredAdvancementIdentifier);
	}
	
	public abstract String getRecipeTypeShortID();
	
	@Override
	public Component getSingleUnlockToastString() {
		return Component.translatable("spectrum.toast." + getRecipeTypeShortID() + "_recipe_unlocked.title");
	}
	
	@Override
	public Component getMultipleUnlockToastString() {
		return Component.translatable("spectrum.toast." + getRecipeTypeShortID() + "_recipes_unlocked.title");
	}
	
	@Override
	public boolean isSpecial() {
		return true;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof GatedSpectrumRecipe) {
			return ((GatedSpectrumRecipe) object).getId().equals(this.getId());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getId().toString();
	}
	
	protected static ItemStack getDefaultStackWithCount(Item item, int count) {
		ItemStack stack = item.getDefaultInstance();
		stack.setCount(count);
		return stack;
	}
	
	protected static boolean matchIngredientStacksExclusively(Container inv, List<IngredientStack> ingredientStacks) {
		if (inv.getContainerSize() < ingredientStacks.size()) {
			return false;
		}
		
		int inputStackCount = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (!inv.getItem(i).isEmpty()) {
				inputStackCount++;
			}
		}
		if (inputStackCount != ingredientStacks.size()) {
			return false;
		}
		
		
		for (IngredientStack ingredientStack : ingredientStacks) {
			boolean found = false;
			for (int i = 0; i < inv.getContainerSize(); i++) {
				if (ingredientStack.test(inv.getItem(i))) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean matchIngredientStacksExclusively(Container inv, List<IngredientStack> ingredients, int[] slots) {
		int inputStackCount = 0;
		for (int slot : slots) {
			if (!inv.getItem(slot).isEmpty()) {
				inputStackCount++;
			}
		}
		if (inputStackCount != ingredients.size()) {
			return false;
		}
		
		for (IngredientStack ingredient : ingredients) {
			boolean found = false;
			for (int slot : slots) {
				if (ingredient.test(inv.getItem(slot))) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		
		return true;
	}
	
}
