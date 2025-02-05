package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.items.tools.DragonTalonItem;
import de.dafuqs.spectrum.mixin.accessors.PersistentProjectileEntityAccessor;
import de.dafuqs.spectrum.mixin.accessors.TridentEntityAccessor;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DragonTalonEntity extends BidentBaseEntity {

    private static final TrackedData<Boolean> HIT = DataTracker.registerData(DragonTalonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public DragonTalonEntity(World world) {
        this(SpectrumEntityTypes.DRAGON_TALON, world);
    }

    public DragonTalonEntity(EntityType<? extends TridentEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        var pos = blockHitResult.getBlockPos();
        var state = getWorld().getBlockState(pos);

        if (state.isOf(Blocks.SLIME_BLOCK) && getVelocity().lengthSquared() > 1) {
            switch (blockHitResult.getSide().getAxis()) {
                case X -> setVelocity(getVelocity().multiply(-1, 1, 1));
                case Y -> setVelocity(getVelocity().multiply(1, -1, 1));
                case Z -> setVelocity(getVelocity().multiply(1, 1, -1));
            }
            playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1, 2);
            return;
        }

        super.onBlockHit(blockHitResult);
        if (dataTracker.get(HIT) || isNoClip())
            return;

        dataTracker.set(HIT, true);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity attacked = entityHitResult.getEntity();
        float f = 2.0F;
        if (attacked instanceof LivingEntity livingAttacked) {
            f *= (getDamage(getTrackedStack()) + EnchantmentHelper.getAttackDamage(getTrackedStack(), livingAttacked.getGroup()));
        }

        Entity owner = this.getOwner();
        DamageSource damageSource = SpectrumDamageTypes.impaling(getWorld(), this, owner);
        ((TridentEntityAccessor) this).spectrum$setDealtDamage(true);
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        if (attacked.damage(damageSource, f)) {
            if (attacked.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (attacked instanceof LivingEntity livingAttacked) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingAttacked, owner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)owner, livingAttacked);
                }

                this.onHit(livingAttacked);
            }
        }

        recall();
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float g = 1.0F;

        this.playSound(soundEvent, g, 1.0F);
    }

    private float getDamage(ItemStack stack) {
        return (float) stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .stream()
                .mapToDouble(EntityAttributeModifier::getValue)
                .sum();
    }

    @Override
    protected void onHit(LivingEntity target) {
        if (getOwner() == null)
            return;

        var owner = getOwner();
        var difMod = 4F;
        var airborne = !owner.isOnGround();
        var sneaking = owner.isSneaking();

        if (sneaking)
            difMod *= 3;

        if (airborne)
            difMod /=2;

        var sizeDif = getVolumeDif(target, difMod);
        yoink(target, getOwner().getPos(), 0.25 * sizeDif, 0.175);

        if (airborne)
            yoink(owner, target.getPos(), 0.125 / sizeDif, 0.16);
    }

    private float getVolumeDif(LivingEntity target, float pullMod) {
        var ownerBox = getOwner().getBoundingBox();
        var targetBox = target.getBoundingBox();
        float ownerVolume = (float) (ownerBox.getXLength() * ownerBox.getYLength() * ownerBox.getZLength());
        float targetVolume = (float) (targetBox.getXLength() * targetBox.getYLength() * targetBox.getZLength());

        return Math.max(Math.min(ownerVolume / (targetVolume / pullMod), 0.8F), 0.5F);
    }

    public void recall() {
        if (dataTracker.get(HIT) && !isNoClip()) {
            yoink(getOwner(), getPos(), 0.125, 0.165);
        }

        getDataTracker().set(TridentEntityAccessor.spectrum$getLoyalty(), (byte) 4);
        setNoClip(true);
    }

    public void yoink(@Nullable Entity yoinked, Vec3d target, double xMod, double yMod) {
        if (yoinked == null)
            return;

        var yPos = yoinked.getPos();
        var heightDif = Math.abs(yPos.y - target.y);
        var velocity = target.subtract(yPos);
        var sneaking = yoinked.isSneaking();
        var bonusMod = 1f;

        if (yoinked instanceof LivingEntity livingYoink) {
            bonusMod /= Optional.ofNullable(livingYoink.getStatusEffect(SpectrumStatusEffects.DENSITY))
                    .map(effect -> effect.getAmplifier() + 2).orElse(1);
            bonusMod *= Optional.ofNullable(livingYoink.getStatusEffect(SpectrumStatusEffects.LIGHTWEIGHT))
                    .map(effect -> (effect.getAmplifier() + 2) / 1.5F).orElse(1F);
        }

        if (!yoinked.isOnGround()) {
            yMod += 0.05;
            xMod -= 0.015;
        }

        yMod = Math.max(0.0725, yMod * (1 - (heightDif * 0.024)));

        xMod *= bonusMod;
        yMod *= bonusMod;

        if (yoinked == getOwner() && yPos.y > target.y && !sneaking)
            yMod = 0;

        yoinked.setVelocity(velocity.multiply(xMod, yMod, xMod).add(0, sneaking ? 0 : 0.25, 0));
        yoinked.fallDistance = 0F;
        yoinked.velocityModified = true;
        yoinked.velocityDirty = true;
    }

    @Override
    public void age() {
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
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HIT, false);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(HIT, nbt.getBoolean("hit"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("hit", this.dataTracker.get(HIT));
    }

    private ItemStack getRootStack() {
        if (getOwner() instanceof PlayerEntity player) {
            var rootStack = DragonTalonItem.findThrownStack(player, uuid);
            return rootStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
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
    public ItemEntity dropStack(ItemStack stack) {
        return null;
    }

    @Nullable
    @Override
    public ItemEntity dropStack(ItemStack stack, float yOffset) {
        return null;
    }
}
