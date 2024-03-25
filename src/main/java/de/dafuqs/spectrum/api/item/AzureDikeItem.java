package de.dafuqs.spectrum.api.item;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeComponent;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.cca.azure_dike.DefaultAzureDikeComponent;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface AzureDikeItem {
	
	ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/create_refined_azurite");
	
	int maxAzureDike(ItemStack stack);
	
	float azureDikeRechargeBonusTicks(ItemStack stack);
	
	float rechargeBonusAfterDamageTicks(ItemStack stack);
	
	default void recalculate(LivingEntity livingEntity) {
		Level world = livingEntity.level();
		if (!world.isClientSide) {
			AzureDikeComponent azureDikeComponent = AzureDikeProvider.AZURE_DIKE_COMPONENT.get(livingEntity);
			
			Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(livingEntity);
			if (trinketComponent.isPresent()) {
				int maxProtection = 0;
				int rechargeRateDefaultBonus = 0;
				int rechargeTicksAfterDamageBonus = 0;
				for (Tuple<SlotReference, ItemStack> pair : trinketComponent.get().getAllEquipped()) {
					ItemStack stack = pair.getB();
					if (pair.getB().getItem() instanceof AzureDikeItem azureDikeItem) {
						maxProtection += azureDikeItem.maxAzureDike(stack);
						rechargeRateDefaultBonus += azureDikeItem.azureDikeRechargeBonusTicks(stack);
						rechargeTicksAfterDamageBonus += azureDikeItem.rechargeBonusAfterDamageTicks(stack);
					}
				}
				
				int rechargeRateDefault = Math.max(1, DefaultAzureDikeComponent.BASE_RECHARGE_RATE_DELAY_TICKS_DEFAULT - rechargeRateDefaultBonus);
				int rechargeTicksAfterDamage = Math.max(1, DefaultAzureDikeComponent.BASE_RECHARGE_RATE_DELAY_TICKS_AFTER_DAMAGE - rechargeTicksAfterDamageBonus);
				
				azureDikeComponent.set(maxProtection, rechargeRateDefault, rechargeTicksAfterDamage, false);
			}
		}
	}
	
}
