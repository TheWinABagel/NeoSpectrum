package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.registries.SpectrumItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class IndestructibleEnchantment extends SpectrumEnchantment {
	
	public IndestructibleEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.BREAKABLE, slotTypes, unlockAdvancementIdentifier);
	}
	
	@Override
	public int getMinCost(int level) {
		return 30;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 30;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other);
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) && !stack.is(SpectrumItemTags.INDESTRUCTIBLE_BLACKLISTED);
	}
	
}
