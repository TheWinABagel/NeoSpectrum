package de.dafuqs.spectrum.recipe.fluid_converting.dynamic;

import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.fluid_converting.DragonrotConvertingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MeatToRottenFleshRecipe extends DragonrotConvertingRecipe {
	
	public static final RecipeSerializer<MeatToRottenFleshRecipe> SERIALIZER = new EmptyRecipeSerializer<>(MeatToRottenFleshRecipe::new);
	
	public MeatToRottenFleshRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER, getMeatsIngredient(), Items.ROTTEN_FLESH.getDefaultInstance());
	}
	
	private static Ingredient getMeatsIngredient() {
		return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(item -> {
			FoodProperties foodComponent = item.getFoodProperties();
			return item != Items.ROTTEN_FLESH && foodComponent != null && foodComponent.isMeat();
		}).map(ItemStack::new));
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
