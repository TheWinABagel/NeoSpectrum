package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.entity.PlayerEntityAccessor;
import de.dafuqs.spectrum.compat.gofish.GoFishCompat;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SpectrumFishingRodItem extends FishingRodItem {
	
	public SpectrumFishingRodItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		
		PlayerEntityAccessor playerEntityAccessor = ((PlayerEntityAccessor) user);
		if (playerEntityAccessor.getSpectrumBobber() != null) {
			if (!world.isClientSide) {
				int damage = playerEntityAccessor.getSpectrumBobber().use(itemStack);
				itemStack.hurtAndBreak(damage, user, (p) -> p.broadcastBreakEvent(hand));
			}
			
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			user.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
		} else {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!world.isClientSide) {
				int luckOfTheSeaLevel = EnchantmentHelper.getFishingLuckBonus(itemStack);
				int lureLevel = EnchantmentHelper.getFishingSpeedBonus(itemStack);
				int exuberanceLevel = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.EXUBERANCE, itemStack, user);
				int bigCatchLevel = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.BIG_CATCH, itemStack, user);
				boolean inventoryInsertion = SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.INVENTORY_INSERTION, itemStack, user) > 0;
				boolean foundry = shouldAutosmelt(itemStack, user);
				spawnBobber(user, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, foundry);
			}
			
			user.awardStat(Stats.ITEM_USED.get(this));
			user.gameEvent(GameEvent.ITEM_INTERACT_START);
		}
		
		return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
	}
	
	public abstract void spawnBobber(Player user, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean foundry);
	
	public boolean canFishIn(FluidState fluidState) {
		return fluidState.is(FluidTags.WATER);
	}
	
	public boolean shouldAutosmelt(ItemStack itemStack, Player user) {
		return SpectrumEnchantmentHelper.getUsableLevel(SpectrumEnchantments.FOUNDRY, itemStack, user) > 0 || GoFishCompat.hasDeepfry(itemStack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.spectrum_fishing_rods.tooltip").withStyle(ChatFormatting.GRAY));
	}
	
}