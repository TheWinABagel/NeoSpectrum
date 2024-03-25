package de.dafuqs.spectrum.blocks.rock_candy;

import de.dafuqs.spectrum.blocks.FluidLogging;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.particle.effect.DynamicParticleEffect;
import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import de.dafuqs.spectrum.registries.SpectrumFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SugarStickBlock extends Block implements RockCandy {
	
	protected final static Map<RockCandyVariant, Block> SUGAR_STICK_BLOCKS = new EnumMap<>(RockCandyVariant.class);
	
	protected final RockCandyVariant rockCandyVariant;
	
	public static final int ITEM_SEARCH_RANGE = 5;
	public static final int REQUIRED_ITEM_COUNT_PER_STAGE = 4;
	
	public static final EnumProperty<FluidLogging.State> LOGGED = FluidLogging.NONE_AND_CRYSTAL;
	public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
	
	protected static final VoxelShape SHAPE = Block.box(5.0D, 3.0D, 5.0D, 11.0D, 16.0D, 11.0D);
	
	public SugarStickBlock(Properties settings, RockCandyVariant rockCandyVariant) {
		super(settings);
		this.rockCandyVariant = rockCandyVariant;
		SUGAR_STICK_BLOCKS.put(this.rockCandyVariant, this);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(LOGGED, FluidLogging.State.NOT_LOGGED));
	}
	
	@Override
	public RockCandyVariant getVariant() {
		return this.rockCandyVariant;
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		if (fluidState.getType() == SpectrumFluids.LIQUID_CRYSTAL) {
			return super.getStateForPlacement(ctx).setValue(LOGGED, FluidLogging.State.LIQUID_CRYSTAL);
		} else {
			return super.getStateForPlacement(ctx);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.getValue(LOGGED).isOf(SpectrumFluids.LIQUID_CRYSTAL) ? SpectrumFluids.LIQUID_CRYSTAL.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, LOGGED);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(LOGGED).isOf(SpectrumFluids.LIQUID_CRYSTAL) && state.getValue(AGE) < BlockStateProperties.MAX_AGE_2;
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		if (state.getValue(LOGGED).isOf(Fluids.EMPTY)) {
			int age = state.getValue(AGE);
			
			if (age == 2 || (age == 1 ? random.nextBoolean() : random.nextFloat() < 0.25)) {
				world.addParticle(new DynamicParticleEffect(0.1F, ColorHelper.getRGBVec(rockCandyVariant.getDyeColor()), 0.5F, 120, true, true),
						pos.getX() + 0.25 + random.nextFloat() * 0.5,
						pos.getY() + 0.25 + random.nextFloat() * 0.5,
						pos.getZ() + 0.25 + random.nextFloat() * 0.5,
						0.08 - random.nextFloat() * 0.16,
						0.04 - random.nextFloat() * 0.16,
						0.08 - random.nextFloat() * 0.16);
			}
			
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.randomTick(state, world, pos, random);
		
		if (state.getValue(LOGGED).isOf(SpectrumFluids.LIQUID_CRYSTAL)) {
			int age = state.getValue(AGE);
			if (age < BlockStateProperties.MAX_AGE_2) {
				List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), ITEM_SEARCH_RANGE, ITEM_SEARCH_RANGE, ITEM_SEARCH_RANGE));
				Collections.shuffle(itemEntities);
				for (ItemEntity itemEntity : itemEntities) {
					// is the item also submerged?
					// lazy, but mostly accurate and performant way to check if it's the same liquid pool
					if (!itemEntity.isEyeInFluid(SpectrumFluidTags.LIQUID_CRYSTAL)) {
						continue;
					}
					
					ItemStack stack = itemEntity.getItem();
					if (stack.getCount() >= REQUIRED_ITEM_COUNT_PER_STAGE) {
						@Nullable RockCandyVariant itemVariant = RockCandyVariant.getFor(stack);
						if (itemVariant != null) {
							BlockState newState;
							if (rockCandyVariant != RockCandyVariant.SUGAR) {
								newState = state;
							} else {
								newState = SUGAR_STICK_BLOCKS.get(itemVariant).defaultBlockState();
							}
							
							stack.shrink(REQUIRED_ITEM_COUNT_PER_STAGE);
							world.setBlockAndUpdate(pos, newState.setValue(AGE, age + 1).setValue(LOGGED, state.getValue(LOGGED)));
							world.playSound(null, pos, newState.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = Direction.UP;
		return Block.canSupportCenter(world, pos.relative(direction), direction.getOpposite());
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.UP && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		CompoundTag nbt = stack.getTag();
		if (nbt != null && nbt.contains("BlockStateTag")) {
			CompoundTag blockStateTag = nbt.getCompound("BlockStateTag");
			if (blockStateTag.contains("age", Tag.TAG_STRING)) {
				String age = blockStateTag.getString("age");
				if ("1".equals(age)) {
					tooltip.add(Component.translatable("block.spectrum.sugar_stick.tooltip.medium"));
				} else if ("2".equals(age)) {
					tooltip.add(Component.translatable("block.spectrum.sugar_stick.tooltip.large"));
				}
				
			}
		}
	}
	
}
