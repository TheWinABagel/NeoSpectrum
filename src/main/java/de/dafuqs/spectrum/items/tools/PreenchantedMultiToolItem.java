package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PreenchantedMultiToolItem extends MultiToolItem implements Preenchanted, LoomPatternProvider {
	
	public PreenchantedMultiToolItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.BLOCK_EFFICIENCY, 1);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.MULTITOOL;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		addBannerPatternProviderTooltip(tooltip);
	}
	
}
