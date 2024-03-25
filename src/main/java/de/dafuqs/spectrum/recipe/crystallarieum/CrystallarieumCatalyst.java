package de.dafuqs.spectrum.recipe.crystallarieum;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public class CrystallarieumCatalyst {
	
	public static final CrystallarieumCatalyst EMPTY = new CrystallarieumCatalyst(Ingredient.EMPTY, 1.0F, 1.0F, 0.0F);
	
	public final Ingredient ingredient;
	public final float growthAccelerationMod;
	public final float inkConsumptionMod;
	public final float consumeChancePerSecond;
	
	protected CrystallarieumCatalyst(Ingredient ingredient, float growthAccelerationMod, float inkConsumptionMod, float consumeChancePerSecond) {
		this.ingredient = ingredient;
		this.growthAccelerationMod = growthAccelerationMod;
		this.inkConsumptionMod = inkConsumptionMod;
		this.consumeChancePerSecond = consumeChancePerSecond;
	}
	
	public static CrystallarieumCatalyst fromJson(JsonObject jsonObject) {
		Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
		float growthAccelerationMod = GsonHelper.getAsFloat(jsonObject, "growth_acceleration_mod");
		float inkConsumptionMod = GsonHelper.getAsFloat(jsonObject, "ink_consumption_mod");
		float consumeChancePerSecond = GsonHelper.getAsFloat(jsonObject, "consume_chance_per_second");
		return new CrystallarieumCatalyst(ingredient, growthAccelerationMod, inkConsumptionMod, consumeChancePerSecond);
	}
	
	public void write(FriendlyByteBuf packetByteBuf) {
		this.ingredient.toNetwork(packetByteBuf);
		packetByteBuf.writeFloat(growthAccelerationMod);
		packetByteBuf.writeFloat(inkConsumptionMod);
		packetByteBuf.writeFloat(consumeChancePerSecond);
	}
	
	public static CrystallarieumCatalyst fromPacket(FriendlyByteBuf packetByteBuf) {
		Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
		float growthAccelerationMod = packetByteBuf.readFloat();
		float inkConsumptionMod = packetByteBuf.readFloat();
		float consumeChancePerSecond = packetByteBuf.readFloat();
		return new CrystallarieumCatalyst(ingredient, growthAccelerationMod, inkConsumptionMod, consumeChancePerSecond);
	}
	
}