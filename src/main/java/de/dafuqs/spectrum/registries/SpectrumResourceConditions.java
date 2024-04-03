package de.dafuqs.spectrum.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class SpectrumResourceConditions { //todoforge resource conditions
	

	public static final ResourceLocation INTEGRATION_PACK_ACTIVE = SpectrumCommon.locate("integration_pack_active");
	
	public static void register() {
//		ResourceConditions.register(ENCHANTMENTS_EXIST, SpectrumResourceConditions::enchantmentExistsMatch);
//		ResourceConditions.register(INTEGRATION_PACK_ACTIVE, SpectrumResourceConditions::integrationPackActive);
	}
	
	private static boolean integrationPackActive(JsonObject object) {
		if (object.has("integration_pack")) {
			return SpectrumIntegrationPacks.isIntegrationPackActive(object.get("integration_pack").getAsString());
		}
		return false;
	}


}
