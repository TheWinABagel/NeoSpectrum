package de.dafuqs.spectrum.blocks.redstone;

import de.dafuqs.spectrum.events.RedstoneTransferGameEvent;
import de.dafuqs.spectrum.events.SpectrumGameEvents;
import de.dafuqs.spectrum.events.listeners.WirelessRedstoneSignalEventQueue;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneTransceiverBlockEntity extends BlockEntity implements WirelessRedstoneSignalEventQueue.Callback<WirelessRedstoneSignalEventQueue.EventEntry> {
	
	private static final int RANGE = 16;
	private final WirelessRedstoneSignalEventQueue listener;
	private int cachedSignal;
	private int currentSignal;
	
	public RedstoneTransceiverBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(SpectrumBlockEntities.REDSTONE_TRANSCEIVER, blockPos, blockState);
		this.listener = new WirelessRedstoneSignalEventQueue(new BlockPositionSource(this.worldPosition), RANGE, this);
	}
	
	private static boolean isSender(Level world, BlockPos blockPos) {
		if (world == null) {
			return false;
		}
		return world.getBlockState(blockPos).getValue(RedstoneTransceiverBlock.SENDER);
	}
	
	public static void serverTick(@NotNull Level world, BlockPos pos, BlockState state, @NotNull RedstoneTransceiverBlockEntity blockEntity) {
		if (isSender(world, pos)) {
			if (blockEntity.currentSignal != blockEntity.cachedSignal) {
				blockEntity.currentSignal = blockEntity.cachedSignal;
				blockEntity.getLevel().gameEvent(null, SpectrumGameEvents.WIRELESS_REDSTONE_SIGNALS.get(state.getValue(RedstoneTransceiverBlock.CHANNEL)).get(blockEntity.currentSignal), blockEntity.getBlockPos());
			}
		} else {
			blockEntity.listener.tick(world);
		}
	}
	
	public static DyeColor getChannel(Level world, BlockPos pos) {
		if (world == null) {
			return DyeColor.RED;
		}
		return world.getBlockState(pos).getValue(RedstoneTransceiverBlock.CHANNEL);
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("signal", this.currentSignal);
		tag.putInt("cached_signal", this.cachedSignal);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.currentSignal = tag.getInt("output_signal");
		this.cachedSignal = tag.getInt("cached_signal");
	}
	
	public @Nullable WirelessRedstoneSignalEventQueue getEventListener() {
		return this.listener;
	}
	
	public int getRange() {
		return RANGE;
	}
	
	@Override
	public boolean canAcceptEvent(Level world, GameEventListener listener, GameEvent.ListenerInfo message, Vec3 sourcePos) {
		return !this.isRemoved()
				&& message.gameEvent() instanceof RedstoneTransferGameEvent redstoneTransferGameEvent
				&& !isSender(this.getLevel(), this.worldPosition)
				&& redstoneTransferGameEvent.getDyeColor() == getChannel(this.getLevel(), this.worldPosition);
	}
	
	@Override
	public void triggerEvent(Level world, GameEventListener listener, WirelessRedstoneSignalEventQueue.EventEntry redstoneEvent) {
		if (!isSender(this.getLevel(), this.worldPosition) && redstoneEvent.gameEvent.getDyeColor() == getChannel(this.getLevel(), this.worldPosition)) {
			int receivedSignal = redstoneEvent.gameEvent.getPower();
			this.currentSignal = receivedSignal;
			// trigger a block update in all cases, even when powered does not change. That way connected blocks
			// can react on the strength change of the block, since we store the power in the block entity, not the block state
			if (receivedSignal == 0) {
				world.setBlock(worldPosition, world.getBlockState(worldPosition).setValue(RedstoneTransceiverBlock.POWERED, false), Block.UPDATE_CLIENTS);
			} else {
				world.setBlock(worldPosition, world.getBlockState(worldPosition).setValue(RedstoneTransceiverBlock.POWERED, true), Block.UPDATE_CLIENTS);
			}
			world.blockUpdated(worldPosition, SpectrumBlocks.REDSTONE_TRANSCEIVER);
		}
	}
	
	// since redstone is weird we have to cache a new signal or so
	// if we would start a game event right here it could be triggered
	// multiple times a tick (because neighboring redstone updates > 1/tick)
	// and therefore receivers receiving a wrong (because old) signal
	public void setSignalStrength(int newSignal) {
		if (isSender(this.getLevel(), this.worldPosition)) {
			this.cachedSignal = newSignal;
		} else {
			this.currentSignal = newSignal;
		}
	}
	
	public int getCurrentSignal() {
		if (isSender(this.getLevel(), this.worldPosition)) {
			return 0;
		}
		return this.currentSignal;
	}
	
	public int getCurrentSignalStrength() {
		return this.currentSignal;
	}
	
}
