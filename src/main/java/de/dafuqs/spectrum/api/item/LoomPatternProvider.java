package de.dafuqs.spectrum.api.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;

public interface LoomPatternProvider {
	
	Component PATTERN_AVAILABLE_TOOLTIP_TEXT = Component.translatable("item.spectrum.tooltip.loom_pattern_available").withStyle(ChatFormatting.GRAY);

	Holder<BannerPattern> getPattern();
	
	default List<Holder<BannerPattern>> getPatterns() {
		return List.of(getPattern());
	}
	
	default void addBannerPatternProviderTooltip(List<Component> tooltips) {
		tooltips.add(PATTERN_AVAILABLE_TOOLTIP_TEXT);
	}

}
