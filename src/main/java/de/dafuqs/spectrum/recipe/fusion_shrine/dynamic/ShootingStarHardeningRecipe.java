package de.dafuqs.spectrum.recipe.fusion_shrine.dynamic;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.FusionShrineRecipeWorldEffect;
import de.dafuqs.spectrum.blocks.fusion_shrine.FusionShrineBlockEntity;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStarItem;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.fusion_shrine.FusionShrineRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class ShootingStarHardeningRecipe extends FusionShrineRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("collect_all_shooting_star_variants");
	public static final Component DESCRIPTION = Component.translatable("spectrum.recipe.fusion_shrine.explanation.shooting_star_hardening");
	public static final RecipeSerializer<ShootingStarHardeningRecipe> SERIALIZER = new EmptyRecipeSerializer<>(ShootingStarHardeningRecipe::new);
	
	public ShootingStarHardeningRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER, List.of(IngredientStack.of(Ingredient.of(SpectrumItemTags.SHOOTING_STARS)), IngredientStack.of(Ingredient.of(Items.DIAMOND))), Fluids.WATER, getHardenedShootingStar(),
				5, 100, true, true, true, new ArrayList<>(), FusionShrineRecipeWorldEffect.NOTHING, new ArrayList<>(), FusionShrineRecipeWorldEffect.NOTHING, DESCRIPTION);
	}
	
	private static ItemStack getHardenedShootingStar() {
		ItemStack stack = SpectrumBlocks.GLISTERING_SHOOTING_STAR.asItem().getDefaultInstance();
		ShootingStarItem.setHardened(stack);
		return stack;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
	@Override
	public void craft(Level world, FusionShrineBlockEntity fusionShrineBlockEntity) {
		ItemStack shootingStarStack = ItemStack.EMPTY;
		ItemStack diamondStack = ItemStack.EMPTY;
		
		for (int j = 0; j < fusionShrineBlockEntity.getContainerSize(); ++j) {
			ItemStack itemStack = fusionShrineBlockEntity.getItem(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof ShootingStarItem) {
					shootingStarStack = itemStack;
				} else if (itemStack.is(Items.DIAMOND)) {
					diamondStack = itemStack;
				}
			}
		}
		
		if (!shootingStarStack.isEmpty() && !diamondStack.isEmpty()) {
			int craftedAmount = Math.min(shootingStarStack.getCount(), diamondStack.getCount());
			
			ItemStack hardenedStack = shootingStarStack.copy();
			ShootingStarItem.setHardened(hardenedStack);
			
			shootingStarStack.shrink(craftedAmount);
			diamondStack.shrink(craftedAmount);
			
			spawnCraftingResultAndXP(world, fusionShrineBlockEntity, hardenedStack, craftedAmount); // spawn results
		}
	}
	
}
