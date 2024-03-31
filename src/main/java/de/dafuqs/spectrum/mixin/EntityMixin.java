package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.cca.LastKillComponent;
import de.dafuqs.spectrum.enchantments.InexorableEnchantment;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import de.dafuqs.spectrum.status_effects.FrenzyStatusEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	
	@Inject(method = "killedEntity", at = @At("HEAD"))
	private void spectrum$rememberKillOther(ServerLevel world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
		Entity entity = (Entity) (Object) this;
		if (entity instanceof LivingEntity livingEntity) {
			LastKillComponent.rememberKillTick(livingEntity, livingEntity.level().getGameTime());
			
			MobEffectInstance frenzy = livingEntity.getEffect(SpectrumStatusEffects.FRENZY);
			if (frenzy != null) {
				((FrenzyStatusEffect) frenzy.getEffect()).onKill(livingEntity, frenzy.getAmplifier());
			}
		}
	}

	@ModifyVariable(method = "makeStuckInBlock", at = @At(value = "LOAD"), argsOnly = true)
	private Vec3 spectrum$applyInexorableAntiBlockSlowdown(Vec3 multiplier) {
		if ((Object) this instanceof LivingEntity livingEntity && InexorableEnchantment.isArmorActive(livingEntity)) {
			return Vec3.ZERO;
		}
		return multiplier;
	}
	
	@Inject(method = "getBlockSpeedFactor", at = @At("RETURN"), cancellable = true)
	private void spectrum$applyInexorableAntiSlowdown(CallbackInfoReturnable<Float> cir) {
		if ((Object) this instanceof LivingEntity livingEntity && InexorableEnchantment.isArmorActive(livingEntity)) {
			cir.setReturnValue(Math.max(cir.getReturnValue(), 1F));
		}
	}
	
	@Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
	public void spectrum$dropStack(ItemStack stack, CallbackInfoReturnable<ItemEntity> cir) {
		if ((Object) this instanceof LivingEntity thisLivingEntity) {
			if (thisLivingEntity.isDeadOrDying() && thisLivingEntity.getLastHurtByMob() instanceof Player killer) {
				if (EnchantmentHelper.getEnchantmentLevel(SpectrumEnchantments.INVENTORY_INSERTION, killer) > 0) {
					Item item = stack.getItem();
					int count = stack.getCount();
					
					if (killer.getInventory().add(stack)) {
						killer.level().playSound(null, killer.getX(), killer.getY(), killer.getZ(),
								SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
								0.2F, ((killer.getRandom().nextFloat() - killer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
						
						if (stack.isEmpty()) {
							killer.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
							cir.cancel();
						}
						killer.awardStat(Stats.ITEM_PICKED_UP.get(item), count - stack.getCount());
					}
				}
			}
		}
	}
	
}
