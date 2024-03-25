package de.dafuqs.spectrum.api.block;

import de.dafuqs.spectrum.items.magic_items.PaintbrushItem;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface PaintbrushTriggered {
	
	/**
	 * Use as first entry of onUse() for a block
	 */
	default InteractionResult checkAndDoPaintbrushTrigger(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.getItemInHand(hand).getItem() instanceof PaintbrushItem) {
			InteractionResult actionResult = onPaintBrushTrigger(state, world, pos, player, hand, hit);
			if (actionResult.consumesAction()) {
				world.playSound(null, pos, SpectrumSoundEvents.PAINTBRUSH_TRIGGER, SoundSource.PLAYERS, 1.0F, 1.0F);
			} else {
				world.playSound(null, pos, SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return actionResult;
		}
		return InteractionResult.PASS;
	}
	
	/**
	 * Do custom logic here
	 * The Pedestal uses it to start crafting, for example
	 */
	InteractionResult onPaintBrushTrigger(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);
	
}
