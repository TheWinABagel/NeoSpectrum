package de.dafuqs.spectrum.recipe.potion_workshop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PotionRecipeEffect(boolean applicableToPotions, boolean applicableToTippedArrows,
								 boolean applicableToPotionFillabes, boolean applicableToWeapons,
								 int baseDurationTicks, float potencyModifier, MobEffect statusEffect,
								 InkColor inkColor, int inkCost) {
	
	public static PotionRecipeEffect read(JsonObject jsonObject) {
		boolean applicableToPotions = GsonHelper.getAsBoolean(jsonObject, "applicable_to_potions", true);
		boolean applicableToTippedArrows = GsonHelper.getAsBoolean(jsonObject, "applicable_to_tipped_arrows", true);
		boolean applicableToPotionFillabes = GsonHelper.getAsBoolean(jsonObject, "applicable_to_potion_fillables", true);
		boolean applicableToWeapons = GsonHelper.getAsBoolean(jsonObject, "applicable_to_potion_weapons", true);
		
		int baseDurationTicks = GsonHelper.getAsInt(jsonObject, "base_duration_ticks", 1600);
		float potencyModifier = GsonHelper.getAsFloat(jsonObject, "potency_modifier", 1.0F);
		
		ResourceLocation statusEffectIdentifier = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "effect"));
		if (!BuiltInRegistries.MOB_EFFECT.containsKey(statusEffectIdentifier)) {
			throw new JsonParseException("Potion Workshop Recipe has a status effect set that does not exist or is disabled: " + statusEffectIdentifier); // otherwise, recipe sync would break multiplayer joining with the non-existing status effect
		}
		MobEffect statusEffect = BuiltInRegistries.MOB_EFFECT.get(statusEffectIdentifier);
		
		InkColor inkColor = InkColor.of(GsonHelper.getAsString(jsonObject, "ink_color"));
		int inkCost = GsonHelper.getAsInt(jsonObject, "ink_cost");
		
		return new PotionRecipeEffect(applicableToPotions, applicableToTippedArrows, applicableToPotionFillabes, applicableToWeapons, baseDurationTicks, potencyModifier, statusEffect, inkColor, inkCost);
	}
	
	public void write(FriendlyByteBuf packetByteBuf) {
		packetByteBuf.writeResourceLocation(BuiltInRegistries.MOB_EFFECT.getKey(statusEffect));
		packetByteBuf.writeInt(baseDurationTicks);
		packetByteBuf.writeFloat(potencyModifier);
		packetByteBuf.writeBoolean(applicableToPotions);
		packetByteBuf.writeBoolean(applicableToTippedArrows);
		packetByteBuf.writeBoolean(applicableToPotionFillabes);
		packetByteBuf.writeBoolean(applicableToWeapons);
		packetByteBuf.writeUtf(inkColor.toString());
		packetByteBuf.writeInt(inkCost);
	}
	
	public static PotionRecipeEffect read(FriendlyByteBuf packetByteBuf) {
		MobEffect statusEffect = BuiltInRegistries.MOB_EFFECT.get(packetByteBuf.readResourceLocation());
		int baseDurationTicks = packetByteBuf.readInt();
		float potencyModifier = packetByteBuf.readFloat();
		boolean applicableToPotions = packetByteBuf.readBoolean();
		boolean applicableToTippedArrows = packetByteBuf.readBoolean();
		boolean applicableToPotionFillabes = packetByteBuf.readBoolean();
		boolean applicableToWeapons = packetByteBuf.readBoolean();
		InkColor inkColor = InkColor.of(packetByteBuf.readUtf());
		int inkCost = packetByteBuf.readInt();
		
		return new PotionRecipeEffect(applicableToPotions, applicableToTippedArrows, applicableToPotionFillabes, applicableToWeapons, baseDurationTicks, potencyModifier, statusEffect, inkColor, inkCost);
	}
	
	public @Nullable InkPoweredStatusEffectInstance getStatusEffectInstance(@NotNull PotionMod potionMod, RandomSource random) {
		float potency = potionMod.flatPotencyBonus;
		int durationTicks = baseDurationTicks() + potionMod.flatDurationBonusTicks;
		switch (statusEffect().getCategory()) {
			case BENEFICIAL -> {
				potency += potionMod.flatPotencyBonusPositiveEffects;
				durationTicks += potionMod.flatDurationBonusPositiveEffects;
			}
			case HARMFUL -> {
				potency += potionMod.flatPotencyBonusNegativeEffects;
				durationTicks += potionMod.flatDurationBonusNegativeEffects;
			}
			default -> {
			}
		}
		durationTicks = statusEffect().isInstantenous() ? 1 : (int) (durationTicks * potionMod.durationMultiplier);
		
		if (potencyModifier() == 0.0F) {
			potency = 0; // effects that only have 1 level, like night vision
		} else {
			potency = (((1 + potency) * potionMod.potencyMultiplier) - 1) * potencyModifier();
			potency = Support.getIntFromDecimalWithChance(potency, random);
			
			// if the result of the potency calculation was negative because of a very low recipe base potencyModifier
			// (not because the player was greedy and got mali because of low multiplicativePotencyModifier)
			// => set to 0 again
			if (potency < 0 && potionMod.potencyMultiplier == 0.0) {
				potency = 0;
			}
		}
		
		if (potency >= 0 && durationTicks > 0) {
			int effectColor = potionMod.getColor(random);
			return new InkPoweredStatusEffectInstance(new MobEffectInstance(statusEffect(), durationTicks, (int) potency, !potionMod.noParticles, !potionMod.noParticles), new InkCost(inkColor(), inkCost()), effectColor, potionMod.unidentifiable);
		} else {
			// the effect is so borked that the effect would be too weak
			return null;
		}
	}
	
	public boolean isApplicableTo(ItemStack baseIngredient, PotionMod potionMod) {
		if (baseIngredient.is(Items.ARROW)) { // arrows require lingering potions as base
			return applicableToTippedArrows && potionMod.makeSplashing && potionMod.makeLingering;
		} else if (baseIngredient.getItem() instanceof InkPoweredPotionFillable inkPoweredPotionFillable) {
			return applicableToPotionFillabes && !inkPoweredPotionFillable.isFull(baseIngredient) || applicableToWeapons && inkPoweredPotionFillable.isWeapon();
		} else {
			return applicableToPotions;
		}
	}
	
}
