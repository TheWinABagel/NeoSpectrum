package de.dafuqs.spectrum.entity.entity;

import com.google.common.collect.Sets;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LightMineEntity extends LightShardBaseEntity {

    private static final int NO_POTION_COLOR = -1;
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(LightMineEntity.class, EntityDataSerializers.INT);
    private boolean colorSet;

    protected final Set<MobEffectInstance> effects = Sets.newHashSet();

	public LightMineEntity(EntityType<? extends Projectile> entityType, Level world) {
		super(entityType, world);
	}

    public LightMineEntity(Level world, LivingEntity owner, Optional<LivingEntity> target, float detectionRange, float damage, float lifeSpanTicks) {
        super(SpectrumEntityTypes.LIGHT_MINE, world, owner, target, detectionRange, damage, lifeSpanTicks);
    }

    public static void summonBarrage(Level world, @NotNull LivingEntity user, @Nullable LivingEntity target, List<MobEffectInstance> effects) {
        summonBarrage(world, user, target, effects, user.getEyePosition(), DEFAULT_COUNT_PROVIDER);
    }

    public static void summonBarrage(Level world, @Nullable LivingEntity user, @Nullable LivingEntity target, List<MobEffectInstance> effects, Vec3 position, IntProvider count) {
        summonBarrageInternal(world, user, () -> {
            LightMineEntity entity = new LightMineEntity(world, user, Optional.ofNullable(target), 8, 1.0F, 800);
            entity.setEffects(effects);
            return entity;
        }, position, count);
    }

    public void setEffects(List<MobEffectInstance> effects) {
        this.effects.addAll(effects);
        if (this.effects.isEmpty()) {
            setColor(16777215);
        } else {
            setColor(PotionUtils.getColor(this.effects));
        }
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }
    
    private void setColor(int color) {
        this.colorSet = true;
        this.entityData.set(COLOR, color);
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        
        if (this.colorSet) {
            nbt.putInt("Color", this.getColor());
        }
        if (!this.effects.isEmpty()) {
            ListTag nbtList = new ListTag();
            for (MobEffectInstance statusEffectInstance : this.effects) {
                nbtList.add(statusEffectInstance.save(new CompoundTag()));
            }
            nbt.put("CustomPotionEffects", nbtList);
        }
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
    
        this.setEffects(PotionUtils.getCustomEffects(nbt));
    
        if (nbt.contains("Color", Tag.TAG_ANY_NUMERIC)) {
            this.setColor(nbt.getInt("Color"));
        } else {
            this.colorSet = false;
            if (this.effects.isEmpty()) {
                this.entityData.set(COLOR, NO_POTION_COLOR);
            } else {
                this.entityData.set(COLOR, PotionUtils.getColor(this.effects));
            }
        }
    }
    
    @Override
    public ResourceLocation getTexture() {
        return SpectrumCommon.locate("textures/entity/projectile/light_mine.png");
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, NO_POTION_COLOR);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && this.tickCount % 4 == 0) {
            this.spawnParticles();
        }
    }
    
    private void spawnParticles() {
        if (!this.effects.isEmpty()) {
            int color = this.getColor();
            double d = (double) (color >> 16 & 255) / 255.0;
            double e = (double) (color >> 8 & 255) / 255.0;
            double f = (double) (color & 255) / 255.0;
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), d, e, f);
        }
    }
    
    @Override
    protected void onHitEntity(LivingEntity attacked) {
        super.onHitEntity(attacked);
        
        Entity attacker = this.getEffectSource();

        Iterator<MobEffectInstance> var3 = this.effects.iterator();
        MobEffectInstance statusEffectInstance;
        while (var3.hasNext()) {
            statusEffectInstance = var3.next();
            attacked.addEffect(new MobEffectInstance(statusEffectInstance.getEffect(), Math.max(statusEffectInstance.getDuration() / 8, 1), statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.isVisible()), attacker);
        }
        if (!this.effects.isEmpty()) {
            var3 = this.effects.iterator();
            while (var3.hasNext()) {
                statusEffectInstance = var3.next();
                attacked.addEffect(statusEffectInstance, attacker);
            }
        }
    }
    
}
