package de.dafuqs.spectrum.cca;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumEntityTypeTags;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OnPrimordialFireComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {

	// 1% of max health as damage every tick as a base.
	public static final float BASE_PERCENT_DAMAGE = 0.01F;

	// Base damage reduction applied by fire resistance
	public static final float FIRE_RESISTANCE_DAMAGE_RESISTANCE = 0.25F;
	// Per-level damage reduction added by fire prot. Caps at 50%
	public static final float FIRE_PROT_DAMAGE_RESISTANCE = 0.05F;

	public static final ComponentKey<OnPrimordialFireComponent> ON_PRIMORDIAL_FIRE_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("on_primordial_fire"), OnPrimordialFireComponent.class);
	
	private LivingEntity provider;
	private long primordialFireTicks = 0;
	
	// this is not optional
	// removing this empty constructor will make the world not load
	public OnPrimordialFireComponent() {
	
	}
	
	public OnPrimordialFireComponent(LivingEntity entity) {
		this.provider = entity;
	}

	@Override
	public void writeToNbt(@NotNull CompoundTag tag) {
		if (this.primordialFireTicks > 0) {
			tag.putLong("ticks", this.primordialFireTicks);
		}
	}
	
	@Override
	public void readFromNbt(CompoundTag tag) {
		if (tag.contains("ticks", Tag.TAG_LONG)) {
			this.primordialFireTicks = tag.getLong("ticks");
		} else {
			this.primordialFireTicks = 0;
		}
	}

	public static void setPrimordialFireTicks(LivingEntity livingEntity, int ticks) {
		OnPrimordialFireComponent component = ON_PRIMORDIAL_FIRE_COMPONENT.get(livingEntity);
		component.primordialFireTicks = ticks;
		ON_PRIMORDIAL_FIRE_COMPONENT.sync(component.provider);
	}

	public static void addPrimordialFireTicks(LivingEntity livingEntity, int ticks) {
		OnPrimordialFireComponent component = ON_PRIMORDIAL_FIRE_COMPONENT.get(livingEntity);
		ticks = ProtectionEnchantment.getFireAfterDampener(livingEntity, ticks);
		component.primordialFireTicks += ticks;


		ON_PRIMORDIAL_FIRE_COMPONENT.sync(component.provider);
	}
	
	public static boolean isOnPrimordialFire(LivingEntity livingEntity) {
		OnPrimordialFireComponent component = ON_PRIMORDIAL_FIRE_COMPONENT.get(livingEntity);
		return component.primordialFireTicks > 0;
	}
	
	public static boolean putOut(LivingEntity livingEntity) {
		OnPrimordialFireComponent component = ON_PRIMORDIAL_FIRE_COMPONENT.get(livingEntity);
		if (component.primordialFireTicks > 0) {
			component.primordialFireTicks = 0;
			ON_PRIMORDIAL_FIRE_COMPONENT.sync(component.provider);
			return true;
		}
		return false;
	}

	@Override
	public void serverTick() {

		//Immune creatures get spared. If we ever add any.
		if (provider.getType().is(SpectrumEntityTypeTags.PRIMORDIAL_FIRE_IMMUNE)) {
			primordialFireTicks = 0;
			ON_PRIMORDIAL_FIRE_COMPONENT.sync(this.provider);
			return;
		}

		if (this.primordialFireTicks > 0) {
			if (!isAffectingConstruct()) {
				var damageScaling = getDamageHealthScaling(provider);
				provider.hurt(SpectrumDamageTypes.primordialFire(this.provider.level()), AzureDikeProvider.absorbDamage(provider, damageScaling * provider.getMaxHealth()));
			}
			//Primordial fire is so strong because it rends the soul. No soul = just slightly spicier fire
			//Constructs have no soul, thus you get 2 dps and no more
			else if (provider.tickCount % 10 == 0) {
				provider.hurt(SpectrumDamageTypes.primordialFire(this.provider.level()), 1);
			}

			this.primordialFireTicks -= this.provider.getFluidHeight(FluidTags.WATER) > 0 ? 3 : 1;
			// was on fire, but is not any longer
			if (this.primordialFireTicks <= 0) {
				ON_PRIMORDIAL_FIRE_COMPONENT.sync(this.provider);
			}
		}
	}

	public boolean isAffectingConstruct() {
		return provider.getType().is(SpectrumEntityTypeTags.CONSTRUCTS);
	}

	/**
	 * Primordial fire's base DPS is 1/t, for a kill time of 5 seconds on a base hp player.
	 */
	public float getDamageHealthScaling(LivingEntity entity) {
		float baseDamage = BASE_PERCENT_DAMAGE;

		//Bosses have great and exceptional souls that can resist a lot more.
		//95% less damage to them before reductions and caps
		if (entity.getType().is(ConventionalEntityTypeTags.BOSSES))
			baseDamage /= 20F;

        return baseDamage * getDamagePenalties(entity) * getDamageBonuses(entity);
	}

	public float getDamagePenalties(LivingEntity entity) {
		//fire prot has a cap of 50% DR, requiring fire protection 10 on an armor piece
		float fireProt = Math.min(FIRE_PROT_DAMAGE_RESISTANCE * EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, provider), 0.5F);
		int fireResLevel = Optional.ofNullable(provider.getEffect(MobEffects.FIRE_RESISTANCE)).map(MobEffectInstance::getAmplifier).orElse(-1) + 1;
		float fireRes = 0;

		// flat 25% for a start on fire res
		if (fireResLevel > 0)
			fireRes = FIRE_RESISTANCE_DAMAGE_RESISTANCE;

		//Fire resistance has diminishing returns
		for (int i = 1; i < fireResLevel; i++) {
			fireRes += (float) (0.05 * (i) + (0.25F * Math.pow(0.5F, i)));
		}

		//Fire immune entities can have a lil res, as a treat
		float immunityReduction = entity.fireImmune() ? 0.25F : 0;

		//Primordial fire has an overall cap of 90% DR
		return Math.max(1 - (fireRes + fireProt + immunityReduction), 0.10F);
	}

	/**
	 * Here for completeness.
	 * <p>
	 * Unused... for now...
	 */
	public float getDamageBonuses(LivingEntity entity) {
		return 1F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientTick() {
		if (this.primordialFireTicks > 0) {
			double fluidHeight = this.provider.getFluidHeight(FluidTags.WATER);
			if (fluidHeight > 0) {

				Level world = this.provider.level();
				RandomSource random = world.random;
				Vec3 pos = this.provider.position();

				for (int i = 0; i < 2; i++) {
					world.addParticle(ParticleTypes.BUBBLE_POP, this.provider.getRandomX(1), pos.y() + Math.min(fluidHeight, provider.getBbHeight()) * random.nextFloat(), this.provider.getRandomZ(1), 0.0, 0.04, 0.0);
					world.addParticle(ParticleTypes.SMOKE, this.provider.getRandomX(1), pos.y() + Math.min(fluidHeight, provider.getBbHeight()) * random.nextFloat(), this.provider.getRandomZ(1), 0.0, 0.04, 0.0);
				}
				if (world.random.nextInt(12) == 0) {
					provider.playSound(SoundEvents.FIRE_EXTINGUISH, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F);
				}
			}
		}
	}

}
