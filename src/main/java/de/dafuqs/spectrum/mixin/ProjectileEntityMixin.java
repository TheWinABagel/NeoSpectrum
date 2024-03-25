package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.items.trinkets.PuffCircletItem;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Projectile.class)
public abstract class ProjectileEntityMixin {
	
	@Shadow
	public abstract void setVelocity(double x, double y, double z, float speed, float divergence);
	
	@Inject(at = @At("HEAD"), method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V", cancellable = true)
	protected void onProjectileHit(EntityHitResult entityHitResult, CallbackInfo ci) {
		// if the target has a Puff circlet equipped
		// protect it from this projectile
		Projectile thisEntity = (Projectile) (Object) this;
		Level world = thisEntity.level();
		if (!world.isClientSide) {
			Entity entity = entityHitResult.getEntity();
			if (entity instanceof LivingEntity livingEntity) {
				boolean protect = false;
				
				MobEffectInstance reboundInstance = livingEntity.getEffect(SpectrumStatusEffects.PROJECTILE_REBOUND);
				if (reboundInstance != null && entity.level().getRandom().nextFloat() < SpectrumStatusEffects.PROJECTILE_REBOUND_CHANCE_PER_LEVEL * reboundInstance.getAmplifier()) {
					protect = true;
				} else {
					Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
					if (component.isPresent()) {
						List<Tuple<SlotReference, ItemStack>> equipped = component.get().getEquipped(SpectrumItems.PUFF_CIRCLET);
						if (!equipped.isEmpty()) {
							int charges = AzureDikeProvider.getAzureDikeCharges(livingEntity);
							if (charges > 0) {
								AzureDikeProvider.absorbDamage(livingEntity, PuffCircletItem.PROJECTILE_DEFLECTION_COST);
								protect = true;
							}
						}
					}
				}
				
				if (protect) {
					this.setVelocity(0, 0, 0, 0, 0);
					
					SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world, thisEntity.position(),
							SpectrumParticleTypes.WHITE_CRAFTING, 6,
							new Vec3(0, 0, 0),
							new Vec3(thisEntity.getX() - livingEntity.position().x, thisEntity.getY() - livingEntity.position().y, thisEntity.getZ() - livingEntity.position().z));
					SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world, thisEntity.position(),
							SpectrumParticleTypes.BLUE_CRAFTING, 6,
							new Vec3(0, 0, 0),
							new Vec3(thisEntity.getX() - livingEntity.position().x, thisEntity.getY() - livingEntity.position().y, thisEntity.getZ() - livingEntity.position().z));
					
					world.playSound(null, thisEntity.blockPosition(), SpectrumSoundEvents.PUFF_CIRCLET_PFFT, SoundSource.PLAYERS, 1.0F, 1.0F);
					ci.cancel();
				}
				
			}
		}
	}
	
	
}
