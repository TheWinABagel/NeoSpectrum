package de.dafuqs.spectrum.blocks.conditional;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.FluidLogging;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import de.dafuqs.spectrum.registries.SpectrumFluids;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QuitoxicReedsBlock extends Block implements RevelationAware, FluidLogging.SpectrumFluidLoggable {
	
	public static final EnumProperty<FluidLogging.State> LOGGED = FluidLogging.ANY_INCLUDING_NONE;
	public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
	public static final BooleanProperty ALWAYS_DROP = BooleanProperty.create("always_drop"); // I have no idea why this works anymore and at this point I am too afraid to ask
	
	public static final int MAX_GROWTH_HEIGHT_WATER = 5;
	public static final int MAX_GROWTH_HEIGHT_CRYSTAL = 7;
	
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	
	public QuitoxicReedsBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(LOGGED, FluidLogging.State.NOT_LOGGED).setValue(ALWAYS_DROP, false).setValue(AGE, 0));
		RevelationAware.register(this);
	}
	
	@Override
	public ResourceLocation getCloakAdvancementIdentifier() {
		return SpectrumCommon.locate("milestones/reveal_quitoxic_reeds");
	}
	
	@Override
	public Map<BlockState, BlockState> getBlockStateCloaks() {
		Map<BlockState, BlockState> map = new Hashtable<>();
		for (int i = 0; i <= BlockStateProperties.MAX_AGE_7; i++) {
			map.put(this.defaultBlockState().setValue(LOGGED, FluidLogging.State.NOT_LOGGED).setValue(AGE, i), Blocks.AIR.defaultBlockState());
			map.put(this.defaultBlockState().setValue(LOGGED, FluidLogging.State.WATER).setValue(AGE, i), Blocks.WATER.defaultBlockState());
			map.put(this.defaultBlockState().setValue(LOGGED, FluidLogging.State.LIQUID_CRYSTAL).setValue(AGE, i), SpectrumBlocks.LIQUID_CRYSTAL.defaultBlockState());
		}
		return map;
	}
	
	@Override
	public Tuple<Item, Item> getItemCloak() {
		return new Tuple<>(this.asItem(), Blocks.SUGAR_CANE.asItem());
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!state.canSurvive(world, pos)) {
			world.destroyBlock(pos, true);
		}
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		if (fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8) {
			return super.getStateForPlacement(ctx).setValue(LOGGED, FluidLogging.State.WATER);
		} else if (fluidState.getType() == SpectrumFluids.LIQUID_CRYSTAL) {
			return super.getStateForPlacement(ctx).setValue(LOGGED, FluidLogging.State.LIQUID_CRYSTAL);
		} else {
			return super.getStateForPlacement(ctx).setValue(LOGGED, FluidLogging.State.NOT_LOGGED);
		}
	}
	
	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		// since the quitoxic reeds are stacked and break from bottom to top
		// bot the player that broke the other blocks is not propagated we
		// have to apply a workaround here by counting the reeds above this
		// and dropping that many times loot to account for it
		for (int i = 1; i < MAX_GROWTH_HEIGHT_CRYSTAL; i++) {
			BlockPos offsetPos = pos.offset(0, i, 0);
			if (world.getBlockState(offsetPos).is(this)) {
				world.setBlockAndUpdate(offsetPos, world.getBlockState(offsetPos).setValue(ALWAYS_DROP, true));
			} else {
				break;
			}
		}
		
		super.playerWillDestroy(world, pos, state, player);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		FluidLogging.State fluidLog = state.getValue(LOGGED);
		if (fluidLog == FluidLogging.State.WATER) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		} else if (fluidLog == FluidLogging.State.LIQUID_CRYSTAL) {
			world.scheduleTick(pos, SpectrumFluids.LIQUID_CRYSTAL, SpectrumFluids.LIQUID_CRYSTAL.getTickDelay(world));
		}
		
		if (!state.canSurvive(world, pos)) {
			world.scheduleTick(pos, this, 1);
		}
		return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(LOGGED).getFluidState();
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, LOGGED, ALWAYS_DROP);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (world.isEmptyBlock(pos.above()) || (world.isWaterAt(pos.above()) && world.isEmptyBlock(pos.above(2)))) {
			
			int i;
			for (i = 1; world.getBlockState(pos.below(i)).is(this); ++i);
			
			boolean bottomLiquidCrystalLogged = world.getBlockState(pos.below(i - 1)).getValue(LOGGED) == FluidLogging.State.LIQUID_CRYSTAL;
			
			// grows taller on liquid crystal
			if (i < MAX_GROWTH_HEIGHT_WATER || (bottomLiquidCrystalLogged && i < MAX_GROWTH_HEIGHT_CRYSTAL)) {
				int j = state.getValue(AGE);
				if (j == 7) {
					// consume 1 block close to the reed when growing.
					// if the quitoxic reeds are growing in liquid crystal: 1/4 chance to consume
					// search for block it could be planted on. 1 block => 1 quitoxic reed
					Optional<BlockPos> plantablePos = searchPlantablePos(world, pos.below(i), SpectrumBlockTags.QUITOXIC_REEDS_PLANTABLE, random);
					if (plantablePos.isEmpty() || world.getBlockState(plantablePos.get().above()).getBlock() instanceof QuitoxicReedsBlock) {
						return;
					}
					
					if (!bottomLiquidCrystalLogged || random.nextInt(4) == 0) {
						world.setBlock(plantablePos.get(), Blocks.DIRT.defaultBlockState(), 3);
						world.playSound(null, plantablePos.get(), SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
					}
					
					world.setBlockAndUpdate(pos.above(), getStateForPos(world, pos.above()));
					world.setBlock(pos, state.setValue(AGE, 0), 4);
				} else {
					// grow twice as fast, if liquid crystal logged
					if (bottomLiquidCrystalLogged) {
						world.setBlock(pos, state.setValue(AGE, Math.min(7, j + 2)), 4);
					} else {
						world.setBlock(pos, state.setValue(AGE, j + 1), 4);
					}
				}
			}
		}
	}
	
	private Optional<BlockPos> searchPlantablePos(Level world, @NotNull BlockPos searchPos, TagKey<Block> searchBlockState, RandomSource random) {
		List<Direction> directions = Util.shuffledCopy(Direction.values(), random);
		
		int i = 0;
		int range = 8;
		BlockPos currentPos = new BlockPos(searchPos.getX(), searchPos.getY(), searchPos.getZ());
		while (i < 6) {
			if (range < 8 && world.getBlockState(currentPos.relative(directions.get(i))).is(searchBlockState)) {
				range++;
				currentPos = currentPos.relative(directions.get(i));
			} else {
				i++;
				range = 0;
			}
		}
		
		if (currentPos.equals(searchPos)) {
			return Optional.empty();
		} else {
			return Optional.of(currentPos);
		}
	}
	
	public BlockState getStateForPos(Level world, BlockPos blockPos) {
		FluidState fluidState = world.getFluidState(blockPos);
		if (fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8) {
			return defaultBlockState().setValue(LOGGED, FluidLogging.State.WATER);
		} else if (fluidState.getType().equals(SpectrumFluids.LIQUID_CRYSTAL)) {
			return defaultBlockState().setValue(LOGGED, FluidLogging.State.LIQUID_CRYSTAL);
		}
		return defaultBlockState();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entityShapeContext) {
			Entity var4 = entityShapeContext.getEntity();
			if (var4 instanceof Player player) {
				return this.isVisibleTo(player) ? SHAPE : Shapes.empty();
			}
		}
		return Shapes.block();
	}
	
	/**
	 * Can be placed in up to 2 blocks deep water / liquid crystal
	 * growing on SpectrumBlockTags.QUITOXIC_REEDS_PLANTABLE only
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockState bottomBlockState = world.getBlockState(pos.below());
		if (bottomBlockState.is(this)) {
			return true;
		} else {
			BlockState topBlockState = world.getBlockState(pos.above());
			BlockState topBlockState2 = world.getBlockState(pos.above(2));
			if (bottomBlockState.is(SpectrumBlockTags.QUITOXIC_REEDS_PLANTABLE) && isValidTopBlock(topBlockState) && isValidTopBlock(topBlockState2)) {
				FluidState fluidState = world.getFluidState(pos);
				return fluidState.getAmount() == 8 && (fluidState.is(FluidTags.WATER) || fluidState.is(SpectrumFluidTags.LIQUID_CRYSTAL));
			}
			return false;
		}
	}
	
	public boolean isValidTopBlock(BlockState blockState) {
		return blockState.isAir() || blockState.is(this) || blockState.is(Blocks.WATER) || blockState.is(SpectrumBlocks.LIQUID_CRYSTAL);
	}
	
	@Override
	public float getMaxHorizontalOffset() {
		return 0.15F;
	}
	
}
