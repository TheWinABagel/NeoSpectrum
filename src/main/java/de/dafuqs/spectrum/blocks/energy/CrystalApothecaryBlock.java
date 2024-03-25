package de.dafuqs.spectrum.blocks.energy;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrystalApothecaryBlock extends BaseEntityBlock {
	
	public CrystalApothecaryBlock(Properties settings) {
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrystalApothecaryBlockEntity(pos, state);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.crystal_apothecary.tooltip").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CrystalApothecaryBlockEntity crystalApothecaryBlockEntity) {
			if (placer instanceof ServerPlayer serverPlayerEntity) {
				crystalApothecaryBlockEntity.setOwner(serverPlayerEntity);
			}
			if (itemStack.hasCustomHoverName()) {
				crystalApothecaryBlockEntity.setCustomName(itemStack.getHoverName());
			}
			crystalApothecaryBlockEntity.harvestExistingClusters();
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CrystalApothecaryBlockEntity crystalApothecaryBlockEntity) {
				player.openMenu(crystalApothecaryBlockEntity);
			}
			return InteractionResult.CONSUME;
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
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CrystalApothecaryBlockEntity crystalApothecaryBlockEntity) {
				Containers.dropContents(world, pos, crystalApothecaryBlockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SpectrumBlockEntities.CRYSTAL_APOTHECARY, CrystalApothecaryBlockEntity::tick);
	}
	
	@Override
	@Nullable
	public <T extends BlockEntity> GameEventListener getListener(ServerLevel world, T blockEntity) {
		return blockEntity instanceof CrystalApothecaryBlockEntity crystalApothecaryBlockEntity ? crystalApothecaryBlockEntity.getEventListener() : null;
	}
	
}
