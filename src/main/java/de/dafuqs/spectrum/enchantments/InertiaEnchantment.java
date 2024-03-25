package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Unique;

public class InertiaEnchantment extends SpectrumEnchantment {
	
	@Unique
	public static final String INERTIA_BLOCK = "Inertia_LastMinedBlock";
	public static final String INERTIA_COUNT = "Inertia_LastMinedBlockCount";
	
	public InertiaEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.DIGGER, slotTypes, unlockAdvancementIdentifier);
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
		return SpectrumCommon.CONFIG.InertiaMaxLevel;
	}
	
	@Override
	public boolean checkCompatibility(Enchantment other) {
		return other != Enchantments.BLOCK_EFFICIENCY && super.checkCompatibility(other);
	}
	
}
