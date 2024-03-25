package de.dafuqs.spectrum.api.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Preenchanted {
	
	Map<Enchantment, Integer> getDefaultEnchantments();
	
	default @NotNull ItemStack getDefaultEnchantedStack(Item item) {
		ItemStack itemStack = new ItemStack(item);
		for (Map.Entry<Enchantment, Integer> defaultEnchantment : getDefaultEnchantments().entrySet()) {
			itemStack.enchant(defaultEnchantment.getKey(), defaultEnchantment.getValue());
		}
		return itemStack;
	}
	
	/**
	 * Checks a stack if it only has enchantments that are lower or equal its DefaultEnchantments,
	 * meaning enchantments had been added on top of the original ones.
	 */
	default boolean onlyHasPreEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> innateEnchantments = getDefaultEnchantments();
		Map<Enchantment, Integer> stackEnchantments = EnchantmentHelper.getEnchantments(stack);
		
		for (Map.Entry<Enchantment, Integer> stackEnchantment : stackEnchantments.entrySet()) {
			int innateLevel = innateEnchantments.getOrDefault(stackEnchantment.getKey(), 0);
			if (stackEnchantment.getValue() > innateLevel) {
				return false;
			}
		}
		
		return true;
	}
	
}
