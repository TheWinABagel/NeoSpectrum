package de.dafuqs.spectrum.blocks.mob_head.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

@Environment(EnvType.CLIENT)
public class WardenHeadModel extends SpectrumHeadModel {
	
	public WardenHeadModel(ModelPart root) {
		super(root);
	}
	
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		
		modelData.getRoot().addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create()
						.texOffs(0, 32).addBox(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(58, 2).addBox(8.0F, -21.0F, 0.0F, 10.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(58, 34).addBox(-18.0F, -21.0F, 0.0F, 10.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		
		return LayerDefinition.create(modelData, 128, 128);
	}
	
	@Override
	public float getScale() {
		return 0.65F;
	}
	
}