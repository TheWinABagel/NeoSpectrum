package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.item.Preenchanted;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public class BedrockHoeItem extends HoeItem implements Preenchanted {
	
	public BedrockHoeItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.BLOCK_FORTUNE, 4);
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