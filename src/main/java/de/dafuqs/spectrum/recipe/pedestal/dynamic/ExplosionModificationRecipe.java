package de.dafuqs.spectrum.recipe.pedestal.dynamic;

import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.ModularExplosionProvider;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntity;
import de.dafuqs.spectrum.explosion.ExplosionArchetype;
import de.dafuqs.spectrum.explosion.ExplosionModifier;
import de.dafuqs.spectrum.explosion.ExplosionModifierProviders;
import de.dafuqs.spectrum.explosion.ModularExplosionDefinition;
import de.dafuqs.spectrum.recipe.EmptyRecipeSerializer;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.recipe.pedestal.ShapelessPedestalRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// this hurt to write
public class ExplosionModificationRecipe extends ShapelessPedestalRecipe {
	
	public static final RecipeSerializer<ExplosionModificationRecipe> SERIALIZER = new EmptyRecipeSerializer<>(ExplosionModificationRecipe::new);
	public static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("unlocks/blocks/modular_explosives");
	
	public ExplosionModificationRecipe(ResourceLocation id) {
		super(id, "", false, UNLOCK_IDENTIFIER, PedestalRecipeTier.BASIC, collectIngredients(), Map.of(), ItemStack.EMPTY, 0.0F, 40, false, true);
	}
	
	private static List<IngredientStack> collectIngredients() {
		List<ItemLike> providers = new ArrayList<>();
		BuiltInRegistries.ITEM.stream().filter(item -> item instanceof ModularExplosionProvider).forEach(providers::add);
		IngredientStack providerIngredient = IngredientStack.of(Ingredient.of(providers.toArray(new ItemLike[]{})));
		
		Set<Item> modifiers = ExplosionModifierProviders.getProviders();
		IngredientStack modifierIngredient = IngredientStack.of(Ingredient.of(modifiers.toArray(new ItemLike[]{})));
		
		return List.of(providerIngredient, modifierIngredient);
	}
	
	@Override
	public boolean matches(Container inventory, Level world) {
		ItemStack nonModStack = validateGridAndFindModularExplosiveStack(inventory);
		if (!(nonModStack.getItem() instanceof ModularExplosionProvider modularExplosionProvider)) {
			return false;
		}
		
		Tuple<List<ExplosionArchetype>, List<ExplosionModifier>> pair = findArchetypeAndModifiers(inventory);
		ModularExplosionDefinition currentSet = ModularExplosionDefinition.getFromStack(nonModStack);
		List<ExplosionArchetype> archetypes = pair.getA();
		List<ExplosionModifier> mods = pair.getB();

		// if there are no new modifiers to add present, treat it
		// as a recipe to clear existing archetype and / or modifiers
		if (archetypes.isEmpty() && mods.isEmpty()) {
			return currentSet.getArchetype() != ExplosionArchetype.COSMETIC || currentSet.getModifierCount() > 0;
		}
		
		if (!archetypes.isEmpty()) {
			@Nullable ExplosionArchetype newArchetype = calculateExplosionArchetype(currentSet.getArchetype(), archetypes);
			if (newArchetype == null) {
				return false;
			}
			currentSet.setArchetype(newArchetype);
		}
		
		currentSet.addModifiers(mods);
		return currentSet.isValid(modularExplosionProvider);
	}
	
	/**
	 * Returns null if the combination of archetypes would result in something nonsensical
	 */
	private static @Nullable ExplosionArchetype calculateExplosionArchetype(ExplosionArchetype existingArchetype, List<ExplosionArchetype> newArchetypes) {
		ExplosionArchetype newArchetype = existingArchetype;
		int newArchetypesCount = newArchetypes.size();
		if (existingArchetype == ExplosionArchetype.ALL && newArchetypesCount > 0) {
			return null;
		}
		if (newArchetypes.contains(ExplosionArchetype.ALL) && newArchetypesCount > 1) {
			return null;
		}
		
		for (ExplosionArchetype archetype : newArchetypes) {
			if (newArchetype == ExplosionArchetype.ALL) {
				return null;
			}
			newArchetype = ExplosionArchetype.get(newArchetype.affectsBlocks || archetype.affectsBlocks, newArchetype.affectsEntities || archetype.affectsEntities);
		}
		return newArchetype;
	}
	
	@Override
	public ItemStack assemble(Container inventory, RegistryAccess drm) {
		ItemStack output = validateGridAndFindModularExplosiveStack(inventory).copy();
		
		Tuple<List<ExplosionArchetype>, List<ExplosionModifier>> pair = findArchetypeAndModifiers(inventory);
		List<ExplosionArchetype> archetypes = pair.getA();
		List<ExplosionModifier> mods = pair.getB();
		
		if (archetypes.isEmpty() && mods.isEmpty()) { // clearing existing modifiers
			ModularExplosionDefinition.removeFromStack(output);
			return output;
		}
		
		ModularExplosionDefinition set = ModularExplosionDefinition.getFromStack(output);
		
		// adding new modifiers
		if (!archetypes.isEmpty()) {
			ExplosionArchetype newArchetype = calculateExplosionArchetype(set.getArchetype(), pair.getA());
			if (newArchetype != null) { // should never happen, but better safe than sorry
				set.setArchetype(newArchetype);
			}
		}
		
		set.addModifiers(mods);
		set.attachToStack(output);
		
		return output;
	}
	
	@Override
	public void consumeIngredients(PedestalBlockEntity pedestal) {
		for (int slot : CRAFTING_GRID_SLOTS) {
			ItemStack slotStack = pedestal.getItem(slot);
			if (slotStack.getItem() instanceof ModularExplosionProvider) {
				pedestal.setItem(slot, ItemStack.EMPTY);
			} else {
				slotStack.shrink(1);
			}
		}
	}
	
	/**
	 * Iterates all stacks in the grid and returns the modular explosive
	 * if the grid only contains that one and modifiers
	 */
	public ItemStack validateGridAndFindModularExplosiveStack(Container inventory) {
		ItemStack foundStack = ItemStack.EMPTY;
		for (int slot : CRAFTING_GRID_SLOTS) {
			ItemStack stack = inventory.getItem(slot);
			if (!stack.isEmpty()
					&& stack.getItem() instanceof ModularExplosionProvider
					&& ExplosionModifierProviders.getModifier(stack) == null
					&& ExplosionModifierProviders.getArchetype(stack) == null) {
				
				if(foundStack == ItemStack.EMPTY) {
					foundStack = stack;
				} else {
					return ItemStack.EMPTY; // multiple non-mod stacks found
				}
			}
		}
		
		return foundStack;
	}
	
	public Tuple<List<ExplosionArchetype>, List<ExplosionModifier>> findArchetypeAndModifiers(Container inventory) {
		List<ExplosionModifier> modifiers = new ArrayList<>();
		List<ExplosionArchetype> archetypes = new ArrayList<>();
		for (int slot : CRAFTING_GRID_SLOTS) {
			ItemStack stack = inventory.getItem(slot);
			if (!stack.isEmpty()) {
				ExplosionModifier modifier = ExplosionModifierProviders.getModifier(stack);
				if (modifier != null) {
					modifiers.add(modifier);
					continue;
				}
				ExplosionArchetype archetype = ExplosionModifierProviders.getArchetype(stack);
				if (archetype != null) {
					archetypes.add(archetype);
				}
			}
		}
		return new Tuple<>(archetypes, modifiers);
	}
	
}
