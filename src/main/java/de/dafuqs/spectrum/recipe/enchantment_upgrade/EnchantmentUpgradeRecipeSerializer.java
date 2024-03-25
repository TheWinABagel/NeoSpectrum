package de.dafuqs.spectrum.recipe.enchantment_upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentUpgradeRecipeSerializer implements GatedRecipeSerializer<EnchantmentUpgradeRecipe> {
	
	public static final List<EnchantmentUpgradeRecipe> enchantmentUpgradeRecipesToInject = new ArrayList<>();
	
	public final EnchantmentUpgradeRecipeSerializer.RecipeFactory recipeFactory;
	
	public EnchantmentUpgradeRecipeSerializer(EnchantmentUpgradeRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		EnchantmentUpgradeRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Enchantment enchantment, int enchantmentDestinationLevel, int requiredExperience, Item requiredItem, int requiredItemCount);
	}
	
	@Override
	public EnchantmentUpgradeRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		ResourceLocation enchantmentIdentifier = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "enchantment"));
		
		if (!BuiltInRegistries.ENCHANTMENT.containsKey(enchantmentIdentifier)) {
			throw new JsonParseException("Enchantment Upgrade Recipe " + identifier + " has an enchantment set that does not exist or is disabled: " + enchantmentIdentifier); // otherwise, recipe sync would break multiplayer joining with the non-existing enchantment
		}
		
		Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantmentIdentifier);
		
		JsonArray levelArray = GsonHelper.getAsJsonArray(jsonObject, "levels");
		int level;
		int requiredExperience;
		Item requiredItem;
		int requiredItemCount;
		EnchantmentUpgradeRecipe recipe = null;
		for (int i = 0; i < levelArray.size(); i++) {
			JsonObject currentElement = levelArray.get(i).getAsJsonObject();
			level = i + 2;
			requiredExperience = GsonHelper.getAsInt(currentElement, "experience");
			requiredItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(currentElement, "item")));
			requiredItemCount = GsonHelper.getAsInt(currentElement, "item_count");
			
			recipe = this.recipeFactory.create(SpectrumCommon.locate(identifier.getPath() + "_level_" + (i + 2)), group, secret, requiredAdvancementIdentifier, enchantment, level, requiredExperience, requiredItem, requiredItemCount);
			if (!enchantmentUpgradeRecipesToInject.contains(recipe) && i < levelArray.size() - 1) { // we return the last one, no need to inject
				enchantmentUpgradeRecipesToInject.add(recipe);
			}
		}
		
		return recipe;
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, EnchantmentUpgradeRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		packetByteBuf.writeResourceLocation(BuiltInRegistries.ENCHANTMENT.getKey(recipe.enchantment));
		packetByteBuf.writeInt(recipe.enchantmentDestinationLevel);
		packetByteBuf.writeInt(recipe.requiredExperience);
		packetByteBuf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.requiredItem));
		packetByteBuf.writeInt(recipe.requiredItemCount);
	}
	
	@Override
	public EnchantmentUpgradeRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(packetByteBuf.readResourceLocation());
		int enchantmentDestinationLevel = packetByteBuf.readInt();
		int requiredExperience = packetByteBuf.readInt();
		Item requiredItem = BuiltInRegistries.ITEM.get(packetByteBuf.readResourceLocation());
		int requiredItemCount = packetByteBuf.readInt();
		
		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, enchantment, enchantmentDestinationLevel, requiredExperience, requiredItem, requiredItemCount);
	}
	
}
