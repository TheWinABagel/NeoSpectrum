package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.MagicProjectileSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class MagicProjectileEntity extends Projectile {

	public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, Level world) {
        super(type, world);
        if (world.isClientSide) {
            MagicProjectileSoundInstance.startSoundInstance(this);
        }
    }

	public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, double x, double y, double z, Level world) {
		this(type, world);
		this.moveTo(x, y, z, this.getYRot(), this.getXRot());
		this.reapplyPosition();
	}

	@Override
	public void tick() {
		super.tick();

		boolean noClip = this.isNoClip();
		Vec3 thisVelocity = this.getDeltaMovement();
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			double d = thisVelocity.horizontalDistance();
			this.setYRot((float) (Mth.atan2(thisVelocity.x, thisVelocity.z) * 57.2957763671875D));
			this.setXRot((float) (Mth.atan2(thisVelocity.y, d) * 57.2957763671875D));
			this.yRotO = this.getYRot();
			this.xRotO = this.getXRot();
		}

		this.age();

		Vec3 vec3d2;
		Vec3 thisPos = this.position();
		vec3d2 = thisPos.add(thisVelocity);
		HitResult hitResult = this.level().clip(new ClipContext(thisPos, vec3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if ((hitResult).getType() != HitResult.Type.MISS) {
			vec3d2 = (hitResult).getLocation();
		}

		if (!this.isRemoved()) {
			EntityHitResult entityHitResult = this.getEntityCollision(thisPos, vec3d2);
			if (entityHitResult != null) {
				hitResult = entityHitResult;
			}

			if (hitResult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult) hitResult).getEntity();
				Entity entity2 = this.getOwner();
				if (entity instanceof Player && entity2 instanceof Player && !((Player) entity2).canHarmPlayer((Player) entity)) {
					hitResult = null;
				}
			}

			if (hitResult != null && !noClip) {
				this.onHit(hitResult);
				this.hasImpulse = true;
			}
		}

		thisVelocity = this.getDeltaMovement();
		double velocityX = thisVelocity.x;
		double velocityY = thisVelocity.y;
		double velocityZ = thisVelocity.z;

		double h = this.getX() + velocityX;
		double j = this.getY() + velocityY;
		double k = this.getZ() + velocityZ;
		double l = thisVelocity.horizontalDistance();
		if (noClip) {
			this.setYRot((float) (Mth.atan2(-velocityX, -velocityZ) * 57.2957763671875D));
		} else {
			this.setYRot((float) (Mth.atan2(velocityX, velocityZ) * 57.2957763671875D));
		}

		this.setXRot((float) (Mth.atan2(velocityY, l) * 57.2957763671875D));
		this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
		this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

		if (this.isInWater()) {
			for (int o = 0; o < 4; ++o) {
				this.level().addParticle(ParticleTypes.BUBBLE, h - velocityX * 0.25D, j - velocityY * 0.25D, k - velocityZ * 0.25D, velocityX, velocityY, velocityZ);
			}
		}

		this.setPos(h, j, k);
		this.checkInsideBlocks();
	}

	protected void age() {
		++this.tickCount;
		if (this.tickCount >= 200) {
			this.discard();
		}

	}

	public boolean isNoClip() {
		if (!this.level().isClientSide()) {
			return this.noPhysics;
		} else {
			return true;
		}
	}

	protected SoundEvent getHitSound() {
		return SpectrumSoundEvents.INK_PROJECTILE_HIT;
	}

	@Nullable
	protected EntityHitResult getEntityCollision(Vec3 currentPosition, Vec3 nextPosition) {
		return ProjectileUtil.getEntityHitResult(this.level(), this, currentPosition, nextPosition, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
	}

    public abstract DyeColor getDyeColor();

}
