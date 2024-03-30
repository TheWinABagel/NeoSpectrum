package de.dafuqs.spectrum.items.trinkets;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PuffCircletItem extends AzureDikeTrinketItem {

	public static final float PROJECTILE_DEFLECTION_COST = 2;
	public static final float FALL_DAMAGE_NEGATING_COST = 2;

	public PuffCircletItem(Item.Properties settings) {
		super(settings);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.puff_circlet.tooltip"));
		tooltip.add(Component.translatable("item.spectrum.puff_circlet.tooltip2"));
	}

	@Override
	public int maxAzureDike(ItemStack stack) {
		return 4;
	}
	
}