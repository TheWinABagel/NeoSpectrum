package de.dafuqs.spectrum.blocks.idols;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestarterIdolBlock extends IdolBlock {
	
	// Block: The Block to burn
	// BlockState: The BlockState when Block is getting burnt
	// Float: The chance to burn
	public static final Map<Block, Tuple<BlockState, Float>> BURNING_MAP = new HashMap<>() {{
		put(Blocks.RED_MUSHROOM, new Tuple<>(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 0.2F));
		put(Blocks.BROWN_MUSHROOM, new Tuple<>(Blocks.WARPED_FUNGUS.defaultBlockState(), 0.2F));
		put(Blocks.SAND, new Tuple<>(Blocks.RED_SAND.defaultBlockState(), 1.0F));
		put(Blocks.GRASS, new Tuple<>(Blocks.DIRT.defaultBlockState(), 0.05F));
		put(Blocks.CALCITE, new Tuple<>(Blocks.BASALT.defaultBlockState(), 0.5F));
		put(Blocks.NETHERRACK, new Tuple<>(Blocks.MAGMA_BLOCK.defaultBlockState(), 0.25F));
		put(Blocks.MAGMA_BLOCK, new Tuple<>(Blocks.LAVA.defaultBlockState(), 0.5F));
		put(SpectrumBlocks.FROSTBITE_CRYSTAL, new Tuple<>(SpectrumBlocks.BLAZING_CRYSTAL.defaultBlockState(), 0.5F));
	}};
	
	public FirestarterIdolBlock(Properties settings, ParticleOptions particleEffect) {
		super(settings, particleEffect);
	}

	public static void addBlockSmeltingRecipes(@NotNull MinecraftServer server) {
		RegistryAccess manager = server.registryAccess();
		for (SmeltingRecipe recipe : server.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
			ItemStack outputStack = recipe.getResultItem(manager);
			if (outputStack.getItem() instanceof BlockItem outputBlockItem && outputBlockItem.getBlock() != Blocks.AIR) {
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				if (!ingredients.isEmpty()) {
					ItemStack[] inputStacks = ingredients.get(0).getItems();
					for (ItemStack inputStack : inputStacks) {
						if (inputStack.getItem() instanceof BlockItem inputBlockItem && inputBlockItem.getBlock() != Blocks.AIR) {
							BURNING_MAP.put(inputBlockItem.getBlock(), new Tuple<>(outputBlockItem.getBlock().defaultBlockState(), 1.0F));
						}
					}
				}
			}
		}
	}
	
	public static boolean causeFire(@NotNull ServerLevel world, BlockPos blockPos, Direction side) {
		BlockState blockState = world.getBlockState(blockPos);
		if (CampfireBlock.canLight(blockState) || CandleBlock.canLight(blockState) || CandleCakeBlock.canLight(blockState)) {
			// light lightable blocks
			world.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, true), 11);
			world.gameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
			return true;
		} else if (blockState.is(BlockTags.ICE)) {
			// smelt ice
			world.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
			return true;
		} else if (BURNING_MAP.containsKey(blockState.getBlock())) {
			Tuple<BlockState, Float> dest = BURNING_MAP.get(blockState.getBlock());
			if (dest.getB() >= 1.0F || world.random.nextFloat() < dest.getB()) {
				// convert netherrack to magma blocks
				world.setBlock(blockPos, dest.getA(), 11);
				world.gameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
			}
			return true;
		} else {
			// place fire
			if (BaseFireBlock.canBePlacedAt(world, blockPos, side)) {
				BlockState blockState2 = BaseFireBlock.getState(world, blockPos);
				world.setBlock(blockPos, blockState2, 11);
				world.gameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean trigger(ServerLevel world, BlockPos blockPos, BlockState state, @Nullable Entity entity, Direction side) {
		for (Direction direction : Direction.values()) {
			if (causeFire(world, blockPos.relative(direction), direction)) {
				world.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
			}
		}
		return true;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
		super.appendHoverText(stack, world, tooltip, options);
		tooltip.add(Component.translatable("block.spectrum.firestarter_idol.tooltip"));
	}
	
}
