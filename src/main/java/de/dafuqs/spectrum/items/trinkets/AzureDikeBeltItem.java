package de.dafuqs.spectrum.items.trinkets;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AzureDikeBeltItem extends AzureDikeTrinketItem {
	
	public AzureDikeBeltItem(Properties settings) {
		super(settings);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.azure_dike_belt.tooltip"));
	}
	
	@Override
	public int maxAzureDike(ItemStack stack) {
		return 6;
	}
	
	@Override
	public float rechargeBonusAfterDamageTicks(ItemStack stack) {
		return 100;
	}
	
}