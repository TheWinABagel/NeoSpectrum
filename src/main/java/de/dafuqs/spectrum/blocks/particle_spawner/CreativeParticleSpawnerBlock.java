package de.dafuqs.spectrum.blocks.particle_spawner;

import de.dafuqs.spectrum.api.item.CreativeOnlyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeParticleSpawnerBlock extends AbstractParticleSpawnerBlock implements CreativeOnlyItem {
	
	public CreativeParticleSpawnerBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.creative_particle_spawner.tooltip").withStyle(ChatFormatting.GRAY));
		CreativeOnlyItem.appendTooltip(tooltip);
	}
	
	@Override
	public boolean shouldSpawnParticles(Level world, BlockPos pos) {
		return true;
	}
	
}
