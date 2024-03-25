package de.dafuqs.spectrum.items.tools;

import de.dafuqs.arrowhead.api.ArrowheadCrossbow;
import de.dafuqs.spectrum.api.item.Preenchanted;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public class BedrockCrossbowItem extends CrossbowItem implements Preenchanted, ArrowheadCrossbow {
	
	public BedrockCrossbowItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.QUICK_CHARGE, 4);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public float getProjectileVelocityModifier(ItemStack stack) {
		return 1.5F;
	}

	@Override
	public float getPullTimeModifier(ItemStack stack) {
		return 3.0F;
	}

	@Override
	public float getDivergenceMod(ItemStack stack) {
		return 0.8F;
	}
	
}