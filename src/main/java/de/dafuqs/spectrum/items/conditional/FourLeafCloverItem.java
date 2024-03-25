package de.dafuqs.spectrum.items.conditional;

import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FourLeafCloverItem extends CloakedBlockItem implements LoomPatternProvider {
	
	public FourLeafCloverItem(Block block, Item.Properties settings, ResourceLocation cloakAdvancementIdentifier, Item cloakItem) {
		super(block, settings, cloakAdvancementIdentifier, cloakItem);
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.FOUR_LEAF_CLOVER;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		addBannerPatternProviderTooltip(tooltip);
	}
	
}
