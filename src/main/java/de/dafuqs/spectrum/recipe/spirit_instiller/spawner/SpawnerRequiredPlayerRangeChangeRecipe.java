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

public class SpawnerRequiredPlayerRangeChangeRecipe extends SpawnerChangeRecipe {
	
	public static final RecipeSerializer<SpawnerRequiredPlayerRangeChangeRecipe> SERIALIZER = new EmptyRecipeSerializer<>(SpawnerRequiredPlayerRangeChangeRecipe::new);
	protected static final int DEFAULT_DETECTION_RANGE = 16;
	protected static final int MAX_DETECTION_RANGE = 64;
	public SpawnerRequiredPlayerRangeChangeRecipe(ResourceLocation identifier) {
		super(identifier, IngredientStack.of(Ingredient.of(SpectrumItems.STRATINE_GEM), Matchbook.empty(), null, 4));
	}
	
	@Override
	public boolean canCraftWithBlockEntityTag(CompoundTag spawnerBlockEntityNbt, ItemStack leftBowlStack, ItemStack rightBowlStack) {
		if (spawnerBlockEntityNbt.contains("RequiredPlayerRange")) {
			return spawnerBlockEntityNbt.getShort("RequiredPlayerRange") < MAX_DETECTION_RANGE;
		}
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
	@Override
	public Component getOutputLoreText() {
		return Component.translatable("recipe.spectrum.spawner.lore.increased_required_player_range");
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
		
		short requiredPlayerRange = DEFAULT_DETECTION_RANGE;
		if (spawnerBlockEntityNbt.contains("RequiredPlayerRange", Tag.TAG_SHORT)) {
			requiredPlayerRange = spawnerBlockEntityNbt.getShort("RequiredPlayerRange");
		}
		
		short newRequiredPlayerRange = (short) Math.pow(requiredPlayerRange, 1.02);
		if (newRequiredPlayerRange == requiredPlayerRange) {
			newRequiredPlayerRange = (short) (requiredPlayerRange + 1);
		}
		
		spawnerBlockEntityNbt.putShort("RequiredPlayerRange", (short) Math.min(MAX_DETECTION_RANGE, newRequiredPlayerRange));
		
		return spawnerBlockEntityNbt;
	}
	
}
