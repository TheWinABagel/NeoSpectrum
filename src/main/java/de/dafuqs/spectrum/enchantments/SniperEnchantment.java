package de.dafuqs.spectrum.enchantments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Increases the speed of shot arrows and makes them invisible
 */
public class SniperEnchantment extends SpectrumEnchantment {
	
	public SniperEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.CROSSBOW, slotTypes, unlockAdvancementIdentifier);
	}
	
	@Override
	public int getMinCost(int level) {
		return 20;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 30;
	}
	
	@Override
	public int getMaxLevel() {
		return 2;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return other != Enchantments.MULTISHOT && super.checkCompatibility(other);
	}
	
}

