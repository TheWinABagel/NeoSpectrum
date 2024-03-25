package de.dafuqs.spectrum.recipe.spirit_instiller.spawner;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.matchbook.Matchbook;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SpawnerSpawnDelayChangeRecipe extends SpawnerChangeRecipe {
	
	public static final RecipeSerializer<SpawnerSpawnDelayChangeRecipe> SERIALIZER = new EmptyRecipeSerializer<>(SpawnerSpawnDelayChangeRecipe::new);
	protected static final int DEFAULT_MIN_DELAY = 200;
	protected static final int DEFAULT_MAX_DELAY = 800;
	protected static final int MIN_DELAY = 20;
	public SpawnerSpawnDelayChangeRecipe(ResourceLocation identifier) {
		super(identifier, IngredientStack.of(Ingredient.of(SpectrumItems.MIDNIGHT_CHIP), Matchbook.empty(), null, 4));
	}
	
	@Override
	public boolean canCraftWithBlockEntityTag(CompoundTag spawnerBlockEntityNbt, ItemStack leftBowlStack, ItemStack rightBowlStack) {
		if (spawnerBlockEntityNbt.contains("MinSpawnDelay") && spawnerBlockEntityNbt.contains("MaxSpawnDelay")) {
			return spawnerBlockEntityNbt.getShort("MinSpawnDelay") > MIN_DELAY
					&& spawnerBlockEntityNbt.getShort("MaxSpawnDelay") > MIN_DELAY;
		}
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
	@Override
	public Component getOutputLoreText() {
		return Component.translatable("recipe.spectrum.spawner.lore.decreased_spawn_delay");
	}
	
	@Override
	public CompoundTag getSpawnerResultNbt(CompoundTag spawnerBlockEntityNbt, ItemStack firstBowlStack, ItemStack secondBowlStack) {
		// Default spawner tag:
		/* BlockEntityTag: {
			MaxNearbyEntities: 6s,
			RequiredPlayerRange: 16s,
			SpawnCount: 4s,
			SpawnData: {entity: {id: "minecraft:xxx"}},
			MaxSpawnDelay: 800s,
			SpawnRange: 4s,
			MinSpawnDelay: 200s,
			SpawnPotentials: []
		   }
		 */
		
		// 800 => 700 => 614 => 540 => 476 => 421 => 373 => 331 => ... (down to a min of 1 each)
		// makes 40 recipes to match the min count for MaxSpawnDelay of 20 ticks
		short minSpawnDelay = DEFAULT_MIN_DELAY;
		if (spawnerBlockEntityNbt.contains("MinSpawnDelay", Tag.TAG_SHORT)) {
			minSpawnDelay = spawnerBlockEntityNbt.getShort("MinSpawnDelay");
		}
		short maxSpawnDelay = DEFAULT_MAX_DELAY;
		if (spawnerBlockEntityNbt.contains("MaxSpawnDelay", Tag.TAG_SHORT)) {
			maxSpawnDelay = spawnerBlockEntityNbt.getShort("MaxSpawnDelay");
		}
		
		short newMinSpawnDelay = (short) Math.pow(minSpawnDelay, 0.98);
		if (newMinSpawnDelay == minSpawnDelay) {
			newMinSpawnDelay = (short) (minSpawnDelay - 1);
		}
		
		short newMaxSpawnDelay = (short) Math.pow(maxSpawnDelay, 0.98);
		if (newMaxSpawnDelay == maxSpawnDelay) {
			newMaxSpawnDelay = (short) (maxSpawnDelay - 1);
		}
		
		spawnerBlockEntityNbt.putShort("MinSpawnDelay", (short) Math.max(MIN_DELAY, newMinSpawnDelay));
		spawnerBlockEntityNbt.putShort("MaxSpawnDelay", (short) Math.max(MIN_DELAY, newMaxSpawnDelay));
		
		return spawnerBlockEntityNbt;
	}
	
}
