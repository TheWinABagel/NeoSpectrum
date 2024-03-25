package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ResonanceEnchantment extends SpectrumEnchantment {
	
	public ResonanceEnchantment(Enchantment.Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.DIGGER, slotTypes, unlockAdvancementIdentifier);
	}
	
	@Override
	public int getMinCost(int level) {
		return 25;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 75;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != SpectrumEnchantments.PEST_CONTROL && other != Enchantments.SILK_TOUCH && other != Enchantments.BLOCK_FORTUNE;
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.getItem() instanceof ShearsItem;
	}
	
}