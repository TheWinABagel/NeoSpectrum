package de.dafuqs.spectrum.items.food;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class DrinkItem extends Item {
	
	public DrinkItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		Player playerEntity = user instanceof Player ? (Player) user : null;
		if (playerEntity instanceof ServerPlayer) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerEntity, stack);
		}
		
		if (!world.isClientSide) {
			List<MobEffectInstance> list = PotionUtils.getMobEffects(stack);
			for (MobEffectInstance statusEffectInstance : list) {
				if (statusEffectInstance.getEffect().isInstantenous()) {
					statusEffectInstance.getEffect().applyInstantenousEffect(playerEntity, playerEntity, user, statusEffectInstance.getAmplifier(), 1.0D);
				} else {
					user.addEffect(new MobEffectInstance(statusEffectInstance));
				}
			}
		}
		
		if (playerEntity != null) {
			playerEntity.awardStat(Stats.ITEM_USED.get(this));
		}
		
		if (playerEntity == null || !playerEntity.getAbilities().instabuild) {
			if (stack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}
			
			if (playerEntity != null) {
				playerEntity.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
			}
		}
		
		user.gameEvent(GameEvent.DRINK);
		return super.finishUsingItem(stack, world, user);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, user, hand);
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 40;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}
	
	@Override
	public SoundEvent getDrinkingSound() {
		return SoundEvents.GENERIC_DRINK;
	}
	
	@Override
	public SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}
	
}
