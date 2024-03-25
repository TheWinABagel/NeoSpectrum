package de.dafuqs.spectrum.spells;

import com.google.common.collect.Maps;
import de.dafuqs.spectrum.api.block.MoonstoneStrikeableBlock;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MoonstoneStrike {

	private final Level world;
	private final double x;
	private final double y;
    private final double z;
    public final @Nullable Entity entity;
    public final float power;
    public final float knockbackMod;
    private final DamageSource damageSource;
	protected final Map<Player, Vec3> affectedPlayers;

    public MoonstoneStrike(Level world, @Nullable Entity entity, @Nullable DamageSource damageSource, double x, double y, double z, float power, float knockbackMod) {
        this.affectedPlayers = Maps.newHashMap();
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.knockbackMod = knockbackMod;
        this.x = x;
        this.y = y;
		this.z = z;
		this.damageSource = damageSource == null ? SpectrumDamageTypes.moonstoneStrike(world, this) : damageSource;
    }

	public static void create(Level world, Entity entity, @Nullable DamageSource damageSource, double x, double y, double z, float power) {
        create(world, entity, damageSource, x, y, z, power, power);
    }

	public static void create(Level world, Entity entity, @Nullable DamageSource damageSource, double x, double y, double z, float power, float knockbackMod) {
        MoonstoneStrike moonstoneStrike = new MoonstoneStrike(world, entity, damageSource, x, y, z, power, knockbackMod);

		if (world.isClientSide) {
            world.playLocalSound(x, y, z, SpectrumSoundEvents.MOONSTONE_STRIKE, SoundSource.BLOCKS, 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F, false);
			world.playLocalSound(x, y, z, SpectrumSoundEvents.SOFT_HUM, SoundSource.BLOCKS, 0.5F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F, false);
			world.addParticle(SpectrumParticleTypes.MOONSTONE_STRIKE, x, y, z, 1.0, 0.0, 0.0);
        } else {
            moonstoneStrike.damageAndKnockbackEntities();
            SpectrumS2CPacketSender.sendMoonstoneBlast((ServerLevel) world, moonstoneStrike);
            moonstoneStrike.affectWorld();
        }
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public float getPower() {
        return power;
    }

	public float getKnockbackMod() {
        return knockbackMod;
    }

	public DamageSource getDamageSource() {
        return this.damageSource;
    }

	public Map<Player, Vec3> getAffectedPlayers() {
        return this.affectedPlayers;
    }

	public static float getExposure(Vec3 source, Entity entity) {
        AABB box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
            int i = 0;
            int j = 0;

            for(double k = 0.0; k <= 1.0; k += d) {
                for(double l = 0.0; l <= 1.0; l += e) {
                    for(double m = 0.0; m <= 1.0; m += f) {
                        double n = Mth.lerp(k, box.minX, box.maxX);
                        double o = Mth.lerp(l, box.minY, box.maxY);
                        double p = Mth.lerp(m, box.minZ, box.maxZ);
                        Vec3 vec3d = new Vec3(n + g, o, p + h);
                        if (entity.level().clip(new ClipContext(vec3d, source, Block.COLLIDER, Fluid.NONE, entity)).getType() == Type.MISS) {
                            ++i;
                        }

						++j;
                    }
                }
            }

			return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }

	public void damageAndKnockbackEntities() {
        this.world.gameEvent(this.entity, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        
        float power2 = this.power * 2.0F;
        int minX = Mth.floor(this.x - (double) power2 - 1.0);
        int maxX = Mth.floor(this.x + (double) power2 + 1.0);
        int minY = Mth.floor(this.y - (double) power2 - 1.0);
        int maxY = Mth.floor(this.y + (double) power2 + 1.0);
        int minZ = Mth.floor(this.z - (double) power2 - 1.0);
        int maxZ = Mth.floor(this.z + (double) power2 + 1.0);
        Vec3 center = new Vec3(this.x, this.y, this.z);

		for (Entity entity : world.getEntities(this.entity, new AABB(minX, minY, minZ, maxX, maxY, maxZ))) {
            if (!entity.ignoreExplosion()) {
                double w = Math.sqrt(entity.distanceToSqr(center)) / (double) power2;
                if (w <= 1.0) {
                    double difX = entity.getX() - this.x;
                    double difY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double difZ = entity.getZ() - this.z;
                    double sqrt = Math.sqrt(difX * difX + difY * difY + difZ * difZ);
                    if (sqrt != 0.0) {
                        difX /= sqrt;
                        difY /= sqrt;
                        difZ /= sqrt;
                        double exposure = getExposure(center, entity);
                        double ac = (1.0 - w) * exposure;
                        entity.hurt(this.getDamageSource(), (float) ((int) ((ac * ac + ac) / 2.0 * 7.0 * (double) power2 + 1.0)));
                        double knockback = ac * this.knockbackMod;
                        if (entity instanceof LivingEntity) {
                            knockback = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, ac);
                        }

						entity.setDeltaMovement(entity.getDeltaMovement().add(difX * knockback, difY * knockback, difZ * knockback));
                        if (entity instanceof Player playerEntity) {
                            if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                                this.affectedPlayers.put(playerEntity, new Vec3(difX * ac, difY * ac, difZ * ac));
                            }
                        }
                    }
                }
            }
        }
    }

	public void affectWorld() {
        LivingEntity cause = getCausingEntity();
        int range = Math.max(2, (int) this.power / 2);
		for (BlockPos pos : BlockPos.withinManhattan(BlockPos.containing(this.x, this.y, this.z), range, range, range)) {
			BlockState blockState = world.getBlockState(pos);
			net.minecraft.world.level.block.Block block = blockState.getBlock();
			if (block instanceof MoonstoneStrikeableBlock moonstoneStrikeableBlock) {
				moonstoneStrikeableBlock.onMoonstoneStrike(world, pos, cause);
			}
		}
    }

	public @Nullable LivingEntity getCausingEntity() {
        if (this.entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        } else if (this.entity instanceof Projectile projectileEntity && projectileEntity.getOwner() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }
    
}
