package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.api.block.ExplosionAware;
import de.dafuqs.spectrum.blocks.PlacedItemBlock;
import de.dafuqs.spectrum.blocks.PlacedItemBlockEntity;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;
import net.minecraft.sound.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.util.RandomSource;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.explosion.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class IncandescentAmalgamBlock extends PlacedItemBlock implements SimpleWaterloggedBlock, ExplosionAware {
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
	
	public IncandescentAmalgamBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(@NotNull BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(@NotNull BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		if (!state.getValue(WATERLOGGED)) {
			int r = random.nextInt(50);
			if (r < 10) {
				double posX = (double) pos.getX() + 0.25D + random.nextDouble() * 0.5D;
				double posY = (double) pos.getY() + random.nextDouble() * 0.5D;
				double posZ = (double) pos.getZ() + 0.25D + random.nextDouble() * 0.5D;
				world.addParticle(ParticleTypes.LAVA, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
				if (r == 0) {
					world.playLocalSound(posX, posY, posZ, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
				}
			}
			if (random.nextInt(100) == 0) {
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		}
	}
	
	@Override
	public boolean dropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(world, pos, state, entity);
		if (!state.getValue(WATERLOGGED)) {
			explode(world, pos);
		}
	}
	
	@Override
	public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		super.fallOn(world, state, pos, entity, fallDistance);
		if (!state.getValue(WATERLOGGED)) {
			explode(world, pos);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
		super.onProjectileHit(world, state, hit, projectile);
		if (!state.getValue(WATERLOGGED)) {
			explode(world, hit.getBlockPos());
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborChanged(state, world, pos, block, fromPos, notify);
		if (!state.getValue(WATERLOGGED) && world.random.nextInt(10) == 0) {
			explode(world, pos);
		}
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		if (!state.getValue(WATERLOGGED)
				|| player.isCreative()
				|| EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.RESONANCE, player.getItemInHand(player.getUsedItemHand())) == 0) {

			explode(world, pos);
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	protected static void explode(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			if (world.getBlockEntity(pos) instanceof PlacedItemBlockEntity placedItemBlockEntity) {
				Player owner = placedItemBlockEntity.getOwnerIfOnline();
				ItemStack stack = placedItemBlockEntity.getStack();

				world.removeBlock(pos, false);
				explode(world, pos, owner, stack);
			}
		}
	}

	public static void explode(Level world, BlockPos pos, Entity owner, ItemStack stack) {
		float power = 8.0F;
		if (stack.getItem() instanceof IncandescentAmalgamItem item) {
			power = item.getExplosionPower(stack, false);
		}
		world.explode(owner, SpectrumDamageTypes.incandescence(world, owner), new ExplosionDamageCalculator(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, power, true, Level.ExplosionInteraction.BLOCK);
	}

	@Override
	public void beforeDestroyedByExplosion(Level world, BlockPos pos, BlockState state, Explosion explosion) {
		explode(world, pos);
	}

}
