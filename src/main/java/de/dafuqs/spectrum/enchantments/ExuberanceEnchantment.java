package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.items.tools.SpectrumFishingRodItem;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ExuberanceEnchantment extends SpectrumEnchantment {
	
	public ExuberanceEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.WEAPON, slotTypes, unlockAdvancementIdentifier);
	}
	
	public static float getExuberanceMod(Player breakingPlayer) {
		if (breakingPlayer != null && EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.EXUBERANCE, breakingPlayer.getMainHandItem()) > 0) {
			int exuberanceLevel = EnchantmentHelper.getEnchantmentLevel(SpectrumEnchantments.EXUBERANCE, breakingPlayer);
			return getExuberanceMod(exuberanceLevel);
		} else {
			return 1.0F;
		}
	}
	
	public static float getExuberanceMod(int level) {
		return 1.0F + level * SpectrumCommon.CONFIG.ExuberanceBonusExperiencePercentPerLevel;
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
		return SpectrumCommon.CONFIG.ExuberanceMaxLevel;
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.getItem() instanceof AxeItem || EnchantmentCategory.DIGGER.canEnchant(stack.getItem()) || stack.getItem() instanceof SpectrumFishingRodItem;
	}
	
}