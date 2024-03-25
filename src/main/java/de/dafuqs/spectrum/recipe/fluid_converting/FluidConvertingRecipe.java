package de.dafuqs.spectrum.recipe.fluid_converting;

import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class FluidConvertingRecipe extends GatedSpectrumRecipe {
	
	protected final Ingredient inputIngredient;
	protected final ItemStack outputItemStack;
	
	public FluidConvertingRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, @NotNull Ingredient inputIngredient, ItemStack outputItemStack) {
		super(id, group, secret, requiredAdvancementIdentifier);
		this.inputIngredient = inputIngredient;
		this.outputItemStack = outputItemStack;
	}
	
	@Override
	public boolean matches(@NotNull Container inv, Level world) {
		return this.inputIngredient.test(inv.getItem(0));
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		return null;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return outputItemStack.copy();
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.inputIngredient);
		return defaultedList;
	}
	
}
