package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class CloversFavorEnchantment extends SpectrumEnchantment {
	
	public CloversFavorEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.WEAPON, slotTypes, unlockAdvancementIdentifier);
	}
	
	public static float rollChance(float baseChance, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			int rareLootLevel = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.CLOVERS_FAVOR, livingEntity.getMainHandItem(), entity);
			if (rareLootLevel > 0) {
				return baseChance * (float) rareLootLevel * rareLootLevel;
			}
		}
		return 0;
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
		return SpectrumCommon.CONFIG.CloversFavorMaxLevel;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != Enchantments.MOB_LOOTING;
	}
	
}
