package de.dafuqs.spectrum.explosion.modifier;

import de.dafuqs.spectrum.explosion.ExplosionModifier;
import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentAddingModifier extends ExplosionModifier {
	
	private final Enchantment enchantment;
	private final int level;
	
	public EnchantmentAddingModifier(ExplosionModifierType type, Enchantment enchantment, int level, ParticleOptions particleEffect, int displayColor) {
		super(type, displayColor);
		this.enchantment = enchantment;
		this.level = level;
	}
	
	@Override
	public void addEnchantments(ItemStack stack) {
		stack.enchant(enchantment, level);
	}
	
}
