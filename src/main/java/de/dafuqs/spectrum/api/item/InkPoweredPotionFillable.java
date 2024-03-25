package de.dafuqs.spectrum.api.item;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface InkPoweredPotionFillable {
	
	int maxEffectCount();
	int maxEffectAmplifier();
	
	// used for calculating the items cost to apply a certain effect
	// calculated once and then stored in the items nbt for quick lookup and nicer modifiability
	// via commands or special loot (so ones found in dungeon chests can be cheaper!)
	default long adjustFinalCostFor(@NotNull InkPoweredStatusEffectInstance instance) {
		return (long) Math.pow(instance.getInkCost().getCost(), 1 + instance.getStatusEffectInstance().getAmplifier());
	}
	
	// saving
	default void addOrUpgradeEffects(ItemStack potionFillableStack, List<InkPoweredStatusEffectInstance> newEffects) {
		if (!isFull(potionFillableStack)) {
			List<InkPoweredStatusEffectInstance> existingEffects = InkPoweredStatusEffectInstance.getEffects(potionFillableStack);
			int maxCount = maxEffectCount();
			int maxAmplifier = maxEffectAmplifier();
			for (InkPoweredStatusEffectInstance newEffect : newEffects) {
				MobEffectInstance statusEffectInstance = newEffect.getStatusEffectInstance();
				if (statusEffectInstance.getAmplifier() > maxAmplifier) {
					statusEffectInstance = new MobEffectInstance(statusEffectInstance.getEffect(), statusEffectInstance.getDuration(), maxAmplifier, statusEffectInstance.isAmbient(), statusEffectInstance.isVisible());
				}
				if (existingEffects.size() == maxCount) {
					break;
				}
				
				// calculate the final cost of this effect and add it
				InkCost adjustedCost = new InkCost(newEffect.getInkCost().getColor(), adjustFinalCostFor(newEffect));
				InkPoweredStatusEffectInstance modifiedInstance = new InkPoweredStatusEffectInstance(statusEffectInstance, adjustedCost, newEffect.getColor(), newEffect.isUnidentifiable());
				existingEffects.add(modifiedInstance);
			}
			
			InkPoweredStatusEffectInstance.setEffects(potionFillableStack, existingEffects);
		}
	}
	
	default List<InkPoweredStatusEffectInstance> getEffects(ItemStack stack) {
		return InkPoweredStatusEffectInstance.getEffects(stack);
	}
	
	@Deprecated
	default List<MobEffectInstance> getVanillaEffects(ItemStack stack) {
		List<MobEffectInstance> effects = new ArrayList<>();
		for (InkPoweredStatusEffectInstance instance : InkPoweredStatusEffectInstance.getEffects(stack)) {
			effects.add(instance.getStatusEffectInstance());
		}
		return effects;
	}
	
	default boolean isFull(ItemStack itemStack) {
		return InkPoweredStatusEffectInstance.getEffects(itemStack).size() >= maxEffectCount();
	}
	
	default boolean isAtLeastPartiallyFilled(ItemStack itemStack) {
		return InkPoweredStatusEffectInstance.getEffects(itemStack).size() > 0;
	}
	
	default void clearEffects(ItemStack itemStack) {
		itemStack.removeTagKey(InkPoweredStatusEffectInstance.NBT_KEY);
	}
	
	default void appendPotionFillableTooltip(ItemStack stack, List<Component> tooltip, MutableComponent attributeModifierText, boolean showDuration) {
		List<InkPoweredStatusEffectInstance> effects = InkPoweredStatusEffectInstance.getEffects(stack);
		InkPoweredStatusEffectInstance.buildTooltip(tooltip, effects, attributeModifierText, showDuration);
		
		int maxEffectCount = maxEffectCount();
		if (effects.size() < maxEffectCount) {
			if (maxEffectCount == 1) {
				tooltip.add(Component.translatable("item.spectrum.potion_pendant.tooltip_not_full_one"));
			} else {
				tooltip.add(Component.translatable("item.spectrum.potion_pendant.tooltip_not_full_count", maxEffectCount));
			}
			tooltip.add(Component.translatable("item.spectrum.potion_pendant.tooltip_max_level").append(Component.translatable("enchantment.level." + (maxEffectAmplifier() + 1))));
		}
	}

	default boolean isWeapon() {
		return false;
	}
	
}