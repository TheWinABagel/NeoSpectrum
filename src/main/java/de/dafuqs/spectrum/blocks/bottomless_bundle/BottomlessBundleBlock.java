package de.dafuqs.spectrum.blocks.bottomless_bundle;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BottomlessBundleBlock extends BaseEntityBlock {
	
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	
	public BottomlessBundleBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BottomlessBundleBlockEntity(pos, state);
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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			if (player.isShiftKeyDown()) {
				world.getBlockEntity(pos, SpectrumBlockEntities.BOTTOMLESS_BUNDLE).ifPresent((bottomlessBundleBlockEntity) -> {
					ItemStack itemStack = bottomlessBundleBlockEntity.retrieveBundle();

					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

					ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemStack);
					itemEntity.setDefaultPickUpDelay();
					world.addFreshEntity(itemEntity);

					itemEntity.playerTouch(player); // auto pickup
				});
			} else {
				world.getBlockEntity(pos, SpectrumBlockEntities.BOTTOMLESS_BUNDLE).ifPresent((bottomlessBundleBlockEntity) -> {
					ItemStack stack = bottomlessBundleBlockEntity.storage.getStackInSlot(0);
					long amount = stack.getCount();
					if (amount == 0) {
						player.displayClientMessage(Component.translatable("item.spectrum.bottomless_bundle.tooltip.empty"), true);
					} else {
						long maxStoredAmount = BottomlessBundleItem.getMaxStoredAmount(bottomlessBundleBlockEntity.bottomlessBundleStack);
						player.displayClientMessage(Component.translatable("item.spectrum.bottomless_bundle.tooltip.count_of", amount, maxStoredAmount).append(stack.getItem().getDescription()), true);
					}
				});
			}
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return SpectrumItems.BOTTOMLESS_BUNDLE.getDefaultInstance();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof BottomlessBundleBlockEntity bottomlessBundleBlockEntity) {
			return List.of(bottomlessBundleBlockEntity.retrieveBundle());
		} else {
			return super.getDrops(state, builder);
		}
	}
	
	@Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
	
	@Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BottomlessBundleBlockEntity bottomlessBundleBlockEntity) {
			float curr = bottomlessBundleBlockEntity.storage.getStackInSlot(0).getCount();
			float max = bottomlessBundleBlockEntity.storage.getSlotLimit(0);
			return Mth.floor(curr / max * 14.0f) + curr > 0 ? 1 : 0;
		}
		
		return 0;
    }
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BottomlessBundleBlockEntity bottomlessBundleBlockEntity) {
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (!world.isClientSide) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BottomlessBundleBlockEntity bottomlessBundleBlockEntity) {
				bottomlessBundleBlockEntity.setBundle(itemStack.copy());
				world.updateNeighbourForOutputSignal(pos, this);
			}
		}
	}
	
	@Override
	public MutableComponent getName() {
		return Component.translatable("item.spectrum.bottomless_bundle");
	}
	
}
