package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.blocks.PlacedItemBlock;
import de.dafuqs.spectrum.blocks.PlacedItemBlockEntity;
import de.dafuqs.spectrum.explosion.ModularExplosionDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ParametricMiningDeviceBlock extends PlacedItemBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>() {{
		put(Direction.UP, Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D));
		put(Direction.DOWN, Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D));
		put(Direction.NORTH, Block.box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D));
		put(Direction.SOUTH, Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 4.0D));
		put(Direction.EAST, Block.box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D));
		put(Direction.WEST, Block.box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D));
	}};
	
	public ParametricMiningDeviceBlock(Properties settings) {
		super(settings);
		this.registerDefaultState((this.stateDefinition.any()).setValue(DirectionalBlock.FACING, Direction.UP));
	}
	
	// Wall mounting stuffs
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockPos = pos.relative(direction.getOpposite());
		return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, direction);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getClickedFace());
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}
	
	// misc
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	
	// actual logic
	// press to boom
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		
		if ((world.getBlockEntity(pos) instanceof PlacedItemBlockEntity blockEntity)) {
			ItemStack stack = blockEntity.getStack();
			Player owner = blockEntity.getOwnerIfOnline();
			
			world.removeBlock(pos, false);
			
			ModularExplosionDefinition.explode((ServerLevel) world, pos, state.getValue(FACING).getOpposite(), owner, stack);
		}
		
		return InteractionResult.CONSUME;
	}
	
}
