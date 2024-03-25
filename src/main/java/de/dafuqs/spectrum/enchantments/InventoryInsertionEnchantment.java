package de.dafuqs.spectrum.enchantments;

import de.dafuqs.spectrum.items.tools.SpectrumFishingRodItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InventoryInsertionEnchantment extends SpectrumEnchantment {
	
	public InventoryInsertionEnchantment(Rarity weight, ResourceLocation unlockAdvancementIdentifier, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentCategory.DIGGER, slotTypes, unlockAdvancementIdentifier);
	}
	
	@Override
	public int getMinCost(int level) {
		return 15;
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
		
		return super.canEnchant(stack)
				|| EnchantmentCategory.WEAPON.canEnchant(item)
				|| EnchantmentCategory.TRIDENT.canEnchant(item)
				|| EnchantmentCategory.BOW.canEnchant(item)
				|| EnchantmentCategory.CROSSBOW.canEnchant(item)
				|| stack.getItem() instanceof ShearsItem
				|| stack.getItem() instanceof SpectrumFishingRodItem;
	}
	
}