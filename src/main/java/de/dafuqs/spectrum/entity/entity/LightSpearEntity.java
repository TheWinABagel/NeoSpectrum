package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LightSpearEntity extends LightShardBaseEntity {
    
    public LightSpearEntity(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }
    
    public LightSpearEntity(Level world, LivingEntity owner, Optional<LivingEntity> target, float damage, int lifeSpanTicks) {
		super(SpectrumEntityTypes.LIGHT_SPEAR, world, owner, target, -1, damage, lifeSpanTicks);
	}

    @Override
    public void tick() {
        super.tick();

        targetEntity.ifPresent(entity -> this.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position()));
	}

	@Override
	public ResourceLocation getTexture() {
		return SpectrumCommon.locate("textures/entity/projectile/light_spear.png");
	}

	public static void summonBarrage(Level world, @NotNull LivingEntity user, @Nullable LivingEntity target) {
		summonBarrage(world, user, target, user.getEyePosition(), DEFAULT_COUNT_PROVIDER);
	}

	public static void summonBarrage(Level world, @Nullable LivingEntity user, @Nullable LivingEntity target, Vec3 position, IntProvider count) {
		summonBarrageInternal(world, user, () -> new LightSpearEntity(world, user, Optional.ofNullable(target), 12.0F, 200), position, count);
	}

}
