package de.dafuqs.spectrum.recipe.pedestal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShapelessPedestalRecipeSerializer extends PedestalRecipeSerializer<ShapelessPedestalRecipe> {
	
	public final ShapelessPedestalRecipeSerializer.RecipeFactory recipeFactory;
	
	public ShapelessPedestalRecipeSerializer(ShapelessPedestalRecipeSerializer.RecipeFactory recipeFactory) {
		super();
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		ShapelessPedestalRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, PedestalRecipeTier tier,
									   List<IngredientStack> inputs, Map<GemstoneColor, Integer> powderInputs,
									   ItemStack output, float experience, int craftingTime, boolean skipRecipeRemainders, boolean noBenefitsFromYieldUpgrades);
	}
	
	@Override
	public ShapelessPedestalRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		ItemStack output = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		PedestalRecipeTier tier = PedestalRecipeTier.valueOf(GsonHelper.getAsString(jsonObject, "tier", "basic").toUpperCase(Locale.ROOT));
		float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0);
		int craftingTime = GsonHelper.getAsInt(jsonObject, "time", 200);
		boolean noBenefitsFromYieldUpgrades = GsonHelper.getAsBoolean(jsonObject, "disable_yield_upgrades", false);
		Map<GemstoneColor, Integer> gemInputs = readGemstonePowderInputs(jsonObject);
		
		boolean skipRecipeRemainders = false;
		if (GsonHelper.isBooleanValue(jsonObject, "skip_recipe_remainders")) {
			skipRecipeRemainders = GsonHelper.getAsBoolean(jsonObject, "skip_recipe_remainders", false);
		}
		
		JsonArray ingredientArray = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
		List<IngredientStack> inputs = RecipeParser.ingredientStacksFromJson(ingredientArray, ingredientArray.size());
		if (inputs.size() > 9) {
			throw new JsonParseException("Recipe cannot have more than 9 ingredients. Has " + inputs.size());
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, tier, inputs, gemInputs, output, experience, craftingTime, skipRecipeRemainders, noBenefitsFromYieldUpgrades);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, ShapelessPedestalRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		packetByteBuf.writeInt(recipe.tier.ordinal());
		packetByteBuf.writeInt(recipe.inputs.size());
		for (IngredientStack ingredient : recipe.inputs) {
			ingredient.write(packetByteBuf);
		}
		writeGemstonePowderInputs(packetByteBuf, recipe);
		packetByteBuf.writeItem(recipe.output);
		packetByteBuf.writeFloat(recipe.experience);
		packetByteBuf.writeInt(recipe.craftingTime);
		packetByteBuf.writeBoolean(recipe.skipRecipeRemainders);
		packetByteBuf.writeBoolean(recipe.noBenefitsFromYieldUpgrades);
	}
	
	@Override
	public ShapelessPedestalRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		PedestalRecipeTier tier = PedestalRecipeTier.values()[packetByteBuf.readInt()];
		int inputCount = packetByteBuf.readInt();
		List<IngredientStack> inputs = IngredientStack.decodeByteBuf(packetByteBuf, inputCount);
		Map<GemstoneColor, Integer> gemInputs = readGemstonePowderInputs(packetByteBuf);
		ItemStack output = packetByteBuf.readItem();
		float experience = packetByteBuf.readFloat();
		int craftingTime = packetByteBuf.readInt();
		boolean skipRecipeRemainders = packetByteBuf.readBoolean();
		boolean noBenefitsFromYieldUpgrades = packetByteBuf.readBoolean();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, tier, inputs, gemInputs, output, experience, craftingTime, skipRecipeRemainders, noBenefitsFromYieldUpgrades);
	}
	
}
