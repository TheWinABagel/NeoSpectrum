package de.dafuqs.spectrum.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

// reworked and yarn version of Botanias AoE breaking mechanism at
// https://github.com/VazkiiMods/Botania/blob/7d526461b21cac3d4e2a084a063d469c4065951f/Xplat/src/main/java/vazkii/botania/common/item/equipment/tool/ToolCommons.java
// hereby used and credited per the Botania license at https://botaniamod.net/license.html
// Shoutout and thanks a bunch to Vazkii, Willie and artemisSystem!
public class AoEHelper {
	
	public static void doAoEBlockBreaking(Player player, ItemStack stack, BlockPos pos, Direction side, int radius) {
		if (radius <= 0) {
			return;
		}
		
		Level world = player.level();
		if (world.isEmptyBlock(pos)) {
			return;
		}
		
		Predicate<BlockState> minableBlocksPredicate = state -> {
			boolean suitableTool = !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
			boolean suitableSpeed = stack.getDestroySpeed(state) > 1;
			return suitableTool && suitableSpeed;
		};

		BlockState targetState = world.getBlockState(pos);
		if (!minableBlocksPredicate.test(targetState)) {
			return;
		}
		
		boolean doX = side.getStepX() == 0;
		boolean doY = side.getStepY() == 0;
		boolean doZ = side.getStepZ() == 0;

		Vec3i beginDiff = new Vec3i(doX ? -radius : 0, doY ? -1 : 0, doZ ? -radius : 0);
		Vec3i endDiff = new Vec3i(doX ? radius : 0, doY ? radius * 2 - 1 : 0, doZ ? radius : 0);

		removeBlocksInIteration(player, stack, world, pos, beginDiff, endDiff, minableBlocksPredicate);
	}

	private static boolean recursive = false;

	private static void removeBlocksInIteration(Player player, ItemStack stack, Level world, BlockPos centerPos, Vec3i startDelta, Vec3i endDelta, Predicate<BlockState> filter) {
		if (recursive) {
			return;
		}

		recursive = true;
		try {
			for (BlockPos blockPos : BlockPos.betweenClosed(centerPos.offset(startDelta), centerPos.offset(endDelta))) {
				if (!blockPos.equals(centerPos)) {
					breakBlockWithDrops(player, stack, world, blockPos, filter);
				}
			}
		} finally {
			recursive = false;
		}
	}

	public static void breakBlocksAround(Player player, ItemStack stack, BlockPos pos, int radius, @Nullable Predicate<BlockState> predicate) {
		if (radius <= 0) {
			return;
		}

		Level world = player.level();

		Predicate<BlockState> minableBlocksPredicate = state -> {
			boolean suitableTool = !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
			boolean suitableSpeed = stack.getDestroySpeed(state) > 1;
			return suitableTool && suitableSpeed;
		};
		if (predicate != null) {
			minableBlocksPredicate = minableBlocksPredicate.and(predicate);
		}

		BlockState targetState = world.getBlockState(pos);
		if (!minableBlocksPredicate.test(targetState)) {
			return;
		}

		for (BlockPos blockPos : BlockPos.withinManhattan(pos, radius, radius, radius)) {
			breakBlockWithDrops(player, stack, world, blockPos, minableBlocksPredicate);
		}
	}

	public static void breakBlockWithDrops(Player player, ItemStack stack, Level world, BlockPos pos, Predicate<BlockState> filter) {
		ChunkPos chunkPos = world.getChunk(pos).getPos();
		if (world.hasChunk(chunkPos.x, chunkPos.z)) {
			BlockState blockstate = world.getBlockState(pos);
			if (!world.isClientSide && !blockstate.isAir() && blockstate.getDestroyProgress(player, world, pos) > 0 && filter.test(blockstate)) {
				ItemStack save = player.getMainHandItem();
				player.setItemInHand(InteractionHand.MAIN_HAND, stack);
				((ServerPlayer) player).connection.send(new ClientboundLevelEventPacket(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(blockstate), false));
				((ServerPlayer) player).gameMode.destroyBlock(pos);
				player.setItemInHand(InteractionHand.MAIN_HAND, save);
			}
		}
		
	}
	
}
