package de.dafuqs.spectrum.items.magic_items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GildedBookItem extends BookItem {
	
	public GildedBookItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public int getEnchantmentValue() {
		return Items.GOLDEN_PICKAXE.getEnchantmentValue();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.gilded_book.tooltip.enchantability").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.gilded_book.tooltip.copy_enchantments").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.gilded_book.tooltip.copy_enchantments2").withStyle(ChatFormatting.GRAY));
	}
	
}
