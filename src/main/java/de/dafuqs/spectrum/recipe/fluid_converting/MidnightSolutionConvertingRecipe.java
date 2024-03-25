package de.dafuqs.spectrum.recipe.fluid_converting;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class MidnightSolutionConvertingRecipe extends FluidConvertingRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/create_midnight_aberration");
	private static final Set<Item> outputItems = new HashSet<>();
	
	public MidnightSolutionConvertingRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, @NotNull Ingredient inputIngredient, ItemStack outputItemStack) {
		super(id, group, secret, requiredAdvancementIdentifier, inputIngredient, outputItemStack);
		outputItems.add(outputItemStack.getItem());
	}
	
	public static boolean isExistingOutputItem(@NotNull ItemStack itemStack) {
		return outputItems.contains(itemStack.getItem());
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumItems.MIDNIGHT_SOLUTION_BUCKET);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING_ID;
	}
	
}
