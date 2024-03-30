package de.dafuqs.spectrum.registries.client;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.mob_head.models.*;
import de.dafuqs.spectrum.entity.models.*;
import de.dafuqs.spectrum.render.armor.BedrockArmorModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
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

	@SubscribeEvent
	public static void register(EntityRenderersEvent.RegisterLayerDefinitions e) {
		e.registerLayerDefinition(WOOLY_PIG, EggLayingWoolyPigEntityModel::getTexturedModelData);
		e.registerLayerDefinition(WOOLY_PIG_HAT, EggLayingWoolyPigHatEntityModel::getTexturedModelData);
		e.registerLayerDefinition(WOOLY_PIG_WOOL, EggLayingWoolyPigWoolEntityModel::getTexturedModelData);
		e.registerLayerDefinition(PRESERVATION_TURRET, PreservationTurretEntityModel::getTexturedModelData);
		e.registerLayerDefinition(MONSTROSITY, MonstrosityEntityModel::getTexturedModelData);
		e.registerLayerDefinition(LIZARD_SCALES, LizardEntityModel::getTexturedModelData);
		e.registerLayerDefinition(LIZARD_FRILLS, LizardEntityModel::getTexturedModelData);
		e.registerLayerDefinition(LIZARD_HORNS, LizardEntityModel::getTexturedModelData);
		e.registerLayerDefinition(KINDLING, KindlingEntityModel::getTexturedModelData);
		e.registerLayerDefinition(KINDLING_SADDLE, KindlingEntityModel::getTexturedModelData);
		e.registerLayerDefinition(KINDLING_ARMOR, KindlingEntityModel::getTexturedModelData);
		e.registerLayerDefinition(KINDLING_COUGH, KindlingCoughEntityModel::getTexturedModelData);
		e.registerLayerDefinition(ERASER, EraserEntityModel::getTexturedModelData);

		e.registerLayerDefinition(EGG_LAYING_WOOLY_PIG_HEAD, EggLayingWoolyPigHeadModel::getTexturedModelData);
		e.registerLayerDefinition(MONSTROSITY_HEAD, MonstrosityHeadModel::getTexturedModelData);
		e.registerLayerDefinition(KINDLING_HEAD, KindlingHeadModel::getTexturedModelData);
		e.registerLayerDefinition(ERASER_HEAD, EraserHeadModel::getTexturedModelData);
		e.registerLayerDefinition(LIZARD_HEAD, LizardHeadModel::getTexturedModelData);
		e.registerLayerDefinition(PRESERVATION_TURRET_HEAD, GuardianTurretHeadModel::getTexturedModelData);
		e.registerLayerDefinition(WARDEN_HEAD, WardenHeadModel::getTexturedModelData);

		e.registerLayerDefinition(FEET_BEDROCK_LAYER, () -> LayerDefinition.create(BedrockArmorModel.getModelData(), 128, 128));
		e.registerLayerDefinition(MAIN_BEDROCK_LAYER, () -> LayerDefinition.create(BedrockArmorModel.getModelData(), 128, 128));
	}
}
