package de.dafuqs.spectrum.progression.advancement;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionWorkshopBrewingCriterion extends SimpleCriterionTrigger<PotionWorkshopBrewingCriterion.Conditions> {
	
	static final ResourceLocation ID = SpectrumCommon.locate("potion_workshop_brewing");
	
	public static PotionWorkshopBrewingCriterion.Conditions create(ItemPredicate itemPredicate, MobEffectsPredicate effectsPredicate, MinMaxBounds.Ints brewedCountRange, MinMaxBounds.Ints maxAmplifierRange, MinMaxBounds.Ints maxDurationRange, MinMaxBounds.Ints effectCountRange, MinMaxBounds.Ints uniqueEffectCountRange) {
		return new PotionWorkshopBrewingCriterion.Conditions(ContextAwarePredicate.ANY, itemPredicate, effectsPredicate, brewedCountRange, maxAmplifierRange, maxDurationRange, effectCountRange, uniqueEffectCountRange);
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public PotionWorkshopBrewingCriterion.Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate extended, DeserializationContext advancementEntityPredicateDeserializer) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		MobEffectsPredicate statusEffectsPredicate = MobEffectsPredicate.fromJson(jsonObject.get("effects"));
		MinMaxBounds.Ints brewedCountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("brewed_count"));
		MinMaxBounds.Ints maxAmplifierRange = MinMaxBounds.Ints.fromJson(jsonObject.get("highest_amplifier"));
		MinMaxBounds.Ints maxDurationRange = MinMaxBounds.Ints.fromJson(jsonObject.get("longest_duration"));
		MinMaxBounds.Ints effectCountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("effect_count"));
		MinMaxBounds.Ints uniqueEffectCountRange = MinMaxBounds.Ints.fromJson(jsonObject.get("unique_effect_count"));
		return new PotionWorkshopBrewingCriterion.Conditions(extended, itemPredicate, statusEffectsPredicate, brewedCountRange, maxAmplifierRange, maxDurationRange, effectCountRange, uniqueEffectCountRange);
	}
	
	@SuppressWarnings("deprecation")
	public void trigger(ServerPlayer player, ItemStack itemStack, int brewedCount) {
		this.trigger(player, conditions -> {
			List<MobEffectInstance> effects;
			if (itemStack.getItem() instanceof InkPoweredPotionFillable inkPoweredPotionFillable) {
				effects = inkPoweredPotionFillable.getVanillaEffects(itemStack);
			} else {
				effects = PotionUtils.getMobEffects(itemStack);
			}
			
			int highestAmplifier = 0;
			int longestDuration = 0;
			for (MobEffectInstance instance : effects) {
				if (instance.getAmplifier() > highestAmplifier) {
					highestAmplifier = instance.getAmplifier();
				}
				if (instance.getDuration() > longestDuration) {
					longestDuration = instance.getDuration();
				}
			}
			
			List<MobEffect> uniqueEffects = new ArrayList<>();
			for (MobEffectInstance instance : effects) {
				if (!uniqueEffects.contains(instance.getEffect())) {
					uniqueEffects.add(instance.getEffect());
				}
			}
			
			return conditions.matches(itemStack, effects, brewedCount, highestAmplifier, longestDuration, effects.size(), uniqueEffects.size());
		});
	}
	
	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final ItemPredicate itemPredicate;
		private final MobEffectsPredicate statusEffectsPredicate;
		private final MinMaxBounds.Ints brewedCountRange;
		private final MinMaxBounds.Ints highestEffectAmplifierRange;
		private final MinMaxBounds.Ints longestEffectDurationRange;
		private final MinMaxBounds.Ints effectCountRange;
		private final MinMaxBounds.Ints uniqueEffectCountRange;
		
		public Conditions(ContextAwarePredicate player, ItemPredicate itemPredicate, MobEffectsPredicate statusEffectsPredicate, MinMaxBounds.Ints brewedCountRange, MinMaxBounds.Ints highestEffectAmplifierRange, MinMaxBounds.Ints longestEffectDurationRange, MinMaxBounds.Ints effectCountRange, MinMaxBounds.Ints uniqueEffectCountRange) {
			super(ID, player);
			this.itemPredicate = itemPredicate;
			this.statusEffectsPredicate = statusEffectsPredicate;
			this.brewedCountRange = brewedCountRange;
			this.highestEffectAmplifierRange = highestEffectAmplifierRange;
			this.longestEffectDurationRange = longestEffectDurationRange;
			this.effectCountRange = effectCountRange;
			this.uniqueEffectCountRange = uniqueEffectCountRange;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("items", this.itemPredicate.serializeToJson());
			jsonObject.add("effects", this.statusEffectsPredicate.serializeToJson());
			jsonObject.add("brewed_count", this.brewedCountRange.serializeToJson());
			jsonObject.add("highest_amplifier", this.highestEffectAmplifierRange.serializeToJson());
			jsonObject.add("longest_duration", this.longestEffectDurationRange.serializeToJson());
			jsonObject.add("effect_count", this.effectCountRange.serializeToJson());
			jsonObject.add("unique_effect_count", this.uniqueEffectCountRange.serializeToJson());
			return jsonObject;
		}
		
		public boolean matches(ItemStack stack, List<MobEffectInstance> effects, int brewedCount, int maxAmplifier, int maxDuration, int effectCount, int uniqueEffectCount) {
			if (this.brewedCountRange.matches(brewedCount) &&
					this.highestEffectAmplifierRange.matches(maxAmplifier) &&
					this.longestEffectDurationRange.matches(maxDuration) &&
					this.effectCountRange.matches(effectCount) &&
					this.uniqueEffectCountRange.matches(uniqueEffectCount) &&
					this.itemPredicate.matches(stack))
			{
				Map<MobEffect, MobEffectInstance> effectMap = new HashMap<>();
				for (MobEffectInstance instance : effects) {
					if (!effectMap.containsKey(instance.getEffect())) {
						effectMap.put(instance.getEffect(), instance);
					}
				}
				
				return this.statusEffectsPredicate.matches(effectMap);
			}

			return false;
		}
	}
	
}
