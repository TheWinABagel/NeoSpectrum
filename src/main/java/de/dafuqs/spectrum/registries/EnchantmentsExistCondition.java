package de.dafuqs.spectrum.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentsExistCondition implements ICondition {
    Enchantment ench;

    public EnchantmentsExistCondition(Enchantment ench) {
        this.ench = ench;
    }

    public static final ResourceLocation ENCHANTMENTS_EXIST = SpectrumCommon.locate("enchantments_exist");
    @Override
    public ResourceLocation getID() {
        return ENCHANTMENTS_EXIST;
    }

    @Override
    public boolean test(IContext context) {
        return true;
    }

    public static class Serializer implements IConditionSerializer<EnchantmentsExistCondition> {

        @Override
        public void write(JsonObject json, EnchantmentsExistCondition value) {
            json.addProperty("values", ForgeRegistries.ENCHANTMENTS.getKey(value.ench).toString());
        }

        @Override
        public EnchantmentsExistCondition read(JsonObject json) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "values");

            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    ResourceLocation identifier = ResourceLocation.tryParse(element.getAsString());
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(identifier);

                    return new EnchantmentsExistCondition(enchantment);
                } else {
                    throw new JsonParseException("Invalid enchantment id entry: " + element);
                }
            }

            return null;
        }

        @Override
        public ResourceLocation getID() {
            return ENCHANTMENTS_EXIST;
        }
    }
}
