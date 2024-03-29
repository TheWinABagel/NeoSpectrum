package de.dafuqs.spectrum.api.recipe;

import de.dafuqs.spectrum.progression.UnlockToastManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface GatedRecipe extends Recipe<Container> {
	
	boolean canPlayerCraft(Player playerEntity);
	
	boolean isSecret();
	
	ResourceLocation getRequiredAdvancementIdentifier();
	
	ResourceLocation getRecipeTypeUnlockIdentifier();
	
	Component getSingleUnlockToastString();
	
	Component getMultipleUnlockToastString();
	
	default void registerInToastManager(RecipeType<?> recipeType, GatedRecipe gatedRecipe) {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
			registerInToastManagerClient(recipeType, gatedRecipe);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void registerInToastManagerClient(RecipeType<?> recipeType, GatedRecipe gatedRecipe) {
		UnlockToastManager.registerGatedRecipe(recipeType, gatedRecipe);
	}
	
}
