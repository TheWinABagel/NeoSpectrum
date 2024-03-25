package de.dafuqs.spectrum.registries;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.status_effects.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SpectrumStatusEffects {
	
	public static boolean effectsAreGettingStacked = false;
	
	/**
	 * Clears negative effects on the entity
	 * and makes it immune against new ones
	 */
	public static final MobEffect IMMUNITY = registerStatusEffect("immunity", new ImmunityStatusEffect(MobEffectCategory.NEUTRAL, 0x4bbed5));
	
	/**
	 * Like Saturation, but not OP
	 */
	public static final MobEffect NOURISHING = registerStatusEffect("nourishing", new NourishingStatusEffect(MobEffectCategory.BENEFICIAL, 0x2324f8));
	
	/**
	 * Rerolls loot table entry counts based on chance (like with fortune/looting) and takes the best one
	 */
	public static final MobEffect ANOTHER_ROLL = registerStatusEffect("another_roll", new SpectrumStatusEffect(MobEffectCategory.BENEFICIAL, 0xa1ce00));
	
	/**
	 * Stops natural regeneration
	 * and prevents sprinting
	 */
	public static final MobEffect SCARRED = registerStatusEffect("scarred", new ScarredStatusEffect(MobEffectCategory.HARMFUL, 0x5b1d1d));
	
	/**
	 * Increases all incoming damage by potency %
	 */
	public static final float VULNERABILITY_ADDITIONAL_DAMAGE_PERCENT_PER_LEVEL = 0.25F;
	public static final MobEffect VULNERABILITY = registerStatusEffect("vulnerability", new SpectrumStatusEffect(MobEffectCategory.HARMFUL, 0x353535));
	
	/**
	 * Removes gravity to the entity
	 * entities will fall slower and with high potencies start levitating
	 */
	public static final MobEffect LIGHTWEIGHT = registerStatusEffect("lightweight", new GravityStatusEffect(MobEffectCategory.NEUTRAL, 0x00dde4, 0.02F));
	
	/**
	 * Adds gravity to the entity
	 * flying mobs will fall and be nearly unable to fall (phantom, ghast)
	 */
	public static final MobEffect DENSITY = registerStatusEffect("density", new GravityStatusEffect(MobEffectCategory.HARMFUL, 0x671a25, -0.02F));
	
	/**
	 * Increases attack speed
	 */
	public static final MobEffect SWIFTNESS = registerStatusEffect("swiftness", new SpectrumStatusEffect(MobEffectCategory.BENEFICIAL, 0xffe566)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "3c2c6c5e-0a9f-4a0a-8ded-314ae028a753", 2 * 0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
	
	/**
	 * Decreases attack speed
	 */
	public static final MobEffect STIFFNESS = registerStatusEffect("stiffness", new SpectrumStatusEffect(MobEffectCategory.HARMFUL, 0x7e7549))
			.addAttributeModifier(Attributes.ATTACK_SPEED, "91e58b5a-d8d9-4037-a520-18c3d7230502", 2 * -0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	
	/**
	 * Reduces incoming magic damage by 1 point / level
	 */
	public static final MobEffect MAGIC_ANNULATION = registerStatusEffect("magic_annulation", new SpectrumStatusEffect(MobEffectCategory.BENEFICIAL, 0x7a1082))
			.addAttributeModifier(AdditionalEntityAttributes.MAGIC_PROTECTION, "2d307e1f-fcc5-4c53-9821-3a7da4a6ef19", 1, AttributeModifier.Operation.ADDITION);
	
	/**
	 * Like poison, but is able to kill
	 */
	public static final MobEffect DEADLY_POISON = registerStatusEffect("deadly_poison", new DeadlyPoisonStatusEffect(MobEffectCategory.HARMFUL, 5149489));
	
	/**
	 * Increases toughness. Simple, effective
	 */
	public static final MobEffect TOUGHNESS = registerStatusEffect("toughness", new SpectrumStatusEffect(MobEffectCategory.BENEFICIAL, 0x28bbe0)
			.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "599817d7-e8d2-4cbc-962b-59b7050ca59c", 1.0, AttributeModifier.Operation.ADDITION));
	
	/**
	 * Ouch.
	 */
	public static final MobEffect EFFECT_PROLONGING = registerStatusEffect("effect_prolonging", new EffectProlongingStatusEffect(MobEffectCategory.BENEFICIAL, 0xc081d5));
	
	/**
	 * Ouch.
	 */
	public static final MobEffect LIFE_DRAIN = registerStatusEffect("life_drain", new LifeDrainStatusEffect(MobEffectCategory.HARMFUL, 0x222222)
			.addAttributeModifier(Attributes.MAX_HEALTH, LifeDrainStatusEffect.ATTRIBUTE_UUID_STRING, -1.0, AttributeModifier.Operation.ADDITION));
	
	/**
	 * Gives loads of buffs, but the player will be handled as if they were playing hardcore
	 */
	public static final MobEffect ASCENSION = registerStatusEffect("ascension", new AscensionStatusEffect(MobEffectCategory.BENEFICIAL, 0xdff9fc));
	public static final MobEffect DIVINITY = registerStatusEffect("divinity", new DivinityStatusEffect(MobEffectCategory.BENEFICIAL, 0xdff9fc)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "2a0a2299-1387-47eb-a120-58bc70a739d8", 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "b8b33b2c-1804-4ec6-9430-7d1a85f9b13b", 0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "b03b6e37-1dc5-4a93-bbae-0ea96c5bd8f8", 2.0D, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "f9e4ae93-2cf5-4ef5-b06a-ae4fefd5c035", 1.0D, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ARMOR, "ce69cebb-c3fe-4f00-8d4a-0e3d524f237e", 2.0D, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "5af92757-cdf2-4443-856c-9f5eb633b1ef", 2.0D, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "924896a5-8538-4b83-a510-509bccf0a897", 1.0D, AttributeModifier.Operation.ADDITION));
	
	/**
	 * damage, attack speed, speed & knockback resistance are buffed the more the player kills.
	 * But if they do not score a kill in 20 seconds they get negative effects.
	 * Stacking $(thing)Frenzy$() (applying the effect while they already have it) increases these effects amplitude
	 */
	public static final MobEffect FRENZY = registerStatusEffect("frenzy", new FrenzyStatusEffect(MobEffectCategory.NEUTRAL, 0x990000))
			.addAttributeModifier(Attributes.ATTACK_SPEED, FrenzyStatusEffect.ATTACK_SPEED_UUID_STRING, FrenzyStatusEffect.ATTACK_SPEED_PER_STAGE, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, FrenzyStatusEffect.ATTACK_DAMAGE_UUID_STRING, FrenzyStatusEffect.ATTACK_DAMAGE_PER_STAGE, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, FrenzyStatusEffect.MOVEMENT_SPEED_UUID_STRING, FrenzyStatusEffect.MOVEMENT_SPEED_PER_STAGE, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, FrenzyStatusEffect.KNOCKBACK_RESISTANCE_UUID_STRING, FrenzyStatusEffect.KNOCKBACK_RESISTANCE_PER_STAGE, AttributeModifier.Operation.ADDITION);
	
	/**
	 * Increases speed and visibility in lava
	 */
	public static final MobEffect LAVA_GLIDING = registerStatusEffect("lava_gliding", new SpectrumStatusEffect(MobEffectCategory.BENEFICIAL, 0xc42e0e)
			.addAttributeModifier(AdditionalEntityAttributes.LAVA_SPEED, "9812c88f-dc8e-47d1-a092-38339da9891e", 0.1D, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(AdditionalEntityAttributes.LAVA_VISIBILITY, "9812c88f-dc8e-47d1-a092-38339da9891e", 8.0D, AttributeModifier.Operation.ADDITION));
	
	/**
	 * % Chance to protect from projectiles per level
	 */
	public static final float PROJECTILE_REBOUND_CHANCE_PER_LEVEL = 0.2F;
	public static final MobEffect PROJECTILE_REBOUND = registerStatusEffect("projectile_rebound", new SpectrumStatusEffect(MobEffectCategory.BENEFICIAL, 0x77e6df));
	
	
	private static MobEffect registerStatusEffect(String id, MobEffect entry) {
		return Registry.register(BuiltInRegistries.MOB_EFFECT, SpectrumCommon.locate(id), entry);
	}
	
	public static void register() {
	
	}
	
}
