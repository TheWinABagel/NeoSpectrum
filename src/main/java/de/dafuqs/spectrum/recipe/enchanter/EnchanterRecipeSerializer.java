package de.dafuqs.spectrum.recipe.enchanter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class EnchanterRecipeSerializer implements GatedRecipeSerializer<EnchanterRecipe> {
	
	public final EnchanterRecipeSerializer.RecipeFactory recipeFactory;
	
	public EnchanterRecipeSerializer(EnchanterRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		EnchanterRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, NonNullList<Ingredient> inputs, ItemStack output, int craftingTime, int requiredExperience, boolean noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
	@Override
	public EnchanterRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		JsonArray ingredientArray = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
		NonNullList<Ingredient> craftingInputs = NonNullList.createWithCapacity(ingredientArray.size());
		for (int i = 0; i < ingredientArray.size(); i++) {
			craftingInputs.add(Ingredient.fromJson(ingredientArray.get(i)));
		}
		
		ItemStack output = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		
		int requiredExperience = GsonHelper.getAsInt(jsonObject, "required_experience", 0);
		int craftingTime = GsonHelper.getAsInt(jsonObject, "time", 200);
		
		boolean noBenefitsFromYieldAndEfficiencyUpgrades = false;
		if (GsonHelper.isValidPrimitive(jsonObject, "disable_yield_and_efficiency_upgrades")) {
			noBenefitsFromYieldAndEfficiencyUpgrades = GsonHelper.getAsBoolean(jsonObject, "disable_yield_and_efficiency_upgrades", false);
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, craftingInputs, output, craftingTime, requiredExperience, noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, EnchanterRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		packetByteBuf.writeShort(recipe.inputs.size());
		for (Ingredient ingredient : recipe.inputs) {
			ingredient.toNetwork(packetByteBuf);
		}
		
		packetByteBuf.writeItem(recipe.output);
		packetByteBuf.writeInt(recipe.craftingTime);
		packetByteBuf.writeInt(recipe.requiredExperience);
		packetByteBuf.writeBoolean(recipe.noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
	@Override
	public EnchanterRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		short craftingInputCount = packetByteBuf.readShort();
		NonNullList<Ingredient> ingredients = NonNullList.withSize(craftingInputCount, Ingredient.EMPTY);
		for (short i = 0; i < craftingInputCount; i++) {
			ingredients.set(i, Ingredient.fromNetwork(packetByteBuf));
		}
		
		ItemStack output = packetByteBuf.readItem();
		int craftingTime = packetByteBuf.readInt();
		int requiredExperience = packetByteBuf.readInt();
		boolean noBenefitsFromYieldAndEfficiencyUpgrades = packetByteBuf.readBoolean();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredients, output, craftingTime, requiredExperience, noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
}
