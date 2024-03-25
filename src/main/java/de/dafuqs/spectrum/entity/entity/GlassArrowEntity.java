package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.entity.SpectrumTrackedDataHandlerRegistry;
import de.dafuqs.spectrum.items.tools.GlassArrowVariant;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GlassArrowEntity extends AbstractArrow {
	
	private static final String VARIANT_STRING = "variant";
	private static final EntityDataAccessor<GlassArrowVariant> VARIANT = SynchedEntityData.defineId(GlassArrowEntity.class, SpectrumTrackedDataHandlerRegistry.GLASS_ARROW_VARIANT);
	
	public static final float DAMAGE_MODIFIER = 1.25F;
	
	public GlassArrowEntity(EntityType<? extends GlassArrowEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	public GlassArrowEntity(Level world, LivingEntity owner) {
		super(SpectrumEntityTypes.GLASS_ARROW, owner, world);
		setBaseDamage(getBaseDamage() * DAMAGE_MODIFIER);
	}
	
	public GlassArrowEntity(Level world, double x, double y, double z) {
		super(SpectrumEntityTypes.GLASS_ARROW, x, y, z, world);
		setBaseDamage(getBaseDamage() * DAMAGE_MODIFIER);
	}

	@Override
	public void setEnchantmentEffectsFromEntity(LivingEntity entity, float damageModifier) {
		super.setEnchantmentEffectsFromEntity(entity, damageModifier);
		setBaseDamage(getBaseDamage() * DAMAGE_MODIFIER);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			if (!this.inGround || this.level().getGameTime() % 8 == 0) {
				spawnParticles(1);
			}
		}
	}
	
	@Override
	public boolean isNoGravity() {
		return this.getDeltaMovement().lengthSqr() > 0.1F;
	}
	
	private void spawnParticles(int amount) {
		ParticleOptions particleType = this.getVariant().getParticleEffect();
		if (particleType != null) {
			for (int j = 0; j < amount; ++j) {
				this.level().addParticle(particleType, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, 0, 0);
			}
		}
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		LivingEntity livingEntityToResetHurtTime = null;
		Level world = this.level();
		
		// additional effects depending on arrow type
		// mundane glass arrows do not have additional effects
		GlassArrowVariant variant = getVariant();
		if (variant == GlassArrowVariant.TOPAZ) {
			if (!this.level().isClientSide() && this.getOwner() != null) {
				Entity entity = entityHitResult.getEntity();
				pullEntityClose(this.getOwner(), entity, 0.2);
			}
		} else if (variant == GlassArrowVariant.AMETHYST) {
			if (!this.level().isClientSide()) {
				Entity entity = entityHitResult.getEntity();
				entity.setTicksFrozen(200);
			}
		} else if (variant == GlassArrowVariant.ONYX) {
			Entity entity = entityHitResult.getEntity();
			if (entity instanceof LivingEntity livingEntity) {
				// we're resetting hurtTime here for once so onEntityHit() can deal damage
				// and also resetting after that again so the target is damageable again after this
				livingEntity.hurtTime = 0;
				livingEntityToResetHurtTime = livingEntity;
				livingEntity.hurtCurrentlyUsedShield(20);
				livingEntity.hurtArmor(world.damageSources().magic(), 20);
			}
		} else if (variant == GlassArrowVariant.MOONSTONE) {
			MoonstoneStrike.create(world, this, null, this.getX(), this.getY(), this.getZ(), 4);
		}
		
		super.onHitEntity(entityHitResult);
		
		if (livingEntityToResetHurtTime != null) {
			livingEntityToResetHurtTime.hurtTime = 0;
		}
		
		this.playSound(SoundEvents.GLASS_BREAK, 0.75F, 0.9F + world.random.nextFloat() * 0.2F);
		this.remove(RemovalReason.DISCARDED);
	}
	
	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		if (getVariant() == GlassArrowVariant.MOONSTONE) {
			MoonstoneStrike.create(this.level(), this, null, this.getX(), this.getY(), this.getZ(), 4);
		}
	}
	
	protected static void pullEntityClose(Entity shooter, Entity entityToPull, double pullStrength) {
		double d = shooter.getX() - entityToPull.getX();
		double e = shooter.getY() - entityToPull.getY();
		double f = shooter.getZ() - entityToPull.getZ();
		
		double pullStrengthModifier = 1.0;
		if (entityToPull instanceof LivingEntity livingEntity) {
			pullStrengthModifier = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
		}
		
		Vec3 additionalVelocity = new Vec3(d * pullStrength, e * pullStrength + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * pullStrength).scale(pullStrengthModifier);
		entityToPull.push(additionalVelocity.x, additionalVelocity.y, additionalVelocity.z);
	}
	
	@Override
	protected ItemStack getPickupItem() {
		return entityData.get(VARIANT).getArrow().getDefaultInstance();
	}
	
	/**
	 * Glass Arrows pass through translucent blocks as if it were air
	 */
	@Override
	protected void onHit(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.BLOCK) {
			BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
			BlockState state = this.level().getBlockState(hitPos);
			if (state.isRedstoneConductor(this.level(), hitPos)) {
				return;
			}
		}
		super.onHit(hitResult);
	}
	
	/**
	 * Glass Arrows pass through water almost effortlessly
	 */
	@Override
	protected float getWaterInertia() {
		return 0.1F;
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(VARIANT, GlassArrowVariant.MALACHITE);
	}
	
	public GlassArrowVariant getVariant() {
		return this.entityData.get(VARIANT);
	}
	
	public void setVariant(GlassArrowVariant variant) {
		this.entityData.set(VARIANT, variant);
		if (variant == GlassArrowVariant.CITRINE) {
			setKnockback(5);
		}
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString(VARIANT_STRING, SpectrumRegistries.GLASS_ARROW_VARIANT.getKey(this.getVariant()).toString());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		GlassArrowVariant variant = SpectrumRegistries.GLASS_ARROW_VARIANT.get(ResourceLocation.tryParse(nbt.getString(VARIANT_STRING)));
		if (variant != null) {
			this.setVariant(variant);
		}
	}
	
}
