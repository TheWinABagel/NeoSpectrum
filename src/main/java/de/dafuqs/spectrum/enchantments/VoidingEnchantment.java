package de.dafuqs.spectrum.enchantments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class VoidingEnchantment extends SpectrumEnchantment {
	
	public VoidingEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.DIGGER, slotTypes, unlockAdvancementIdentifier);
	}
	
	@Override
	public int getMinCost(int level) {
		return 25;
	}
	
	@Override
	public int getMaxCost(int level) {
		return 50;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public boolean isCurse() {
		return true;
	}
	
}
