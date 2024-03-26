package de.dafuqs.spectrum.mixin.accessors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
	
	@Invoker(value = "dissolvePattern")
	static NonNullList<Ingredient> invokeCreatePatternMatrix(String[] pattern, Map<String, Ingredient> key, int width, int height) {
		throw new AssertionError();
	}
	
	@Invoker(value = "shrink")
	static String[] invokeRemovePadding(String... lines) {
		throw new AssertionError();
	}
	
	@Invoker(value = "patternFromJson")
	static String[] invokeGetPattern(JsonArray json) {
		throw new AssertionError();
	}
	
	@Invoker(value = "keyFromJson")
	static Map<String, Ingredient> invokeReadSymbols(JsonObject json) {
		throw new AssertionError();
	}
	
}