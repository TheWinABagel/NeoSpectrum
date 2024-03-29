package de.dafuqs.spectrum.blocks.chests;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

@Environment(EnvType.CLIENT)
public class HeartboundChestBlockEntityRenderer extends SpectrumChestBlockEntityRenderer<HeartboundChestBlockEntity> {
	
	public HeartboundChestBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}
	
	@Override
	protected ModelPart getModel(BlockEntityRendererProvider.Context ctx) {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		
		modelPartData.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 18).addBox(1.0F, 0.0F, 1.0F, 14.0F, 11.0F, 14.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		modelPartData.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 14.0F, 4.0F, 14.0F), PartPose.offset(1.0F, 10.0F, 1.0F));
		
		// the heart lock
		modelPartData.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(5, 22).addBox(6.5F, -3.0F, 14.0F, 1.0F, 1.0F, 1.0F), PartPose.offset(1.0F, 10.0F, 1.0F));
		modelPartData.getChild("lock").addOrReplaceChild("heart1", CubeListBuilder.create().texOffs(6, 23).addBox(5.5F, 1.0F, 14.0F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		modelPartData.getChild("lock").addOrReplaceChild("heart2", CubeListBuilder.create().texOffs(6, 23).addBox(7.5F, 1.0F, 14.0F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		modelPartData.getChild("lock").addOrReplaceChild("heart3", CubeListBuilder.create().texOffs(1, 20).addBox(4.5F, -1.0F, 14.0F, 5.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		modelPartData.getChild("lock").addOrReplaceChild("heart4", CubeListBuilder.create().texOffs(4, 22).addBox(5.5F, -2.0F, 14.0F, 3.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		return modelData.getRoot().bake(64, 64);
	}
	
}