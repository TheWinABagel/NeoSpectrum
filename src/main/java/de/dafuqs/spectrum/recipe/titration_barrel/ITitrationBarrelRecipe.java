package de.dafuqs.spectrum.recipe.titration_barrel;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.FluidIngredient;
import de.dafuqs.spectrum.api.recipe.GatedRecipe;
import de.dafuqs.spectrum.helpers.TimeHelper;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * In contrast to most other Minecraft things, the titration barrel also counts the fermenting time
 * when the game is not running (comparing the time of sealing to the time of opening)
 * Making it a non-ticking block entity and also "fermenting" when the game is not running
 * This also means TitrationBarrelRecipes have to calculate their time using real life seconds, instead of game ticks
 */
public interface ITitrationBarrelRecipe extends GatedRecipe {
	
	ResourceLocation UNLOCK_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("unlocks/blocks/titration_barrel");
	
	ItemStack tap(Container inventory, long secondsFermented, float downfall);
	
	Item getTappingItem();
	
	FluidIngredient getFluidInput();
	
	float getAngelsSharePerMcDay();
	
	// the amount of bottles able to get out of a single barrel
	default int getOutputCountAfterAngelsShare(Level world, float temperature, long secondsFermented) {
		if (getFermentationData() == null) {
			return getResultItem(world.registryAccess()).getCount();
		}
		
		float angelsSharePercent = getAngelsSharePercent(secondsFermented, temperature);
		if (angelsSharePercent > 0) {
			return (int) (getResultItem(world.registryAccess()).getCount() * Math.ceil(1F - angelsSharePercent / 100F));
		} else {
			return (int) (getResultItem(world.registryAccess()).getCount() * Math.floor(1F - angelsSharePercent / 100F));
		}
	}
	
	// the amount of fluid that evaporated while fermenting
	// the higher the temperature in the biome is, the more evaporates
	// making colder biomes more desirable
	default float getAngelsSharePercent(long secondsFermented, float temperature) {
		return Math.max(0.1F, temperature) * TimeHelper.minecraftDaysFromSeconds(secondsFermented) * getAngelsSharePerMcDay();
	}
	
	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	default ItemStack getToastSymbol() {
		return SpectrumBlocks.TITRATION_BARREL.asItem().getDefaultInstance();
	}
	
	@Override
	default RecipeType<?> getType() {
		return SpectrumRecipeTypes.TITRATION_BARREL;
	}
	
	List<IngredientStack> getIngredientStacks();
	
	int getMinFermentationTimeHours();
	
	FermentationData getFermentationData();

}
