package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class PestControlEnchantment extends SpectrumEnchantment {
	
	public PestControlEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.DIGGER, slotTypes, unlockAdvancementIdentifier);
	}
	
	@Override
	public int getMinCost(int level) {
		return 10;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 20;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != SpectrumEnchantments.RESONANCE;
	}
	
}