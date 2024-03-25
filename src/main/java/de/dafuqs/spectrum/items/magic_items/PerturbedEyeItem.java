package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.blocks.CrackedEndPortalFrameBlock;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PerturbedEyeItem extends Item {

	public PerturbedEyeItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.is(Blocks.END_PORTAL_FRAME) || blockState.is(SpectrumBlocks.CRACKED_END_PORTAL_FRAME)) {
			if (world.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				BlockState targetBlockState;
				boolean facingVertical;
				if (blockState.is(Blocks.END_PORTAL_FRAME)) {
					Direction direction = blockState.getValue(EndPortalFrameBlock.FACING);
					facingVertical = direction.equals(Direction.EAST) || direction.equals(Direction.WEST);
					targetBlockState = SpectrumBlocks.CRACKED_END_PORTAL_FRAME.defaultBlockState()
							.setValue(CrackedEndPortalFrameBlock.EYE_TYPE, CrackedEndPortalFrameBlock.EndPortalFrameEye.VANILLA_WITH_PERTURBED_EYE)
							.setValue(CrackedEndPortalFrameBlock.FACING_VERTICAL, facingVertical);
				} else {
					facingVertical = blockState.getValue(CrackedEndPortalFrameBlock.FACING_VERTICAL);
					targetBlockState = SpectrumBlocks.CRACKED_END_PORTAL_FRAME.defaultBlockState()
							.setValue(CrackedEndPortalFrameBlock.EYE_TYPE, CrackedEndPortalFrameBlock.EndPortalFrameEye.WITH_PERTURBED_EYE)
							.setValue(CrackedEndPortalFrameBlock.FACING_VERTICAL, facingVertical);
				}
				
				Block.pushEntitiesUp(blockState, targetBlockState, world, blockPos);
				world.setBlock(blockPos, targetBlockState, 2);
				world.updateNeighbourForOutputSignal(blockPos, Blocks.END_PORTAL_FRAME);
				context.getItemInHand().shrink(1);
				world.levelEvent(LevelEvent.END_PORTAL_FRAME_FILL, blockPos, 0);
				
				return InteractionResult.CONSUME;
			}
		} else {
			return InteractionResult.PASS;
		}
	}
	
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		tooltip.add(Component.translatable("item.spectrum.perturbed_eye.tooltip").withStyle(ChatFormatting.GRAY));
	}

}
