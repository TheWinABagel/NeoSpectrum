package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.blocks.decay.BlackMateriaBlock;
import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntity;
import de.dafuqs.spectrum.helpers.SpectrumEnchantmentHelper;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MidnightSolutionFluidBlock extends SpectrumFluidBlock {

	public static final BlockState SPREAD_BLOCKSTATE = SpectrumBlocks.BLACK_MATERIA.defaultBlockState().setValue(BlackMateriaBlock.AGE, 0);
	private static final int EXPERIENCE_DISENCHANT_RETURN_DIV = 3;

	public MidnightSolutionFluidBlock(FlowingFluid fluid, Properties settings) {
		super(fluid, settings);
	}

	@Override
	public SimpleParticleType getSplashParticle() {
		return SpectrumParticleTypes.MIDNIGHT_SOLUTION_SPLASH;
	}

	@Override
	public Tuple<SimpleParticleType, SimpleParticleType> getFishingParticles() {
		return new Tuple<>(SpectrumParticleTypes.GRAY_SPARKLE_RISING, SpectrumParticleTypes.MIDNIGHT_SOLUTION_FISHING);
	}

	@Override
	public RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType() {
		return SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING;
	}

	public static boolean tryConvertNeighbor(@NotNull Level world, BlockPos pos, BlockPos fromPos) {
		FluidState fluidState = world.getFluidState(fromPos);
		if (!fluidState.isEmpty() && fluidState.is(SpectrumFluidTags.MIDNIGHT_SOLUTION_CONVERTED)) {
			world.setBlockAndUpdate(fromPos, SpectrumBlocks.MIDNIGHT_SOLUTION.defaultBlockState());
			fizz(world, fromPos);
			return true;
		}
		return false;
	}

	public static void fizz(@NotNull LevelAccessor world, BlockPos pos) {
		world.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(world));
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (this.shouldSpreadLiquid(world, pos, state)) {
			world.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(world));
		}
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);

		if (!world.isClientSide) {
			if (entity instanceof LivingEntity livingEntity) {
				if (!livingEntity.isDeadOrDying() && world.getGameTime() % 20 == 0) {
					if (livingEntity.isEyeInFluid(SpectrumFluidTags.MIDNIGHT_SOLUTION)) {
						livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0));
						livingEntity.hurt(SpectrumDamageTypes.midnightSolution(world), 2);
					} else {
						livingEntity.hurt(SpectrumDamageTypes.midnightSolution(world), 1);
					}
					if (livingEntity.isDeadOrDying()) {
						livingEntity.spawnAtLocation(SpectrumItems.MIDNIGHT_CHIP.getDefaultInstance());
					}
				}
			} else if (entity instanceof ItemEntity itemEntity && !itemEntity.hasPickUpDelay()) {
				if (world.random.nextInt(120) == 0) {
					disenchantItemAndSpawnXP(world, itemEntity);
				}
			}
		}
	}

	private static void disenchantItemAndSpawnXP(Level world, ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getItem();
		// if the item is enchanted: remove enchantments and spawn XP
		// basically disenchanting the item
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
		if (!enchantments.isEmpty()) {
			int randomEnchantmentIndex = world.random.nextInt(enchantments.size());
			Enchantment enchantmentToRemove = (Enchantment) enchantments.keySet().toArray()[randomEnchantmentIndex];
			Tuple<ItemStack, Integer> result = SpectrumEnchantmentHelper.removeEnchantments(itemStack, enchantmentToRemove);

			if(result.getB() > 0) {
				int experience = EnchanterBlockEntity.getEnchantingPrice(itemStack, enchantmentToRemove, enchantments.get(enchantmentToRemove));
				experience /= EXPERIENCE_DISENCHANT_RETURN_DIV;
				if (experience > 0) {
					ExperienceOrb experienceOrbEntity = new ExperienceOrb(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), experience);
					world.addFreshEntity(experienceOrbEntity);
				}

				world.playSound(null, itemEntity.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.NEUTRAL, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);
				SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world, itemEntity.position(), SpectrumParticleTypes.GRAY_SPARKLE_RISING, 10, Vec3.ZERO, new Vec3(0.2, 0.4, 0.2));

				itemEntity.setItem(result.getA());
				itemEntity.setDefaultPickUpDelay();
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);
		if (!world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above()) && random.nextFloat() < 0.03F) {
			world.addParticle(SpectrumParticleTypes.VOID_FOG, pos.getX() + random.nextDouble(), pos.getY() + 1, pos.getZ() + random.nextDouble(), 0, random.nextDouble() * 0.1, 0);
		}
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}

	/**
	 * @param world The world
	 * @param pos   The position in the world
	 * @param state BlockState of the midnight solution. Included the height/fluid level
	 * @return Dunno, actually. I just mod things.
	 */
	private boolean shouldSpreadLiquid(Level world, BlockPos pos, BlockState state) {
		for (Direction direction : Direction.values()) {
			BlockPos neighborPos = pos.relative(direction);
			FluidState neighborFluidState = world.getFluidState(neighborPos);
			if (neighborFluidState.is(FluidTags.LAVA)) {
				world.setBlockAndUpdate(pos, Blocks.TERRACOTTA.defaultBlockState());
				fizz(world, pos);
				return false;
			}

			boolean isNeighborFluidBlock = world.getBlockState(neighborPos).getBlock() instanceof LiquidBlock;
			// spread to the fluid
			boolean doesTickEntities = world.getChunkAt(pos).getFullStatus().isOrAfter(FullChunkStatus.ENTITY_TICKING);
			if (!neighborFluidState.isEmpty() && doesTickEntities) {
				if (!isNeighborFluidBlock) {
					world.setBlockAndUpdate(pos, SPREAD_BLOCKSTATE);
					fizz(world, pos);
				} else {
					if (!neighborFluidState.is(this.fluid) && !neighborFluidState.is(SpectrumFluidTags.MIDNIGHT_SOLUTION_CONVERTED) && !world.getBlockState(neighborPos).is(this)) {
						world.setBlockAndUpdate(pos, SPREAD_BLOCKSTATE);
						fizz(world, neighborPos);
					}
				}
			}
		}
		return true;
	}

}
