package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
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
import java.util.function.Supplier;

public class LightShardEntity extends LightShardBaseEntity {
	
	public LightShardEntity(EntityType<? extends Projectile> entityType, Level world) {
		super(entityType, world);
	}
	
	public LightShardEntity(Level world, LivingEntity owner, Optional<LivingEntity> target, float damageMod, float lifeSpanTicks) {
		super(SpectrumEntityTypes.LIGHT_SHARD, world, owner, target, 48, damageMod, lifeSpanTicks);
	}
	
	public static void summonBarrage(Level world, @NotNull LivingEntity user, @Nullable LivingEntity target) {
		summonBarrage(world, user, target, user.getEyePosition(), DEFAULT_COUNT_PROVIDER);
	}

	public static void summonBarrage(Level world, @Nullable LivingEntity user, @Nullable LivingEntity target, Vec3 position, IntProvider count) {
		summonBarrage(world, user, position, count, () -> new LightShardEntity(world, user, Optional.ofNullable(target), 0.5F, 200));
	}

	public static void summonBarrage(Level world, @Nullable LivingEntity user, Vec3 position, IntProvider count, Supplier<LightShardBaseEntity> supplier) {
		summonBarrageInternal(world, user, supplier, position, count);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return SpectrumCommon.locate("textures/entity/projectile/light_shard.png");
	}
	
}
