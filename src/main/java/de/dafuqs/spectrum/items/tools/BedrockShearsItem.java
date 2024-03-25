package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.item.Preenchanted;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

// Waiting for https://github.com/FabricMC/fabric/pull/1804
public class BedrockShearsItem extends ShearsItem implements Preenchanted {
	
	public BedrockShearsItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.BLOCK_EFFICIENCY, 6);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}
	
}