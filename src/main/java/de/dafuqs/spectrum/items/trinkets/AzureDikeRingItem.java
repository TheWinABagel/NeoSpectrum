package de.dafuqs.spectrum.items.trinkets;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AzureDikeRingItem extends AzureDikeTrinketItem {
	
	public AzureDikeRingItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public boolean canEquipMoreThanOne() {
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.azure_dike_ring.tooltip"));
	}
	
	@Override
	public int maxAzureDike(ItemStack stack) {
		return 4;
	}
	
	@Override
	public float azureDikeRechargeBonusTicks(ItemStack stack) {
		return 5;
	}
	
}