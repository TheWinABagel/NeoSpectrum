package de.dafuqs.spectrum.recipe.crafting;

import de.dafuqs.spectrum.items.magic_items.EnderSpliceItem;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ClearEnderSpliceRecipe extends SingleItemCraftingRecipe {
	
	public static final RecipeSerializer<ClearEnderSpliceRecipe> SERIALIZER = new EmptyRecipeSerializer<>(ClearEnderSpliceRecipe::new);
	
	public ClearEnderSpliceRecipe(ResourceLocation identifier) {
		super(identifier);
	}
	
	@Override
	public boolean matches(Level world, ItemStack stack) {
		return stack.getItem() instanceof EnderSpliceItem && EnderSpliceItem.hasTeleportTarget(stack);
	}
	
	@Override
	public ItemStack craft(ItemStack stack) {
		ItemStack returnStack = stack.copy();
		returnStack.setCount(1);
		EnderSpliceItem.clearTeleportTarget(returnStack);
		return returnStack;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
