package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.entity.entity.BedrockFishingBobberEntity;
import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BedrockFishingRodItem extends SpectrumFishingRodItem implements Preenchanted {
	
	public BedrockFishingRodItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.FISHING_LUCK, 4);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean canFishIn(FluidState fluidState) {
		return fluidState.is(SpectrumFluidTags.BEDROCK_ROD_FISHABLE_IN);
	}
	
	@Override
	public void spawnBobber(Player user, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean foundry) {
		world.addFreshEntity(new BedrockFishingBobberEntity(user, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, foundry));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.bedrock_fishing_rod.tooltip").withStyle(ChatFormatting.GRAY));
	}
	
}