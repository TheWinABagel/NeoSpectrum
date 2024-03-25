package de.dafuqs.spectrum.recipe.spirit_instiller;

import com.google.gson.JsonObject;
import de.dafuqs.matchbooks.recipe.IngredientStack;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class SpiritInstillerRecipeSerializer implements GatedRecipeSerializer<SpiritInstillerRecipe> {
	
	public final SpiritInstillerRecipeSerializer.RecipeFactory recipeFactory;
	
	public SpiritInstillerRecipeSerializer(SpiritInstillerRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		SpiritInstillerRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, IngredientStack centerIngredient, IngredientStack bowlIngredient1, IngredientStack bowlIngredient2, ItemStack outputItemStack,
		                             int craftingTime, float experience, boolean noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
	@Override
	public SpiritInstillerRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		IngredientStack centerIngredient = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "center_ingredient"));
		IngredientStack bowlIngredient1 = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient1"));
		IngredientStack bowlIngredient2 = RecipeParser.ingredientStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient2"));
		ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
		
		int craftingTime = GsonHelper.getAsInt(jsonObject, "time", 200);
		float experience = GsonHelper.getAsFloat(jsonObject, "experience", 1.0F);
		
		boolean noBenefitsFromYieldAndEfficiencyUpgrades = false;
		if (GsonHelper.isValidPrimitive(jsonObject, "disable_yield_and_efficiency_upgrades")) {
			noBenefitsFromYieldAndEfficiencyUpgrades = GsonHelper.getAsBoolean(jsonObject, "disable_yield_and_efficiency_upgrades", false);
		}
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, centerIngredient, bowlIngredient1, bowlIngredient2, outputItemStack, craftingTime, experience, noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, SpiritInstillerRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.centerIngredient.write(packetByteBuf);
		recipe.bowlIngredient1.write(packetByteBuf);
		recipe.bowlIngredient2.write(packetByteBuf);
		packetByteBuf.writeItem(recipe.outputItemStack);
		packetByteBuf.writeInt(recipe.craftingTime);
		packetByteBuf.writeFloat(recipe.experience);
		packetByteBuf.writeBoolean(recipe.noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
	@Override
	public SpiritInstillerRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		IngredientStack centerIngredient = IngredientStack.fromByteBuf(packetByteBuf);
		IngredientStack bowlIngredient1 = IngredientStack.fromByteBuf(packetByteBuf);
		IngredientStack bowlIngredient2 = IngredientStack.fromByteBuf(packetByteBuf);
		ItemStack outputItemStack = packetByteBuf.readItem();
		int craftingTime = packetByteBuf.readInt();
		float experience = packetByteBuf.readFloat();
		boolean noBenefitsFromYieldAndEfficiencyUpgrades = packetByteBuf.readBoolean();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, centerIngredient, bowlIngredient1, bowlIngredient2, outputItemStack, craftingTime, experience, noBenefitsFromYieldAndEfficiencyUpgrades);
	}
	
}
