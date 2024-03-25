package de.dafuqs.spectrum.blocks.fluid;

import de.dafuqs.spectrum.api.block.MultiblockCrafter;
import de.dafuqs.spectrum.inventories.AutoCraftingInventory;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public abstract class SpectrumFluidBlock extends LiquidBlock {
	
	private static AutoCraftingInventory AUTO_INVENTORY;
	
	public SpectrumFluidBlock(FlowingFluid fluid, Properties settings) {
		super(fluid, settings);
	}
	
	public abstract SimpleParticleType getSplashParticle();
	
	public abstract Tuple<SimpleParticleType, SimpleParticleType> getFishingParticles();
	
	public abstract RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType();
	
	public <R extends FluidConvertingRecipe> R getConversionRecipeFor(RecipeType<R> recipeType, @NotNull Level world, ItemStack itemStack) {
		if (AUTO_INVENTORY == null) {
			AUTO_INVENTORY = new AutoCraftingInventory(1, 1);
		}
		AUTO_INVENTORY.setInputInventory(Collections.singletonList(itemStack));
		return world.getRecipeManager().getRecipeFor(recipeType, AUTO_INVENTORY, world).orElse(null);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		
		if (!world.isClientSide) {
			if (entity instanceof ItemEntity itemEntity && !itemEntity.hasPickUpDelay()) {
				if (world.random.nextInt(200) == 0) {
					ItemStack itemStack = itemEntity.getItem();
					FluidConvertingRecipe recipe = getConversionRecipeFor(getDippingRecipeType(), world, itemStack);
					if (recipe != null) {
						world.playSound(null, itemEntity.blockPosition(), SoundEvents.WOOL_BREAK, SoundSource.NEUTRAL, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);
						MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, itemEntity.position(), recipe.getResultItem(world.registryAccess()), recipe.getResultItem(world.registryAccess()).getCount() * itemStack.getCount(), Vec3.ZERO);
						itemEntity.discard();
					}
				}
			}
		}
	}
	
}
