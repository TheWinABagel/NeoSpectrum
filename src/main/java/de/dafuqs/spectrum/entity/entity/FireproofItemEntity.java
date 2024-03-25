package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireproofItemEntity extends ItemEntity {
	
	public FireproofItemEntity(EntityType<? extends ItemEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	public FireproofItemEntity(Level world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
	}
	
	public FireproofItemEntity(Level world, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ) {
		this(SpectrumEntityTypes.FIREPROOF_ITEM, world);
		this.setPos(x, y, z);
		this.setDeltaMovement(velocityX, velocityY, velocityZ);
		this.setItem(stack);
	}
	
	private FireproofItemEntity(ItemEntity entity) {
		super(SpectrumEntityTypes.FIREPROOF_ITEM, entity.level());
	}
	
	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return damageSource.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(damageSource);
	}
	
	public ItemEntity copy() {
		return new FireproofItemEntity(this);
	}
	
	public static void scatter(Level world, double x, double y, double z, ItemStack stack) {
		double d = EntityType.ITEM.getWidth();
		double e = 1.0 - d;
		double f = d / 2.0;
		double g = Math.floor(x) + world.random.nextDouble() * e + f;
		double h = Math.floor(y) + world.random.nextDouble() * e;
		double i = Math.floor(z) + world.random.nextDouble() * e + f;
		
		while(!stack.isEmpty()) {
			FireproofItemEntity itemEntity = new FireproofItemEntity(world, g, h, i, stack.split(world.random.nextInt(21) + 10));
			itemEntity.setDeltaMovement(world.random.triangle(0.0, 0.11485000171139836), world.random.triangle(0.2, 0.11485000171139836), world.random.triangle(0.0, 0.11485000171139836));
			world.addFreshEntity(itemEntity);
		}
		
	}
	
}
