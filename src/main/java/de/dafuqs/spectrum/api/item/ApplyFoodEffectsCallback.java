package de.dafuqs.spectrum.api.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ApplyFoodEffectsCallback {
	
	/**
	 * Called when an item is consumed by an entity
	 *
	 * @param stack  the stack that is consumed
	 * @param entity the entity consuming the item
	 */
	void afterConsumption(Level world, ItemStack stack, LivingEntity entity);
	
	static void applyFoodComponent(Level world, LivingEntity entity, FoodProperties foodComponent) {
		if (entity instanceof Player player) {
			player.getFoodData().eat(foodComponent.getNutrition(), foodComponent.getSaturationModifier());
		}
		
		List<Pair<MobEffectInstance, Float>> list = foodComponent.getEffects();
		for (Pair<MobEffectInstance, Float> statusEffectInstanceFloatPair : list) {
			if (!world.isClientSide && statusEffectInstanceFloatPair.getFirst() != null && world.random.nextFloat() < statusEffectInstanceFloatPair.getSecond()) {
				entity.addEffect(new MobEffectInstance(statusEffectInstanceFloatPair.getFirst()));
			}
		}
	}
	
}
