package de.dafuqs.spectrum.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class SpectrumResourceConditions {
	
	public static final ResourceLocation ENCHANTMENTS_EXIST = SpectrumCommon.locate("enchantments_exist");
	public static final ResourceLocation INTEGRATION_PACK_ACTIVE = SpectrumCommon.locate("integration_pack_active");
	
	public static void register() {
		ResourceConditions.register(ENCHANTMENTS_EXIST, SpectrumResourceConditions::enchantmentExistsMatch);
		ResourceConditions.register(INTEGRATION_PACK_ACTIVE, SpectrumResourceConditions::integrationPackActive);
	}
	
	private static boolean enchantmentExistsMatch(JsonObject object) {
		JsonArray array = GsonHelper.getAsJsonArray(object, "values");
		
		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				ResourceLocation identifier = ResourceLocation.tryParse(element.getAsString());
				Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(identifier);
				
				return enchantment != null;
			} else {
				throw new JsonParseException("Invalid enchantment id entry: " + element);
			}
		}
		
		return false;
	}
	
	private static boolean integrationPackActive(JsonObject object) {
		if (object.has("integration_pack")) {
			return SpectrumIntegrationPacks.isIntegrationPackActive(object.get("integration_pack").getAsString());
		}
		return false;
	}
	
}
