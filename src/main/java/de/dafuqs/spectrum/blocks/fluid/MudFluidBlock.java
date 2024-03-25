package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
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

public class MudFluidBlock extends SpectrumFluidBlock {
	
	public MudFluidBlock(FlowingFluid fluid, Properties settings) {
		super(fluid, settings);
	}
	
	@Override
	public SimpleParticleType getSplashParticle() {
		return SpectrumParticleTypes.MUD_SPLASH;
	}
	
	@Override
	public Tuple<SimpleParticleType, SimpleParticleType> getFishingParticles() {
		return new Tuple<>(SpectrumParticleTypes.MUD_POP, SpectrumParticleTypes.MUD_FISHING);
	}
	
	@Override
	public RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType() {
		return SpectrumRecipeTypes.MUD_CONVERTING;
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(world));
		}
	}
	
	/**
	 * Entities colliding with mud will get a slowness effect
	 * and losing their breath far quicker
	 */
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		
		if (!world.isClientSide && entity instanceof LivingEntity livingEntity) {
			// the entity is hurt at air == -20 and then reset to air = 0
			// this way the entity loses its breath way faster, but gets damaged just as slow afterwards
			if (livingEntity.isEyeInFluid(SpectrumFluidTags.MUD) && world.getGameTime() % 2 == 0 && livingEntity.getAirSupply() > 0) {
				livingEntity.setAirSupply(livingEntity.getAirSupply() - 1);
			}
			
			// just check every 20 ticks for performance
			if (world.getGameTime() % 20 == 0) {
				MobEffectInstance slownessInstance = livingEntity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
				if (slownessInstance == null || slownessInstance.getDuration() < 20) {
					MobEffectInstance newSlownessInstance = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3);
					livingEntity.addEffect(newSlownessInstance);
				}
			}
		}
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		if (!world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above()) && random.nextFloat() < 0.03F) {
			world.addParticle(SpectrumParticleTypes.MUD_POP, pos.getX() + random.nextDouble(), pos.getY() + 1, pos.getZ() + random.nextDouble(), 0, random.nextDouble() * 0.1, 0);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(world));
		}
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return true;
	}
	
	/**
	 * @param world The world
	 * @param pos   The position in the world
	 * @param state BlockState of the mud. Included the height/fluid level
	 * @return Dunno, actually. I just mod things.
	 */
	private boolean shouldSpreadLiquid(Level world, BlockPos pos, BlockState state) {
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.relative(direction);
			if (world.getFluidState(blockPos).is(FluidTags.WATER)) {
				world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
				this.fizz(world, pos);
				return false;
			}
			if (world.getFluidState(blockPos).is(FluidTags.LAVA)) {
				world.setBlockAndUpdate(pos, Blocks.COARSE_DIRT.defaultBlockState());
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
