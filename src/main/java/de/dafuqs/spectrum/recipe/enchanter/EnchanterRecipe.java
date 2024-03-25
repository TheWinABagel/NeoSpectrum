package de.dafuqs.spectrum.recipe.enchanter;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class EnchanterRecipe extends GatedSpectrumRecipe {
	
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/build_enchanting_structure");
	
	protected final NonNullList<Ingredient> inputs; // first input is the center, all others around clockwise
	protected final ItemStack output;
	
	protected final int requiredExperience;
	protected final int craftingTime;
	protected final boolean noBenefitsFromYieldAndEfficiencyUpgrades;
	
	public EnchanterRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, NonNullList<Ingredient> inputs, ItemStack output, int craftingTime, int requiredExperience, boolean noBenefitsFromYieldAndEfficiencyUpgrades) {
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.inputs = inputs;
		this.output = output;
		this.requiredExperience = requiredExperience;
		this.craftingTime = craftingTime;
		this.noBenefitsFromYieldAndEfficiencyUpgrades = noBenefitsFromYieldAndEfficiencyUpgrades;
		
		registerInToastManager(getType(), this);
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		if (inv.getContainerSize() > 9) {
			if (!inputs.get(0).test(inv.getItem(0))) {
				return false;
			}
			if (this.getRequiredExperience() > 0
					&& !(inv.getItem(1).getItem() instanceof ExperienceStorageItem)
					&& ExperienceStorageItem.getStoredExperience(inv.getItem(1)) < this.getRequiredExperience()) {
				return false;
			}
			
			for (int i = 1; i < 9; i++) {
				if (!inputs.get(i).test(inv.getItem(i + 1))) {
					return false;
				}
			}
			
			return true;
		}
		return false;
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
		return output;
	}
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(SpectrumBlocks.ENCHANTER);
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return UNLOCK_IDENTIFIER;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.ENCHANTER_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.ENCHANTER;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return inputs;
	}
	
	public int getRequiredExperience() {
		return requiredExperience;
	}
	
	public int getCraftingTime() {
		return this.craftingTime;
	}
	
	public boolean areYieldAndEfficiencyUpgradesDisabled() {
		return noBenefitsFromYieldAndEfficiencyUpgrades;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.ENCHANTER_ID;
	}
	
}
