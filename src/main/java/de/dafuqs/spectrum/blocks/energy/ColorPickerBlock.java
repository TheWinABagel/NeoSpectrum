package de.dafuqs.spectrum.blocks.energy;

import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColorPickerBlock extends HorizontalDirectionalBlock implements EntityBlock {
	
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 13.0D, 15.0D);
	
	public ColorPickerBlock(Properties settings) {
		super(settings);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.color_picker.tooltip").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ColorPickerBlockEntity(pos, state);
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide ? null : Support.checkType(type, SpectrumBlockEntities.COLOR_PICKER, ColorPickerBlockEntity::tick);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			this.openScreen(world, pos, player);
			return InteractionResult.CONSUME;
		}
	}
	
	protected void openScreen(Level world, BlockPos pos, Player player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ColorPickerBlockEntity colorPickerBlockEntity) {
			colorPickerBlockEntity.setOwner(player);
			player.openMenu(colorPickerBlockEntity);
		}
	}
	
	@Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
	
	@Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		if(world.getBlockEntity(pos) instanceof ColorPickerBlockEntity blockEntity) {
			int i = 0;
			float f = 0.0f;
			for (int j = 0; j < blockEntity.inventory.size(); ++j) {
				ItemStack itemStack = blockEntity.inventory.get(j);
				if (itemStack.isEmpty()) continue;
				f += (float)itemStack.getCount() / (float)itemStack.getMaxStackSize();
				++i;
			}
			return Mth.floor(f / (float) blockEntity.inventory.size() * 14.0f) + (i > 0 ? 1 : 0);
		}
		
		return 0;
    }
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ColorPickerBlockEntity colorPickerBlockEntity) {
				Containers.dropContents(world, pos, colorPickerBlockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, world, pos, newState, moved);
		}
	}
	
}