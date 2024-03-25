package de.dafuqs.spectrum.api.item;

import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Items deserve the ability to be seen
 * not by the target assigned to them by
 * their inheritance, but by what they
 * decide themselves to be.
 * <p>
 * The conditions of one's birth do not
 * dictate who we are.
 */
public interface TranstargetItem {

    EnchantmentCategory getRealTarget();
}