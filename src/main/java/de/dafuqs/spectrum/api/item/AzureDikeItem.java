package de.dafuqs.spectrum.api.item;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.CapabilityHelper;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeCapability;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.cca.azure_dike.DefaultAzureDikeCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public interface AzureDikeItem {
	
	ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/create_refined_azurite");
	
	int maxAzureDike(ItemStack stack);
	
	float azureDikeRechargeBonusTicks(ItemStack stack);
	
	float rechargeBonusAfterDamageTicks(ItemStack stack);
	
	default void recalculate(LivingEntity livingEntity) {
		Level world = livingEntity.level();
		if (!world.isClientSide) {
			AzureDikeCapability azureDikeCapability = CapabilityHelper.getComponent(livingEntity, DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY);
			Optional<ICuriosItemHandler> trinketComponent = CuriosApi.getCuriosInventory(livingEntity).resolve();
			if (trinketComponent.isPresent()) {
				int maxProtection = 0;
				int rechargeRateDefaultBonus = 0;
				int rechargeTicksAfterDamageBonus = 0;
				var curiosList = trinketComponent.get().findCurios(stack -> stack.getItem() instanceof AzureDikeItem);
				for (SlotResult slotResult : curiosList) {
					ItemStack stack = slotResult.stack();
					AzureDikeItem azureDikeItem = (AzureDikeItem) stack.getItem();
					maxProtection += azureDikeItem.maxAzureDike(stack);
					rechargeRateDefaultBonus += azureDikeItem.azureDikeRechargeBonusTicks(stack);
					rechargeTicksAfterDamageBonus += azureDikeItem.rechargeBonusAfterDamageTicks(stack);
				}
				
				int rechargeRateDefault = Math.max(1, DefaultAzureDikeCapability.BASE_RECHARGE_RATE_DELAY_TICKS_DEFAULT - rechargeRateDefaultBonus);
				int rechargeTicksAfterDamage = Math.max(1, DefaultAzureDikeCapability.BASE_RECHARGE_RATE_DELAY_TICKS_AFTER_DAMAGE - rechargeTicksAfterDamageBonus);
				
				azureDikeCapability.set(maxProtection, rechargeRateDefault, rechargeTicksAfterDamage, false);
			}
		}
	}
	
}
