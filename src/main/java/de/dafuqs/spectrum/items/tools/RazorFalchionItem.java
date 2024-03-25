package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.item.Preenchanted;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public class RazorFalchionItem extends SwordItem implements Preenchanted {
	
	public RazorFalchionItem(Tier toolMaterial, int attackDamage, float attackSpeed, Properties settings) {
		super(toolMaterial, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.MOB_LOOTING, 3);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
}
