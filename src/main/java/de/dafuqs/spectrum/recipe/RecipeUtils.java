package de.dafuqs.spectrum.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.helpers.NbtHelper;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RecipeUtils {
	
	public static ItemStack itemStackWithNbtFromJson(JsonObject json) {
		Item item = ShapedRecipe.itemFromJson(json);
		if (json.has("data")) {
			throw new JsonParseException("Disallowed data tag found");
		} else {
			int count = GsonHelper.getAsInt(json, "count", 1);
			
			if (count < 1) {
				throw new JsonSyntaxException("Invalid output count: " + count);
			} else {
				ItemStack stack = new ItemStack(item, count);
				
				Optional<CompoundTag> nbt = NbtHelper.getNbtCompound(json.get("nbt"));
				nbt.ifPresent(stack::setTag);
				
				return stack;
			}
		}
	}
	
	public static BlockState blockStateFromString(String string) throws CommandSyntaxException {
		return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(string), true).blockState();
	}
	
	public static String blockStateToString(BlockState state) {
		return BlockStateParser.serialize(state);
	}
	

	
	public static List<IngredientStack> createIngredientStackPatternMatrix(String[] pattern, Map<String, IngredientStack> symbols, int width, int height) {
		List<IngredientStack> list = NonNullList.withSize(width * height, IngredientStack.EMPTY);
		Set<String> set = Sets.newHashSet(symbols.keySet());
		set.remove(" ");

		for (int i = 0; i < pattern.length; ++i) {
			for (int j = 0; j < pattern[i].length(); ++j) {
				String string = pattern[i].substring(j, j + 1);
				var ingredient = symbols.get(string);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
				}

				set.remove(string);
				list.set(j + width * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return list;
		}
	}

	public static Map<String, IngredientStack> readIngredientStackSymbols(JsonObject json) {
		Map<String, IngredientStack> map = Maps.newHashMap();
		for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.entrySet()) {
			if (stringJsonElementEntry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + stringJsonElementEntry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}
			if (" ".equals(stringJsonElementEntry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}
			map.put(stringJsonElementEntry.getKey(), RecipeParser.ingredientStackFromJson((JsonObject) stringJsonElementEntry.getValue()));
		}

		map.put(" ", IngredientStack.EMPTY);
		return map;
	}

}
