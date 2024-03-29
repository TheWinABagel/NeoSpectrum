package de.dafuqs.spectrum.blocks.chests;

import de.dafuqs.spectrum.inventories.BlackHoleChestScreenHandler;
import de.dafuqs.spectrum.inventories.CompactingChestScreenHandler;
import de.dafuqs.spectrum.inventories.RestockingChestScreenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = LidBlockEntity.class
)})
public abstract class SpectrumChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
	
	public final ContainerOpenersCounter stateManager;
	protected final ChestLidController lidAnimator;
	protected NonNullList<ItemStack> inventory;
	
	protected SpectrumChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		this.lidAnimator = new ChestLidController();
		
		this.stateManager = new ContainerOpenersCounter() {
			@Override
			protected void onOpen(Level world, BlockPos pos, BlockState state) {
				playSound(world, pos, state, getOpenSound());
				SpectrumChestBlockEntity.this.onOpen();
			}
			
			@Override
			protected void onClose(Level world, BlockPos pos, BlockState state) {
				playSound(world, pos, state, getCloseSound());
				SpectrumChestBlockEntity.this.onClose();
			}
			
			@Override
			protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
				onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
			}
			
			@Override
			protected boolean isOwnContainer(Player player) {
				AbstractContainerMenu screenHandler = player.containerMenu;
				
				Container inventory = null;
				if (screenHandler instanceof ChestMenu) {
					inventory = ((ChestMenu) screenHandler).getContainer();
				} else if (screenHandler instanceof RestockingChestScreenHandler restockingChestScreenHandler) {
					inventory = restockingChestScreenHandler.getInventory();
				} else if (screenHandler instanceof BlackHoleChestScreenHandler blackHoleChestScreenHandler) {
					inventory = blackHoleChestScreenHandler.getInventory();
				} else if (screenHandler instanceof CompactingChestScreenHandler compactingChestScreenHandler) {
					inventory = compactingChestScreenHandler.getInventory();
				}
				
				return inventory == SpectrumChestBlockEntity.this;
			}
		};
	}
	
	private static void playSound(Level world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
		world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, soundEvent, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
	}
	
	public static void clientTick(Level world, BlockPos pos, BlockState state, SpectrumChestBlockEntity blockEntity) {
		blockEntity.lidAnimator.tickLid();
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public float getOpenNess(float tickDelta) {
		return this.lidAnimator.getOpenness(tickDelta);
	}
	
	public void onOpen() {
	
	}
	
	public void onClose() {
	
	}
	
	@Override
	public boolean triggerEvent(int type, int data) {
		if (type == 1) {
			this.lidAnimator.shouldBeOpen(data > 0);
			return true;
		} else {
			return super.triggerEvent(type, data);
		}
	}
	
	protected void onInvOpenOrClose(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
		Block block = state.getBlock();
		world.blockEvent(pos, block, 1, newViewerCount);
	}
	
	@Override
	public void startOpen(Player player) {
		if (!player.isSpectator()) {
			this.stateManager.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
		
	}
	
	@Override
	public void stopOpen(Player player) {
		if (!player.isSpectator()) {
			this.stateManager.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
	}
	
	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}
	
	@Override
	protected void setItems(NonNullList<ItemStack> list) {
		this.inventory = list;
	}
	
	public void onScheduledTick() {
		this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.tryLoadLootTable(tag);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.inventory);
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		this.trySaveLootTable(tag);
		if (!this.inventory.isEmpty()) {
			ContainerHelper.saveAllItems(tag, this.inventory);
		}
	}
	
	public SoundEvent getOpenSound() {
		return SoundEvents.CHEST_OPEN;
	}
	
	public SoundEvent getCloseSound() {
		return SoundEvents.CHEST_CLOSE;
	}
	
}
