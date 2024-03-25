package de.dafuqs.spectrum.recipe.titration_barrel.dynamic;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.FluidIngredient;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.helpers.TimeHelper;
import de.dafuqs.spectrum.items.food.beverages.properties.StatusEffectBeverageProperties;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.titration_barrel.FermentationData;
import de.dafuqs.spectrum.recipe.titration_barrel.TitrationBarrelRecipe;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SuspiciousBrewRecipe extends TitrationBarrelRecipe {
	
	
	public static final RecipeSerializer<SuspiciousBrewRecipe> SERIALIZER = new EmptyRecipeSerializer<>(SuspiciousBrewRecipe::new);
	public static final Item TAPPING_ITEM = Items.GLASS_BOTTLE;
	public static final int MIN_FERMENTATION_TIME_HOURS = 4;
	public static final ItemStack OUTPUT_STACK = getDefaultStackWithCount(SpectrumItems.SUSPICIOUS_BREW, 4);
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("unlocks/food/suspicious_brew");
	public static final List<IngredientStack> INGREDIENT_STACKS = new ArrayList<>() {{
		add(IngredientStack.of(Ingredient.of(ItemTags.SMALL_FLOWERS)));
		add(IngredientStack.of(Ingredient.of(ItemTags.SMALL_FLOWERS)));
		add(IngredientStack.of(Ingredient.of(ItemTags.SMALL_FLOWERS)));
		add(IngredientStack.of(Ingredient.of(ItemTags.SMALL_FLOWERS)));
	}};
	
	public SuspiciousBrewRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER, INGREDIENT_STACKS, FluidIngredient.of(Fluids.WATER), OUTPUT_STACK, TAPPING_ITEM, MIN_FERMENTATION_TIME_HOURS, new FermentationData(1.0F, 0.01F, List.of()));
	}

	@Override
	public ItemStack getPreviewTap(int timeMultiplier) {
		ItemStack flowerStack = Items.POPPY.getDefaultInstance();
		flowerStack.setCount(4);
		return tapWith(List.of(flowerStack), 1.0F, this.minFermentationTimeHours * 60L * 60L * timeMultiplier, 0.4F);
	}
	
	@Override
	public ItemStack tap(Container inventory, long secondsFermented, float downfall) {
		List<ItemStack> stacks = new ArrayList<>();
		int itemCount = 0;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty()) {
				stacks.add(stack);
				itemCount += stack.getCount();
			}
		}
		float thickness = getThickness(itemCount);
		return tapWith(stacks, thickness, secondsFermented, downfall);
	}

	public ItemStack tapWith(List<ItemStack> stacks, float thickness, long secondsFermented, float downfall) {
		if (secondsFermented / 60 / 60 < this.minFermentationTimeHours) {
			return NOT_FERMENTED_LONG_ENOUGH_OUTPUT_STACK.copy();
		}
		
		float ageIngameDays = TimeHelper.minecraftDaysFromSeconds(secondsFermented);
		double alcPercent = getAlcPercent(this.fermentationData.fermentationSpeedMod(), thickness, downfall, ageIngameDays);
		if (alcPercent >= 100) {
			return SpectrumItems.PURE_ALCOHOL.getDefaultInstance();
		} else {
			// add up all stew effects with their durations from the input stacks
			Map<MobEffect, Integer> stewEffects = new HashMap<>();
			for (ItemStack stack : stacks) {
				Optional<Pair<MobEffect, Integer>> stewEffect = getStewEffectFrom(stack);
				if (stewEffect.isPresent()) {
					MobEffect effect = stewEffect.get().getLeft();
					int duration = (int) (stewEffect.get().getRight() * (1 + Support.logBase(stack.getCount(), 2)));
					if (stewEffects.containsKey(effect)) {
						stewEffects.put(effect, stewEffects.get(effect) + duration);
					} else {
						stewEffects.put(effect, duration);
					}
				}
			}
			
			List<MobEffectInstance> finalStatusEffects = new ArrayList<>();
			double cappedAlcPercent = Math.min(alcPercent, 20D);
			for (Map.Entry<MobEffect, Integer> entry : stewEffects.entrySet()) {
				int finalDurationTicks = (int) (entry.getValue() * Math.pow(2, 1 + cappedAlcPercent));
				finalStatusEffects.add(new MobEffectInstance(entry.getKey(), finalDurationTicks, 0));
			}
			
			ItemStack outputStack = OUTPUT_STACK.copy();
			outputStack.setCount(1);
			return new StatusEffectBeverageProperties((long) ageIngameDays, (int) alcPercent, thickness, finalStatusEffects).getStack(outputStack);
		}
	}
	
	// taken from SuspiciousStewItem
	private Optional<Pair<MobEffect, Integer>> getStewEffectFrom(ItemStack stack) {
		if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FlowerBlock flowerBlock) {
			return Optional.of(Pair.of(flowerBlock.getSuspiciousEffect(), flowerBlock.getEffectDuration()));
		}
		return Optional.empty();
	}
	
	@Override
	public boolean matches(Container inventory, Level world) {
		boolean flowerFound = false;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FlowerBlock) {
					flowerFound = true;
				} else {
					return false;
				}
			}
		}
		
		return flowerFound;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
