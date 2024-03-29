package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.KindlingEntity;
import de.dafuqs.spectrum.entity.models.KindlingEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class KindlingEntitySaddleFeatureRenderer extends RenderLayer<KindlingEntity, KindlingEntityModel> {
	
	public static final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/entity/kindling/saddle.png");
	
	private final KindlingEntityModel model;
	
	public KindlingEntitySaddleFeatureRenderer(RenderLayerParent<KindlingEntity, KindlingEntityModel> context, EntityModelSet loader) {
		super(context);
		this.model = new KindlingEntityModel(loader.bakeLayer(SpectrumModelLayers.KINDLING_SADDLE));
	}
	
	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, KindlingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (entity.isSaddled()) {
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
			this.model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
}
