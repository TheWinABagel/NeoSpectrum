package de.dafuqs.spectrum.loot.functions;

import com.google.gson.*;
import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import de.dafuqs.spectrum.loot.SpectrumLootFunctionTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FillPotionFillableLootCondition extends LootItemConditionalFunction {
	
	record InkPoweredPotionTemplate(boolean ambient, boolean showParticles, NumberProvider duration,
									List<MobEffect> statusEffects, int color, NumberProvider amplifier,
									List<InkColor> inkColors, NumberProvider inkCost, boolean unidentifiable) {
		
		public static InkPoweredPotionTemplate fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			boolean ambient = GsonHelper.getAsBoolean(jsonObject, "ambient", false);
			boolean showParticles = GsonHelper.getAsBoolean(jsonObject, "show_particles", false);
			boolean unidentifiable = GsonHelper.getAsBoolean(jsonObject, "unidentifiable", false);
			NumberProvider duration = GsonHelper.getAsObject(jsonObject, "duration", jsonDeserializationContext, NumberProvider.class);
			Set<MobEffect> statusEffects = new HashSet<>();
			JsonElement statusEffectElement = jsonObject.get("status_effect");
			if (statusEffectElement instanceof JsonArray jsonArray) {
				for (JsonElement element : jsonArray) {
					statusEffects.add(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryParse(element.getAsString())));
				}
			} else {
				statusEffects.add(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryParse(statusEffectElement.getAsString())));
			}
			
			int color = GsonHelper.getAsInt(jsonObject, "color", -1);
			NumberProvider amplifier = GsonHelper.getAsObject(jsonObject, "amplifier", jsonDeserializationContext, NumberProvider.class);
			NumberProvider inkCost = GsonHelper.getAsObject(jsonObject, "ink_cost", jsonDeserializationContext, NumberProvider.class);
			
			Set<InkColor> inkColors = new HashSet<>();
			JsonElement colorElement = jsonObject.get("ink_color");
			if (colorElement instanceof JsonArray jsonArray) {
				for (JsonElement element : jsonArray) {
					inkColors.add(InkColor.of(element.getAsString()));
				}
			} else {
				inkColors.add(InkColor.of(colorElement.getAsString()));
			}
			
			return new InkPoweredPotionTemplate(ambient, showParticles, duration, statusEffects.stream().toList(), color, amplifier, inkColors.stream().toList(), inkCost, unidentifiable);
		}
		
		public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("ambient", this.ambient);
			jsonObject.addProperty("show_particles", this.showParticles);
			jsonObject.add("duration", jsonSerializationContext.serialize(this.duration));
			JsonArray statusEffectArray = new JsonArray();
			for (MobEffect statusEffect : this.statusEffects) {
				statusEffectArray.add(BuiltInRegistries.MOB_EFFECT.getKey(statusEffect).toString());
			}
			jsonObject.add("status_effect", statusEffectArray);
			jsonObject.addProperty("color", this.color);
			jsonObject.addProperty("unidentifiable", this.unidentifiable);
			jsonObject.add("amplifier", jsonSerializationContext.serialize(this.amplifier));
			jsonObject.add("ink_cost", jsonSerializationContext.serialize(this.inkCost));
			
			JsonArray inkColorArray = new JsonArray();
			for (InkColor inkColor : this.inkColors) {
				inkColorArray.add(inkColor.toString());
			}
			jsonObject.add("ink_color", inkColorArray);
		}
		
		public InkPoweredStatusEffectInstance get(LootContext context) {
			MobEffect statusEffect = this.statusEffects.get(context.getRandom().nextInt(this.statusEffects.size()));
			MobEffectInstance statusEffectInstance = new MobEffectInstance(statusEffect, this.duration.getInt(context), this.amplifier.getInt(context), ambient, showParticles, true);
			InkColor inkColor = this.inkColors.get(context.getRandom().nextInt(this.inkColors.size()));
			int cost = this.inkCost.getInt(context);
			return new InkPoweredStatusEffectInstance(statusEffectInstance, new InkCost(inkColor, cost), this.color, this.unidentifiable);
		}
		
	}
	
	final InkPoweredPotionTemplate template;
	
	FillPotionFillableLootCondition(LootItemCondition[] conditions, InkPoweredPotionTemplate template) {
		super(conditions);
		this.template = template;
	}
	
	@Override
	public LootItemFunctionType getType() {
		return SpectrumLootFunctionTypes.FILL_POTION_FILLABLE;
	}
	
	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		if (this.template == null) {
			return stack;
		}
		if (!(stack.getItem() instanceof InkPoweredPotionFillable inkPoweredPotionFillable)) {
			return stack;
		}
		if (inkPoweredPotionFillable.isFull(stack)) {
			return stack;
		}
		
		InkPoweredStatusEffectInstance effect = template.get(context);
		inkPoweredPotionFillable.addOrUpgradeEffects(stack, List.of(effect));
		
		return stack;
	}
	
	public static LootItemConditionalFunction.Builder<?> builder(InkPoweredPotionTemplate template) {
		return simpleBuilder((conditions) -> new FillPotionFillableLootCondition(conditions, template));
	}
	
	public static class Serializer extends LootItemConditionalFunction.Serializer<FillPotionFillableLootCondition> {
		
		@Override
		public void serialize(JsonObject jsonObject, FillPotionFillableLootCondition lootFunction, JsonSerializationContext jsonSerializationContext) {
			super.serialize(jsonObject, lootFunction, jsonSerializationContext);
			lootFunction.template.toJson(jsonObject, jsonSerializationContext);
		}
		
		@Override
		public FillPotionFillableLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
			return new FillPotionFillableLootCondition(lootConditions, InkPoweredPotionTemplate.fromJson(jsonObject, jsonDeserializationContext));
		}
	}
	
}
