package de.dafuqs.spectrum.blocks.chests;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HeartboundChestBlock extends SpectrumChestBlock {
	
	public HeartboundChestBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new HeartboundChestBlockEntity(pos, state);
	}
	
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide ? createTickerHelper(type, SpectrumBlockEntities.HEARTBOUND_CHEST, HeartboundChestBlockEntity::clientTick) : null;
	}
	
	@Override
	public void openScreen(Level world, BlockPos pos, Player player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof HeartboundChestBlockEntity heartboundChestBlockEntity) {
			
			if (!heartboundChestBlockEntity.hasOwner()) {
				heartboundChestBlockEntity.setOwner(player);
			}
			
			if (!isChestBlocked(world, pos)) {
				// Permissions are handled with vanilla lock()
				// => TileEntities "checkUnlocked" function
				player.openMenu(heartboundChestBlockEntity);
			}
		}
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof HeartboundChestBlockEntity heartboundChestBlockEntity) {
			if (placer instanceof ServerPlayer serverPlayerEntity) {
				heartboundChestBlockEntity.setOwner(serverPlayerEntity);
			}
			if (itemStack.hasCustomHoverName()) {
				heartboundChestBlockEntity.setCustomName(itemStack.getHoverName());
			}
		}
	}
	
	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
	
	/*
	The chest emits redstone power of strength...
	7: if the owner has it opened
	15: if it was tried to open by a non-owner in the last 20 ticks
	 */
	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof HeartboundChestBlockEntity) {
			if (((HeartboundChestBlockEntity) blockEntity).wasRecentlyTriedToOpenByNonOwner()) {
				return 15;
			}
			int lookingPlayers = HeartboundChestBlockEntity.getPlayersLookingInChestCount(world, pos);
			if (lookingPlayers > 0) {
				return 7;
			}
		}
		return 0;
	}
	
	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? state.getSignal(world, pos, direction) : 0;
	}
	
	/*
	Only the chest owner may break it
	 */
	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof HeartboundChestBlockEntity heartboundChestBlockEntity) {
			if (heartboundChestBlockEntity.canBreak(player.getUUID())) {
				float hardness = 20.0F;
				int i = player.hasCorrectToolForDrops(state) ? 30 : 100;
				return player.getDestroySpeed(state) / hardness / (float) i;
			}
		}
		return -1;
	}
	
	@Override
	public float defaultDestroyTime() {
		return -1;
	}
	
	
}
