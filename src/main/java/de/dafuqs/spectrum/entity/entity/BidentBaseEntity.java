package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.mixin.accessors.TridentEntityAccessor;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class BidentBaseEntity extends ThrownTrident {
	
	private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(BidentBaseEntity.class, EntityDataSerializers.ITEM_STACK);
	
	public BidentBaseEntity(EntityType<? extends ThrownTrident> entityType, Level world) {
		super(entityType, world);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(STACK, Items.AIR.getDefaultInstance());
	}
	
	public void setStack(ItemStack stack) {
		setTrackedStack(stack.copy());
		((TridentEntityAccessor) this).spectrum$setTridentStack(stack);
		this.entityData.set(TridentEntityAccessor.spectrum$getLoyalty(), (byte) EnchantmentHelper.getLoyalty(stack));
		this.entityData.set(TridentEntityAccessor.spectrum$getEnchanted(), stack.hasFoil());
	}
	
	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SpectrumSoundEvents.BIDENT_HIT_GROUND;
	}
	
	public ItemStack getTrackedStack() {
		return this.entityData.get(STACK);
	}

	public void setTrackedStack(ItemStack stack) {
		entityData.set(STACK, stack);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		this.entityData.set(STACK, ItemStack.of(nbt.getCompound("Trident")));
	}

	@Override
	public AABB makeBoundingBox() {
		return super.makeBoundingBox();
	}
}
