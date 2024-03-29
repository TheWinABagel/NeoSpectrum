package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.KindlingCoughEntity;
import de.dafuqs.spectrum.entity.models.KindlingCoughEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
public class KindlingCoughEntityRenderer extends EntityRenderer<KindlingCoughEntity> {
	
	private static final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/entity/kindling/cough.png");
	private final KindlingCoughEntityModel model;
	
	public KindlingCoughEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new KindlingCoughEntityModel(context.bakeLayer(SpectrumModelLayers.KINDLING_COUGH));
	}
	
	public void render(KindlingCoughEntity kindlingCoughEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
		matrixStack.pushPose();
		matrixStack.translate(0.0, 0.15000000596046448, 0.0);
		matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, kindlingCoughEntity.yRotO, kindlingCoughEntity.getYRot()) - 90.0F));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, kindlingCoughEntity.xRotO, kindlingCoughEntity.getXRot())));
		this.model.setupAnim(kindlingCoughEntity, g, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.renderType(TEXTURE));
		this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.popPose();
		super.render(kindlingCoughEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}
	
	public ResourceLocation getTextureLocation(KindlingCoughEntity kindlingCoughEntity) {
		return TEXTURE;
	}
}
