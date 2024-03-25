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

public class SpawnerSpawnCountChangeRecipe extends SpawnerChangeRecipe {
	
	public static final RecipeSerializer<SpawnerSpawnCountChangeRecipe> SERIALIZER = new EmptyRecipeSerializer<>(SpawnerSpawnCountChangeRecipe::new);
	protected static final int DEFAULT_SPAWN_COUNT = 4;
	protected static final int MAX_SPAWN_COUNT = 16;
	public SpawnerSpawnCountChangeRecipe(ResourceLocation identifier) {
		super(identifier, IngredientStack.of(Ingredient.of(SpectrumItems.NEOLITH), Matchbook.empty(), null, 4));
	}
	
	@Override
	public boolean canCraftWithBlockEntityTag(CompoundTag spawnerBlockEntityNbt, ItemStack leftBowlStack, ItemStack rightBowlStack) {
		if (spawnerBlockEntityNbt.contains("SpawnCount")) {
			return spawnerBlockEntityNbt.getShort("SpawnCount") < MAX_SPAWN_COUNT;
		}
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
	@Override
	public Component getOutputLoreText() {
		return Component.translatable("recipe.spectrum.spawner.lore.increased_spawn_count");
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
		
		short spawnCount = DEFAULT_SPAWN_COUNT;
		if (spawnerBlockEntityNbt.contains("SpawnCount", Tag.TAG_SHORT)) {
			spawnCount = spawnerBlockEntityNbt.getShort("SpawnCount");
		}
		spawnerBlockEntityNbt.putShort("SpawnCount", (short) Math.min(MAX_SPAWN_COUNT, spawnCount + 1));
		
		return spawnerBlockEntityNbt;
	}
	
}
