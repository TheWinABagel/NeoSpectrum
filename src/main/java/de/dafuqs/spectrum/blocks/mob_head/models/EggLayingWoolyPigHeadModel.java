package de.dafuqs.spectrum.blocks.mob_head.models;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

@OnlyIn(Dist.CLIENT)
public class EggLayingWoolyPigHeadModel extends SpectrumHeadModel {
	
	public EggLayingWoolyPigHeadModel(ModelPart root) {
		super(root);
	}
	
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		
		PartDefinition head = modelData.getRoot().addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create().texOffs(56, 24).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 44).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 0).addBox(-5.02F, -9.0F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.ZERO);
		
		head.addOrReplaceChild("ear_r1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(7.0964F, -15.0939F, -8.5F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, 6.0F, 0.0F, 0.0F, -0.3927F));
		
		head.addOrReplaceChild("ear_r2", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-10.0964F, -15.0939F, -8.5F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, 6.0F, 0.0F, 0.0F, 0.3927F));
		
		return LayerDefinition.create(modelData, 128, 128);
	}
	
}