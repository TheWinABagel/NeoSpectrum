package de.dafuqs.spectrum.recipe.potion_workshop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PotionMod {
	
	public int flatDurationBonusTicks = 0;
	public float flatPotencyBonus = 0.0F;
	
	public float durationMultiplier = 1.0F;
	public float potencyMultiplier = 1.0F;
	
	public float flatPotencyBonusPositiveEffects = 0.0F;
	public float flatPotencyBonusNegativeEffects = 0.0F;
	public int flatDurationBonusPositiveEffects = 0;
	public int flatDurationBonusNegativeEffects = 0;
	
	public float additionalRandomPositiveEffectCount = 0;
	public float additionalRandomNegativeEffectCount = 0;
	
	public float chanceToAddLastEffect = 0.0F;
	public float lastEffectDurationMultiplier = 1.0F;
	public float lastEffectPotencyMultiplier = 1.0F;
	
	public float yield = 0;
	public int additionalDrinkDurationTicks = 0;
	
	public boolean makeSplashing = false;
	public boolean makeLingering = false;
	
	public boolean noParticles = false;
	public boolean unidentifiable = false;
	public boolean makeEffectsPositive = false;
	public boolean potentDecreasingEffect = false;
	public boolean negateDecreasingDuration = false;
	public boolean randomColor = false;
	
	public List<Tuple<PotionRecipeEffect, Float>> additionalEffects = new ArrayList<>();
	
	
	public static PotionMod fromJson(JsonObject jsonObject) {
		PotionMod mod = new PotionMod();
		
		if (GsonHelper.isNumberValue(jsonObject, "flat_duration_bonus_ticks")) {
			mod.flatDurationBonusTicks += GsonHelper.getAsInt(jsonObject, "flat_duration_bonus_ticks");
		}
		if (GsonHelper.isNumberValue(jsonObject, "flat_potency_bonus")) {
			mod.flatPotencyBonus += GsonHelper.getAsFloat(jsonObject, "flat_potency_bonus");
		}
		if (GsonHelper.isNumberValue(jsonObject, "duration_multiplier")) {
			mod.durationMultiplier = GsonHelper.getAsFloat(jsonObject, "duration_multiplier");
		}
		if (GsonHelper.isNumberValue(jsonObject, "potency_multiplier")) {
			mod.potencyMultiplier = GsonHelper.getAsFloat(jsonObject, "potency_multiplier");
		}
		if (GsonHelper.isNumberValue(jsonObject, "flat_potency_bonus_positive_effects")) {
			mod.flatPotencyBonusPositiveEffects += GsonHelper.getAsFloat(jsonObject, "flat_potency_bonus_positive_effects");
		}
		if (GsonHelper.isNumberValue(jsonObject, "flat_potency_bonus_negative_effects")) {
			mod.flatPotencyBonusNegativeEffects += GsonHelper.getAsFloat(jsonObject, "flat_potency_bonus_negative_effects");
		}
		if (GsonHelper.isNumberValue(jsonObject, "flat_duration_bonus_positive_effects")) {
			mod.flatDurationBonusPositiveEffects += GsonHelper.getAsInt(jsonObject, "flat_duration_bonus_positive_effects");
		}
		if (GsonHelper.isNumberValue(jsonObject, "flat_duration_bonus_negative_effects")) {
			mod.flatDurationBonusNegativeEffects += GsonHelper.getAsInt(jsonObject, "flat_duration_bonus_negative_effects");
		}
		if (GsonHelper.isNumberValue(jsonObject, "additional_random_positive_effect_count")) {
			mod.additionalRandomPositiveEffectCount += GsonHelper.getAsFloat(jsonObject, "additional_random_positive_effect_count");
		}
		if (GsonHelper.isNumberValue(jsonObject, "additional_random_negative_effect_count")) {
			mod.additionalRandomNegativeEffectCount += GsonHelper.getAsFloat(jsonObject, "additional_random_negative_effect_count");
		}
		if (GsonHelper.isNumberValue(jsonObject, "chance_to_add_last_effect")) {
			mod.chanceToAddLastEffect += GsonHelper.getAsFloat(jsonObject, "chance_to_add_last_effect");
		}
		if (GsonHelper.isNumberValue(jsonObject, "last_effect_potency_modifier")) {
			mod.lastEffectPotencyMultiplier = GsonHelper.getAsFloat(jsonObject, "last_effect_potency_modifier");
		}
		if (GsonHelper.isNumberValue(jsonObject, "last_effect_duration_modifier")) {
			mod.lastEffectDurationMultiplier = GsonHelper.getAsFloat(jsonObject, "last_effect_duration_modifier");
		}
		if (GsonHelper.isNumberValue(jsonObject, "flat_yield_bonus")) {
			mod.yield += GsonHelper.getAsFloat(jsonObject, "flat_yield_bonus");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "make_splashing")) {
			mod.makeSplashing = GsonHelper.getAsBoolean(jsonObject, "make_splashing");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "make_lingering")) {
			mod.makeLingering = GsonHelper.getAsBoolean(jsonObject, "make_lingering");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "no_particles")) {
			mod.noParticles = GsonHelper.getAsBoolean(jsonObject, "no_particles");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "unidentifiable")) {
			mod.unidentifiable = GsonHelper.getAsBoolean(jsonObject, "unidentifiable");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "make_effects_positive")) {
			mod.makeEffectsPositive = GsonHelper.getAsBoolean(jsonObject, "make_effects_positive");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "potent_decreasing_effect")) {
			mod.potentDecreasingEffect = GsonHelper.getAsBoolean(jsonObject, "potent_decreasing_effect");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "negate_decreasing_duration")) {
			mod.negateDecreasingDuration = GsonHelper.getAsBoolean(jsonObject, "negate_decreasing_duration");
		}
		if (GsonHelper.isNumberValue(jsonObject, "additional_drink_duration_ticks")) {
			mod.additionalDrinkDurationTicks = GsonHelper.getAsInt(jsonObject, "additional_drink_duration_ticks");
		}
		if (GsonHelper.isBooleanValue(jsonObject, "random_color")) {
			mod.randomColor = GsonHelper.getAsBoolean(jsonObject, "random_color");
		}
		if (GsonHelper.isArrayNode(jsonObject, "additional_effects")) {
			for (JsonElement e : GsonHelper.getAsJsonArray(jsonObject, "additional_effects")) {
				if (e instanceof JsonObject effectObject) {
					float chance = GsonHelper.getAsFloat(effectObject, "chance", 1.0F);
					PotionRecipeEffect effect = PotionRecipeEffect.read(effectObject);
					mod.additionalEffects.add(new Tuple<>(effect, chance));
				}
			}
		}
		
		return mod;
	}
	
	public void write(FriendlyByteBuf packetByteBuf) {
		packetByteBuf.writeInt(flatDurationBonusTicks);
		packetByteBuf.writeFloat(flatPotencyBonus);
		packetByteBuf.writeFloat(durationMultiplier);
		packetByteBuf.writeFloat(potencyMultiplier);
		packetByteBuf.writeFloat(flatPotencyBonusPositiveEffects);
		packetByteBuf.writeFloat(flatPotencyBonusNegativeEffects);
		packetByteBuf.writeInt(flatDurationBonusPositiveEffects);
		packetByteBuf.writeInt(flatDurationBonusNegativeEffects);
		packetByteBuf.writeFloat(additionalRandomPositiveEffectCount);
		packetByteBuf.writeFloat(additionalRandomNegativeEffectCount);
		packetByteBuf.writeFloat(chanceToAddLastEffect);
		packetByteBuf.writeFloat(lastEffectDurationMultiplier);
		packetByteBuf.writeFloat(lastEffectPotencyMultiplier);
		packetByteBuf.writeFloat(yield);
		packetByteBuf.writeBoolean(makeSplashing);
		packetByteBuf.writeBoolean(makeLingering);
		packetByteBuf.writeBoolean(noParticles);
		packetByteBuf.writeBoolean(unidentifiable);
		packetByteBuf.writeBoolean(makeEffectsPositive);
		packetByteBuf.writeBoolean(potentDecreasingEffect);
		packetByteBuf.writeBoolean(negateDecreasingDuration);
		packetByteBuf.writeInt(additionalDrinkDurationTicks);
		packetByteBuf.writeBoolean(randomColor);
		
		packetByteBuf.writeInt(additionalEffects.size());
		for (Tuple<PotionRecipeEffect, Float> effectAndChance : additionalEffects) {
			effectAndChance.getA().write(packetByteBuf);
			packetByteBuf.writeFloat(effectAndChance.getB());
		}
	}
	
	public static PotionMod fromPacket(FriendlyByteBuf packetByteBuf) {
		PotionMod potionMod = new PotionMod();
		potionMod.flatDurationBonusTicks = packetByteBuf.readInt();
		potionMod.flatPotencyBonus = packetByteBuf.readFloat();
		potionMod.durationMultiplier = packetByteBuf.readFloat();
		potionMod.potencyMultiplier = packetByteBuf.readFloat();
		potionMod.flatPotencyBonusPositiveEffects = packetByteBuf.readFloat();
		potionMod.flatPotencyBonusNegativeEffects = packetByteBuf.readFloat();
		potionMod.flatDurationBonusPositiveEffects = packetByteBuf.readInt();
		potionMod.flatDurationBonusNegativeEffects = packetByteBuf.readInt();
		potionMod.additionalRandomPositiveEffectCount = packetByteBuf.readFloat();
		potionMod.additionalRandomNegativeEffectCount = packetByteBuf.readFloat();
		potionMod.chanceToAddLastEffect = packetByteBuf.readFloat();
		potionMod.lastEffectDurationMultiplier = packetByteBuf.readFloat();
		potionMod.lastEffectPotencyMultiplier = packetByteBuf.readFloat();
		potionMod.yield = packetByteBuf.readFloat();
		potionMod.makeSplashing = packetByteBuf.readBoolean();
		potionMod.makeLingering = packetByteBuf.readBoolean();
		potionMod.noParticles = packetByteBuf.readBoolean();
		potionMod.unidentifiable = packetByteBuf.readBoolean();
		potionMod.makeEffectsPositive = packetByteBuf.readBoolean();
		potionMod.potentDecreasingEffect = packetByteBuf.readBoolean();
		potionMod.negateDecreasingDuration = packetByteBuf.readBoolean();
		potionMod.additionalDrinkDurationTicks = packetByteBuf.readInt();
		potionMod.randomColor = packetByteBuf.readBoolean();
		
		int statusEffectCount = packetByteBuf.readInt();
		for (int i = 0; i < statusEffectCount; i++) {
			potionMod.additionalEffects.add(new Tuple<>(PotionRecipeEffect.read(packetByteBuf), packetByteBuf.readFloat()));
		}
		
		return potionMod;
	}
	
	public void modifyFrom(PotionMod potionMod) {
		this.flatDurationBonusTicks += potionMod.flatDurationBonusTicks;
		this.flatPotencyBonus += potionMod.flatPotencyBonus;
		this.durationMultiplier += potionMod.durationMultiplier - 1;
		this.potencyMultiplier += potionMod.potencyMultiplier - 1;
		this.flatPotencyBonusPositiveEffects += potionMod.flatPotencyBonusPositiveEffects;
		this.flatPotencyBonusNegativeEffects += potionMod.flatPotencyBonusNegativeEffects;
		this.flatDurationBonusPositiveEffects += potionMod.flatDurationBonusPositiveEffects;
		this.flatDurationBonusNegativeEffects += potionMod.flatDurationBonusNegativeEffects;
		this.additionalRandomPositiveEffectCount += potionMod.additionalRandomPositiveEffectCount;
		this.additionalRandomNegativeEffectCount += potionMod.additionalRandomNegativeEffectCount;
		this.chanceToAddLastEffect += potionMod.chanceToAddLastEffect;
		this.lastEffectPotencyMultiplier += potionMod.lastEffectPotencyMultiplier - 1;
		this.lastEffectDurationMultiplier += potionMod.lastEffectDurationMultiplier - 1;
		this.yield += potionMod.yield;
		this.additionalDrinkDurationTicks += potionMod.additionalDrinkDurationTicks;
		this.makeSplashing |= potionMod.makeSplashing;
		this.makeLingering |= potionMod.makeLingering;
		this.noParticles |= potionMod.noParticles;
		this.unidentifiable |= potionMod.unidentifiable;
		this.makeEffectsPositive |= potionMod.makeEffectsPositive;
		this.potentDecreasingEffect |= potionMod.potentDecreasingEffect;
		this.negateDecreasingDuration |= potionMod.negateDecreasingDuration;
		this.randomColor |= potionMod.randomColor;
		this.additionalEffects.addAll(potionMod.additionalEffects);
	}
	
	public int getColor(RandomSource random) {
		return this.randomColor ? java.awt.Color.getHSBColor(random.nextFloat(), 0.7F, 0.9F).getRGB() : this.unidentifiable ? 0x2f2f2f : -1; // dark gray
	}
	
}