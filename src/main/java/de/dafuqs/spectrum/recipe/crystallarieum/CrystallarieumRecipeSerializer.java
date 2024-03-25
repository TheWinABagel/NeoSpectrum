package de.dafuqs.spectrum.recipe.crystallarieum;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CrystallarieumRecipeSerializer implements GatedRecipeSerializer<CrystallarieumRecipe> {
	
	public final CrystallarieumRecipeSerializer.RecipeFactory recipeFactory;
	
	public CrystallarieumRecipeSerializer(CrystallarieumRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}
	
	public interface RecipeFactory {
		CrystallarieumRecipe create(ResourceLocation id, String group, boolean secret, ResourceLocation requiredAdvancementIdentifier, Ingredient inputIngredient, List<BlockState> growthStages, int secondsPerGrowthStage, InkColor inkColor, int inkPerSecond, boolean growsWithoutCatalyst, List<CrystallarieumCatalyst> catalysts, List<ItemStack> additionalOutputs);
	}
	
	@Override
	public CrystallarieumRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		String group = readGroup(jsonObject);
		boolean secret = readSecret(jsonObject);
		ResourceLocation requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);
		
		Ingredient inputIngredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
		
		List<BlockState> growthStages = new ArrayList<>();
		JsonArray growthStageArray = GsonHelper.getAsJsonArray(jsonObject, "growth_stage_states");
		for (int i = 0; i < growthStageArray.size(); i++) {
			String blockStateString = growthStageArray.get(i).getAsString();
			try {
				growthStages.add(RecipeUtils.blockStateFromString(blockStateString));
			} catch (CommandSyntaxException e) {
				SpectrumCommon.logError("Recipe " + identifier + " specifies block state " + blockStateString + " that does not seem valid or the block does not exist. Recipe will be ignored.");
				return null;
			}
		}
		int secondsPerGrowthStage = GsonHelper.getAsInt(jsonObject, "seconds_per_growth_stage");
		InkColor inkColor = InkColor.of(GsonHelper.getAsString(jsonObject, "ink_color"));
		int inkCostTier = GsonHelper.getAsInt(jsonObject, "ink_cost_tier");
		int inkPerSecond = inkCostTier == 0 ? 0 : (int) Math.pow(2, inkCostTier - 1); // 0=0; 1=1; 2=4; 3=16; 4=64; 5=256)
		boolean growsWithoutCatalyst = GsonHelper.getAsBoolean(jsonObject,   "grows_without_catalyst", false);
		
		List<CrystallarieumCatalyst> catalysts = new ArrayList<>();
		if (jsonObject.has("catalysts")) {
			JsonArray catalystArray = GsonHelper.getAsJsonArray(jsonObject, "catalysts");
			for (JsonElement jsonElement : catalystArray) {
				catalysts.add(CrystallarieumCatalyst.fromJson(jsonElement.getAsJsonObject()));
			}
		}
		List<ItemStack> additionalOutputs = new ArrayList<>();
		if (jsonObject.has("additional_recipe_manager_outputs")) {
			JsonArray additionalOutputArray = GsonHelper.getAsJsonArray(jsonObject, "additional_recipe_manager_outputs");
			for (JsonElement jsonElement : additionalOutputArray) {
				ResourceLocation additionalOutputItemIdentifier = new ResourceLocation(jsonElement.getAsString());
				ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getOptional(additionalOutputItemIdentifier).orElseThrow(() -> new IllegalStateException("Item: " + additionalOutputItemIdentifier + " does not exist")));
				additionalOutputs.add(itemStack);
			}
		}

		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, inputIngredient, growthStages, secondsPerGrowthStage, inkColor, inkPerSecond, growsWithoutCatalyst, catalysts, additionalOutputs);
	}
	
	@Override
	public void write(FriendlyByteBuf packetByteBuf, CrystallarieumRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		packetByteBuf.writeBoolean(recipe.secret);
		writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);
		
		recipe.inputIngredient.toNetwork(packetByteBuf);
		packetByteBuf.writeInt(recipe.growthStages.size());
		for (BlockState state : recipe.growthStages) {
			packetByteBuf.writeUtf(RecipeUtils.blockStateToString(state));
		}
		packetByteBuf.writeInt(recipe.secondsPerGrowthStage);
		packetByteBuf.writeUtf(recipe.inkColor.toString());
		packetByteBuf.writeInt(recipe.inkPerSecond);
		packetByteBuf.writeBoolean(recipe.growsWithoutCatalyst);
		packetByteBuf.writeInt(recipe.catalysts.size());
		for (CrystallarieumCatalyst catalyst : recipe.catalysts) {
			catalyst.write(packetByteBuf);
		}
		packetByteBuf.writeInt(recipe.additionalOutputs.size());
		for (ItemStack additionalOutput : recipe.additionalOutputs) {
			packetByteBuf.writeItem(additionalOutput);
		}
	}
	
	@Override
	public CrystallarieumRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		String group = packetByteBuf.readUtf();
		boolean secret = packetByteBuf.readBoolean();
		ResourceLocation requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);
		
		Ingredient inputIngredient = Ingredient.fromNetwork(packetByteBuf);
		List<BlockState> growthStages = new ArrayList<>();
		int count = packetByteBuf.readInt();
		for (int i = 0; i < count; i++) {
			String blockStateString = packetByteBuf.readUtf();
			try {
				growthStages.add(RecipeUtils.blockStateFromString(blockStateString));
			} catch (CommandSyntaxException e) {
				SpectrumCommon.logError("Recipe " + identifier + " specifies block state " + blockStateString + " that does not seem valid or the block does not exist. Recipe will be ignored.");
				return null;
			}
		}

		int secondsPerGrowthStage = packetByteBuf.readInt();
		InkColor inkColor = InkColor.of(packetByteBuf.readUtf());
		int inkPerSecond = packetByteBuf.readInt();
		boolean growthWithoutCatalyst = packetByteBuf.readBoolean();
		List<CrystallarieumCatalyst> catalysts = new ArrayList<>();
		count = packetByteBuf.readInt();
		for (int i = 0; i < count; i++) {
			catalysts.add(CrystallarieumCatalyst.fromPacket(packetByteBuf));
		}
		List<ItemStack> additionalOutputs = new ArrayList<>();
		count = packetByteBuf.readInt();
		for (int i = 0; i < count; i++) {
			additionalOutputs.add(packetByteBuf.readItem());
		}

		return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, inputIngredient, growthStages, secondsPerGrowthStage, inkColor, inkPerSecond, growthWithoutCatalyst, catalysts, additionalOutputs);
	}
	
}
