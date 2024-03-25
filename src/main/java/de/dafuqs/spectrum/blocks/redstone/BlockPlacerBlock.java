package de.dafuqs.spectrum.blocks.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockPlacerBlock extends RedstoneInteractionBlock implements EntityBlock {
	
	public BlockPlacerBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockPlacerBlockEntity(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			if (world.getBlockEntity(pos) instanceof BlockPlacerBlockEntity blockPlacerBlockEntity) {
				player.openMenu(blockPlacerBlockEntity);
			}
			return InteractionResult.CONSUME;
		}
	}
	
	protected void dispense(ServerLevel world, BlockPos pos) {
		BlockSourceImpl pointer = new BlockSourceImpl(world, pos);
		BlockPlacerBlockEntity blockEntity = pointer.getEntity();
		
		int slot = blockEntity.getRandomSlot(world.random);
		if (slot < 0) {
			world.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
			world.gameEvent(null, GameEvent.BLOCK_ACTIVATE, pos);
		} else {
			ItemStack stack = blockEntity.getItem(slot);
			tryPlace(stack, pointer);
		}
	}
	
	// We can't reuse the vanilla BlockPlacementDispenserBehavior, since we are using a different orientation for our block:
	// BlockPlacerBlock.ORIENTATION instead of DispenserBlock.FACING
	protected void tryPlace(@NotNull ItemStack stack, BlockSource pointer) {
        Level world = pointer.getLevel();
        if (stack.getItem() instanceof BlockItem blockItem) {
			Direction facing = pointer.getBlockState().getValue(BlockPlacerBlock.ORIENTATION).front();
			BlockPos placementPos = pointer.getPos().relative(facing);
            Direction placementDirection = world.isEmptyBlock(placementPos.below()) ? facing : Direction.UP;

			try {
				blockItem.place(new DirectionalPlaceContext(world, placementPos, facing, stack, placementDirection));
				world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pointer.getPos(), 0);
				world.levelEvent(LevelEvent.PARTICLES_SHOOT, pointer.getPos(), pointer.getBlockState().getValue(BlockPlacerBlock.ORIENTATION).front().get3DDataValue());
                world.gameEvent(null, GameEvent.BLOCK_PLACE, placementPos);
			} catch (Exception ignored) {
			}
		} else {
			world.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pointer.getPos(), 0);
            world.gameEvent(null, GameEvent.BLOCK_ACTIVATE, pointer.getPos());
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
		boolean bl2 = state.getValue(TRIGGERED);
		if (bl && !bl2) {
			world.scheduleTick(pos, this, 4);
			world.setBlock(pos, state.setValue(TRIGGERED, true), 4);
		} else if (!bl && bl2) {
			world.setBlock(pos, state.setValue(TRIGGERED, false), 4);
		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		this.dispense(world, pos);
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomHoverName()) {
			if (world.getBlockEntity(pos) instanceof BlockPlacerBlockEntity blockPlacerBlockEntity) {
				blockPlacerBlockEntity.setCustomName(itemStack.getHoverName());
			}
		}
		
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof BlockPlacerBlockEntity blockPlacerBlockEntity) {
				Containers.dropContents(world, pos, blockPlacerBlockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}
	
	
}
