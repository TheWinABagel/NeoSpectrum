package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
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

public class DragonrotFluidBlock extends SpectrumFluidBlock {

	public DragonrotFluidBlock(FlowingFluid fluid, Properties settings) {
		super(fluid, settings);
	}

	@Override
	public SimpleParticleType getSplashParticle() {
		return SpectrumParticleTypes.DRAGONROT;
	}

	@Override
	public Tuple<SimpleParticleType, SimpleParticleType> getFishingParticles() {
		return new Tuple<>(SpectrumParticleTypes.DRAGONROT, SpectrumParticleTypes.DRAGONROT_FISHING);
	}

	@Override
	public RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType() {
		return SpectrumRecipeTypes.DRAGONROT_CONVERTING;
	}
	
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(world));
		}
	}
	
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		
		if (!world.isClientSide && entity instanceof LivingEntity livingEntity) {
			// just check every 20 ticks for performance
			if (!livingEntity.isDeadOrDying() && world.getGameTime() % 20 == 0 && !(livingEntity instanceof Enemy)) {
				if (livingEntity.isEyeInFluid(SpectrumFluidTags.DRAGONROT)) {
					livingEntity.hurt(SpectrumDamageTypes.dragonrot(world), 6);
				} else {
					livingEntity.hurt(SpectrumDamageTypes.dragonrot(world), 3);
				}
				if (!livingEntity.isDeadOrDying()) {
					MobEffectInstance existingEffect = livingEntity.getEffect(SpectrumStatusEffects.LIFE_DRAIN);
					if (existingEffect == null || existingEffect.getDuration() < 1000) {
						livingEntity.addEffect(new MobEffectInstance(SpectrumStatusEffects.LIFE_DRAIN, 2000, 0));
					}
					existingEffect = livingEntity.getEffect(SpectrumStatusEffects.DEADLY_POISON);
					if (existingEffect == null || existingEffect.getDuration() < 80) {
						livingEntity.addEffect(new MobEffectInstance(SpectrumStatusEffects.DEADLY_POISON, 160, 0));
					}
				}
			}
		}
	}
	
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		if (!world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above()) && random.nextFloat() < 0.03F) {
			world.addParticle(SpectrumParticleTypes.DRAGONROT, pos.getX() + random.nextDouble(), pos.getY() + 1, pos.getZ() + random.nextDouble(), 0, random.nextDouble() * 0.1, 0);
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
		return false;
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
				world.setBlockAndUpdate(pos, SpectrumBlocks.SLUSH.defaultBlockState());
				this.fizz(world, pos);
				return false;
			} else if (world.getFluidState(blockPos).is(FluidTags.LAVA)) {
				world.setBlockAndUpdate(pos, Blocks.BLACKSTONE.defaultBlockState());
				this.fizz(world, pos);
				return false;
			} else if (world.getFluidState(blockPos).is(SpectrumFluidTags.MUD)) {
				world.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
				this.fizz(world, pos);
				return false;
			} else if (world.getFluidState(blockPos).is(SpectrumFluidTags.LIQUID_CRYSTAL)) {
				world.setBlockAndUpdate(pos, SpectrumBlocks.ROTTEN_GROUND.defaultBlockState());
				this.fizz(world, pos);
				return false;
			} else if (world.getFluidState(blockPos).is(SpectrumFluidTags.MIDNIGHT_SOLUTION)) {
				world.setBlockAndUpdate(pos, SpectrumBlocks.BLACK_SLUDGE.defaultBlockState());
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
