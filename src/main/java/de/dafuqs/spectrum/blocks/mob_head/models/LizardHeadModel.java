package de.dafuqs.spectrum.blocks.mob_head.models;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@Environment(EnvType.CLIENT)
public class LizardHeadModel extends SpectrumHeadModel {
	
	public LizardHeadModel(ModelPart root) {
		super(root);
	}
	
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		
		PartDefinition head = modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create()
				.texOffs(11, 58).addBox(-2.5F, -6.0F, 1.0F, 5.0F, 6.0F, 5.0F)
				.texOffs(44, 44).addBox(-2.0F, -6.0F, -8.0F, 4.0F, 3.0F, 9.0F)
				.texOffs(26, 21).addBox(0.0F, -13.0F, -9.0F, 0.0F, 8.0F, 15.0F), PartPose.ZERO);
		
		head.addOrReplaceChild("rightfrills_r1", CubeListBuilder.create().texOffs(61, 40).addBox(-1.9733F, -9.9307F, 0.0F, 8.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(2.5F, -6.0F, 1.0F, -0.8281F, 0.001F, 1.5679F));
		head.addOrReplaceChild("leftfrills_r1", CubeListBuilder.create().texOffs(45, 68).addBox(-6.0267F, -9.9307F, 0.0F, 8.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(-2.5F, -6.0F, 1.0F, -0.8282F, 0.0F, -1.5615F));
		head.addOrReplaceChild("topfrills_r1", CubeListBuilder.create().texOffs(60, 56).addBox(-4.5F, -11.75F, -0.15F, 9.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -0.8727F, 0.0F, 0.0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(61, 0).addBox(-1.5F, 0.0F, -5.5F, 3.0F, 1.0F, 6.0F), PartPose.offset(0.0F, -3.0F, 1.0F));
		
		return LayerDefinition.create(modelData, 128, 128);
	}
	
}