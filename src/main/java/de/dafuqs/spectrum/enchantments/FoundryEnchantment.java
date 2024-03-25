package de.dafuqs.spectrum.enchantments;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.compat.gofish.GoFishCompat;
import de.dafuqs.spectrum.items.tools.MoltenRodItem;
import de.dafuqs.spectrum.items.tools.SpectrumFishingRodItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FoundryEnchantment extends SpectrumEnchantment {
	
	private static final AutoSmeltInventory autoSmeltInventory = new AutoSmeltInventory();
	
	public FoundryEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.DIGGER, slotTypes, unlockAdvancementIdentifier);
	}
	
	public static ItemStack getAutoSmeltedItemStack(ItemStack inputItemStack, Level world) {
		SmeltingRecipe smeltingRecipe = autoSmeltInventory.getRecipe(inputItemStack, world);
		if (smeltingRecipe != null) {
			ItemStack recipeOutputStack = smeltingRecipe.getResultItem(world.registryAccess()).copy();
			recipeOutputStack.setCount(recipeOutputStack.getCount() * inputItemStack.getCount());
			return recipeOutputStack;
		} else {
			return null;
		}
	}
	
	@NotNull
	public static List<ItemStack> applyFoundry(Level world, List<ItemStack> originalStacks) {
		List<ItemStack> returnItemStacks = new ArrayList<>();
		
		for (ItemStack is : originalStacks) {
			ItemStack smeltedStack = FoundryEnchantment.getAutoSmeltedItemStack(is, world);
			if (smeltedStack == null) {
				returnItemStacks.add(is);
			} else {
				while (smeltedStack.getCount() > 0) {
					int currentAmount = Math.min(smeltedStack.getCount(), smeltedStack.getItem().getMaxStackSize());
					ItemStack currentStack = smeltedStack.copy();
					currentStack.setCount(currentAmount);
					returnItemStacks.add(currentStack);
					smeltedStack.setCount(smeltedStack.getCount() - currentAmount);
				}
			}
		}
		return returnItemStacks;
	}
	
	@Override
	public int getMinCost(int level) {
		return 15;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 50;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != Enchantments.SILK_TOUCH && !GoFishCompat.isDeepfry(other);
	}
	
	@Override
	public boolean canEntityUse(Entity entity) {
		return super.canEntityUse(entity) || (entity instanceof Player playerEntity && AdvancementHelper.hasAdvancement(playerEntity, MoltenRodItem.UNLOCK_IDENTIFIER));
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.getItem() instanceof SpectrumFishingRodItem;
	}
	
	public static class AutoSmeltInventory implements Container, StackedContentsCompatible {
		ItemStack input = ItemStack.EMPTY;
		
		@Override
		public int getContainerSize() {
			return 1;
		}
		
		@Override
		public boolean isEmpty() {
			return input.isEmpty();
		}
		
		@Override
		public ItemStack getItem(int slot) {
			return input;
		}
		
		@Override
		public ItemStack removeItem(int slot, int amount) {
			return null;
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot) {
			return null;
		}
		
		@Override
		public void setItem(int slot, ItemStack stack) {
			this.input = stack;
		}
		
		@Override
		public void setChanged() {
		}
		
		@Override
		public boolean stillValid(Player player) {
			return false;
		}
		
		@Override
		public void clearContent() {
			input = ItemStack.EMPTY;
		}
		
		private SmeltingRecipe getRecipe(ItemStack itemStack, Level world) {
			setItem(0, itemStack);
			return world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this, world).orElse(null);
		}
		
		@Override
		public void fillStackedContents(StackedContents recipeMatcher) {
			recipeMatcher.accountStack(input);
		}
		
	}
	
}