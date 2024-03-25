package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.status_effects.AscensionStatusEffect;
import de.dafuqs.spectrum.status_effects.DivinityStatusEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class SpectrumFoodComponents {
	
	public static final FoodProperties MOONSTRUCK_NECTAR = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2), 1.0F)
			.build();
	
	public static final FoodProperties JADE_JELLY = new FoodProperties.Builder()
			.nutrition(4).saturationMod(0.6F)
			.effect(new MobEffectInstance(MobEffects.HEAL, 1, 2), 0.2F)
			.build();
	
	public static final FoodProperties GLASS_PEACH = new FoodProperties.Builder()
			.nutrition(3).saturationMod(0.6F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, 300, 0), 0.05F)
			.build();
	
	public static final FoodProperties STAR_CANDY = new FoodProperties.Builder()
			.nutrition(3).saturationMod(0.2F).fast()
			.build();
	
	public static final FoodProperties PURPLE_STAR_CANDY = new FoodProperties.Builder()
			.nutrition(20).saturationMod(1.2F).fast()
			.build();
	
	public static final FoodProperties ENCHANTED_GOLDEN_CARROT = new FoodProperties.Builder()
			.nutrition(6).saturationMod(1.2F).alwaysEat()
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 0), 1.0F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
			.effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2400, 0), 1.0F)
			.effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1), 1.0F)
			.build();

	public static final FoodProperties JARAMEL = new FoodProperties.Builder()
			.nutrition(1).saturationMod(0.2F).fast().alwaysEat()
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1), 1.0F)
			.effect(new MobEffectInstance(MobEffects.DIG_SPEED, 300, 1), 1.0F)
			.build();
	
	public static final FoodProperties LUCKY_ROLL = new FoodProperties.Builder()
			.nutrition(4).saturationMod(0.2F).alwaysEat()
			.effect(new MobEffectInstance(SpectrumStatusEffects.ANOTHER_ROLL, 1200), 1.0F)
			.build();
	
	public static final FoodProperties TRIPLE_MEAT_POT_PIE = new FoodProperties.Builder()
			.nutrition(20).saturationMod(1.2F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 100), 1.0F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 12000, 1), 1.0F)
			.build();
	
	public static final FoodProperties GLISTERING_JELLY_TEA = new FoodProperties.Builder()
			.nutrition(4).saturationMod(0.6F).alwaysEat()
			.effect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200), 1.0F)
			.build();
	
	public static final FoodProperties GLISTERING_JELLY_TEA_SCONE_BONUS = new FoodProperties.Builder()
			.effect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 3), 1.0F)
			.build();
	
	public static final FoodProperties RESTORATION_TEA = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).alwaysEat()
			.effect(new MobEffectInstance(SpectrumStatusEffects.IMMUNITY, 1200), 1.0F)
			.build();
	
	public static final FoodProperties RESTORATION_TEA_SCONE_BONUS = new FoodProperties.Builder()
			.effect(new MobEffectInstance(SpectrumStatusEffects.IMMUNITY, 1800), 1.0F)
			.build();
	
	public static final FoodProperties BODACIOUS_BERRY_BAR = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.6F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 600, 2), 1.0F)
			.effect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1800, 4), 1.0F)
			.build();
	
	public static final FoodProperties DEMON_TEA = new FoodProperties.Builder()
			.fast().nutrition(2).saturationMod(0.2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.FRENZY, 800, 1), 2.0F / 3.0F)
			.build();
	
	public static final FoodProperties DEMON_TEA_SCONE_BONUS = new FoodProperties.Builder()
			.effect(new MobEffectInstance(SpectrumStatusEffects.FRENZY, 800, 0), 1.0F)
			.build();
	
	public static final FoodProperties BEVERAGE = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F)
			.build();
	
	public static final FoodProperties PURE_ALCOHOL = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).alwaysEat()
			.effect(new MobEffectInstance(MobEffects.POISON, 20 * 15, 4), 1.0F)
			.effect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 30, 2), 1.0F)
			.effect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 60, 2), 1.0F)
			.build();
	
	public static final FoodProperties KIMCHI = new FoodProperties.Builder()
			.nutrition(6).saturationMod(0.6F)
			.build();
	
	public static final FoodProperties CLOTTED_CREAM = new FoodProperties.Builder()
			.alwaysEat()
			.build();
	
	public static final FoodProperties FRESH_CHOCOLATE = new FoodProperties.Builder()
			.fast().nutrition(4).saturationMod(0.6F)
			.build();
	
	public static final FoodProperties HOT_CHOCOLATE = new FoodProperties.Builder()
			.nutrition(6).saturationMod(0.2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 1200), 1.0F)
			.build();
	
	public static final FoodProperties HOT_CHOCOLATE_SCONE_BONUS = new FoodProperties.Builder()
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 1200), 1.0F)
			.build();
	
	public static final FoodProperties SCONE = new FoodProperties.Builder()
			.fast().nutrition(3).saturationMod(0.6F)
			.build();
	
	public static final FoodProperties FREIGEIST = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).alwaysEat()
			.effect(new MobEffectInstance(SpectrumStatusEffects.ASCENSION, AscensionStatusEffect.MUSIC_INTRO_TICKS), 1.0F)
			.build();
	
	public static final FoodProperties INCANDESCENT_AMALGAM = new FoodProperties.Builder()
			.nutrition(1).saturationMod(0.2F).alwaysEat()
			.build();
	
	public static final FoodProperties DIVINATION_HEART = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).alwaysEat()
			.effect(new MobEffectInstance(SpectrumStatusEffects.DIVINITY, 600, DivinityStatusEffect.ASCENSION_AMPLIFIER), 1.0F)
			.build();
	
	public static final FoodProperties ROCK_CANDY = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).fast()
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 15, 4), 1.0F)
			.build();

	public static final FoodProperties TOPAZ_ROCK_CANDY = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).fast()
			.effect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 15), 1.0F)
			.build();

	public static final FoodProperties AMETHYST_ROCK_CANDY = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).fast()
			.effect(new MobEffectInstance(MobEffects.DIG_SPEED, 20 * 15, 4), 1.0F)
			.build();

	public static final FoodProperties CITRINE_ROCK_CANDY = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).fast()
			.effect(new MobEffectInstance(MobEffects.JUMP, 20 * 15, 2), 1.0F)
			.build();

	public static final FoodProperties ONYX_ROCK_CANDY = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).fast()
			.effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 1), 1.0F)
			.build();

	public static final FoodProperties MOONSTONE_ROCK_CANDY = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).fast()
			.effect(new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 15), 1.0F)
			.build();
	
	public static final FoodProperties BLOODBOIL_SYRUP = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.2F).alwaysEat()
			.effect(new MobEffectInstance(SpectrumStatusEffects.FRENZY, 400), 1.0F)
			.build();
	
	public static final FoodProperties HONEY_PASTRY = new FoodProperties.Builder()
			.nutrition(6).saturationMod(1.5F)
			.build();
	
	public static final FoodProperties JARAMEL_TART = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.2F)
			.build();
	
	public static final FoodProperties SALTED_JARAMEL_TART = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.2F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 200), 1.0F)
			.build();
	
	public static final FoodProperties ASHEN_TART = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.LAVA_GLIDING, 20 * 30), 1.0F)
			.build();
	
	public static final FoodProperties WEEPING_TART = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.2F)
			.effect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20 * 30), 1.0F)
			.effect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 20 * 30), 1.0F)
			.build();
	
	public static final FoodProperties WHISPY_TART = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.2F)
			.build();
	
	public static final FoodProperties PUFF_TART = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.PROJECTILE_REBOUND, 20 * 30), 1.0F)
			.build();
	
	public static final FoodProperties JARAMEL_TRIFLE = new FoodProperties.Builder()
			.nutrition(10).saturationMod(0.2F)
			.build();
	
	public static final FoodProperties SALTED_JARAMEL_TRIFLE = new FoodProperties.Builder()
			.nutrition(10).saturationMod(0.2F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 200), 1.0F)
			.build();
	
	public static final FoodProperties MONSTER_TRIFLE = new FoodProperties.Builder()
			.nutrition(10).saturationMod(0.2F)
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200), 0.2F)
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200), 0.2F)
			.effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200), 0.2F)
			.effect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200), 0.2F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200), 0.2F)
			.effect(new MobEffectInstance(MobEffects.WEAKNESS, 1200), 0.2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 1200), 0.2F)
			.effect(new MobEffectInstance(MobEffects.HUNGER, 1200), 0.2F)
			.build();

	public static final FoodProperties DEMON_TRIFLE = new FoodProperties.Builder()
			.nutrition(10).saturationMod(0.2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.FRENZY, 1200), 2.0F / 3.0F)
			.build();

	public static final FoodProperties MYCEYLON_APPLE_PIE = new FoodProperties.Builder()
			.nutrition(10).saturationMod(0.3F)
			.build();

	public static final FoodProperties MYCEYLON_PUMPKIN_PIE = new FoodProperties.Builder()
			.nutrition(10).saturationMod(0.3F)
			.build();

	public static final FoodProperties MYCEYLON_COOKIE = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.1F)
			.build();

	public static final FoodProperties ALOE_LEAF = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.1F)
			.build();

	public static final FoodProperties PRICKLY_BAYLEAF = new FoodProperties.Builder()
			.nutrition(1).saturationMod(0.3F)
			.effect(new MobEffectInstance(MobEffects.HARM, 0, 0), 0.6F)
			.build();
	
	public static final FoodProperties TRIPLE_MEAT_POT_STEW = new FoodProperties.Builder()
			.nutrition(20).saturationMod(1.2F).meat()
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 100), 1.0F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 12000, 1), 1.0F)
			.build();
	
	public static final FoodProperties DRAGONBONE_BROTH = new FoodProperties.Builder()
			.nutrition(6).saturationMod(0.6F).meat()
			.effect(new MobEffectInstance(SpectrumStatusEffects.MAGIC_ANNULATION, 2400, 1), 1.0F)
			.build();
	
	public static final FoodProperties AQUA_REGIA = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.6F)
			.build();

	public static final FoodProperties BAGNUN = new FoodProperties.Builder()
			.nutrition(10).saturationMod(1F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2000), 1.0F)
			.build();

	public static final FoodProperties BANYASH = new FoodProperties.Builder()
			.nutrition(10).saturationMod(1F)
			.effect(new MobEffectInstance(MobEffects.DIG_SPEED, 2000), 1.0F)
			.build();

	public static final FoodProperties BERLINER = new FoodProperties.Builder()
			.nutrition(9).saturationMod(1.5F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 2400), 1.0F)
			.build();

	public static final FoodProperties CHAUVE_SOURIS_AU_VIN = new FoodProperties.Builder()
			.nutrition(20).saturationMod(1F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.EFFECT_PROLONGING, 6000, 0), 1.0F)
			.build();

	public static final FoodProperties CRAWFISH = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.6F)
			.build();

	public static final FoodProperties CRAWFISH_COCKTAIL = new FoodProperties.Builder()
			.nutrition(6).saturationMod(0.9F).fast()
			.build();

	public static final FoodProperties CREAM_PASTRY = new FoodProperties.Builder()
			.nutrition(4).saturationMod(1.5F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 3600, 1), 1.0F)
			.build();

	public static final FoodProperties FADED_KOI = new FoodProperties.Builder()
			.nutrition(2).saturationMod(10F)
			.build();

	public static final FoodProperties FISHCAKE = new FoodProperties.Builder()
			.nutrition(8).saturationMod(1F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 1), 1)
			.effect(new MobEffectInstance(SpectrumStatusEffects.SWIFTNESS, 1000, 1), 1.0F)
			.build();

	public static final FoodProperties LIZARD_MEAT = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.6F).meat()
			.build();

	public static final FoodProperties GOLDEN_BRISTLE_TEA = new FoodProperties.Builder()
			.nutrition(1).saturationMod(1F)
			.effect(new MobEffectInstance(MobEffects.HEAL), 0.5F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 4), 1)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0), 0.5F)
			.build();

	public static final FoodProperties GOLDEN_BRISTLE_TEA_SCONE_BONUS = new FoodProperties.Builder()
			.nutrition(1).saturationMod(0F)
			.effect(new MobEffectInstance(MobEffects.HEAL), 1F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.MAGIC_ANNULATION, 1200, 1), 1.0F)
			.build();

	public static final FoodProperties HARE_ROAST = new FoodProperties.Builder()
			.nutrition(12).saturationMod(1F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.NOURISHING, 1200, 1), 1.0F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 1200), 1)
			.build();

	public static final FoodProperties JUNKET = new FoodProperties.Builder()
			.nutrition(6).saturationMod(0.8F)
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 2), 1)
			.effect(new MobEffectInstance(SpectrumStatusEffects.SWIFTNESS, 800, 2), 1.0F)
			.build();

	public static final FoodProperties KOI = new FoodProperties.Builder()
			.nutrition(4).saturationMod(0.5F)
			.build();

	public static final FoodProperties MEATLOAF = new FoodProperties.Builder()
			.nutrition(20).saturationMod(2F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.MAGIC_ANNULATION, 6000, 2), 1.0F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, 6000, 1), 1.0F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6000, 0), 1.0F)
			.build();

	public static final FoodProperties MEATLOAF_SANDWICH = new FoodProperties.Builder()
			.nutrition(9).saturationMod(1.5F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.MAGIC_ANNULATION, 600, 2), 1.0F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, 600, 1), 1.0F)
			.effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0), 1.0F)
			.build();

	public static final FoodProperties MELLOW_SHALLOT_SOUP = new FoodProperties.Builder()
			.nutrition(7).saturationMod(0.8F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.EFFECT_PROLONGING, 600, 3), 1.0F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.IMMUNITY, 600, 0), 1.0F)
			.build();

	public static final FoodProperties NECTERED_VIOGNIER = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.6F)
			.build();

	public static final FoodProperties PEACHES_FLAMBE = new FoodProperties.Builder()
			.nutrition(14).saturationMod(1.5F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, 3000, 5), 1.0F)
			.build();

	public static final FoodProperties PEACH_CREAM = new FoodProperties.Builder()
			.nutrition(8).saturationMod(1F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, 3000, 1), 1.0F)
			.build();

	public static final FoodProperties PEACH_CREAM_SCONE_BONUS = new FoodProperties.Builder()
			.nutrition(0).saturationMod(1F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.TOUGHNESS, 6000, 3), 1.0F)
			.build();

	public static final FoodProperties PEACH_JAM = new FoodProperties.Builder()
			.nutrition(16).saturationMod(1F)
			.effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0), 1.0F)
			.effect(new MobEffectInstance(MobEffects.CONFUSION, 1200, 0), 1.0F)
			.build();

	public static final FoodProperties RABBIT_CREAM_PIE = new FoodProperties.Builder()
			.nutrition(16).saturationMod(0.8F)
			.effect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1), 1.0F)
			.effect(new MobEffectInstance(MobEffects.HEAL, 1, 1), 1.0F)
			.build();

	public static final FoodProperties SEDATIVES = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.5F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.STIFFNESS, 300, 4), 1.0F)
			.build();

	public static final FoodProperties SLUSHSLIDE = new FoodProperties.Builder()
			.nutrition(9).saturationMod(0.6F)
			.effect(new MobEffectInstance(SpectrumStatusEffects.SWIFTNESS, 2400, 1), 1.0F)
			.effect(new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 1), 1.0F)
			.build();

	public static final FoodProperties SURSTROMMING = new FoodProperties.Builder()
			.nutrition(5).saturationMod(0.6F)
			.effect(new MobEffectInstance(MobEffects.CONFUSION, 6000, 10), 1.0F)
			.build();
	
}
