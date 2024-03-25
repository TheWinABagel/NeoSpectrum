package de.dafuqs.spectrum.entity.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;

public class LivingMarkerEntity extends LivingEntity {
	
	public LivingMarkerEntity(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	@Override
	public double getAttributeValue(Attribute attribute) {
		return 0;
	}
	
	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return new ArrayList<>();
	}
	
	@Override
	public ItemStack getItemBySlot(EquipmentSlot slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
	
	}
	
	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.LEFT;
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
	}
	
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		throw new IllegalStateException("Living Markers should never be sent");
	}
	
	@Override
	protected void addPassenger(Entity passenger) {
		passenger.stopRiding();
	}
	
	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}
	
}
