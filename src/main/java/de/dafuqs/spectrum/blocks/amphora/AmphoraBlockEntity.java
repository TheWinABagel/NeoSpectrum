package de.dafuqs.spectrum.blocks.amphora;

import de.dafuqs.spectrum.inventories.GenericSpectrumContainerScreenHandler;
import de.dafuqs.spectrum.inventories.ScreenBackgroundVariant;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AmphoraBlockEntity extends RandomizableContainerBlockEntity {
	
	private NonNullList<ItemStack> inventory;
	private final ContainerOpenersCounter stateManager;
	
	public AmphoraBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.AMPHORA, pos, state);
		
		this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		this.stateManager = new ContainerOpenersCounter() {
			@Override
			protected void onOpen(Level world, BlockPos pos, BlockState state) {
				playSound(state, SoundEvents.BARREL_OPEN);
				setOpen(state, true);
			}
			
			@Override
			protected void onClose(Level world, BlockPos pos, BlockState state) {
				playSound(state, SoundEvents.BARREL_CLOSE);
				setOpen(state, false);
			}
			
			@Override
			protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
			}
			
			@Override
			protected boolean isOwnContainer(Player player) {
				if (player.containerMenu instanceof ChestMenu) {
					Container inventory = ((ChestMenu)player.containerMenu).getContainer();
					return inventory == AmphoraBlockEntity.this;
				} else {
					return false;
				}
			}
		};
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (!this.trySaveLootTable(nbt)) {
			ContainerHelper.saveAllItems(nbt, this.inventory);
		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(nbt)) {
			ContainerHelper.loadAllItems(nbt, this.inventory);
		}
	}
	
	@Override
	public int getContainerSize() {
		return 54;
	}
	
	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}
	
	@Override
	protected void setItems(NonNullList<ItemStack> list) {
		this.inventory = list;
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.spectrum.amphora");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return GenericSpectrumContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this, ScreenBackgroundVariant.EARLYGAME);
	}
	
	@Override
	public void startOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			this.stateManager.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}
	
	@Override
	public void stopOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			this.stateManager.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}
	
	public void tick() {
		if (!this.remove) {
			this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}
	
	void setOpen(BlockState state, boolean open) {
		this.getLevel().setBlock(this.getBlockPos(), state.setValue(BarrelBlock.OPEN, open), 3);
	}
	
	void playSound(BlockState state, SoundEvent soundEvent) {
		Level world = this.getLevel();
		Vec3i vec3i = (state.getValue(BarrelBlock.FACING)).getNormal();
		double d = (double)this.worldPosition.getX() + 0.5 + (double)vec3i.getX() / 2.0;
		double e = (double)this.worldPosition.getY() + 0.5 + (double)vec3i.getY() / 2.0;
		double f = (double)this.worldPosition.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
		this.getLevel().playSound(null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
	}
	
}
