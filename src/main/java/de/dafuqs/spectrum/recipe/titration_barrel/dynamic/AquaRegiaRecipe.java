package de.dafuqs.spectrum.recipe.titration_barrel.dynamic;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.matchbook.Matchbook;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.FluidIngredient;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.titration_barrel.FermentationData;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AquaRegiaRecipe extends SweetenableTitrationBarrelRecipe {

	public static final RecipeSerializer<AquaRegiaRecipe> SERIALIZER = new EmptyRecipeSerializer<>(AquaRegiaRecipe::new);
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("hidden/collect_cookbooks/imbrifer_cookbook");
	public static final int MIN_FERMENTATION_TIME_HOURS = 24;
	public static final ItemStack OUTPUT_STACK = getDefaultStackWithCount(SpectrumItems.AQUA_REGIA, 4);
	public static final Item TAPPING_ITEM = Items.GLASS_BOTTLE;
	public static final List<IngredientStack> INGREDIENT_STACKS = new ArrayList<>() {{
		add(IngredientStack.of(Ingredient.of(SpectrumItems.JADEITE_LOTUS_BULB)));
		add(IngredientStack.of(Ingredient.of(SpectrumItems.JADEITE_PETALS), Matchbook.empty(), null, 3));
	}};

	public AquaRegiaRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER, INGREDIENT_STACKS, FluidIngredient.of(Fluids.WATER), OUTPUT_STACK, TAPPING_ITEM, MIN_FERMENTATION_TIME_HOURS, new FermentationData(0.2F, 0.01F, List.of()));
	}
	
	@Override
	public ItemStack tap(Container inventory, long secondsFermented, float downfall) {
		int bulbCount = InventoryHelper.getItemCountInInventory(inventory, SpectrumItems.JADEITE_LOTUS_BULB);
		int petalCount = InventoryHelper.getItemCountInInventory(inventory, SpectrumItems.JADEITE_PETALS);
		boolean nectar = InventoryHelper.getItemCountInInventory(inventory, SpectrumItems.MOONSTRUCK_NECTAR) > 0;
		
		float thickness = getThickness(bulbCount, petalCount);
		return tapWith(bulbCount, petalCount, nectar, thickness, secondsFermented, downfall);
	}
	
	@Override
	protected @NotNull List<MobEffectInstance> getEffects(boolean nectar, double bloominess, double alcPercent) {
		List<MobEffectInstance> effects = new ArrayList<>();

		int effectDuration = 1800;
		if (alcPercent >= 40) {
			effectDuration *= 2;
			effects.add(new MobEffectInstance(MobEffects.CONDUIT_POWER, effectDuration, 3));
			effectDuration *= 1.5;
		}
		if (alcPercent >= 35) {
			effects.add(new MobEffectInstance(SpectrumStatusEffects.SWIFTNESS, effectDuration, (int) (alcPercent / 15)));
			effectDuration *= 2;
		}
		if (alcPercent >= 30) {
			effects.add(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, effectDuration));
			effectDuration *= 3;
		}
		if (alcPercent >= 20) {
			effects.add(new MobEffectInstance(MobEffects.ABSORPTION, effectDuration, (int) (alcPercent / 10)));
			effectDuration *= 2;
		}
		if (alcPercent >= 10) {
			effects.add(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, effectDuration));
			effectDuration *= 2;
		}
		if (nectar) {
			effects.add(new MobEffectInstance(SpectrumStatusEffects.IMMUNITY, effectDuration / 2));
		}

		int nectarMod = nectar ? 3 : 1;
		effectDuration = 1200;
		int alcAfterBloominess = (int) (alcPercent / (nectarMod + bloominess));
		if (alcAfterBloominess >= 40) {
			effects.add(new MobEffectInstance(MobEffects.BLINDNESS, effectDuration));
			effectDuration *= 2;
		}
		if (alcAfterBloominess >= 30) {
			effects.add(new MobEffectInstance(MobEffects.POISON, effectDuration));
			effectDuration *= 2;
		}
		if (alcAfterBloominess >= 20) {
			effects.add(new MobEffectInstance(MobEffects.CONFUSION, effectDuration));
			effectDuration *= 2;
		}
		if (alcAfterBloominess >= 10) {
			effects.add(new MobEffectInstance(MobEffects.WEAKNESS, effectDuration));
		}
		return effects;
	}
	
	@Override
	public boolean matches(Container inventory, Level world) {
		boolean bulbsFound = false;
		
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.is(SpectrumItems.JADEITE_LOTUS_BULB)) {
				bulbsFound = true;
			} else if (!stack.is(SpectrumItems.JADEITE_PETALS) && !stack.is(SpectrumItems.MOONSTRUCK_NECTAR)) {
				return false;
			}
		}
		
		return bulbsFound;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
