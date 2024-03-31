package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.HardcoreDeathCapability;
import de.dafuqs.spectrum.enchantments.DisarmingEnchantment;
import de.dafuqs.spectrum.enchantments.TreasureHunterEnchantment;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.items.trinkets.AshenCircletItem;
import de.dafuqs.spectrum.items.trinkets.GleamingPinItem;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin {

	@Shadow public abstract ServerLevel serverLevel();

	@Unique
	private long spectrum$lastGleamingPinTriggerTick = 0;
	
	@Inject(method = "die", at = @At("HEAD"))
	protected void spectrum$dropPlayerHeadWithTreasureHunt(DamageSource source, CallbackInfo ci) {
		TreasureHunterEnchantment.doTreasureHunterForPlayer((ServerPlayer) (Object) this, source);
	}
	
	@Inject(at = @At("TAIL"), method = "die")
	protected void spectrum$onDeath(DamageSource source, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (player.level().getLevelData().isHardcore() || HardcoreDeathCapability.isInHardcore(player)) {
			HardcoreDeathCapability.addHardcoreDeath(player.getGameProfile());
		}
	}
	
	@Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
	public void spectrum$damageHead(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// If the player is damaged by lava and wears an ashen circlet:
		// cancel damage and grant fire resistance
		if (source.is(DamageTypes.LAVA)) {
			Player player = (Player) (Object) this;
			
			Optional<ItemStack> ashenCircletStack = SpectrumTrinketItem.getFirstEquipped(player, SpectrumItems.ASHEN_CIRCLET);
			if (ashenCircletStack.isPresent()) {
				if (AshenCircletItem.getCooldownTicks(ashenCircletStack.get(), player.level()) == 0) {
					AshenCircletItem.grantFireResistance(ashenCircletStack.get(), player);
				}
			}
		} else if (source.is(DamageTypeTags.IS_FIRE) && SpectrumTrinketItem.hasEquipped((Player) (Object) this, SpectrumItems.ASHEN_CIRCLET)) {
			cir.setReturnValue(false);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "hurt")
	public void spectrum$damageReturn(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		ServerLevel world = this.serverLevel();
		if (!world.isClientSide) {
			// true if the entity got hurt
			if (cir.getReturnValue() != null && cir.getReturnValue()) {
				if (source.getEntity() instanceof LivingEntity livingSource) {
					ServerPlayer thisPlayer = (ServerPlayer) (Object) this;
					
					int disarmingLevel = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.DISARMING, livingSource.getMainHandItem(), livingSource);
					if (disarmingLevel > 0 && Math.random() < disarmingLevel * SpectrumCommon.CONFIG.DisarmingChancePerLevelPlayers) {
						DisarmingEnchantment.disarmPlayer(thisPlayer);
					}
					
					Optional<ItemStack> gleamingPinStack = SpectrumTrinketItem.getFirstEquipped(thisPlayer, SpectrumItems.GLEAMING_PIN);
					if (gleamingPinStack.isPresent() && world.getGameTime() - this.spectrum$lastGleamingPinTriggerTick > GleamingPinItem.COOLDOWN_TICKS) {
						GleamingPinItem.doGleamingPinEffect(thisPlayer, world, gleamingPinStack.get());
						this.spectrum$lastGleamingPinTriggerTick = world.getGameTime();
					}
				}
			}
		}
	}
	
	@Inject(at = @At("RETURN"), method = "awardKillScore")
	public void spectrum$triggerJeopardantKillAdvancementCriterion(Entity killed, int score, DamageSource damageSource, CallbackInfo ci) {
		if (killed != (Object) this && SpectrumTrinketItem.hasEquipped(this, SpectrumItems.JEOPARDANT)) {
			SpectrumAdvancementCriteria.JEOPARDANT_KILL.trigger((ServerPlayer) (Object) this, killed, damageSource);
		}
	}
	
}
