package de.dafuqs.spectrum.enchantments;

import dev.emi.trinkets.api.Trinket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SteadfastEnchantment extends SpectrumEnchantment {
	
	public SteadfastEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
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
		Item item = stack.getItem();
		return super.canEnchant(stack) || this.category.canEnchant(item) || item instanceof TieredItem || item instanceof ShearsItem || item instanceof Vanishable || item instanceof Trinket;
	}
	
}
