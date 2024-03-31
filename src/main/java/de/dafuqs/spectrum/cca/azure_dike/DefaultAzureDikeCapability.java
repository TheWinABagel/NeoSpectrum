package de.dafuqs.spectrum.cca.azure_dike;

import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class DefaultAzureDikeCapability implements AzureDikeCapability {
	
	public final static int BASE_RECHARGE_RATE_DELAY_TICKS_DEFAULT = 40;
	public final static int BASE_RECHARGE_RATE_DELAY_TICKS_AFTER_DAMAGE = 200;
	public static final Capability<AzureDikeCapability> AZURE_DIKE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

	private LivingEntity provider;
	
	private int protection = 0;
	private int currentRechargeDelay = 0;
	
	private int maxProtection = 0;
	private int rechargeDelayDefault = 0;
	private int rechargeDelayTicksAfterDamage = 0;
	
	public DefaultAzureDikeCapability(LivingEntity entity) {
		this.provider = entity;
	}

	public DefaultAzureDikeCapability() {
	}

	@Override
	public void setEntity(LivingEntity entity) {
		this.provider = entity;
	}

	@Override
	public LivingEntity getEntity() {
		return this.provider;
	}

	@Override
	public int getProtection() {
		return this.protection;
	}
	
	@Override
	public int getMaxProtection() {
		return this.maxProtection;
	}
	
	@Override
	public int getRechargeDelayDefault() {
		return this.rechargeDelayDefault;
	}
	
	@Override
	public int getCurrentRechargeDelay() {
		return this.currentRechargeDelay;
	}
	
	@Override
	public int getRechargeDelayTicksAfterDamage() {
		return this.rechargeDelayTicksAfterDamage;
	}
	
	@Override
	public float absorbDamage(float incomingDamage) {
		this.currentRechargeDelay = this.rechargeDelayTicksAfterDamage;
		if (this.protection > 0) {
			int usedProtection = Math.min(protection, (int) incomingDamage);
			this.protection -= usedProtection;
			
			if (usedProtection > 0) {
				this.sync();
//				AzureDikeProvider.AZURE_DIKE_COMPONENT.sync(provider);
				if (provider instanceof ServerPlayer serverPlayerEntity) {
					SpectrumAdvancementCriteria.AZURE_DIKE_CHARGE.trigger(serverPlayerEntity, this.protection, this.rechargeDelayDefault, -usedProtection);
				}
			}
			
			return incomingDamage - usedProtection;
		} else {
			return incomingDamage;
		}
	}
	
	@Override
	public void set(int maxProtection, int rechargeDelayDefault, int fasterRechargeAfterDamageTicks, boolean resetCharge) {
		this.maxProtection = maxProtection;
		this.rechargeDelayDefault = rechargeDelayDefault;
		this.rechargeDelayTicksAfterDamage = fasterRechargeAfterDamageTicks;
		this.currentRechargeDelay = this.rechargeDelayDefault;
		if (resetCharge) {
			this.protection = 0;
		} else {
			this.protection = Math.min(this.protection, this.maxProtection);
		}

		this.sync();
//		AzureDikeProvider.AZURE_DIKE_COMPONENT.sync(provider);
	}
	
//	@Override
//	public void serverTick() {
//		if (this.currentRechargeDelay > 0) {
//			this.currentRechargeDelay--;
//		} else if (this.protection < this.maxProtection) {
//			this.protection++;
//			this.currentRechargeDelay = this.rechargeDelayDefault;
//			AzureDikeProvider.AZURE_DIKE_COMPONENT.sync(provider);
//			if (provider instanceof ServerPlayer serverPlayerEntity) {
//				SpectrumAdvancementCriteria.AZURE_DIKE_CHARGE.trigger(serverPlayerEntity, this.protection, this.rechargeDelayDefault, 1);
//			}
//		}
//	}

	@SubscribeEvent
	public void serverTick(TickEvent.ServerTickEvent e) {
		if (e.phase.equals(TickEvent.Phase.END)) {
			if (this.currentRechargeDelay > 0) {
				this.currentRechargeDelay--;
			} else if (this.protection < this.maxProtection) {
				this.protection++;
				this.currentRechargeDelay = this.rechargeDelayDefault;
//				AzureDikeProvider.AZURE_DIKE_COMPONENT.sync(provider);
				this.sync();
				if (provider instanceof ServerPlayer serverPlayerEntity) {
					SpectrumAdvancementCriteria.AZURE_DIKE_CHARGE.trigger(serverPlayerEntity, this.protection, this.rechargeDelayDefault, 1);
				}
			}
		}
	}

	@SubscribeEvent
	public void copyToNewPlayer(PlayerEvent.Clone e) {
		if (e.getEntity() instanceof ServerPlayer newPlayer && e.getOriginal() instanceof ServerPlayer oldPlayer) {
			oldPlayer.getCapability(DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY).ifPresent(o -> {
				newPlayer.getCapability(DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY).ifPresent(c -> {
					c.set(o.getMaxProtection(), o.getRechargeDelayDefault(), o.getRechargeDelayTicksAfterDamage(), e.isWasDeath());
				});
			});
		}
	}

//	@Override
//	public void copyData(@NotNull ServerPlayer original, @NotNull ServerPlayer clone, boolean lossless) {
//		AzureDikeCapability o = AzureDikeProvider.AZURE_DIKE_COMPONENT.get(original);
//		AzureDikeCapability c = AzureDikeProvider.AZURE_DIKE_COMPONENT.get(clone);
//		c.set(o.getMaxProtection(), o.getRechargeDelayDefault(), o.getRechargeDelayTicksAfterDamage(), lossless);
//	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("protection", this.protection);
		tag.putInt("current_recharge_delay", this.currentRechargeDelay);

		tag.putInt("max_protection", this.maxProtection);
		tag.putInt("recharge_delay_default", this.rechargeDelayDefault);
		tag.putInt("recharge_delay_after_damage", this.rechargeDelayTicksAfterDamage);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.protection = tag.getInt("protection");
		this.currentRechargeDelay = tag.getInt("current_recharge_delay");

		this.maxProtection = tag.getInt("max_protection");
		this.rechargeDelayDefault = tag.getInt("recharge_delay_default");
		this.rechargeDelayTicksAfterDamage = tag.getInt("recharge_delay_after_damage");
	}

	public void sync() {
		if (this.provider instanceof ServerPlayer serverPlayer) {
			//todoforge sync caps
		}
	}
}
