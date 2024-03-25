package de.dafuqs.spectrum.recipe.cinderhearth;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CinderhearthRecipe extends GatedSpectrumRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("unlocks/blocks/cinderhearth");
	
	protected final Ingredient inputIngredient;
	protected final int time;
	protected final float experience;
	protected final List<Tuple<ItemStack, Float>> outputsWithChance;
	
	public CinderhearthRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, int time, float experience, List<Tuple<ItemStack, Float>> outputsWithChance) {
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.inputIngredient = inputIngredient;
		this.time = time;
		this.experience = experience;
		this.outputsWithChance = outputsWithChance;
		
		registerInToastManager(getType(), this);
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		return this.inputIngredient.test(inv.getItem(0));
	}
	
	@Override
	public ItemStack assemble(Container inv, RegistryAccess drm) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return outputsWithChance.get(0).getA();
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumBlocks.CINDERHEARTH);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.CINDERHEARTH_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.CINDERHEARTH;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.CINDERHEARTH_ID;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.inputIngredient);
		return defaultedList;
	}
	
	public float getExperience() {
		return this.experience;
	}
	
	public int getCraftingTime() {
		return this.time;
	}
	
	public List<ItemStack> getRolledOutputs(RandomSource random, float yieldMod) {
		List<ItemStack> output = new ArrayList<>();
		for (Tuple<ItemStack, Float> possibleOutput : this.outputsWithChance) {
			float chance = possibleOutput.getB();
			if (chance >= 1.0 || random.nextFloat() < chance * yieldMod) {
				ItemStack currentOutputStack = possibleOutput.getA();
				if (yieldMod > 1) {
					int totalCount = Support.getIntFromDecimalWithChance(currentOutputStack.getCount() * yieldMod, random);
					while (totalCount > 0) { // if the rolled count exceeds the max stack size we need to split them (unstackable items, counts > 64, ...)
						int count = Math.min(totalCount, currentOutputStack.getMaxStackSize());
						ItemStack outputStack = currentOutputStack.copy();
						outputStack.setCount(count);
						output.add(outputStack);
						totalCount -= count;
					}
				} else {
					output.add(currentOutputStack.copy());
				}
			}
		}
		return output;
	}
	
	public List<ItemStack> getPossibleOutputs() {
		List<ItemStack> outputs = new ArrayList<>();
		for (Tuple<ItemStack, Float> pair : this.outputsWithChance) {
			outputs.add(pair.getA());
		}
		return outputs;
	}
	
	public List<Tuple<ItemStack, Float>> getOutputsWithChance(RegistryAccess registryManager) {
		return outputsWithChance;
	}
	
}
