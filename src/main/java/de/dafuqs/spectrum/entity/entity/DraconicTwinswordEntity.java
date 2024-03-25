package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.api.entity.NonLivingAttackable;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.items.tools.DraconicTwinswordItem;
import de.dafuqs.spectrum.mixin.accessors.TridentEntityAccessor;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DraconicTwinswordEntity extends BidentBaseEntity implements NonLivingAttackable {

    private static final EntityDataAccessor<Boolean> HIT = SynchedEntityData.defineId(DraconicTwinswordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PROPELLED = SynchedEntityData.defineId(DraconicTwinswordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> REBOUND = SynchedEntityData.defineId(DraconicTwinswordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions initialSize = EntityDimensions.scalable(1.5F, 1.5F);
    private static final EntityDimensions shortSize = EntityDimensions.scalable(1F, 1F);
    private static final EntityDimensions tallSize = EntityDimensions.scalable(1F, 1.8F);

    private int travelingTicks = 0, jiggleTicks = 20, jiggleIntensity = 8;


    public DraconicTwinswordEntity(Level world) {
        this(SpectrumEntityTypes.DRAGON_TWINSWORD, world);
    }

    public DraconicTwinswordEntity(EntityType<? extends ThrownTrident> entityType, Level world) {
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
            travelingTicks = 0;
            return;
        }

        if (!isRebounding() && state.getDestroySpeed(level(), pos) >= 25F && !state.is(BlockTags.PLANKS) && !state.is(BlockTags.DIRT)) {
            travelingTicks = 0;
            rebound(getOwner().position(), 0.105, 0.15);
            playSound(SoundEvents.SHIELD_BLOCK, 1, 2);
            return;
        }

        super.onHitBlock(blockHitResult);
        if (entityData.get(HIT) || isNoPhysics())
            return;

        setRebounding(false);
        setPropelled(false);
        entityData.set(HIT, true);
        jiggleTicks = 0;
        jiggleIntensity = 4;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void tick() {
        if (isPropelled() && !isRebounding()) {
            if (travelingTicks < 12) {
                travelingTicks++;

                if (travelingTicks > 6 && getDeltaMovement().lengthSqr() > 2) {
                    setDeltaMovement(getDeltaMovement().scale(0.5));
                    hasImpulse = true;
                    hurtMarked = true;
                }
            }
        }
        else if(inGround){


            if (jiggleTicks < 15) {
                jiggleTicks++;

                var intensity = 1 - (jiggleTicks / 15F);

                xRotO = getXRot();
                setXRot(xRotO + jiggleIntensity * intensity / 2 * (random.nextInt(3) - 1));
                yRotO = getYRot();
                setYRot(yRotO + jiggleIntensity * intensity * (random.nextInt(3) - 1));
            }

            for(Entity thornCandidate : level().getEntities(this, makeBoundingBox(), this::canHitEntity)) {
                if (entityData.get(HIT)) {
                    if (!(thornCandidate instanceof ItemEntity) && thornCandidate.hurt(damageSources().thorns(this), 4))
                        playSound(SoundEvents.THORNS_HIT, 1, 0.9F + random.nextFloat() * 0.2F);
                }
            }
        }

        super.tick();
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        var striker = source.getEntity();

        if (striker == null)
            return false;

        if (!entityData.get(HIT)) {
            travelingTicks = 0;
            this.setVelocity(striker.getXRot(), striker.getYRot(), 0.0F, 3F);
            setXRot(striker.getXRot());
            setYRot(striker.getYRot());
            xRotO = getXRot();
            yRotO = getYRot();
            setPropelled(true);
            setRebounding(false);
            ((TridentEntityAccessor) this).spectrum$setDealtDamage(false);
        }
        else {
            jiggleTicks = 0;
            jiggleIntensity = 8;
        }

        playSound(SoundEvents.TRIDENT_HIT_GROUND, 1, 1);

        return false;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public AABB makeBoundingBox() {
        if (isPropelled()) {
            return super.makeBoundingBox();
        }
        else if(isRebounding()) {
            return shortSize.makeBoundingBox(position());
        }

        if (inGround) {
            var absPitch = Math.abs(getXRot());
            if (absPitch > 55)
                return tallSize.makeBoundingBox(position());
            return shortSize.makeBoundingBox(position());
        }

        return initialSize.makeBoundingBox(position());
    }

    public void setVelocity(float pitch, float yaw, float roll, float speed) {
        float f = -Mth.sin(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
        float g = -Mth.sin((pitch + roll) * (float) (Math.PI / 180.0));
        float h = Mth.cos(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
        setDeltaMovement(new Vec3(f, g, h).scale(speed));
        hasImpulse = true;
        hurtMarked = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        var propelled = isPropelled();
        Entity attacked = entityHitResult.getEntity();
        float f = propelled ? 2F : 1F;
        boolean crit = false;

        if (attacked instanceof LivingEntity livingAttacked) {
            f *= (getDamage(getTrackedStack()) + EnchantmentHelper.getDamageBonus(getTrackedStack(), livingAttacked.getMobType()));
        }

        if (!attacked.onGround() && propelled) {
            f *= 3;
            crit = true;
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

        this.setDeltaMovement(this.getDeltaMovement().multiply(-1, 1, -1));
        travelingTicks = 0;
        float g = 1.0F;

        setPropelled(false);
        if (owner != null)
            rebound(owner.position(), 0.105, 0.15);

        this.playSound(soundEvent, g, 1.0F);
        if (crit) {
            this.playSound(SpectrumSoundEvents.CRITICAL_HIT, 1.25F, 1.0F);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 currentPosition, Vec3 nextPosition) {
        return ProjectileUtil.getEntityHitResult(
                this.level(), this, currentPosition, nextPosition, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity
        );
    }

    private float getDamage(ItemStack stack) {
        return (float) stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)
                .stream()
                .mapToDouble(AttributeModifier::getAmount)
                .sum();
    }

    @Override
    public void tickDespawn() {}

    public boolean isPropelled() {
        return entityData.get(PROPELLED);
    }

    public boolean isRebounding() {
        return entityData.get(REBOUND);
    }

    public void setPropelled(boolean propelled) {
        entityData.set(PROPELLED, propelled);
    }

    public void setRebounding(boolean rebounding) {
        entityData.set(REBOUND, rebounding);
    }

    public void rebound(Vec3 target, double xMod, double yMod) {
        setRebounding(true);

        var yPos = this.position();
        var heightDif = Math.abs(yPos.y - target.y);
        var velocity = target.subtract(yPos);

        yMod = Math.max(0.0725, yMod * (1 - (heightDif * 0.024)));

        this.setDeltaMovement(velocity.multiply(xMod, yMod, xMod).add(0, 0.3, 0));
        this.setYRot(-getYRot());
        this.setXRot(-getXRot());
        this.hurtMarked = true;
        this.hasImpulse = true;
    }

    @Override
    public void remove(RemovalReason reason) {
        var rootStack = getRootStack();
        if (!rootStack.isEmpty()) {
            SpectrumItems.DRACONIC_TWINSWORD.markReserved(rootStack, false);
        }
        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HIT, false);
        this.entityData.define(PROPELLED, false);
        this.entityData.define(REBOUND, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(HIT, nbt.getBoolean("hit"));
        setTrackedStack(ItemStack.of(nbt));
        setPropelled(nbt.getBoolean("propelled"));
        setRebounding(nbt.getBoolean("rebounding"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        getTrackedStack().save(nbt);
        nbt.putBoolean("hit", this.entityData.get(HIT));
        nbt.putBoolean("propelled", isPropelled());
        nbt.putBoolean("rebounding", isRebounding());
    }

    private ItemStack getRootStack() {
        if (getOwner() instanceof Player player) {
            var rootStack = DraconicTwinswordItem.findThrownStack(player, uuid);
            return rootStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void playerTouch(Player player) {

    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return tryPickup(player) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    protected boolean tryPickup(Player player) {
        if (player != getOwner()) {
            player.hurt(damageSources().thorns(this), 20);
            playSound(SoundEvents.THORNS_HIT, 1, 0.9F + random.nextFloat() * 0.2F);
            return false;
        }

        var rootStack = DraconicTwinswordItem.findThrownStack(player, uuid);
        if (!rootStack.isEmpty()) {
            if (this.level().isClientSide())
                return true;

            SpectrumItems.DRACONIC_TWINSWORD.markReserved(rootStack, false);
            player.take(this, 1);
            player.playSound(SoundEvents.ITEM_PICKUP, 1, 1);
            discard();
            return true;
        }
        else {
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
