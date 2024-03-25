package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.UUID;

public class ImprovedCriticalEnchantment extends SpectrumEnchantment {
	
	public static final UUID EXTRA_CRIT_DAMAGE_MULTIPLIER_ATTRIBUTE_UUID = UUID.fromString("e9bca8d4-9dcb-4e9e-8a7b-48b129c7ec5a");
	public static final String EXTRA_CRIT_DAMAGE_MULTIPLIER_ATTRIBUTE_NAME = "spectrum:improved_critical";
	
	public ImprovedCriticalEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.WEAPON, slotTypes, unlockAdvancementIdentifier);
	}
	
	public static float getAddtionalCritDamageMultiplier(int improvedCriticalLevel) {
		return SpectrumCommon.CONFIG.ImprovedCriticalExtraDamageMultiplierPerLevel * improvedCriticalLevel;
	}
	
	@Override
	public int getMinCost(int level) {
		return 10;
	}
	
	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 30;
	}
	
	@Override
	public int getMaxLevel() {
		return SpectrumCommon.CONFIG.ImprovedCriticalMaxLevel;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return other != Enchantments.SHARPNESS && super.checkCompatibility(other);
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.getItem() instanceof AxeItem;
	}
	
}

