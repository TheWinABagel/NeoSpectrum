package de.dafuqs.spectrum.recipe.spirit_instiller.spawner;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockItem;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.registries.SpectrumEntityTypeTags;
import de.dafuqs.spectrum.registries.SpectrumItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class SpawnerCreatureChangeRecipe extends SpawnerChangeRecipe {
	
	public static final RecipeSerializer<SpawnerCreatureChangeRecipe> SERIALIZER = new EmptyRecipeSerializer<>(SpawnerCreatureChangeRecipe::new);
	
	public SpawnerCreatureChangeRecipe(ResourceLocation identifier) {
		super(identifier, IngredientStack.of(Ingredient.of(SpectrumItemTags.SKULLS)));
	}
	
	@Override
	public boolean canCraftWithBlockEntityTag(CompoundTag spawnerBlockEntityNbt, ItemStack firstBowlStack, ItemStack secondBowlStack) {
		Optional<EntityType<?>> entityType = SpectrumSkullBlockItem.getEntityTypeOfSkullStack(firstBowlStack);
		entityType = entityType.isEmpty() ? SpectrumSkullBlockItem.getEntityTypeOfSkullStack(secondBowlStack) : entityType;

		if (entityType.isEmpty()) {
			return false;
		}
		if (entityType.get().is(SpectrumEntityTypeTags.SPAWNER_MANIPULATION_BLACKLISTED)) {
			return false;
		}
		
		if (spawnerBlockEntityNbt.contains("SpawnData")) {
			CompoundTag spawnData = spawnerBlockEntityNbt.getCompound("SpawnData");
			if (spawnData.contains("entity")) {
				CompoundTag entity = spawnData.getCompound("entity");
				if (entity.contains("id")) {
					ResourceLocation entityTypeIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey(entityType.get());
					return !entityTypeIdentifier.toString().equals(entity.getString("id"));
				}
			}
		}
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
	@Override
	public Component getOutputLoreText() {
		return Component.translatable("recipe.spectrum.spawner.lore.changed_creature");
	}
	
	@Override
	public CompoundTag getSpawnerResultNbt(CompoundTag spawnerBlockEntityNbt, ItemStack firstBowlStack, ItemStack secondBowlStack) {
		Optional<EntityType<?>> entityType = SpectrumSkullBlockItem.getEntityTypeOfSkullStack(firstBowlStack);
		entityType = entityType.isEmpty() ? SpectrumSkullBlockItem.getEntityTypeOfSkullStack(secondBowlStack) : entityType;

		if (entityType.isEmpty()) {
			return spawnerBlockEntityNbt;
		}
		
		ResourceLocation entityTypeIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey(entityType.get());
		
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
		
		CompoundTag idCompound = new CompoundTag();
		idCompound.putString("id", entityTypeIdentifier.toString());
		CompoundTag entityCompound = new CompoundTag();
		entityCompound.put("entity", idCompound);
		spawnerBlockEntityNbt.put("SpawnData", entityCompound);
		
		if (spawnerBlockEntityNbt.contains("SpawnPotentials")) {
			spawnerBlockEntityNbt.remove("SpawnPotentials");
		}
		
		return spawnerBlockEntityNbt;
	}
	
}
