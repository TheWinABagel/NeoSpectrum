package de.dafuqs.spectrum.recipe.pedestal.dynamic;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntity;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.pedestal.BuiltinGemstoneColor;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.recipe.pedestal.ShapedPedestalRecipe;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class StarCandyRecipe extends ShapedPedestalRecipe {
	
	public static final RecipeSerializer<StarCandyRecipe> SERIALIZER = new EmptyRecipeSerializer<>(StarCandyRecipe::new);
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("unlocks/food/star_candy");
	public static final float PURPLE_STAR_CANDY_CHANCE = 0.02F;
	
	public StarCandyRecipe(ResourceLocation id) {
		super(id, "", false, UNLOCK_IDENTIFIER, PedestalRecipeTier.BASIC, 3, 3, generateInputs(), Map.of(BuiltinGemstoneColor.YELLOW, 1), SpectrumItems.STAR_CANDY.getDefaultInstance(), 1.0F, 20, false, false);
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		if (inv instanceof PedestalBlockEntity pedestal) {
			Level world = pedestal.getLevel();
			if (world.random.nextFloat() < PURPLE_STAR_CANDY_CHANCE) {
				return SpectrumItems.PURPLE_STAR_CANDY.getDefaultInstance();
			}
		}
		return this.output.copy();
	}
	
	private static List<IngredientStack> generateInputs() {
		return List.of(
				IngredientStack.of(Ingredient.of(Items.SUGAR)),
				IngredientStack.of(Ingredient.of(Items.SUGAR)),
				IngredientStack.of(Ingredient.of(Items.SUGAR)),
				IngredientStack.of(Ingredient.of(SpectrumItems.STARDUST)),
				IngredientStack.of(Ingredient.of(SpectrumItems.STARDUST)),
				IngredientStack.of(Ingredient.of(SpectrumItems.STARDUST)),
				IngredientStack.of(Ingredient.of(SpectrumItems.AMARANTH_GRAINS)),
				IngredientStack.of(Ingredient.of(SpectrumItems.AMARANTH_GRAINS)),
				IngredientStack.of(Ingredient.of(SpectrumItems.AMARANTH_GRAINS)));
	}
	
	
}
