package de.dafuqs.spectrum.explosion;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.explosion.modifier.*;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExplosionModifiers {
	
	// MODIFIER TYPES
	// A Modifier Type defines an abstract set of modifiers that can be used a set number of times
	public static final ExplosionModifierType GENERIC = registerModifierType("generic", new ExplosionModifierType(ExplosionArchetype.ALL, Integer.MAX_VALUE)); // general improvements, particles and other cosmetic effects
	
	public static final ExplosionModifierType DAMAGE_TYPE = registerModifierType("damage_type", new ExplosionModifierType(ExplosionArchetype.DAMAGE_ENTITIES, 1)); // changes the damage source
	public static final ExplosionModifierType DAMAGE_MODIFICATION = registerModifierType("damage_modification", new ExplosionModifierType(ExplosionArchetype.DAMAGE_ENTITIES, Integer.MAX_VALUE)); // changes the damage source
	
	public static final ExplosionModifierType DESTRUCTION_SHAPE = registerModifierType("destruction_shape", new ExplosionModifierType(ExplosionArchetype.DESTROY_BLOCKS, 1)); // explosion shape
	public static final ExplosionModifierType DESTRUCTION_MODIFICATION = registerModifierType("destruction_modification", new ExplosionModifierType(ExplosionArchetype.DESTROY_BLOCKS, Integer.MAX_VALUE)); // all sorts of stuff, like damage improvements

	// MODIFIERS
	// A modifier changes the effect of the modular explosion in some way
	// General boosts
	public static final ExplosionModifier EXPLOSION_BOOST = registerModifier("explosion_boost", new MoreBoomModifier(GENERIC, 0xffbf40));
	
	// Damage source changers
	public static final ExplosionModifier FIRE = registerModifier("fire", new FireModifier(DAMAGE_TYPE, ParticleTypes.FLAME, 0xaff3eb));
	public static final ExplosionModifier PRIMORDIAL_FIRE = registerModifier("primordial_fire", new PrimordialFireModifier(DAMAGE_TYPE, SpectrumParticleTypes.PRIMORDIAL_FLAME_SMALL, 0x76254d));
	public static final ExplosionModifier LIGHTNING = registerModifier("lightning_damage", new DamageChangingModifier(DAMAGE_TYPE, SpectrumParticleTypes.WHITE_EXPLOSION, 0xf0f24d) {
		@Override
		public Optional<DamageSource> getDamageSource(@Nullable LivingEntity owner) {
			if (owner == null) {
				return Optional.empty();
			}
			return Optional.of(owner.damageSources().lightningBolt());
		}
	});
	public static final ExplosionModifier MAGIC = registerModifier(
			"magic_damage",
			new DamageChangingModifier(DAMAGE_TYPE, SpectrumParticleTypes.PURPLE_CRAFTING, 0x5433a5) {
		@Override
		public Optional<DamageSource> getDamageSource(@Nullable LivingEntity owner) {
			if (owner == null) {
				return Optional.empty();
			}
			return Optional.of(owner.damageSources().magic());
		}
	});
	public static final ExplosionModifier INCANDESCENCE = registerModifier("incandescence", new DamageChangingModifier(DAMAGE_TYPE, ParticleTypes.ENCHANT, 0xff59ff) {
		@Override
		public Optional<DamageSource> getDamageSource(@Nullable LivingEntity owner) {
			if (owner == null) {
				return Optional.empty();
			}
			return Optional.of(SpectrumDamageTypes.incandescence(owner.level()));
		}
	});
	
	// Other entity damage modifications
	public static final ExplosionModifier KILL_ZONE = registerModifier("kill_zone", new KillZoneModifier(DAMAGE_MODIFICATION, 0.5F, 20F, 0xb3801b));
	// public static final ExplosionModifier LOOTING = registerModifier("looting", new EnchantmentAddingModifier(DAMAGE_MODIFICATION, Enchantments.LOOTING, 3, ParticleTypes.ENCHANT, 0x5433a5)); // TODO: For this we'd have to do really bad things to loot table processing
	
	// Shapes
	public static final ExplosionModifier SHAPE_SQUARE = registerModifier("shape_square", new ExplosionModifier(DESTRUCTION_SHAPE, 0x5433a5) {
		@Override
		public Optional<ExplosionShape> getShape() {
			return Optional.of(ExplosionShape.SQUARE);
		}
	});
	
	// Block Breaking modifications
	public static final ExplosionModifier FORTUNE = registerModifier("fortune", new EnchantmentAddingModifier(DESTRUCTION_MODIFICATION, Enchantments.BLOCK_FORTUNE, 3, ParticleTypes.ENCHANT, 0x5433a5));
	public static final ExplosionModifier SILK_TOUCH = registerModifier("silk_touch", new EnchantmentAddingModifier(DESTRUCTION_MODIFICATION, Enchantments.SILK_TOUCH, 1, ParticleTypes.ENCHANT, 0x5433a5));
	public static final ExplosionModifier INVENTORY_INSERTION = registerModifier("inventory_insertion", new EnchantmentAddingModifier(DESTRUCTION_MODIFICATION, SpectrumEnchantments.INVENTORY_INSERTION, 1, ParticleTypes.ENCHANT, 0x5433a5));
	
	// Cosmetic
	public static final ExplosionModifier STARRY = registerModifier("starry", new ParticleAddingModifier(GENERIC, ParticleTypes.END_ROD, 0xc3c8d4));
	public static final ExplosionModifier LIGHT = registerModifier("light", new LightPlacingModifier(GENERIC, SpectrumParticleTypes.SHIMMERSTONE_SPARKLE, 0xfaf87a));
	
	private static <T extends ExplosionModifier> T registerModifier(String name, T modifier) {
		return Registry.register(SpectrumRegistries.EXPLOSION_MODIFIERS, SpectrumCommon.locate(name), modifier);
	}
	
	private static ExplosionModifierType registerModifierType(String name, ExplosionModifierType type) {
		return Registry.register(SpectrumRegistries.EXPLOSION_MODIFIER_TYPES, SpectrumCommon.locate(name), type);
	}
	
	public static void register() {
	
	}
	
}
