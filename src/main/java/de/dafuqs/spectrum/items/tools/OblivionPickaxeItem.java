package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class OblivionPickaxeItem extends SpectrumPickaxeItem {
	
	public OblivionPickaxeItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
		super.mineBlock(stack, world, state, pos, miner);
		
		// Break the tool if it is used without the voiding enchantment
		// Otherwise this would be a VERY cheap early game diamond tier tool
		if (!world.isClientSide && !EnchantmentHelper.getEnchantments(stack).containsKey(SpectrumEnchantments.VOIDING)) {
			stack.hurtAndBreak(5000, miner, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		
		return true;
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(SpectrumEnchantments.VOIDING, 1);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
}