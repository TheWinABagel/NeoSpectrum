package de.dafuqs.spectrum.recipe.primordial_fire_burning.dynamic;

import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.primordial_fire_burning.PrimordialFireBurningRecipe;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MemoryDementiaRecipe extends PrimordialFireBurningRecipe {
	
	public static final RecipeSerializer<MemoryDementiaRecipe> SERIALIZER = new EmptyRecipeSerializer<>(MemoryDementiaRecipe::new);
	
	public MemoryDementiaRecipe(ResourceLocation identifier) {
		super(identifier, "", false, UNLOCK_IDENTIFIER,
				Ingredient.of(MemoryItem.getForEntityType(EntityType.BEE, 1), MemoryItem.getForEntityType(EntityType.FOX, 10), MemoryItem.getForEntityType(EntityType.SKELETON, 5), MemoryItem.getForEntityType(EntityType.HUSK, 50), MemoryItem.getForEntityType(EntityType.BLAZE, -1)),
				SpectrumBlocks.MEMORY.asItem().getDefaultInstance());
	}
	
	@Override
	public boolean matches(Container inv, Level world) {
		return MemoryItem.getEntityType(inv.getItem(0).getTag()).isPresent();
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
	
}
