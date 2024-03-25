package de.dafuqs.spectrum.items.food;

import de.dafuqs.spectrum.api.item.ApplyFoodEffectsCallback;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class TeaItem extends DrinkItem implements ApplyFoodEffectsCallback {
	
	protected final FoodProperties bonusFoodComponentWithScone;
	
	public TeaItem(Properties settings, FoodProperties bonusFoodComponentWithScone) {
		super(settings);
		this.bonusFoodComponentWithScone = bonusFoodComponentWithScone;
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		
		CompoundTag nbtCompound = itemStack.getTag();
		if (nbtCompound != null && nbtCompound.contains("Milk")) {
			tooltip.add(Component.translatable("item.spectrum.restoration_tea.tooltip_milk"));
		}
	}
	
	@Override
	public void afterConsumption(Level world, ItemStack teaStack, LivingEntity entity) {
		if (entity instanceof Player player) {
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack sconeStack = player.getInventory().getItem(i);
				if (sconeStack.is(SpectrumItems.SCONE)) {
					if (player instanceof ServerPlayer serverPlayerEntity) {
						CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, sconeStack);
						SpectrumAdvancementCriteria.CONSUMED_TEA_WITH_SCONE.trigger(serverPlayerEntity, sconeStack, teaStack);
					}
					
					world.playSound(null, player.getX(), player.getY(), player.getZ(), player.getEatingSound(sconeStack), SoundSource.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
					ApplyFoodEffectsCallback.applyFoodComponent(player.level(), player, sconeStack.getItem().getFoodProperties());
					
					ApplyFoodEffectsCallback.applyFoodComponent(player.level(), player, this.bonusFoodComponentWithScone);
					
					if (!player.isCreative()) {
						sconeStack.shrink(1);
					}
					player.gameEvent(GameEvent.EAT);
					
					return;
				}
			}
		}
	}
	
}
