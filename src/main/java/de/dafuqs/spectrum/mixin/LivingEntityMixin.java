package de.dafuqs.spectrum.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.entity.TouchingWaterAware;
import de.dafuqs.spectrum.api.item.ApplyFoodEffectsCallback;
import de.dafuqs.spectrum.api.item.ArmorWithHitEffect;
import de.dafuqs.spectrum.api.item.SplitDamageItem;
import de.dafuqs.spectrum.api.status_effect.StackableStatusEffect;
import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.cca.EverpromiseRibbonComponent;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.enchantments.DisarmingEnchantment;
import de.dafuqs.spectrum.enchantments.InexorableEnchantment;
import de.dafuqs.spectrum.helpers.ParticleHelper;
import de.dafuqs.spectrum.items.tools.DragonTalonItem;
import de.dafuqs.spectrum.items.trinkets.PuffCircletItem;
import de.dafuqs.spectrum.mixin.accessors.StatusEffectInstanceAccessor;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.registries.*;
import de.dafuqs.spectrum.status_effects.EffectProlongingStatusEffect;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow
	@Nullable
	protected Player attackingPlayer;

	@Shadow
	@Final
	private NonNullList<ItemStack> syncedArmorStacks;

	@Shadow
	public abstract boolean hasStatusEffect(MobEffect effect);

	@Shadow
	public abstract boolean blockedByShield(DamageSource source);

	@Shadow
	public abstract ItemStack getMainHandStack();

	@Shadow
	@Nullable
	public abstract MobEffectInstance getStatusEffect(MobEffect effect);

	@Shadow
	public abstract boolean canHaveStatusEffect(MobEffectInstance effect);

	@Shadow
	protected ItemStack activeItemStack;

	@Shadow
	public abstract void readCustomDataFromNbt(CompoundTag nbt);
	
	@Shadow
	public abstract boolean damage(DamageSource source, float amount);
	
	@Shadow
	public abstract boolean removeStatusEffect(MobEffect type);
	
	@Shadow
	public abstract boolean addStatusEffect(MobEffectInstance effect);

	@Shadow public abstract ItemStack getOffHandStack();

	@Shadow public abstract void damageArmor(DamageSource source, float amount);

	@Shadow public abstract int getArmor();

	@Shadow public abstract double getAttributeValue(Attribute attribute);

	@ModifyArg(method = "dropXp()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"), index = 2)
	protected int spectrum$applyExuberance(int originalXP) {
		return (int) (originalXP * spectrum$getExuberanceMod(this.attackingPlayer));
	}
	
	@Unique
	private float spectrum$getExuberanceMod(Player attackingPlayer) {
		if (attackingPlayer != null && SpectrumEnchantments.EXUBERANCE.canEntityUse(attackingPlayer)) {
			int exuberanceLevel = EnchantmentHelper.getEnchantmentLevel(SpectrumEnchantments.EXUBERANCE, attackingPlayer);
			return 1.0F + exuberanceLevel * SpectrumCommon.CONFIG.ExuberanceBonusExperiencePercentPerLevel;
		} else {
			return 1.0F;
		}
	}

	@Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasNoDrag()Z"))
	public void spectrum$modifyDragPhysics(CallbackInfo ci, @Local(ordinal = 1) LocalFloatRef f) {
		var needle = (DragonTalonItem) SpectrumItems.DRAGON_TALON;
		if (needle.isReservingSlot(this.getMainHandStack()) || needle.isReservingSlot(this.getOffHandStack())) {
			if (!((LivingEntity) (Object) this).onGround()) {
				f.set(0.945F);
			}
		}
	}

	@Inject(method = "applyArmorToDamage", at = @At("HEAD"), cancellable = true)
	public void spectrum$applySpecialArmorEffects(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
		if (source.is(SpectrumDamageTypes.IMPALING)) {
			this.damageArmor(source, amount * 10);
			amount = CombatRules.getDamageAfterAbsorb(amount, (float)this.getAttributeValue(Attributes.ARMOR_TOUGHNESS), Float.MAX_VALUE);
			cir.setReturnValue(amount);
			cir.cancel();
		}
		else if(source.is(SpectrumDamageTypes.EVISCERATION)) {
			this.damageArmor(source, amount);
			amount = CombatRules.getDamageAfterAbsorb(amount, (float) getArmor() / 2, (float)this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
			cir.setReturnValue(amount);
			cir.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V")
	public void spectrum$mitigateFallDamageWithPuffCirclet(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
		if (onGround) {
			LivingEntity thisEntity = (LivingEntity) (Object) this;
			if (!thisEntity.isInvulnerableTo(thisEntity.damageSources().fall()) && thisEntity.fallDistance > thisEntity.getMaxFallDistance()) {
				Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(thisEntity);
				if (component.isPresent()) {
					if (!component.get().getEquipped(SpectrumItems.PUFF_CIRCLET).isEmpty()) {
						int charges = AzureDikeProvider.getAzureDikeCharges(thisEntity);
						if (charges > 0) {
							AzureDikeProvider.absorbDamage(thisEntity, PuffCircletItem.FALL_DAMAGE_NEGATING_COST);
							
							thisEntity.fallDistance = 0;
							thisEntity.setDeltaMovement(thisEntity.getDeltaMovement().x, 0.5, thisEntity.getDeltaMovement().z);
							Level world = thisEntity.level();
							if (world.isClientSide) { // it is split here so the particles spawn immediately, without network lag
								ParticleHelper.playParticleWithPatternAndVelocityClient(thisEntity.level(), thisEntity.position(), SpectrumParticleTypes.WHITE_CRAFTING, VectorPattern.EIGHT, 0.4);
								ParticleHelper.playParticleWithPatternAndVelocityClient(thisEntity.level(), thisEntity.position(), SpectrumParticleTypes.BLUE_CRAFTING, VectorPattern.EIGHT_OFFSET, 0.5);
							} else if (thisEntity instanceof ServerPlayer serverPlayerEntity) {
								SpectrumS2CPacketSender.playParticleWithPatternAndVelocity(serverPlayerEntity, (ServerLevel) thisEntity.level(), thisEntity.position(), SpectrumParticleTypes.WHITE_CRAFTING, VectorPattern.EIGHT, 0.4);
								SpectrumS2CPacketSender.playParticleWithPatternAndVelocity(serverPlayerEntity, (ServerLevel) thisEntity.level(), thisEntity.position(), SpectrumParticleTypes.BLUE_CRAFTING, VectorPattern.EIGHT_OFFSET, 0.5);
							}
							thisEntity.level().playSound(null, thisEntity.blockPosition(), SpectrumSoundEvents.PUFF_CIRCLET_PFFT, SoundSource.PLAYERS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}

	@ModifyVariable(at = @At("HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", argsOnly = true)
	public float spectrum$modifyDamage(float amount, DamageSource source) {
		@Nullable MobEffectInstance vulnerability = getStatusEffect(SpectrumStatusEffects.VULNERABILITY);
		if (vulnerability != null) {
			amount *= 1 + (SpectrumStatusEffects.VULNERABILITY_ADDITIONAL_DAMAGE_PERCENT_PER_LEVEL * vulnerability.getAmplifier());
		}
		
		if (amount <= 0
				|| source.is(SpectrumDamageTypeTags.BYPASSES_DIKE)
				|| this.blockedByShield(source)
				|| ((Entity) (Object) this).isInvulnerableTo(source)
				|| source.is(DamageTypeTags.IS_FIRE) && hasStatusEffect(MobEffects.FIRE_RESISTANCE)) {
			
			return amount;
		}
		
		return AzureDikeProvider.absorbDamage((LivingEntity) (Object) this, amount);
	}

	@Inject(at = @At("RETURN"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
	public void spectrum$applyDisarmingEnchantment(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// true if the entity got hurt
		if (cir.getReturnValue() != null && cir.getReturnValue()) {
			// Disarming does not trigger when dealing damage to enemies using thorns
			if (!source.is(DamageTypes.THORNS)) {
				if (source.getEntity() instanceof LivingEntity livingSource && SpectrumEnchantments.DISARMING.canEntityUse(livingSource)) {
					int disarmingLevel = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.DISARMING, livingSource.getMainHandItem());
					if (disarmingLevel > 0 && Math.random() < disarmingLevel * SpectrumCommon.CONFIG.DisarmingChancePerLevelMobs) {
						DisarmingEnchantment.disarmEntity((LivingEntity) (Object) this, this.syncedArmorStacks);
					}
				}
			}
		}
	}

	@Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
	public void spectrum$applyBonusDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity target = (LivingEntity) (Object) this;

		// SetHealth damage does exactly that
		if (amount > 0 && source.is(SpectrumDamageTypeTags.USES_SET_HEALTH)) {
			float h = target.getHealth();
			target.setHealth(h - amount);
			target.getCombatTracker().recordDamage(source, amount);
			if (target.isDeadOrDying()) {
				target.die(source);
			}
			return;
		}

		// If this entity is hit with a SplitDamageItem, damage() gets called recursively for each type of damage dealt
		if (!SpectrumDamageTypes.recursiveDamageFlag && amount > 0 && source.getDirectEntity() instanceof LivingEntity livingSource) {
			ItemStack mainHandStack = livingSource.getMainHandItem();
			if (mainHandStack.getItem() instanceof SplitDamageItem splitDamageItem) {
				SpectrumDamageTypes.recursiveDamageFlag = true;
				SplitDamageItem.DamageComposition composition = splitDamageItem.getDamageComposition(livingSource, target, activeItemStack, amount);
				
				boolean damaged = false;
				for (Tuple<DamageSource, Float> entry : composition.get()) {
					damaged |= damage(entry.getA(), entry.getB());
				}
				
				SpectrumDamageTypes.recursiveDamageFlag = false;
				cir.setReturnValue(damaged);
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z", cancellable = true)
	public void spectrum$checkForTotemPendant(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			// if no other totem triggered: check for a totem pendant
			LivingEntity thisEntity = (LivingEntity) (Object) this;
			Optional<TrinketComponent> optionalTrinketComponent = TrinketsApi.getTrinketComponent(thisEntity);
			if (optionalTrinketComponent.isPresent()) {
				List<Tuple<SlotReference, ItemStack>> totems = optionalTrinketComponent.get().getEquipped(SpectrumItems.TOTEM_PENDANT);
				for (Tuple<SlotReference, ItemStack> pair : totems) {
					if (pair.getB().getCount() > 0) {
						// consume pendant
						pair.getB().shrink(1);

						// Heal and add effects
						thisEntity.setHealth(1.0F);
						thisEntity.removeAllEffects();
						thisEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
						thisEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
						thisEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
						thisEntity.level().broadcastEntityEvent(thisEntity, (byte) 35);

						// override the previous return value
						cir.setReturnValue(true);
					}
				}
			}
		}
	}

	@Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z", ordinal = 1))
	public void spectrum$TriggerArmorWithHitEffect(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity thisEntity = (LivingEntity) (Object) this;
		Level world = thisEntity.level();
		if (!world.isClientSide) {
			if (thisEntity instanceof Mob thisMobEntity) {
				for (ItemStack armorItemStack : thisMobEntity.getArmorSlots()) {
					if (armorItemStack.getItem() instanceof ArmorWithHitEffect armorWithHitEffect) {
						armorWithHitEffect.onHit(armorItemStack, source, thisMobEntity, amount);
					}
				}
			} else if (thisEntity instanceof ServerPlayer thisPlayerEntity) {
				for (ItemStack armorItemStack : thisPlayerEntity.getArmorSlots()) {
					if (armorItemStack.getItem() instanceof ArmorWithHitEffect armorWithHitEffect) {
						armorWithHitEffect.onHit(armorItemStack, source, thisPlayerEntity, amount);
					}
				}
			}
		}
	}

	@Inject(method = "canHaveStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("RETURN"), cancellable = true)
	public void spectrum$canHaveStatusEffect(MobEffectInstance statusEffectInstance, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && this.hasStatusEffect(SpectrumStatusEffects.IMMUNITY) && statusEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL && !SpectrumStatusEffectTags.isUncurable(statusEffectInstance.getEffect())) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "setSprinting(Z)V", at = @At("HEAD"), cancellable = true)
	public void spectrum$setSprinting(boolean sprinting, CallbackInfo ci) {
		if (sprinting && ((LivingEntity) (Object) this).hasEffect(SpectrumStatusEffects.SCARRED)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("TAIL"), method = "applyFoodEffects(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)V")
	public void spectrum$eat(ItemStack stack, Level world, LivingEntity targetEntity, CallbackInfo ci) {
		Item item = stack.getItem();
		if (item instanceof ApplyFoodEffectsCallback foodWithCallback) {
			foodWithCallback.afterConsumption(world, stack, (LivingEntity) (Object) this);
		}
	}

	@Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
	public void spectrum$addStatusEffect(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
		MobEffect effectType = effect.getEffect();
		if (effectType instanceof StackableStatusEffect) {
			if (!SpectrumStatusEffects.effectsAreGettingStacked) {
				if (this.canHaveStatusEffect(effect)) {
					MobEffectInstance existingInstance = getStatusEffect(effect.getEffect());
					if (existingInstance != null) {
						SpectrumStatusEffects.effectsAreGettingStacked = true;

						int newAmplifier = 1 + existingInstance.getAmplifier() + effect.getAmplifier();
						MobEffectInstance newInstance = new MobEffectInstance(existingInstance.getEffect(), existingInstance.getDuration(), newAmplifier, existingInstance.isAmbient(), existingInstance.isVisible(), existingInstance.showIcon());
						removeStatusEffect(existingInstance.getEffect());
						addStatusEffect(newInstance);
						cir.cancel();
					}
				} else {
					SpectrumStatusEffects.effectsAreGettingStacked = false;
				}
			} else {
				SpectrumStatusEffects.effectsAreGettingStacked = false;
			}
		} else if (EffectProlongingStatusEffect.canBeExtended(effectType)) {
			MobEffectInstance effectProlongingInstance = this.getStatusEffect(SpectrumStatusEffects.EFFECT_PROLONGING);
			if (effectProlongingInstance != null) {
				((StatusEffectInstanceAccessor) effect).setDuration(EffectProlongingStatusEffect.getExtendedDuration(effect.getDuration(), effectProlongingInstance.getAmplifier()));
			}
		}
	}

	@Inject(method = "drop(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD"), cancellable = true)
	protected void drop(DamageSource source, CallbackInfo ci) {
		LivingEntity thisEntity = (LivingEntity) (Object) this;

		if (EverpromiseRibbonComponent.hasRibbon(thisEntity)) {
			ItemStack memoryStack = MemoryItem.getMemoryForEntity(thisEntity);
			MemoryItem.setTicksToManifest(memoryStack, 20);
			MemoryItem.setSpawnAsAdult(memoryStack, true);
			MemoryItem.markAsBrokenPromise(memoryStack, true);

			Vec3 entityPos = thisEntity.position();
			ItemEntity itemEntity = new ItemEntity(thisEntity.level(), entityPos.x(), entityPos.y(), entityPos.z(), memoryStack);
			thisEntity.level().addFreshEntity(itemEntity);

			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	protected void applyInexorableEffects(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity.level() != null && entity.level().getGameTime() % 20 == 0) {
			InexorableEnchantment.checkAndRemoveSlowdownModifiers(entity);
		}
	}
	
	@Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isWet()Z"))
	public boolean spectrum$isWet(LivingEntity livingEntity) {
		return livingEntity.isInWater() ? ((TouchingWaterAware) livingEntity).spectrum$isActuallyTouchingWater() : livingEntity.isInWaterRainOrBubble();
	}
}