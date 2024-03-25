package de.dafuqs.spectrum.blocks.dd_deco;

import de.dafuqs.spectrum.blocks.decoration.SpectrumFacingBlock;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class PyriteRipperBlock extends SpectrumFacingBlock {
	
	public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");
	
	public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>() {{
		put(Direction.UP, Block.box(0.0D, 0.0D, 5.0D, 16.0D, 6.0D, 11.0D));
		put(Direction.DOWN, Block.box(0.0D, 10.0D, 5.0D, 16.0D, 16.0D, 11.0D));
		put(Direction.NORTH, Block.box(0.0D, 5.0D, 10.0D, 16.0D, 11.0D, 16.0D));
		put(Direction.SOUTH, Block.box(0.0D, 5.0D, 0.0D, 16.0D, 11.0D, 6.0D));
		put(Direction.EAST, Block.box(0.0D, 5.0D, 0.0D, 6.0D, 11.0D, 16.0));
		put(Direction.WEST, Block.box(10.0D, 5.0D, 0.0D, 16.0D, 11.0D, 16.0));
	}};
	public static final Map<Direction, VoxelShape> SHAPES_MIRRORED = new HashMap<>() {{
		put(Direction.UP, Block.box(5.0D, 0.0D, 0.0D, 11.0D, 6.0D, 16.0));
		put(Direction.DOWN, Block.box(5.0D, 10.0D, 0.0D, 11.0D, 16.0D, 16.0D));
		put(Direction.NORTH, Block.box(5.0D, 0.0D, 10.0D, 11.0D, 16.0D, 16.0D));
		put(Direction.SOUTH, Block.box(5.0D, 0.0D, 0.0D, 11.0D, 16.0D, 6.0D));
		put(Direction.EAST, Block.box(0.0D, 0.0D, 5.0D, 6.0D, 16.0D, 11.0D));
		put(Direction.WEST, Block.box(10.0D, 0.0D, 5.0D, 16.0D, 16.0D, 11.0D));
	}};
	
	public PyriteRipperBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.EAST).setValue(MIRRORED, false));
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, MIRRORED);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction targetDirection = state.getValue(FACING).getOpposite();
		BlockPos targetPos = pos.relative(targetDirection);
		return world.getBlockState(targetPos).isFaceSturdy(world, targetPos, targetDirection, SupportType.FULL);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction direction = ctx.getClickedFace();
		BlockState placedOnState = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(direction.getOpposite()));
		if (placedOnState.is(this)) {
			return placedOnState;
		}
		
		if (ctx.getHorizontalDirection().getAxis().isHorizontal()) {
			return this.defaultBlockState().setValue(FACING, direction).setValue(MIRRORED, ctx.getHorizontalDirection().getStepX() != 0);
		}
		
		boolean mirrored = ctx.getNearestLookingDirection().getAxis().isVertical();
		return this.defaultBlockState().setValue(FACING, direction).setValue(MIRRORED, mirrored);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : state;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return state.getValue(MIRRORED) ? SHAPES_MIRRORED.get(state.getValue(FACING)) : SHAPES.get(state.getValue(FACING));
	}
	
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity && !entity.getType().is(SpectrumEntityTypeTags.POKING_DAMAGE_IMMUNE)) {
			if (!world.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
				double difX = Math.abs(entity.getX() - entity.xOld);
				double difZ = Math.abs(entity.getZ() - entity.zOld);
				if (difX >= 0.003 || difZ >= 0.003) {
					entity.hurt(SpectrumDamageTypes.sawtooth(world), 2.0F);
				}
			}
		}
	}
	
}
