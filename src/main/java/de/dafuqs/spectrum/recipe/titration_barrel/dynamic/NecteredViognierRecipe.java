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

public class NecteredViognierRecipe extends SweetenableTitrationBarrelRecipe {

	public static final RecipeSerializer<NecteredViognierRecipe> SERIALIZER = new EmptyRecipeSerializer<>(NecteredViognierRecipe::new);
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("hidden/collect_cookbooks/imperial_cookbook");

	public static final int MIN_FERMENTATION_TIME_HOURS = 24;
	public static final ItemStack OUTPUT_STACK = getDefaultStackWithCount(SpectrumItems.NECTERED_VIOGNIER, 4);
	public static final Item TAPPING_ITEM = Items.GLASS_BOTTLE;
	public static final List<IngredientStack> INGREDIENT_STACKS = new ArrayList<>() {{
		add(IngredientStack.of(Ingredient.of(SpectrumItems.NEPHRITE_BLOSSOM_BULB)));
		add(IngredientStack.of(Ingredient.of(SpectrumItems.GLASS_PEACH), Matchbook.empty(), null, 4));
	}};

	public NecteredViognierRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER, INGREDIENT_STACKS, FluidIngredient.of(Fluids.WATER), OUTPUT_STACK, TAPPING_ITEM, MIN_FERMENTATION_TIME_HOURS, new FermentationData(0.15F, 0.01F, List.of()));
	}
	
	@Override
	public ItemStack tap(Container inventory, long secondsFermented, float downfall) {
		int bulbCount = InventoryHelper.getItemCountInInventory(inventory, SpectrumItems.NEPHRITE_BLOSSOM_BULB);
		int petalCount = InventoryHelper.getItemCountInInventory(inventory, SpectrumItems.GLASS_PEACH);
		boolean nectar = InventoryHelper.getItemCountInInventory(inventory, SpectrumItems.MOONSTRUCK_NECTAR) > 0;
		
		float thickness = getThickness(bulbCount, petalCount);
		return tapWith(bulbCount, petalCount, nectar, thickness, secondsFermented, downfall);
	}
	
	@Override
	protected @NotNull List<MobEffectInstance> getEffects(boolean nectar, double bloominess, double alcPercent) {
		List<MobEffectInstance> effects = new ArrayList<>();

		int effectDuration = (int) (150 * Math.round(alcPercent % 10));
		if (alcPercent >= 35) {
			effects.add(new MobEffectInstance(SpectrumStatusEffects.MAGIC_ANNULATION, effectDuration, (int) (alcPercent / 10)));
		}
		if (alcPercent >= 35) {
			effects.add(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, effectDuration, (int) (alcPercent / 10)));
			effectDuration *= 1.5;
		}
		if (alcPercent >= 30) {
			effects.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST, effectDuration, (int) (alcPercent / 10)));
			effectDuration *= 1.5;
		}
		if (alcPercent >= 20) {
			effects.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, effectDuration, (int) (alcPercent / 45)));
			effectDuration *= 1.5;
		}
		if (alcPercent >= 10) {
			effects.add(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, effectDuration));
			effectDuration *= 1.5;
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
			if (stack.is(SpectrumItems.NEPHRITE_BLOSSOM_BULB)) {
				bulbsFound = true;
			} else if (!stack.is(SpectrumItems.GLASS_PEACH) && !stack.is(SpectrumItems.MOONSTRUCK_NECTAR)) {
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
