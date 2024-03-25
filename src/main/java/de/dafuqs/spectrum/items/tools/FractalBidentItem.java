package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// gets thrown as copy instead of getting removed from the player's inv
public class FractalBidentItem extends MalachiteBidentItem {
	
	public static final InkCost MIRROR_IMAGE_COST = new InkCost(InkColors.WHITE, 25);
	
	public FractalBidentItem(Properties settings, double damage) {
		super(settings, damage);
	}
	
	@Override
	public boolean isThrownAsMirrorImage(ItemStack stack, ServerLevel world, Player player) {
		return InkPowered.tryDrainEnergy(player, MIRROR_IMAGE_COST);
	}
	
	@Override
	public float getThrowSpeed() {
		return 5.0F;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.fractal_glass_crest_bident.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.fractal_glass_crest_bident.tooltip2").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.fractal_glass_crest_bident.tooltip3").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.white").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return super.acceptsEnchantment(enchantment) || enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment == Enchantments.POWER_ARROWS;
	}
	
}
