package de.dafuqs.spectrum.items;

import com.google.common.collect.Maps;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.items.conditional.CloakedItem;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PigmentItem extends CloakedItem implements LoomPatternProvider {
	
	private static final Map<DyeColor, PigmentItem> PIGMENTS = Maps.newEnumMap(DyeColor.class);
	protected final DyeColor color;
	
	public PigmentItem(Properties settings, DyeColor color) {
		super(settings, SpectrumCommon.locate("craft_colored_sapling"), DyeItem.byColor(color));
		this.color = color;
		PIGMENTS.put(color, this);
	}
	
	public DyeColor getColor() {
		return this.color;
	}
	
	public static PigmentItem byColor(DyeColor color) {
		return PIGMENTS.get(color);
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.PIGMENT;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		addBannerPatternProviderTooltip(tooltip);
	}
	
}
