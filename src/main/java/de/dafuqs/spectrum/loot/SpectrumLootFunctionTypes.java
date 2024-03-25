package de.dafuqs.spectrum.loot;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.loot.functions.DyeRandomlyLootFunction;
import de.dafuqs.spectrum.loot.functions.FermentRandomlyLootFunction;
import de.dafuqs.spectrum.loot.functions.FillPotionFillableLootCondition;
import de.dafuqs.spectrum.loot.functions.MergeNbtRandomlyLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class SpectrumLootFunctionTypes {
	
	public static LootItemFunctionType DYE_RANDOMLY;
	public static LootItemFunctionType FERMENT_RANDOMLY;
	public static LootItemFunctionType SET_NBT_RANDOMLY;
	public static LootItemFunctionType FILL_POTION_FILLABLE;
	
	private static LootItemFunctionType register(String id, Serializer<? extends LootItemFunction> jsonSerializer) {
		return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, SpectrumCommon.locate(id), new LootItemFunctionType(jsonSerializer));
	}
	
	public static void register() {
		DYE_RANDOMLY = register("dye_randomly", new DyeRandomlyLootFunction.Serializer());
		FERMENT_RANDOMLY = register("ferment_randomly", new FermentRandomlyLootFunction.Serializer());
		SET_NBT_RANDOMLY = register("merge_nbt_randomly", new MergeNbtRandomlyLootFunction.Serializer());
		FILL_POTION_FILLABLE = register("fill_potion_fillable", new FillPotionFillableLootCondition.Serializer());
	}
	
}
