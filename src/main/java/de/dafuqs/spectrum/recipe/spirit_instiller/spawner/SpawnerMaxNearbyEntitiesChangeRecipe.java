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

public class SpawnerMaxNearbyEntitiesChangeRecipe extends SpawnerChangeRecipe {
	
	public static final RecipeSerializer<SpawnerMaxNearbyEntitiesChangeRecipe> SERIALIZER = new EmptyRecipeSerializer<>(SpawnerMaxNearbyEntitiesChangeRecipe::new);
	protected static final int DEFAULT_MAX_ENTITIES = 6;
	protected static final int MAX_MAX_ENTITIES = 40;
	public SpawnerMaxNearbyEntitiesChangeRecipe(ResourceLocation identifier) {
		super(identifier, IngredientStack.of(Ingredient.of(SpectrumItems.MERMAIDS_GEM), Matchbook.empty(), null, 4));
	}
	
	@Override
	public boolean canCraftWithBlockEntityTag(CompoundTag spawnerBlockEntityNbt, ItemStack leftBowlStack, ItemStack rightBowlStack) {
		if (spawnerBlockEntityNbt.contains("MaxNearbyEntities")) {
			return spawnerBlockEntityNbt.getShort("MaxNearbyEntities") < MAX_MAX_ENTITIES;
		}
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
	@Override
	public Component getOutputLoreText() {
		return Component.translatable("recipe.spectrum.spawner.lore.increased_max_nearby_entities");
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
		
		short maxNearbyEntities = DEFAULT_MAX_ENTITIES;
		if (spawnerBlockEntityNbt.contains("MaxNearbyEntities", Tag.TAG_SHORT)) {
			maxNearbyEntities = spawnerBlockEntityNbt.getShort("MaxNearbyEntities");
		}
		spawnerBlockEntityNbt.putShort("MaxNearbyEntities", (short) Math.min(MAX_MAX_ENTITIES, maxNearbyEntities + 1));
		
		return spawnerBlockEntityNbt;
	}
	
}
