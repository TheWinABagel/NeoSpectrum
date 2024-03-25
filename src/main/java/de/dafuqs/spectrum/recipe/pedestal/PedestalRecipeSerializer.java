package de.dafuqs.spectrum.recipe.pedestal;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.api.recipe.GatedRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class PedestalRecipeSerializer<T extends PedestalRecipe> implements GatedRecipeSerializer<T> {
	
	public PedestalRecipeSerializer() {
	}
	
	protected @NotNull
	static Map<GemstoneColor, Integer> readGemstonePowderInputs(JsonObject jsonObject) {
		HashMap<GemstoneColor, Integer> gemInputs = new HashMap<>();
		int amount = GsonHelper.getAsInt(jsonObject, "cyan", 0);
		if (amount > 0) {
			gemInputs.put(BuiltinGemstoneColor.CYAN, amount);
		}
		amount = GsonHelper.getAsInt(jsonObject, "magenta", 0);
		if (amount > 0) {
			gemInputs.put(BuiltinGemstoneColor.MAGENTA, amount);
		}
		amount = GsonHelper.getAsInt(jsonObject, "yellow", 0);
		if (amount > 0) {
			gemInputs.put(BuiltinGemstoneColor.YELLOW, amount);
		}
		amount = GsonHelper.getAsInt(jsonObject, "black", 0);
		if (amount > 0) {
			gemInputs.put(BuiltinGemstoneColor.BLACK, amount);
		}
		amount = GsonHelper.getAsInt(jsonObject, "white", 0);
		if (amount > 0) {
			gemInputs.put(BuiltinGemstoneColor.WHITE, amount);
		}
		return gemInputs;
	}
	
	protected void writeGemstonePowderInputs(@NotNull FriendlyByteBuf packetByteBuf, @NotNull PedestalRecipe recipe) {
		packetByteBuf.writeInt(recipe.getGemstonePowderAmount(BuiltinGemstoneColor.CYAN));
		packetByteBuf.writeInt(recipe.getGemstonePowderAmount(BuiltinGemstoneColor.MAGENTA));
		packetByteBuf.writeInt(recipe.getGemstonePowderAmount(BuiltinGemstoneColor.YELLOW));
		packetByteBuf.writeInt(recipe.getGemstonePowderAmount(BuiltinGemstoneColor.BLACK));
		packetByteBuf.writeInt(recipe.getGemstonePowderAmount(BuiltinGemstoneColor.WHITE));
	}
	
	protected @NotNull Map<GemstoneColor, Integer> readGemstonePowderInputs(@NotNull FriendlyByteBuf packetByteBuf) {
		int cyan = packetByteBuf.readInt();
		int magenta = packetByteBuf.readInt();
		int yellow = packetByteBuf.readInt();
		int black = packetByteBuf.readInt();
		int white = packetByteBuf.readInt();
		Map<GemstoneColor, Integer> gemInputs = new HashMap<>();
		if (cyan > 0) {
			gemInputs.put(BuiltinGemstoneColor.CYAN, cyan);
		}
		if (magenta > 0) {
			gemInputs.put(BuiltinGemstoneColor.MAGENTA, magenta);
		}
		if (yellow > 0) {
			gemInputs.put(BuiltinGemstoneColor.YELLOW, yellow);
		}
		if (black > 0) {
			gemInputs.put(BuiltinGemstoneColor.BLACK, black);
		}
		if (white > 0) {
			gemInputs.put(BuiltinGemstoneColor.WHITE, white);
		}
		return gemInputs;
	}
	
}
