package de.dafuqs.spectrum.api.block;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface MultiblockCrafter extends Upgradeable, PlayerOwned {
	
	Vec3 RECIPE_STACK_VELOCITY = new Vec3(0.0, 0.3, 0.0);
	
	static @Nullable Recipe<?> getRecipeFromId(@Nullable Level world, ResourceLocation recipeIdentifier) {
		if (world != null) {
			return world.getRecipeManager().byKey(recipeIdentifier).orElse(null);
		}
		if (SpectrumCommon.minecraftServer != null) {
			return SpectrumCommon.minecraftServer.getRecipeManager().byKey(recipeIdentifier).orElse(null);
		}
		return null;
	}
	
	static void spawnExperience(Level world, BlockPos blockPos, float amount, RandomSource random) {
		spawnExperience(world, blockPos, Support.getIntFromDecimalWithChance(amount, random));
	}
	
	static void spawnExperience(Level world, BlockPos blockPos, int amount) {
		if (amount > 0) {
			ExperienceOrb experienceOrbEntity = new ExperienceOrb(world, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, amount);
			world.addFreshEntity(experienceOrbEntity);
		}
	}
	
	static void spawnItemStackAsEntitySplitViaMaxCount(Level world, BlockPos blockPos, ItemStack itemStack, int amount, Vec3 velocity) {
		spawnItemStackAsEntitySplitViaMaxCount(world, Vec3.atCenterOf(blockPos), itemStack, amount, velocity);
	}
	
	static void spawnItemStackAsEntitySplitViaMaxCount(Level world, Vec3 pos, ItemStack itemStack, int amount, Vec3 velocity) {
		while (amount > 0) {
			int currentAmount = Math.min(amount, itemStack.getMaxStackSize());
			
			ItemStack resultStack = itemStack.copy();
			resultStack.setCount(currentAmount);
			ItemEntity itemEntity = new ItemEntity(world, pos.x(), pos.y(), pos.z(), resultStack);
			itemEntity.setDeltaMovement(velocity);
			itemEntity.setUnlimitedLifetime();
			world.addFreshEntity(itemEntity);
			
			amount -= currentAmount;
		}
	}
	
	static void spawnOutputAsItemEntity(Level world, BlockPos pos, ItemStack outputItemStack) {
		ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, outputItemStack);
		itemEntity.push(0, 0.1, 0);
		world.addFreshEntity(itemEntity);
	}
	
}
