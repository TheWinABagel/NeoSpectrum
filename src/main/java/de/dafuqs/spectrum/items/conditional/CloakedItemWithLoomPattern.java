package de.dafuqs.spectrum.items.conditional;

import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CloakedItemWithLoomPattern extends CloakedItem implements LoomPatternProvider {
	
	private final Holder<BannerPattern> patternItemTag;
	
	public CloakedItemWithLoomPattern(Properties settings, ResourceLocation cloakAdvancementIdentifier, Item cloakItem, Holder<BannerPattern> patternItemTag) {
		super(settings, cloakAdvancementIdentifier, cloakItem);
		this.patternItemTag = patternItemTag;
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return patternItemTag;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		addBannerPatternProviderTooltip(tooltip);
	}
	
}
