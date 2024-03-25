package de.dafuqs.spectrum.blocks;

import de.dafuqs.spectrum.inventories.BedrockAnvilScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BedrockAnvilBlock extends AnvilBlock {
	
	private static final Component TITLE = Component.translatable("container.spectrum.bedrock_anvil");
	
	public BedrockAnvilBlock(Properties settings) {
		super(settings);
	}
	
	// Heavier => More damage
	@Override
	protected void falling(FallingBlockEntity entity) {
		entity.setHurtsEntities(3.0F, 64);
	}
	
	@Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		return new SimpleMenuProvider((syncId, inventory, player) -> new BedrockAnvilScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos)), TITLE);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("container.spectrum.bedrock_anvil.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("container.spectrum.bedrock_anvil.tooltip2").withStyle(ChatFormatting.GRAY));
	}
	
}
