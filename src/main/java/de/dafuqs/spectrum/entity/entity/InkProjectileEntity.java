package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.api.block.ColorableBlock;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.helpers.BlockVariantHelper;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class InkProjectileEntity extends MagicProjectileEntity {

	private static final int COLOR_SPLAT_RANGE = 2;
	private static final int SPELL_POTENCY = 2;
	private static final float DAMAGE_PER_POTENCY = 0.5F;

	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.INT);

	public InkProjectileEntity(EntityType<InkProjectileEntity> type, Level world) {
		super(type, world);
	}

	public InkProjectileEntity(double x, double y, double z, Level world) {
		this(SpectrumEntityTypes.INK_PROJECTILE, world);
		this.moveTo(x, y, z, this.getYRot(), this.getXRot());
		this.reapplyPosition();
	}

	public InkProjectileEntity(Level world, LivingEntity owner) {
		this(owner.getX(), owner.getEyeY() - 0.10000000149011612D, owner.getZ(), world);
		this.setOwner(owner);
		this.setRot(owner.getYRot(), owner.getXRot());
	}

	public static void shoot(Level world, LivingEntity entity, InkColor color) {
		InkProjectileEntity projectile = new InkProjectileEntity(world, entity);
		projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 2.0F, 1.0F);
		projectile.setColor(color);
		world.addFreshEntity(projectile);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(COLOR, -1);
	}

	public int getColor() {
		return this.entityData.get(COLOR);
	}
	
	@Override
	public DyeColor getDyeColor() {
		return DyeColor.byId(this.entityData.get(COLOR));
	}
	
	public void setColor(InkColor inkColor) {
		this.entityData.set(COLOR, inkColor.getDyeColor().getId());
	}
	
	protected void setColor(int color) {
		this.entityData.set(COLOR, color);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		
		int color = this.getColor();
		if (color != -1) {
			nbt.putInt("Color", color);
		}
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		
		if (nbt.contains("Color", 99)) {
			this.setColor(nbt.getInt("Color"));
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		this.spawnParticles(1);
	}

	private void spawnParticles(int amount) {
		int colorOrdinal = this.getColor();
		if (colorOrdinal != -1 && amount > 0) {
			DyeColor dyeColor = DyeColor.byId(colorOrdinal);
			for (int j = 0; j < amount; ++j) {
				this.level().addParticle(SpectrumParticleTypes.getCraftingParticle(dyeColor), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0, 0);
			}
		}
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		
		Entity entity = entityHitResult.getEntity();
		
		ColorHelper.tryColorEntity(null, entity, getDyeColor());
		
		float velocity = (float) this.getDeltaMovement().length();
		int damage = Mth.ceil(Mth.clamp((double) velocity * DAMAGE_PER_POTENCY * SPELL_POTENCY, 0.0D, 2.147483647E9D));
		
		Entity entity2 = this.getOwner();
		DamageSource damageSource;
		if (entity2 == null) {
			damageSource = SpectrumDamageTypes.inkProjectile(this, this);
		} else {
			damageSource = SpectrumDamageTypes.inkProjectile(this, entity2);
			if (entity2 instanceof LivingEntity) {
				((LivingEntity) entity2).setLastHurtMob(entity);
			}
		}
		
		if (entity.hurt(damageSource, (float) damage)) {
			if (entity instanceof LivingEntity livingEntity) {
				
				if (!this.level().isClientSide() && entity2 instanceof LivingEntity) {
					EnchantmentHelper.doPostHurtEffects(livingEntity, entity2);
					EnchantmentHelper.doPostDamageEffects((LivingEntity) entity2, livingEntity);
				}
				
				this.onHit(livingEntity);
				
				if (livingEntity != entity2 && livingEntity instanceof Player && entity2 instanceof ServerPlayer && !this.isSilent()) {
					((ServerPlayer) entity2).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
				}
				
				if (!this.level().isClientSide() && entity2 instanceof ServerPlayer serverPlayerEntity) {
					if (!entity.isAlive()) {
						SpectrumAdvancementCriteria.KILLED_BY_INK_PROJECTILE.trigger(serverPlayerEntity, List.of(entity));
					}
				}
			}
			
			this.playSound(this.getHitSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.discard();
		} else {
			this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
			this.setYRot(this.getYRot() + 180.0F);
			this.yRotO += 180.0F;
			if (!this.level().isClientSide() && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
				this.discard();
			}
		}
	}
	
	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		
		Vec3 vec3d = blockHitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
		this.setDeltaMovement(vec3d);
		Vec3 vec3d2 = vec3d.normalize().scale(0.05000000074505806D);
		this.setPosRaw(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
		this.playSound(this.getHitSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
		
		int colorOrdinal = this.getColor();
		if (colorOrdinal != -1) {
			DyeColor dyeColor = DyeColor.byId(colorOrdinal);
			
			for (BlockPos blockPos : BlockPos.withinManhattan(blockHitResult.getBlockPos(), COLOR_SPLAT_RANGE, COLOR_SPLAT_RANGE, COLOR_SPLAT_RANGE)) {
				if (this.level().getBlockState(blockPos).getBlock() instanceof ColorableBlock colorableBlock) {
					if (!GenericClaimModsCompat.canModify(this.level(), blockPos, this.getOwner())) {
						continue;
					}
					colorableBlock.color(this.level(), blockPos, dyeColor);
					continue;
				}
				BlockState coloredBlockState = BlockVariantHelper.getCursedBlockColorVariant(this.level(), blockPos, dyeColor);
				if (!coloredBlockState.isAir()) {
					this.level().setBlockAndUpdate(blockPos, coloredBlockState);
				}
			}
			
			affectEntitiesInRange(this.getOwner());
			
			// TODO: uncomment this when all 16 ink effects are finished
			// InkSpellEffect.trigger(InkColor.of(dyeColor), this.getWorld(), blockHitResult.getPos(), SPELL_POTENCY);
		}
		
		this.discard();
	}

	protected void onHit(LivingEntity target) {
		int colorOrdinal = this.getColor();
		if (colorOrdinal != -1) {
			//InkColor.all().get(colorOrdinal);
			
			
			Entity entity = target; //this.getEffectCause();
			
			// TODO: this is a dummy effect
			Vec3 vec3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) 3 * 0.6D);
			if (vec3d.lengthSqr() > 0.0D) {
				entity.push(vec3d.x, 0.1D, vec3d.z);
			}
			
			affectEntitiesInRange(this.getOwner());
			
			/*Iterator var3 = this.potion.getEffects().iterator();
			
			StatusEffectInstance statusEffectInstance;
			while (var3.hasNext()) {
				statusEffectInstance = (StatusEffectInstance) var3.next();
				target.addStatusEffect(new StatusEffectInstance(statusEffectInstance.getEffectType(), Math.max(statusEffectInstance.getDuration() / 8, 1), statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()), entity);
			}
			
			if (!this.effects.isEmpty()) {
				var3 = this.effects.iterator();
				
				while (var3.hasNext()) {
					statusEffectInstance = (StatusEffectInstance) var3.next();
					target.addStatusEffect(statusEffectInstance, entity);
				}
			}*/
		}
		
		this.discard();
	}
	
	public void affectEntitiesInRange(Entity attacker) {
		this.level().gameEvent(this, GameEvent.PROJECTILE_LAND, BlockPos.containing(this.position().x, this.position().y, this.position().z));
		
		double posX = this.position().x;
		double posY = this.position().y;
		double posZ = this.position().z;
		
		float q = SPELL_POTENCY * 2.0F;
		double k = Mth.floor(posX - (double) q - 1.0D);
		double l = Mth.floor(posX + (double) q + 1.0D);
		int r = Mth.floor(posY - (double) q - 1.0D);
		int s = Mth.floor(posY + (double) q + 1.0D);
		int t = Mth.floor(posZ - (double) q - 1.0D);
		int u = Mth.floor(posZ + (double) q + 1.0D);
		List<Entity> list = this.level().getEntities(this, new AABB(k, r, t, l, s, u));
		Vec3 vec3d = new Vec3(posX, posY, posZ);
		
		for (Entity entity : list) {
			if (!GenericClaimModsCompat.canInteract(this.level(), entity, attacker)) {
				continue;
			}
			
			ColorHelper.tryColorEntity(null, entity, getDyeColor());
			
			if (!entity.ignoreExplosion()) {
				double w = Math.sqrt(entity.distanceToSqr(vec3d)) / (double) q;
				if (w <= 1.0D) {
					double x = entity.getX() - posX;
					double y = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - posY;
					double z = entity.getZ() - posZ;
					double aa = Math.sqrt(x * x + y * y + z * z);
					if (aa != 0.0D) {
						x /= aa;
						y /= aa;
						z /= aa;
						double ab = Explosion.getSeenPercent(vec3d, entity);
						double ac = (1.0D - w) * ab;
						
						//float damage = (float) ((int) ((ac * ac + ac) / 2.0D * (double) q + 1.0D));
						//entity.damage(SpectrumDamageSources.inkProjectile(this, attacker), damage);
						
						double ad = ac;
						if (entity instanceof LivingEntity livingEntity) {
							ad = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, ac);
						}
						
						entity.setDeltaMovement(entity.getDeltaMovement().add(x * ad, y * ad, z * ad));
					}
				}
			}
		}
	}
	
}
