package de.dafuqs.spectrum.recipe.enchantment_upgrade;

import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.enchanter.EnchanterRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.Map;

public class EnchantmentUpgradeRecipe extends GatedSpectrumRecipe {
	
	protected final Enchantment enchantment;
	protected final int enchantmentDestinationLevel;
	protected final int requiredExperience;
	protected final Item requiredItem;
	protected final int requiredItemCount;
	
	protected final NonNullList<Ingredient> inputs;
	protected final ItemStack output;
	
	public EnchantmentUpgradeRecipe(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Enchantment enchantment, int enchantmentDestinationLevel, int requiredExperience, Item requiredItem, int requiredItemCount) {
		super(id, group, secret, requiredAdvancementIdentifier);
		
		this.enchantment = enchantment;
		this.enchantmentDestinationLevel = enchantmentDestinationLevel;
		this.requiredExperience = requiredExperience;
		this.requiredItem = requiredItem;
		this.requiredItemCount = requiredItemCount;
		
		NonNullList<Ingredient> inputs = NonNullList.withSize(2, Ingredient.EMPTY);
		
		ItemStack ingredientStack = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(ingredientStack, new EnchantmentInstance(enchantment, enchantmentDestinationLevel - 1));
		inputs.set(0, Ingredient.of(ingredientStack));
		inputs.set(1, Ingredient.of(new ItemStack(requiredItem)));
		this.inputs = inputs;
		
		ItemStack outputStack = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(outputStack, new EnchantmentInstance(enchantment, enchantmentDestinationLevel));
		this.output = outputStack;
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		if (inv.getContainerSize() > 9) {
			if (!inputs.get(0).test(inv.getItem(0))) {
				return false;
			}
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(inv.getItem(0));
			if (!enchantments.containsKey(enchantment) || enchantments.get(enchantment) != enchantmentDestinationLevel - 1) {
				return false;
			}
			if (this.getRequiredExperience() > 0
					&& (!(inv.getItem(1).getItem() instanceof ExperienceStorageItem)
					|| !(ExperienceStorageItem.getStoredExperience(inv.getItem(1)) >= this.getRequiredExperience()))) {
				return false;
			}
			
			Ingredient inputIngredient = inputs.get(1);
			int ingredientsFound = 0;
			for (int i = 1; i < 9; i++) {
				ItemStack currentStack = inv.getItem(i + 1);
				
				if (!currentStack.isEmpty()) {
					ItemStack slotStack = inv.getItem(i + 1);
					if (inputIngredient.test(slotStack)) {
						ingredientsFound += slotStack.getCount();
					} else {
						return false;
					}
				}
			}
			
			return ingredientsFound >= requiredItemCount;
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
	public RecipeSerializer<?> getSerializer() {
		return SpectrumRecipeTypes.ENCHANTMENT_UPGRADE_RECIPE_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType() {
		return SpectrumRecipeTypes.ENCHANTMENT_UPGRADE;
	}
	
	@Override
	public ResourceLocation getRecipeTypeUnlockIdentifier() {
		return EnchanterRecipe.UNLOCK_IDENTIFIER;
	}
	
	@Override
	public String getRecipeTypeShortID() {
		return SpectrumRecipeTypes.ENCHANTMENT_UPGRADE_ID;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return inputs;
	}
	
	public int getRequiredExperience() {
		return requiredExperience;
	}
	
	public Item getRequiredItem() {
		return requiredItem;
	}
	
	public int getRequiredItemCount() {
		return requiredItemCount;
	}
	
	public Enchantment getEnchantment() {
		return enchantment;
	}
	
	public int getEnchantmentDestinationLevel() {
		return enchantmentDestinationLevel;
	}
	
	public boolean requiresUnlockedOverEnchanting() {
		return this.enchantmentDestinationLevel > this.enchantment.getMaxLevel();
	}
	
}
