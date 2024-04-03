package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class LiquidCrystalFluidBlock extends SpectrumFluidBlock {
	
	public static final int LUMINANCE = 11;
	
	public LiquidCrystalFluidBlock(FlowingFluid fluid, Properties settings) {
		super(fluid, settings);
	}
	
	@Override
	public SimpleParticleType getSplashParticle() {
		return SpectrumParticleTypes.LIQUID_CRYSTAL_FISHING;
	}
	
	@Override
	public Tuple<SimpleParticleType, SimpleParticleType> getFishingParticles() {
		return new Tuple<>(SpectrumParticleTypes.LIQUID_CRYSTAL_SPARKLE, SpectrumParticleTypes.LIQUID_CRYSTAL_FISHING);
	}
	
	@Override
	public RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType() {
		return SpectrumRecipeTypes.LIQUID_CRYSTAL_CONVERTING;
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.getFluid().getTickDelay(world));
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.getFluid().getTickDelay(world));
		}
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return true;
	}
	
	/**
	 * Entities colliding with liquid crystal will get a slight regeneration effect
	 */
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		
		if (!world.isClientSide && entity instanceof LivingEntity livingEntity) {
			// just check every x ticks for performance and slow healing
			if (world.getGameTime() % 200 == 0) {
				MobEffectInstance regenerationInstance = livingEntity.getEffect(MobEffects.REGENERATION);
				if (regenerationInstance == null) {
					MobEffectInstance newRegenerationInstance = new MobEffectInstance(MobEffects.REGENERATION, 80);
					livingEntity.addEffect(newRegenerationInstance);
				}
			}
		}
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		if (random.nextFloat() < 0.10F) {
			world.addParticle(SpectrumParticleTypes.LIQUID_CRYSTAL_SPARKLE, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0, random.nextDouble() * 0.1, 0);
		}
	}
	
	/**
	 * @param world The world
	 * @param pos   The position in the world
	 * @param state BlockState of the liquid crystal. Included the height/fluid level
	 * @return Dunno, actually. I just mod things.
	 */
	private boolean shouldSpreadLiquid(Level world, BlockPos pos, BlockState state) {
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.relative(direction);
			if (world.getFluidState(blockPos).is(FluidTags.WATER)) {
				Block block = world.getFluidState(pos).isSource() ? SpectrumBlocks.FROSTBITE_CRYSTAL : Blocks.CALCITE;
				world.setBlockAndUpdate(pos, block.defaultBlockState());
				this.fizz(world, pos);
				return false;
			}
			if (world.getFluidState(blockPos).is(FluidTags.LAVA)) {
				Block block;
				if (world.getFluidState(pos).isSource()) {
					block = SpectrumBlocks.BLAZING_CRYSTAL;
				} else {
					block = Blocks.COBBLED_DEEPSLATE;
				}
				world.setBlockAndUpdate(pos, block.defaultBlockState());
				this.fizz(world, pos);
				return false;
			}
			if (world.getFluidState(blockPos).is(SpectrumFluidTags.MUD)) {
				world.setBlockAndUpdate(pos, Blocks.CLAY.defaultBlockState());
				this.fizz(world, pos);
				return false;
			}
		}
		return true;
	}
	
	private void fizz(LevelAccessor world, BlockPos pos) {
		world.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
	}
	
}
