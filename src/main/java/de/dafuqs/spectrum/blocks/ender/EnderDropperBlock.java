package de.dafuqs.spectrum.blocks.ender;

import de.dafuqs.spectrum.inventories.GenericSpectrumContainerScreenHandler;
import de.dafuqs.spectrum.inventories.ScreenBackgroundVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EnderDropperBlock extends DispenserBlock {
	
	private static final DispenseItemBehavior BEHAVIOR = new DefaultDispenseItemBehavior();
	
	public EnderDropperBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	protected DispenseItemBehavior getDispenseMethod(ItemStack stack) {
		return BEHAVIOR;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EnderDropperBlockEntity(pos, state);
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (placer instanceof ServerPlayer) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnderDropperBlockEntity) {
				((EnderDropperBlockEntity) blockEntity).setOwner((ServerPlayer) placer);
				blockEntity.setChanged();
			}
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnderDropperBlockEntity enderDropperBlockEntity) {
				
				if (!enderDropperBlockEntity.hasOwner()) {
					enderDropperBlockEntity.setOwner(player);
				}
				
				if (enderDropperBlockEntity.isOwner(player)) {
					PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
					
					player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) -> GenericSpectrumContainerScreenHandler.createGeneric9x3(i, playerInventory, enderChestInventory, ScreenBackgroundVariant.EARLYGAME), enderDropperBlockEntity.getContainerName()));
					
					PiglinAi.angerNearbyPiglins(player, true);
				} else {
					player.displayClientMessage(Component.translatable("block.spectrum.ender_dropper_with_owner", enderDropperBlockEntity.getOwnerName()), true);
				}
			}
			return InteractionResult.CONSUME;
		}
	}
	
	@SuppressWarnings("UnstableApiUsage")
	@Override
	protected void dispenseFrom(ServerLevel world, BlockPos pos) {
		BlockSourceImpl blockPointerImpl = new BlockSourceImpl(world, pos);
		EnderDropperBlockEntity enderDropperBlockEntity = blockPointerImpl.getEntity();
		
		int i = enderDropperBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0); // no items in inv
		} else {
			ItemStack itemStack = enderDropperBlockEntity.getStack(i);
			if (!itemStack.isEmpty()) {
				Direction direction = world.getBlockState(pos).getValue(FACING);
				if (world.getBlockState(pos.relative(direction)).isAir()) {
					ItemStack itemStack3 = BEHAVIOR.dispense(blockPointerImpl, itemStack);
					enderDropperBlockEntity.setStack(i, itemStack3);
				} else {
					Storage<ItemVariant> target = ItemStorage.SIDED.find(world, pos.relative(direction), direction.getOpposite());
					if (target != null) {
						// getting inv will always work since .chooseNonEmptySlot() and others would fail otherwise
						Container inv = enderDropperBlockEntity.getOwnerIfOnline().getEnderChestInventory();
						long moved = StorageUtil.move(
								InventoryStorage.of(inv, direction).getSlot(i),
								target,
								iv -> true,
								1,
								null
						);
						// return without triggering fail event if successfully moved
						if (moved == 1) return;
					}
					world.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0); // no room to dispense to
				}
			}
		}
	}

	public DispenseItemBehavior getDefaultBehaviorForItem(ItemStack stack) {
		return super.getDispenseMethod(stack);
	}

}