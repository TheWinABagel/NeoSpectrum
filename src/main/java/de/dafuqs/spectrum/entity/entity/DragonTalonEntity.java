package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.items.tools.DragonTalonItem;
import de.dafuqs.spectrum.mixin.accessors.PersistentProjectileEntityAccessor;
import de.dafuqs.spectrum.mixin.accessors.TridentEntityAccessor;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DragonTalonEntity extends BidentBaseEntity {

    private static final EntityDataAccessor<Boolean> HIT = SynchedEntityData.defineId(DragonTalonEntity.class, EntityDataSerializers.BOOLEAN);

    public DragonTalonEntity(Level world) {
        this(SpectrumEntityTypes.DRAGON_TALON, world);
    }

    public DragonTalonEntity(EntityType<? extends ThrownTrident> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        var pos = blockHitResult.getBlockPos();
        var state = level().getBlockState(pos);

        if (state.is(Blocks.SLIME_BLOCK) && getDeltaMovement().lengthSqr() > 1) {
            switch (blockHitResult.getDirection().getAxis()) {
                case X -> setDeltaMovement(getDeltaMovement().multiply(-1, 1, 1));
                case Y -> setDeltaMovement(getDeltaMovement().multiply(1, -1, 1));
                case Z -> setDeltaMovement(getDeltaMovement().multiply(1, 1, -1));
            }
            playSound(SoundEvents.SHIELD_BLOCK, 1, 2);
            return;
        }

        super.onHitBlock(blockHitResult);
        if (entityData.get(HIT) || isNoPhysics())
            return;

        entityData.set(HIT, true);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity attacked = entityHitResult.getEntity();
        float f = 2.0F;
        if (attacked instanceof LivingEntity livingAttacked) {
            f *= (getDamage(getTrackedStack()) + EnchantmentHelper.getDamageBonus(getTrackedStack(), livingAttacked.getMobType()));
        }

        Entity owner = this.getOwner();
        DamageSource damageSource = SpectrumDamageTypes.impaling(level(), this, owner);
        ((TridentEntityAccessor) this).spectrum$setDealtDamage(true);
        SoundEvent soundEvent = SoundEvents.TRIDENT_HIT;
        if (attacked.hurt(damageSource, f)) {
            if (attacked.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (attacked instanceof LivingEntity livingAttacked) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingAttacked, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, livingAttacked);
                }

                this.doPostHurtEffects(livingAttacked);
            }
        }

        recall();
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float g = 1.0F;

        this.playSound(soundEvent, g, 1.0F);
    }

    private float getDamage(ItemStack stack) {
        return (float) stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)
                .stream()
                .mapToDouble(AttributeModifier::getAmount)
                .sum();
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        if (getOwner() == null)
            return;

        var owner = getOwner();
        var difMod = 4F;
        var airborne = !owner.onGround();
        var sneaking = owner.isShiftKeyDown();

        if (sneaking)
            difMod *= 3;

        if (airborne)
            difMod /=2;

        var sizeDif = getVolumeDif(target, difMod);
        yoink(target, getOwner().position(), 0.25 * sizeDif, 0.175);

        if (airborne)
            yoink(owner, target.position(), 0.125 / sizeDif, 0.16);
    }

    private float getVolumeDif(LivingEntity target, float pullMod) {
        var ownerBox = getOwner().getBoundingBox();
        var targetBox = target.getBoundingBox();
        float ownerVolume = (float) (ownerBox.getXsize() * ownerBox.getYsize() * ownerBox.getZsize());
        float targetVolume = (float) (targetBox.getXsize() * targetBox.getYsize() * targetBox.getZsize());

        return Math.max(Math.min(ownerVolume / (targetVolume / pullMod), 0.8F), 0.5F);
    }

    public void recall() {
        if (entityData.get(HIT) && !isNoPhysics()) {
            yoink(getOwner(), position(), 0.125, 0.165);
        }

        getEntityData().set(TridentEntityAccessor.spectrum$getLoyalty(), (byte) 4);
        setNoPhysics(true);
    }

    public void yoink(@Nullable Entity yoinked, Vec3 target, double xMod, double yMod) {
        if (yoinked == null)
            return;

        var yPos = yoinked.position();
        var heightDif = Math.abs(yPos.y - target.y);
        var velocity = target.subtract(yPos);
        var sneaking = yoinked.isShiftKeyDown();
        var bonusMod = 1f;

        if (yoinked instanceof LivingEntity livingYoink) {
            bonusMod /= Optional.ofNullable(livingYoink.getEffect(SpectrumStatusEffects.DENSITY))
                    .map(effect -> effect.getAmplifier() + 2).orElse(1);
            bonusMod *= Optional.ofNullable(livingYoink.getEffect(SpectrumStatusEffects.LIGHTWEIGHT))
                    .map(effect -> (effect.getAmplifier() + 2) / 1.5F).orElse(1F);
        }

        if (!yoinked.onGround()) {
            yMod += 0.05;
            xMod -= 0.015;
        }

        yMod = Math.max(0.0725, yMod * (1 - (heightDif * 0.024)));

        xMod *= bonusMod;
        yMod *= bonusMod;

        if (yoinked == getOwner() && yPos.y > target.y && !sneaking)
            yMod = 0;

        yoinked.setDeltaMovement(velocity.multiply(xMod, yMod, xMod).add(0, sneaking ? 0 : 0.25, 0));
        yoinked.fallDistance = 0F;
        yoinked.hurtMarked = true;
        yoinked.hasImpulse = true;
    }

    @Override
    public void tickDespawn() {
        if (!getRootStack().isEmpty())
            return;

        var life = ((PersistentProjectileEntityAccessor) this).getLife() + 1;
        ((PersistentProjectileEntityAccessor) this).setLife(life);
        if (life >= 1200) {
            this.discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        var rootStack = getRootStack();
        if (!rootStack.isEmpty()) {
            SpectrumItems.DRAGON_TALON.markReserved(rootStack, false);
        }
        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HIT, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(HIT, nbt.getBoolean("hit"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("hit", this.entityData.get(HIT));
    }

    private ItemStack getRootStack() {
        if (getOwner() instanceof Player player) {
            var rootStack = DragonTalonItem.findThrownStack(player, uuid);
            return rootStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean tryPickup(Player player) {
        var rootStack = DragonTalonItem.findThrownStack(player, uuid);
        if (!rootStack.isEmpty()) {
            SpectrumItems.DRAGON_TALON.markReserved(rootStack, false);
            return true;
        }
        else if(player == getOwner()) {
            discard();
        }
        return false;
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack stack) {
        return null;
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack stack, float yOffset) {
        return null;
    }
}
