package de.dafuqs.spectrum.blocks;

import com.google.common.collect.ImmutableMap;
import de.dafuqs.spectrum.cca.OnPrimordialFireComponent;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.primordial_fire_burning.PrimordialFireBurningRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrimordialFireBlock extends BaseFireBlock {
	
	public static boolean EXPLOSION_CAUSES_PRIMORDIAL_FIRE_FLAG = false;
	
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final BooleanProperty UP = PipeBlock.UP;
	private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((entry) -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
	private static final VoxelShape UP_SHAPE = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape WEST_SHAPE = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape EAST_SHAPE = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    private final Map<BlockState, VoxelShape> shapesByState;
    private static final float DAMAGE = 0.2F;

    public PrimordialFireBlock(Properties settings) {
        super(settings, DAMAGE);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false));
        this.shapesByState = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), PrimordialFireBlock::getShapeForState)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP);
    }

    public static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = Shapes.empty();
        if (state.getValue(UP)) {
            voxelShape = UP_SHAPE;
        }
        if (state.getValue(NORTH)) {
            voxelShape = Shapes.or(voxelShape, NORTH_SHAPE);
        }
        if (state.getValue(SOUTH)) {
            voxelShape = Shapes.or(voxelShape, SOUTH_SHAPE);
        }
        if (state.getValue(EAST)) {
            voxelShape = Shapes.or(voxelShape, EAST_SHAPE);
        }
        if (state.getValue(WEST)) {
            voxelShape = Shapes.or(voxelShape, WEST_SHAPE);
        }
        return voxelShape.isEmpty() ? DOWN_AABB : voxelShape;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return this.canSurvive(state, world, pos) ? getStateForPosition(world, pos) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.shapesByState.get(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.getStateForPosition(ctx.getLevel(), ctx.getClickedPos());
    }

    public BlockState getStateForPosition(BlockGetter world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = world.getBlockState(blockPos);
        if (!this.canBurm(blockState, world, pos) && !blockState.isFaceSturdy(world, blockPos, Direction.UP)) {
            BlockState blockState2 = this.defaultBlockState();
            for (Direction direction : Direction.values()) {
                BooleanProperty booleanProperty = DIRECTION_PROPERTIES.get(direction);
                if (booleanProperty != null) {
                    blockState2 = blockState2.setValue(booleanProperty, this.canBurm(world.getBlockState(pos.relative(direction)), world, pos, direction));
                }
            }
    
            return blockState2;
        } else {
            return this.defaultBlockState();
        }
    }
    
    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            entity.hurt(SpectrumDamageTypes.primordialFire(world, null), DAMAGE);
            OnPrimordialFireComponent.addPrimordialFireTicks(livingEntity, 5);
        }
        if (world.getGameTime() % 20 == 0 && entity instanceof ItemEntity itemEntity) {
            PrimordialFireBurningRecipe.processItemEntity(world, itemEntity);
        }
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, Direction.UP) || this.areBlocksAroundFlammable(world, pos);
    }
    
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random) {
        world.scheduleTick(pos, this, getFireTickDelay(world.random));

        if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!state.canSurvive(world, pos)) {
                world.removeBlock(pos, false);
            }

            BlockState blockState = world.getBlockState(pos.below());
            boolean isAboveInfiniburnBlock = blockState.is(world.dimensionType().infiniburn()) || blockState.is(SpectrumBlockTags.PRIMORDIAL_FIRE_BASE_BLOCKS);
            if (!isAboveInfiniburnBlock && random.nextFloat() < 0.01F) {
                world.removeBlock(pos, false);
            } else {
                if (!isAboveInfiniburnBlock) {
                    if (!this.areBlocksAroundFlammable(world, pos)) {
                        BlockPos blockPos = pos.below();
                        if (!world.getBlockState(blockPos).isFaceSturdy(world, blockPos, Direction.UP)) {
                            world.removeBlock(pos, false);
                        }
                        if (random.nextInt(10) == 0 && !this.canBurm(world.getBlockState(pos.below()), world, pos)) {
                            world.removeBlock(pos, false);
                            return;
                        }
                        return;
                    }
                }

                boolean biomeHasIncreasedFireBurnout = world.getBiome(pos).is(BiomeTags.INCREASED_FIRE_BURNOUT);
                int spreadReduction = biomeHasIncreasedFireBurnout ? -50 : 0;
                this.trySpreadingFire(world, pos.east(), 300 + spreadReduction, random);
                this.trySpreadingFire(world, pos.west(), 300 + spreadReduction, random);
                this.trySpreadingFire(world, pos.below(), 250 + spreadReduction, random);
                this.trySpreadingFire(world, pos.above(), 250 + spreadReduction, random);
                this.trySpreadingFire(world, pos.north(), 300 + spreadReduction, random);
                this.trySpreadingFire(world, pos.south(), 300 + spreadReduction, random);

                BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                for (int x = -1; x <= 1; ++x) {
                    for (int z = -1; z <= 1; ++z) {
                        for (int y = -1; y <= 4; ++y) {
                            if (x != 0 || y != 0 || z != 0) {
                                int o = 100;
                                if (y > 1) {
                                    o += (y - 1) * 100;
                                }
                                mutable.setWithOffset(pos, x, y, z);
                                int burnChance = this.getBurnChance(world, mutable);
                                if (burnChance > 0) {
                                    int q = (burnChance + 40 + world.getDifficulty().getId() * 7) / 30;
                                    if (q > 0 && random.nextInt(o) <= q) {
                                        world.setBlock(mutable, getStateForPosition(world, mutable), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int getSpreadChance(BlockState state, BlockGetter level, BlockPos pos) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED) ? 0 : state.getFireSpreadSpeed(level, pos, Direction.NORTH);
    }

    private int getBurnChance(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED) ? 0 : state.getFireSpreadSpeed(level, pos, direction);
    }

    private int getBurnChance(BlockState state, BlockGetter level, BlockPos pos) {
        if (!state.hasProperty(BlockStateProperties.WATERLOGGED) || !state.getValue(BlockStateProperties.WATERLOGGED)) {
            for (Direction direction : Direction.values()) {
                int speed = state.getFireSpreadSpeed(level, pos, direction);
                if (speed != 0) return speed;
            }
        }
        return 0;
    }

    private void trySpreadingFire(Level world, BlockPos pos, int spreadFactor, RandomSource random) {
        if (!GenericClaimModsCompat.canBreak(world, pos, null)) {
            return;
        }
    
        int spreadChance = this.getSpreadChance(world.getBlockState(pos), world, pos);
        if (random.nextInt(spreadFactor) < spreadChance) {
            BlockState currentState = world.getBlockState(pos);
            if (random.nextBoolean() ) {
                if(PrimordialFireBurningRecipe.processBlock(world, pos, currentState)) {
                    return;
                }
                
                // replace the current block with fire
                world.setBlock(pos, getStateForPosition(world, pos), 3);
            }
            
            if (currentState.getBlock() instanceof TntBlock) {
                TntBlock.explode(world, pos);
            }
        }
    }
    
    private boolean areBlocksAroundFlammable(BlockGetter world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.canBurm(world.getBlockState(pos.relative(direction)), world, pos, direction)) {
                return true;
            }
        }
        return false;
    }

    private int getBurnChance(ServerLevel world, BlockPos pos) {
        if (!world.isEmptyBlock(pos)) {
            return 0;
        } else {
            int i = 0;
            for (Direction direction : Direction.values()) {
                BlockState blockState = world.getBlockState(pos.relative(direction));
                i = Math.max(this.getBurnChance(blockState, world, pos), i);
            }
            return i;
        }
    }

    protected boolean canBurm(BlockState state, BlockGetter level, BlockPos pos) {
        return this.getBurnChance(state, level, pos) > 0;
    }

    protected boolean canBurm(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return this.getBurnChance(state, level, pos, direction) > 0;
    }

    @Deprecated
    @Override
    protected boolean canBurn(BlockState state) {
        //should never be called, use getBurnChance
        //todoforge move to NeoForgeDataMaps.FLAMMABLES once 1.20.4?
        return false;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onPlace(state, world, pos, oldState, notify);
		world.scheduleTick(pos, this, getFireTickDelay(world.random));
    }

    private static int getFireTickDelay(RandomSource random) {
        return 20 + random.nextInt(10);
    }

    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(24) == 0) {
            world.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }

        BlockPos blockPos = pos.below();
        BlockState blockState = world.getBlockState(blockPos);
        int i;
        double d;
        double e;
        double f;

        if (blockState.isFaceSturdy(world, blockPos, Direction.UP)) {
            var particle = this.canBurm(blockState, world, pos, Direction.UP) ? SpectrumParticleTypes.PRIMORDIAL_SIGNAL_SMOKE : SpectrumParticleTypes.PRIMORDIAL_COSY_SMOKE;
            for(i = 0; i < 2; ++i) {
                d = (double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1);
                e = (double)pos.getY() + 0.15;
                f = (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1);
                world.addParticle(particle, d, e, f, 0.0015, 0.07, 0.0015);
            }
        }

        if (!this.canBurm(blockState, world, pos) && !blockState.isFaceSturdy(world, blockPos, Direction.UP)) {
            if (this.canBurm(world.getBlockState(pos.west()), world, pos, Direction.UP)) {
                for(i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble() * 0.10000000149011612;
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurm(world.getBlockState(pos.east()), world, pos, Direction.EAST)) {
                for(i = 0; i < 2; ++i) {
                    d = (double)(pos.getX() + 1) - random.nextDouble() * 0.10000000149011612;
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurm(world.getBlockState(pos.north()), world, pos, Direction.NORTH)) {
                for(i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble();
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)pos.getZ() + random.nextDouble() * 0.10000000149011612;
                    world.addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurm(world.getBlockState(pos.south()), world, pos, Direction.SOUTH)) {
                for(i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble();
                    e = (double)pos.getY() + random.nextDouble();
                    f = (double)(pos.getZ() + 1) - random.nextDouble() * 0.10000000149011612;
                    world.addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurm(world.getBlockState(pos.above()), world, pos, Direction.DOWN)) {
                for(i = 0; i < 2; ++i) {
                    d = (double)pos.getX() + random.nextDouble();
                    e = (double)(pos.getY() + 1) - random.nextDouble() * 0.10000000149011612;
                    f = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for(i = 0; i < 3; ++i) {
                d = (double)pos.getX() + random.nextDouble();
                e = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
                f = (double)pos.getZ() + random.nextDouble();
                world.addParticle(SpectrumParticleTypes.PRIMORDIAL_SMOKE, d, e, f, 0.0, 0.0, 0.0);
            }
        }

    }

}
