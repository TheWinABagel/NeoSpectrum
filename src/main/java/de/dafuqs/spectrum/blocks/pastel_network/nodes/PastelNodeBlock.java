package de.dafuqs.spectrum.blocks.pastel_network.nodes;

import de.dafuqs.spectrum.blocks.decoration.SpectrumFacingBlock;
import de.dafuqs.spectrum.blocks.pastel_network.network.PastelNetwork;
import de.dafuqs.spectrum.blocks.pastel_network.network.ServerPastelNetwork;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastelNodeBlock extends SpectrumFacingBlock implements EntityBlock {
	
	public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>() {{
		put(Direction.UP, Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D));
		put(Direction.DOWN, Block.box(4.0D, 8.0D, 4.0D, 12.0D, 16.0D, 12.0D));
		put(Direction.NORTH, Block.box(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D));
		put(Direction.SOUTH, Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D));
		put(Direction.EAST, Block.box(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D));
		put(Direction.WEST, Block.box(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D));
	}};

    protected final PastelNodeType pastelNodeType;
	
	public PastelNodeBlock(Properties settings, PastelNodeType pastelNodeType) {
		super(settings);
		this.pastelNodeType = pastelNodeType;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction targetDirection = state.getValue(FACING).getOpposite();
		BlockPos targetPos = pos.relative(targetDirection);
		return world.getBlockState(targetPos).isSolid();
	}


    @Override
	@SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.is(state.getBlock())) {
            PastelNodeBlockEntity blockEntity = getBlockEntity(world, pos);
            if (blockEntity != null) {
                blockEntity.onBroken();
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getClickedFace();
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(direction.getOpposite()));
        return blockState.is(this) && blockState.getValue(FACING) == direction ? this.defaultBlockState().setValue(FACING, direction.getOpposite()) : this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (placer instanceof ServerPlayer serverPlayerEntity) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PastelNodeBlockEntity pastelNodeBlockEntity) {
                SpectrumAdvancementCriteria.PASTEL_NETWORK_CREATING.trigger(serverPlayerEntity, (ServerPastelNetwork) pastelNodeBlockEntity.getNetwork());
            }
        }
    }
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(this.pastelNodeType.getTooltip().withStyle(ChatFormatting.WHITE));
		tooltip.add(Component.translatable("block.spectrum.pastel_network_nodes.tooltip.placing").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("block.spectrum.pastel_network_nodes.tooltip.range", PastelNodeBlockEntity.RANGE).withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : state;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		PastelNodeBlockEntity blockEntity = getBlockEntity(world, pos);
		if (player.getItemInHand(hand).is(SpectrumItems.PAINTBRUSH)) {
			return sendDebugMessage(world, player, blockEntity);
		} else if (this.pastelNodeType.usesFilters()) {
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                player.openMenu(blockEntity);
                return InteractionResult.CONSUME;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @NotNull
    private static InteractionResult sendDebugMessage(Level world, Player player, PastelNodeBlockEntity blockEntity) {
        if (world.isClientSide) {
            if (blockEntity != null) {
                PastelNetwork network = blockEntity.network;
                player.sendSystemMessage(Component.translatable("block.spectrum.pastel_network_nodes.connection_debug"));
                if (network == null) {
                    player.sendSystemMessage(Component.literal("C: No connected network :("));
                } else {
                    player.sendSystemMessage(Component.literal("C: " + network.getUUID().toString()));
                    player.sendSystemMessage(Component.literal("C: " + network.getNodeDebugText()));
                }
            }
            return InteractionResult.SUCCESS;
        } else {
            if (blockEntity != null) {
                PastelNetwork network = blockEntity.network;
                if (network == null) {
                    player.sendSystemMessage(Component.literal("S: No connected network :("));
                } else {
                    player.sendSystemMessage(Component.literal("S: " + network.getUUID().toString()));
                    player.sendSystemMessage(Component.literal("S: " + network.getNodeDebugText()));
                }
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    public @Nullable PastelNodeBlockEntity getBlockEntity(LevelAccessor world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof PastelNodeBlockEntity pastelNodeBlockEntity) {
            return pastelNodeBlockEntity;
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PastelNodeBlockEntity(pos, state);
    }

}