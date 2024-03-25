package de.dafuqs.spectrum.api.item;

import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Making Items immune to certain forms of damage
 */
public class ItemDamageImmunity {

	private static final Map<Item, List<TagKey<DamageType>>> damageSourceImmunities = new HashMap<>();
	
	public static void registerImmunity(ItemLike itemConvertible, TagKey<DamageType> damageTypeTag) {
		Item item = itemConvertible.asItem();
		List<TagKey<DamageType>> current = damageSourceImmunities.getOrDefault(item, new ArrayList<>());
		current.add(damageTypeTag);
		damageSourceImmunities.put(item, current);
	}
	
	public static boolean isImmuneTo(ItemStack itemStack, DamageSource damageSource) {
		// otherwise items would fall endlessly when falling into the end, causing lag
		if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
			return false;
		}
		
		// does itemStack have Damage Proof enchantment?
		if (EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.STEADFAST, itemStack) > 0) {
			return true;
			// is item immune to this specific kind of damage?
		}
		
		Item item = itemStack.getItem();
		if (damageSourceImmunities.containsKey(item)) {
			for (TagKey<DamageType> type : damageSourceImmunities.get(item)) {
				if (damageSource.is(type)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean isImmuneTo(ItemStack itemStack, TagKey<DamageType> damageTypeTag) {
		Item item = itemStack.getItem();
		if (damageSourceImmunities.containsKey(item)) {
			for (TagKey<DamageType> type : damageSourceImmunities.get(item)) {
				if (type.equals(damageTypeTag)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
