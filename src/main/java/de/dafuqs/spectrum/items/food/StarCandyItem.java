package de.dafuqs.spectrum.items.food;

import de.dafuqs.spectrum.items.trinkets.WhispyCircletItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class StarCandyItem extends Item {
	
	public StarCandyItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		ItemStack itemStack = super.finishUsingItem(stack, world, user);
		if (!world.isClientSide) {
			WhispyCircletItem.removeSingleStatusEffect(user, MobEffectCategory.HARMFUL);
		}
		return itemStack;
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		tooltip.add(Component.translatable("item.spectrum.star_candy.tooltip").withStyle(ChatFormatting.GRAY));
	}
	
}
