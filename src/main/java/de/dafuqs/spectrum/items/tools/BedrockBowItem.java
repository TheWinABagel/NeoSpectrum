package de.dafuqs.spectrum.items.tools;

import de.dafuqs.arrowhead.api.ArrowheadBow;
import de.dafuqs.spectrum.api.item.Preenchanted;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public class BedrockBowItem extends BowItem implements Preenchanted, ArrowheadBow {
	
	public BedrockBowItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.POWER_ARROWS, 6);
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
	public float getZoom(ItemStack stack) {
		return 30F;
	}

	@Override
	public float getProjectileVelocityModifier(ItemStack stack) {
		return 1.3F;
	}

	@Override
	public float getDivergenceMod(ItemStack stack) {
		return 0.8F;
	}
	
}