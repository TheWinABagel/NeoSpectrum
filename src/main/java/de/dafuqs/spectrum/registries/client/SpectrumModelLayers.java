package de.dafuqs.spectrum.registries.client;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.mob_head.models.*;
import de.dafuqs.spectrum.entity.models.*;
import de.dafuqs.spectrum.render.armor.BedrockArmorModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class SpectrumModelLayers {
	
	/**
	 * Entities
	 */
	public static final ModelLayerLocation WOOLY_PIG = new ModelLayerLocation(SpectrumCommon.locate("egg_laying_wooly_pig"), "main");
	public static final ModelLayerLocation WOOLY_PIG_HAT = new ModelLayerLocation(SpectrumCommon.locate("egg_laying_wooly_pig"), "hat");
	public static final ModelLayerLocation WOOLY_PIG_WOOL = new ModelLayerLocation(SpectrumCommon.locate("egg_laying_wooly_pig"), "wool");
	
	public static final ModelLayerLocation PRESERVATION_TURRET = new ModelLayerLocation(SpectrumCommon.locate("preservation_turret"), "main");
	public static final ModelLayerLocation MONSTROSITY = new ModelLayerLocation(SpectrumCommon.locate("monstrosity"), "main");
	public static final ModelLayerLocation LIZARD_SCALES = new ModelLayerLocation(SpectrumCommon.locate("lizard"), "main");
	public static final ModelLayerLocation LIZARD_FRILLS = new ModelLayerLocation(SpectrumCommon.locate("lizard"), "frills");
	public static final ModelLayerLocation LIZARD_HORNS = new ModelLayerLocation(SpectrumCommon.locate("lizard"), "horns");
	public static final ModelLayerLocation KINDLING = new ModelLayerLocation(SpectrumCommon.locate("kindling"), "main");
	public static final ModelLayerLocation KINDLING_SADDLE = new ModelLayerLocation(SpectrumCommon.locate("kindling_saddle"), "main");
	public static final ModelLayerLocation KINDLING_ARMOR = new ModelLayerLocation(SpectrumCommon.locate("kindling_armor"), "main");
	public static final ModelLayerLocation KINDLING_COUGH = new ModelLayerLocation(SpectrumCommon.locate("kindling_cough"), "main");
	public static final ModelLayerLocation ERASER = new ModelLayerLocation(SpectrumCommon.locate("eraser"), "body");

	
	/**
	 * Blocks
	 */
	public static final ModelLayerLocation EGG_LAYING_WOOLY_PIG_HEAD = new ModelLayerLocation(SpectrumCommon.locate("egg_laying_wooly_pig_head"), "main");
	public static final ModelLayerLocation MONSTROSITY_HEAD = new ModelLayerLocation(SpectrumCommon.locate("monstrosity_head"), "main");
	public static final ModelLayerLocation KINDLING_HEAD = new ModelLayerLocation(SpectrumCommon.locate("kindling_head"), "main");
	public static final ModelLayerLocation LIZARD_HEAD = new ModelLayerLocation(SpectrumCommon.locate("lizard_head"), "main");
	public static final ModelLayerLocation PRESERVATION_TURRET_HEAD = new ModelLayerLocation(SpectrumCommon.locate("preservation_turret_head"), "main");
	public static final ModelLayerLocation WARDEN_HEAD = new ModelLayerLocation(SpectrumCommon.locate("warden_head"), "main");
	public static final ModelLayerLocation ERASER_HEAD = new ModelLayerLocation(SpectrumCommon.locate("eraser_head"), "body");
	
	/**
	 * Armor
	 */
	public static final ModelLayerLocation FEET_BEDROCK_LAYER = new ModelLayerLocation(SpectrumCommon.locate("bedrock_armor"), "feet");
	public static final ModelLayerLocation MAIN_BEDROCK_LAYER = new ModelLayerLocation(SpectrumCommon.locate("bedrock_armor"), "main");
	public static final ResourceLocation BEDROCK_ARMOR_LOCATION = SpectrumCommon.locate("textures/armor/bedrock_armor_main.png");
	
	public static void register() {
		EntityModelLayerRegistry.registerModelLayer(WOOLY_PIG, EggLayingWoolyPigEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(WOOLY_PIG_HAT, EggLayingWoolyPigHatEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(WOOLY_PIG_WOOL, EggLayingWoolyPigWoolEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(PRESERVATION_TURRET, PreservationTurretEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MONSTROSITY, MonstrosityEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(LIZARD_SCALES, LizardEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(LIZARD_FRILLS, LizardEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(LIZARD_HORNS, LizardEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(KINDLING, KindlingEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(KINDLING_SADDLE, KindlingEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(KINDLING_ARMOR, KindlingEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(KINDLING_COUGH, KindlingCoughEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(ERASER, EraserEntityModel::getTexturedModelData);
		
		EntityModelLayerRegistry.registerModelLayer(EGG_LAYING_WOOLY_PIG_HEAD, EggLayingWoolyPigHeadModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MONSTROSITY_HEAD, MonstrosityHeadModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(KINDLING_HEAD, KindlingHeadModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(ERASER_HEAD, EraserHeadModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(LIZARD_HEAD, LizardHeadModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(PRESERVATION_TURRET_HEAD, GuardianTurretHeadModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(WARDEN_HEAD, WardenHeadModel::getTexturedModelData);
		
		EntityModelLayerRegistry.registerModelLayer(FEET_BEDROCK_LAYER, () -> LayerDefinition.create(BedrockArmorModel.getModelData(), 128, 128));
		EntityModelLayerRegistry.registerModelLayer(MAIN_BEDROCK_LAYER, () -> LayerDefinition.create(BedrockArmorModel.getModelData(), 128, 128));
	}
	
}
