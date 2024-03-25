package de.dafuqs.spectrum.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemWithTooltip extends Item {
	
	private final List<MutableComponent> tooltipTexts = new ArrayList<>();
	
	public ItemWithTooltip(Properties settings, String tooltip) {
		super(settings);
		this.tooltipTexts.add(Component.translatable(tooltip));
	}
	
	public ItemWithTooltip(Properties settings, String[] tooltips) {
		super(settings);
		Arrays.stream(tooltips)
				.map(Component::translatable)
				.forEach(tooltipTexts::add);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		for (MutableComponent text : this.tooltipTexts) {
			tooltip.add(text.withStyle(ChatFormatting.GRAY));
		}
	}
	
}
