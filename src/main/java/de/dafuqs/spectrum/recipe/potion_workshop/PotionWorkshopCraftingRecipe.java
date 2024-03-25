package de.dafuqs.spectrum.recipe.potion_workshop;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.blocks.potion_workshop.PotionWorkshopBlockEntity;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PotionWorkshopCraftingRecipe extends PotionWorkshopRecipe {
	
	protected final IngredientStack baseIngredient;
	protected final boolean consumeBaseIngredient;
	protected final int requiredExperience;
	protected final ItemStack output;
	
	public PotionWorkshopCraftingRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier,
										IngredientStack baseIngredient, boolean consumeBaseIngredient, int requiredExperience, IngredientStack ingredient1, IngredientStack ingredient2, IngredientStack ingredient3, ItemStack output, int craftingTime, int color) {
		
		super(id, group, secret, requiredAdvancementIdentifier, craftingTime, color, ingredient1, ingredient2, ingredient3);
		this.output = output;
		this.baseIngredient = baseIngredient;
		this.requiredExperience = requiredExperience;
		this.consumeBaseIngredient = consumeBaseIngredient;
		
		registerInToastManager(getType(), this);
	}
	
	public IngredientStack getBaseIngredient() {
		return baseIngredient;
	}
	
	public boolean consumesBaseIngredient() {
		return consumeBaseIngredient;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_CRAFTING_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_CRAFTING;
	}
	
	@Override
	public boolean usesReagents() {
		return false;
	}
	
	@Override
	public int getRequiredExperience() {
		return this.requiredExperience;
	}
	
	@Override
	public ItemStack assemble(Container inventory, RegistryAccess drm) {
		return null;
	}
	
	@Override
	public boolean isValidBaseIngredient(ItemStack itemStack) {
		return baseIngredient.test(itemStack);
	}
	
	@Override
	public List<IngredientStack> getIngredientStacks() {
		NonNullList<IngredientStack> defaultedList = NonNullList.create();
		defaultedList.add(IngredientStack.ofStacks(SpectrumItems.MERMAIDS_GEM.getDefaultInstance()));
		defaultedList.add(this.baseIngredient);
		addIngredientStacks(defaultedList);
		return defaultedList;
	}
	
	@Override
	public boolean matches(@NotNull Container inv, Level world) {
		if (enoughExperienceSupplied(inv)) {
			return super.matches(inv, world);
		}
		return false;
	}
	
	// we just test for a single ExperienceStorageItem here instead
	// of iterating over every item. The specification mentions that
	// Only one is supported and just a single ExperienceStorageItem
	// should be used per recipe, tough
	private boolean enoughExperienceSupplied(Container inv) {
		if (this.requiredExperience > 0) {
			for (int i : new int[]{PotionWorkshopBlockEntity.BASE_INPUT_SLOT_ID, PotionWorkshopBlockEntity.FIRST_INGREDIENT_SLOT,
					PotionWorkshopBlockEntity.FIRST_INGREDIENT_SLOT + 1, PotionWorkshopBlockEntity.FIRST_INGREDIENT_SLOT + 2}) {
				
				if ((inv.getItem(i).getItem() instanceof ExperienceStorageItem)) {
					return ExperienceStorageItem.getStoredExperience(inv.getItem(i)) >= requiredExperience;
				}
			}
		}
		return true;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess registryManager) {
		return output;
	}
	
	@Override
	public int getMinOutputCount(ItemStack itemStack) {
		return 1;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.POTION_WORKSHOP_CRAFTING_ID;
	}
	
}
