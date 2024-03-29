package de.dafuqs.spectrum.blocks.mob_head.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;

@Environment(EnvType.CLIENT)
public abstract class SpectrumHeadModel extends SkullModelBase {
	
	private final ModelPart head;
	
	public SpectrumHeadModel(ModelPart root) {
		this.head = root.getChild(PartNames.HEAD);
	}
	
	@Override
	public void setupAnim(float animationProgress, float yaw, float pitch) {
		this.head.yRot = yaw * 0.017453292F;
		this.head.xRot = pitch * 0.017453292F;
	}
	
	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		matrices.pushPose();
		float scale = getScale();
		matrices.scale(scale, scale, scale);
		this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		matrices.popPose();
	}
	
	public float getScale() {
		return 0.86F;
	}
	
}