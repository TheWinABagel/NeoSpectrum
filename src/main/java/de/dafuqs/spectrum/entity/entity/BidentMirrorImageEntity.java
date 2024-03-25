package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BidentMirrorImageEntity extends BidentBaseEntity {
    public BidentMirrorImageEntity(Level world) {
        this(SpectrumEntityTypes.BIDENT_MIRROR_IMAGE, world);
    }
    
    public BidentMirrorImageEntity(EntityType<? extends ThrownTrident> entityType, Level world) {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.level().addParticle(SpectrumParticleTypes.MIRROR_IMAGE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, 0, 0);
        }
    }
    
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Level world = this.level();
        world.playSound(null, entityHitResult.getEntity().blockPosition(), SpectrumSoundEvents.MEDIUM_CRYSTAL_RING, SoundSource.PLAYERS, 1.334F, 0.9F + random.nextFloat() * 0.334F);
        world.playSound(null, entityHitResult.getEntity().blockPosition(), SpectrumSoundEvents.SHATTER_HEAVY, SoundSource.PLAYERS, 0.75F, 1.0F  + random.nextFloat() * 0.2F);
        MoonstoneStrike.create(world, this, null, this.getX(), this.getY(), this.getZ(), 1);
        if (!world.isClientSide) {
            processHit(entityHitResult.getEntity(), 1F);
        }
        this.discard();
    }
    
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Level world = this.level();
        world.playSound(null, blockHitResult.getBlockPos(), SpectrumSoundEvents.SHATTER_HEAVY, SoundSource.PLAYERS, 0.75F, 1.0F);
        MoonstoneStrike.create(world, this, null, this.getX(), this.getY(), this.getZ(), 1);
        if (!world.isClientSide) {
            processHit(null, 0.667F);
        }
        this.discard();
    }
    
    private void processHit(@Nullable Entity target, float effectMult) {
        var stack = getTrackedStack();
        var power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack) * 0.3F + 1;
        var efficiency = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
        var world = this.level();
        var user = getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
        Optional<LivingEntity> optionalTarget = target instanceof LivingEntity livingEntity ? Optional.of(livingEntity) : Optional.empty();
        LightShardEntity.summonBarrage(world, user, this.position(), UniformInt.of(5, 8 + 2 * efficiency),
                () -> new LightShardEntity(world, user, optionalTarget, effectMult * power, 200 + 40 * efficiency / effectMult)
        );
    }

}
