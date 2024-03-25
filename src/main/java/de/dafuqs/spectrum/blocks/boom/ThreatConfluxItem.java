package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThreatConfluxItem extends ModularExplosionBlockItem {
	
	public ThreatConfluxItem(Block block, Properties settings) {
		super(block, 5, 20, 5, settings);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		tooltip.add(Component.translatable("block.spectrum.threat_conflux.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("block.spectrum.threat_conflux.tooltip2").withStyle(ChatFormatting.GRAY).append(SpectrumItems.MIDNIGHT_CHIP.getDescription()));
		super.appendHoverText(stack, world, tooltip, context);
	}
	
}
