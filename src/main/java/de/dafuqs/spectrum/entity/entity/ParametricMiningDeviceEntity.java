package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.explosion.ModularExplosionDefinition;
import de.dafuqs.spectrum.helpers.Orientation;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ParametricMiningDeviceEntity extends ThrowableItemProjectile {
	
	public ParametricMiningDeviceEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
		super(entityType, world);
	}
	
	public ParametricMiningDeviceEntity(Level world, LivingEntity owner) {
		super(SpectrumEntityTypes.PARAMETRIC_MINING_DEVICE_ENTITY, owner, world);
	}
	
	public ParametricMiningDeviceEntity(Level world, double x, double y, double z) {
		super(SpectrumEntityTypes.PARAMETRIC_MINING_DEVICE_ENTITY, x, y, z, world);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		Level world = this.level();
		if (!world.isClientSide) {
			Entity owner = getOwner();
			Player playerOwner = owner instanceof Player player ? player : null;
			ModularExplosionDefinition.explode((ServerLevel) world, entityHitResult.getEntity().blockPosition(), playerOwner, getItem());
		}
		world.broadcastEntityEvent(this, (byte) 1);
		
		remove(Entity.RemovalReason.DISCARDED);
	}
	
	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		Level world = this.level();
		if (!world.isClientSide) {
			Entity owner = getOwner();
			Player playerOwner = owner instanceof Player player ? player : null;
			ModularExplosionDefinition.explode((ServerLevel) world, blockHitResult.getBlockPos(), blockHitResult.getDirection().getOpposite(), playerOwner, getItem());
		}
		world.broadcastEntityEvent(this, (byte) 2);
		
		remove(Entity.RemovalReason.DISCARDED);
	}
	
	@Override
	public void handleEntityEvent(byte status) {
		var pos = position();
		
		if (status == 1) {
			for (int i = 0; i < 20; i++) {
				var particle = random.nextBoolean() ? SpectrumParticleTypes.PRIMORDIAL_SMOKE : SpectrumParticleTypes.PRIMORDIAL_FLAME;
				this.level().addAlwaysVisibleParticle(particle, true, pos.x(), pos.y(), pos.z(), random.nextFloat() * 0.25 - 0.125, random.nextFloat() * 0.25 - 0.125, random.nextFloat() * 0.25 - 0.125);
			}
		} else if (status == 2) {
			var particles = 15 + random.nextInt(16);
			for (int i = 0; i < particles; i++) {
				var r = random.nextDouble() * 4;
				var orientation = Orientation.create(random.nextDouble() * Math.PI * 2, random.nextDouble() * Math.PI * 2);
				var particle = orientation.toVector(r).add(pos);
				this.level().addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, particle.x(), particle.y(), particle.z(), 0, 0, 0);
			}
		}
	}
	
	@Override
	protected Item getDefaultItem() {
		return SpectrumBlocks.PARAMETRIC_MINING_DEVICE.asItem();
	}
	
}
