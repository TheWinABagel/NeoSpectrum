package de.dafuqs.spectrum.blocks.crystallarieum;

import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlock;
import de.dafuqs.spectrum.helpers.NullableDyeColor;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrystallarieumBlock extends InWorldInteractionBlock {
	
	public static final EnumProperty<NullableDyeColor> COLOR = EnumProperty.create("color", NullableDyeColor.class);
	
	public CrystallarieumBlock(Properties settings) {
		super(settings);
		this.registerDefaultState((this.stateDefinition.any()).setValue(COLOR, NullableDyeColor.NONE));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(COLOR);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrystallarieumBlockEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SpectrumBlockEntities.CRYSTALLARIEUM, world.isClientSide ? CrystallarieumBlockEntity::clientTick : CrystallarieumBlockEntity::serverTick);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if (!world.isClientSide() && direction == Direction.UP) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CrystallarieumBlockEntity crystallarieumBlockEntity) {
				crystallarieumBlockEntity.onTopBlockChange(neighborState, null);
			}
		}
		return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (!world.isClientSide && entity instanceof ItemEntity itemEntity) {
			if (itemEntity.position().x % 0.5 != 0 && itemEntity.position().z % 0.5 != 0) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof CrystallarieumBlockEntity crystallarieumBlockEntity) {
					ItemStack stack = itemEntity.getItem();
					crystallarieumBlockEntity.acceptStack(stack, false, itemEntity.getOwner() != null ? itemEntity.getOwner().getUUID() : null);
				}
			}
		} else {
			super.fallOn(world, state, pos, entity, fallDistance);
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			// if the structure is valid the player can put / retrieve blocks into the shrine
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CrystallarieumBlockEntity crystallarieumBlockEntity) {
				
				ItemStack handStack = player.getItemInHand(hand);
				if (player.isShiftKeyDown() || handStack.isEmpty()) {
					// sneaking or empty hand: remove items
					if (retrieveStack(world, pos, player, hand, handStack, crystallarieumBlockEntity, 1) || retrieveStack(world, pos, player, hand, handStack, crystallarieumBlockEntity, 0)) {
						crystallarieumBlockEntity.inventoryChanged();
						crystallarieumBlockEntity.setOwner(player);
					}
					return InteractionResult.CONSUME;
				} else {
					// hand is full and inventory is empty: add
					// hand is full and inventory already contains item: exchange them
					if (handStack.getItem() instanceof InkStorageItem<?> inkStorageItem) {
						if (inkStorageItem.getDrainability().canDrain(false) && exchangeStack(world, pos, player, hand, handStack, crystallarieumBlockEntity, CrystallarieumBlockEntity.INK_STORAGE_STACK_SLOT_ID)) {
							crystallarieumBlockEntity.inventoryChanged();
							crystallarieumBlockEntity.setOwner(player);
						}
					} else {
						if (exchangeStack(world, pos, player, hand, handStack, crystallarieumBlockEntity, CrystallarieumBlockEntity.CATALYST_SLOT_ID)) {
							crystallarieumBlockEntity.inventoryChanged();
							crystallarieumBlockEntity.setOwner(player);
						}
					}
				}
			}
			return InteractionResult.CONSUME;
		}
	}
	
	public ItemStack asStackWithColor(NullableDyeColor color) {
		ItemStack stack = asItem().getDefaultInstance();
		NullableDyeColor.set(stack, color);
		return stack;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		NullableDyeColor.addTooltip(stack, tooltip);
	}
	
}
