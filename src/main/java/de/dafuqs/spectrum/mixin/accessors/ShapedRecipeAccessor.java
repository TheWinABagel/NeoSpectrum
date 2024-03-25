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
	
	@Invoker(value = "createPatternMatrix")
	static NonNullList<Ingredient> invokeCreatePatternMatrix(String[] pattern, Map<String, Ingredient> key, int width, int height) {
		throw new AssertionError();
	}
	
	@Invoker(value = "removePadding")
	static String[] invokeRemovePadding(String... lines) {
		throw new AssertionError();
	}
	
	@Invoker(value = "getPattern")
	static String[] invokeGetPattern(JsonArray json) {
		throw new AssertionError();
	}
	
	@Invoker(value = "readSymbols")
	static Map<String, Ingredient> invokeReadSymbols(JsonObject json) {
		throw new AssertionError();
	}
	
}