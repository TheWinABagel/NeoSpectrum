package de.dafuqs.spectrum.mixin;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import de.dafuqs.spectrum.api.entity.PlayerEntityAccessor;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.cca.last_kill.LastKillComponent;
import de.dafuqs.spectrum.enchantments.ImprovedCriticalEnchantment;
import de.dafuqs.spectrum.entity.entity.SpectrumFishingBobberEntity;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.items.trinkets.AttackRingItem;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import de.dafuqs.spectrum.status_effects.FrenzyStatusEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Shadow
	public abstract Iterable<ItemStack> getHandSlots();

	@Unique
	public SpectrumFishingBobberEntity spectrum$fishingBobber;
	
	@Inject(method = "killedEntity", at = @At("HEAD"))
	private void spectrum$rememberKillOther(ServerLevel world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
		Player entity = (Player) (Object) this;
		LastKillComponent.rememberKillTick(entity, entity.level().getGameTime());
		
		MobEffectInstance frenzy = entity.getEffect(SpectrumStatusEffects.FRENZY);
		if (frenzy != null) {
			((FrenzyStatusEffect) frenzy.getEffect()).onKill(entity, frenzy.getAmplifier());
		}
	}
	
	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"))
	protected void spectrum$calculateModifiers(Entity target, CallbackInfo ci) {
		Player player = (Player) (Object) this;
		
		Multimap<Attribute, AttributeModifier> map = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
		
		AttributeModifier jeopardantModifier;
		if (SpectrumTrinketItem.hasEquipped(player, SpectrumItems.JEOPARDANT)) {
			jeopardantModifier = new AttributeModifier(AttackRingItem.ATTACK_RING_DAMAGE_UUID, AttackRingItem.ATTACK_RING_DAMAGE_NAME, AttackRingItem.getAttackModifierForEntity(player), AttributeModifier.Operation.MULTIPLY_TOTAL);
		} else {
			jeopardantModifier = new AttributeModifier(AttackRingItem.ATTACK_RING_DAMAGE_UUID, AttackRingItem.ATTACK_RING_DAMAGE_NAME, 0, AttributeModifier.Operation.MULTIPLY_TOTAL);
		}
		map.put(Attributes.ATTACK_DAMAGE, jeopardantModifier);
		
		if (SpectrumEnchantments.IMPROVED_CRITICAL.canEntityUse(player)) {
			int improvedCriticalLevel = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.IMPROVED_CRITICAL, player.getMainHandItem(), player);
			AttributeModifier improvedCriticalModifier = new AttributeModifier(ImprovedCriticalEnchantment.EXTRA_CRIT_DAMAGE_MULTIPLIER_ATTRIBUTE_UUID, ImprovedCriticalEnchantment.EXTRA_CRIT_DAMAGE_MULTIPLIER_ATTRIBUTE_NAME, ImprovedCriticalEnchantment.getAddtionalCritDamageMultiplier(improvedCriticalLevel), AttributeModifier.Operation.ADDITION);
			map.put(AdditionalEntityAttributes.CRITICAL_BONUS_DAMAGE, improvedCriticalModifier);
		}
		
		player.getAttributes().addTransientAttributeModifiers(map);
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	protected List<LivingEntity> spectrum$increaseSweepRadius(List<LivingEntity> original, Entity target) {
		var stack = this.getItemInHand(InteractionHand.MAIN_HAND);
		if (stack.getItem() == SpectrumItems.DRACONIC_TWINSWORD)
			return this.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(2.5, 0.4, 2.5));
		return original;
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D", shift = At.Shift.AFTER))
	protected double spectrum$increaseSweepMaxDistance(double original) {
		var stack = this.getItemInHand(InteractionHand.MAIN_HAND);
		if (stack.getItem() == SpectrumItems.DRACONIC_TWINSWORD)
			return original * 5;
		return original;
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	protected void spectrum$jumpAdvancementCriterion(CallbackInfo ci) {

		if ((Object) this instanceof ServerPlayer serverPlayerEntity) {
			SpectrumAdvancementCriteria.TAKE_OFF_BELT_JUMP.trigger(serverPlayerEntity);
		}
	}
	
	@Inject(method = "isInvulnerableTo", at = @At("TAIL"), cancellable = true)
	public void spectrum$isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue() && damageSource.is(DamageTypeTags.IS_FIRE) && SpectrumTrinketItem.hasEquipped((Player) (Object) this, SpectrumItems.ASHEN_CIRCLET)) {
			cir.setReturnValue(true);
		}
	}
	
	@Override
	public void setSpectrumBobber(SpectrumFishingBobberEntity bobber) {
		this.spectrum$fishingBobber = bobber;
	}
	
	@Override
	public SpectrumFishingBobberEntity getSpectrumBobber() {
		return this.spectrum$fishingBobber;
	}
	
	@Inject(method = "isHurt", at = @At("HEAD"), cancellable = true)
	public void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
		Player player = (Player) (Object) this;
		if (player.hasEffect(SpectrumStatusEffects.SCARRED)) {
			cir.setReturnValue(false);
		}
	}
	
	// If the player holds an ExperienceStorageItem in their hands
	// experience is tried to get put in there first
	@ModifyVariable(at = @At("HEAD"), method = "giveExperiencePoints", argsOnly = true)
	public int addExperience(int experience) {
		if (experience < 0) { // draining XP, like Botania's Rosa Arcana
			return experience;
		}
		
		// if the player has a ExperienceStorageItem in hand add the XP to that
		Player player = (Player) (Object) this;
		for (ItemStack stack : getHandSlots()) {
			if (!player.isUsingItem() && stack.getItem() instanceof ExperienceStorageItem) {
				experience = ExperienceStorageItem.addStoredExperience(stack, experience);
				player.takeXpDelay = 0;
				if (experience == 0) {
					break;
				}
			}
		}
		return experience;
	}
	
	@ModifyVariable(method = "getDestroySpeed",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"),
					to = @At("TAIL")
			),
			at = @At(value = "LOAD"),
			ordinal = 1
	)
	public float applyInexorableEffects(float value) {
		if (isInexorableActive())
			return 1F;
		
		return value;
	}

	@ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
	public float applyInexorableAntiSlowdowns(float original) {
		if (isInexorableActive()) {
			var player = (Player) (Object) this;
			var f = original;

			if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player))
				f *= 5;

			if (!player.onGround())
				f *= 5;

			return f;
		}

		return original;
	}
	
	@Unique
	private boolean isInexorableActive() {
		Player player = (Player) (Object) this;
		return SpectrumEnchantments.INEXORABLE.canEntityUse(player) && EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INEXORABLE, player.getItemInHand(player.getUsedItemHand())) > 0;
	}
	
}