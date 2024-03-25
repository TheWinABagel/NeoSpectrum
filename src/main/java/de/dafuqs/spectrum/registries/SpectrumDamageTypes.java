package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.entity.entity.InkProjectileEntity;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static de.dafuqs.spectrum.SpectrumCommon.locate;

// Damage Types handle the logic of how the damage behaves, determined via tags
// Damage Sources decide how death messages are handled
// Make a custom damage source if you want a custom message, otherwise just return a damage source with the type you want
public class SpectrumDamageTypes {
	
	public static boolean recursiveDamageFlag = false;
	
	public static final ResourceKey<DamageType> DECAY = ResourceKey.create(Registries.DAMAGE_TYPE, locate("decay"));
	public static final ResourceKey<DamageType> FLOATBLOCK = ResourceKey.create(Registries.DAMAGE_TYPE, locate("floatblock"));
	public static final ResourceKey<DamageType> SHOOTING_STAR = ResourceKey.create(Registries.DAMAGE_TYPE, locate("shooting_star"));
	public static final ResourceKey<DamageType> MIDNIGHT_SOLUTION = ResourceKey.create(Registries.DAMAGE_TYPE, locate("midnight_solution"));
	public static final ResourceKey<DamageType> DRAGONROT = ResourceKey.create(Registries.DAMAGE_TYPE, locate("dragonrot"));
	public static final ResourceKey<DamageType> DIKE_GATE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("dike_gate"));
	public static final ResourceKey<DamageType> INK_PROJECTILE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("ink_projectile"));
	public static final ResourceKey<DamageType> DEADLY_POISON = ResourceKey.create(Registries.DAMAGE_TYPE, locate("deadly_poison"));
	public static final ResourceKey<DamageType> INCANDESCENCE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("incandescence")); // explosions with that type cause Primordial Fire
	public static final ResourceKey<DamageType> MOONSTONE_STRIKE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("moonstone_strike"));
	public static final ResourceKey<DamageType> BRISTLE_SPROUTS = ResourceKey.create(Registries.DAMAGE_TYPE, locate("bristle_sprouts"));
	public static final ResourceKey<DamageType> SAWTOOTH = ResourceKey.create(Registries.DAMAGE_TYPE, locate("sawtooth"));
	public static final ResourceKey<DamageType> SET_HEALTH_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("set_health_damage"));
	public static final ResourceKey<DamageType> IRRADIANCE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("irradiance"));
	public static final ResourceKey<DamageType> KINDLING_COUGH = ResourceKey.create(Registries.DAMAGE_TYPE, locate("kindling_cough"));
	public static final ResourceKey<DamageType> SNAPPING_IVY = ResourceKey.create(Registries.DAMAGE_TYPE, locate("snapping_ivy"));
	public static final ResourceKey<DamageType> PRIMORDIAL_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, locate("primordial_fire"));
	public static final ResourceKey<DamageType> IMPALING = ResourceKey.create(Registries.DAMAGE_TYPE, locate("impaling"));
	public static final ResourceKey<DamageType> EVISCERATION = ResourceKey.create(Registries.DAMAGE_TYPE, locate("evisceration"));

	public static DamageSource sawtooth(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(SAWTOOTH));
	}
	
	public static DamageSource dragonrot(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(DRAGONROT));
	}
	
	public static DamageSource inkProjectile(InkProjectileEntity projectile, @Nullable Entity attacker) {
		return new DamageSource(projectile.damageSources().damageTypes.getHolderOrThrow(INK_PROJECTILE), projectile, attacker);
	}
	
	public static DamageSource moonstoneStrike(Level world, @Nullable MoonstoneStrike moonstoneStrike) {
		return moonstoneStrike(world, moonstoneStrike != null ? moonstoneStrike.getCausingEntity() : null);
	}
	
	public static DamageSource moonstoneStrike(Level world, @Nullable LivingEntity attacker) {
		return new MoonstoneStrikeDamageSource(world, attacker);
	}
	
	public static DamageSource irradiance(Level world, @Nullable LivingEntity attacker) {
		return new IrradianceDamageSource(world, attacker);
	}

	public static DamageSource impaling(Level world, Entity weapon, @Nullable Entity attacker) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(IMPALING), weapon, attacker);
	}

	public static DamageSource evisceration(Level world, @Nullable Entity attacker) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(EVISCERATION), attacker);
	}

	public static DamageSource setHealth(Level world, @Nullable LivingEntity attacker) {
		return new SetHealthDamageSource(world, attacker);
	}

	public static DamageSource floatblock(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(FLOATBLOCK));
	}

	public static DamageSource shootingStar(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(SHOOTING_STAR));
	}

	public static DamageSource incandescence(Level world) {
		return incandescence(world, null);
	}

	public static DamageSource incandescence(Level world, @Nullable Entity attacker) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(INCANDESCENCE), attacker);
	}

	public static DamageSource midnightSolution(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(MIDNIGHT_SOLUTION));
	}

	public static DamageSource decay(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(DECAY));
	}

	public static DamageSource deadlyPoison(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(DEADLY_POISON));
	}

	public static DamageSource dike(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(DIKE_GATE));
	}

	public static DamageSource bristeSprouts(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(BRISTLE_SPROUTS));
	}

	public static DamageSource kindlingCough(Level world, @Nullable LivingEntity attacker) {
		return new KindlingCoughDamageSource(world, attacker);
	}

	public static DamageSource snappingIvy(Level world) {
		return new DamageSource(world.damageSources().damageTypes.getHolderOrThrow(SNAPPING_IVY));
	}

	public static DamageSource primordialFire(Level world) {
		return new PrimordialFireDamageSource(world, null);
	}

	public static DamageSource primordialFire(Level world, @Nullable LivingEntity attacker) {
		return new PrimordialFireDamageSource(world, attacker);
	}
	
	public static class SetHealthDamageSource extends DamageSource {
		
		public SetHealthDamageSource(Level world, @Nullable LivingEntity attacker) {
			super(world.damageSources().damageTypes.getHolderOrThrow(SET_HEALTH_DAMAGE), attacker);
		}
	}

	public static class MoonstoneStrikeDamageSource extends DamageSource {

		public MoonstoneStrikeDamageSource(Level world, LivingEntity attacker) {
			super(world.damageSources().damageTypes.getHolderOrThrow(MOONSTONE_STRIKE), attacker);
		}

		public MoonstoneStrikeDamageSource(MoonstoneStrike moonstoneStrike) {
			super(moonstoneStrike.getDamageSource().typeHolder(), moonstoneStrike.getCausingEntity());
		}
	}

	public static class IrradianceDamageSource extends DamageSource {

		public IrradianceDamageSource(Level world, @Nullable LivingEntity attacker) {
			super(world.damageSources().damageTypes.getHolderOrThrow(IRRADIANCE), attacker);
		}
	}

	public static class KindlingCoughDamageSource extends DamageSource {

		public KindlingCoughDamageSource(Level world, @Nullable LivingEntity attacker) {
			super(world.damageSources().damageTypes.getHolderOrThrow(KINDLING_COUGH), attacker);
		}
	}
	
	public static class PrimordialFireDamageSource extends DamageSource {
		
		public PrimordialFireDamageSource(Level world, @Nullable LivingEntity attacker) {
			super(world.damageSources().damageTypes.getHolderOrThrow(PRIMORDIAL_FIRE), attacker);
		}
	}

}
