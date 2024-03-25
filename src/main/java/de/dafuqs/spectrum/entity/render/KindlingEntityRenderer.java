package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.KindlingEntity;
import de.dafuqs.spectrum.entity.models.KindlingEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class KindlingEntityRenderer extends MobRenderer<KindlingEntity, KindlingEntityModel> {
	
	public static final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/entity/kindling/kindling.png");
	public static final ResourceLocation TEXTURE_BLINKING = SpectrumCommon.locate("textures/entity/kindling/kindling_blink.png");
	public static final ResourceLocation TEXTURE_ANGRY = SpectrumCommon.locate("textures/entity/kindling/kindling_angry.png");
public static final ResourceLocation TEXTURE_CLIPPED = SpectrumCommon.locate("textures/entity/kindling/kindling_clipped.png");
	public static final ResourceLocation TEXTURE_BLINKING_CLIPPED = SpectrumCommon.locate("textures/entity/kindling/kindling_blink_clipped.png");
	public static final ResourceLocation TEXTURE_ANGRY_CLIPPED = SpectrumCommon.locate("textures/entity/kindling/kindling_angry_clipped.png");

	public KindlingEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new KindlingEntityModel(context.bakeLayer(SpectrumModelLayers.KINDLING)), 0.7F);
		this.addLayer(new KindlingEntitySaddleFeatureRenderer(this, context.getModelSet()));
		this.addLayer(new KindlingEntityArmorFeatureRenderer(this, context.getModelSet()));
	}
	
	@Override
	public void render(KindlingEntity entity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
	}
	
	@Override
	public ResourceLocation getTextureLocation(@NotNull KindlingEntity entity) {
		boolean isClipped = entity.isClipped();
		if (entity.getRemainingPersistentAngerTime() > 0) {
			return isClipped ? TEXTURE_ANGRY_CLIPPED : TEXTURE_ANGRY;
		}

		boolean isBlinking = (entity.getId() - entity.level().getGameTime()) % 120 == 0; // based on the entities' id, so not all blink at the same time
		if (isClipped) {
			return isBlinking ? TEXTURE_BLINKING_CLIPPED : TEXTURE_CLIPPED;
		}

		return isBlinking ? TEXTURE_BLINKING : TEXTURE;
	}
	
}
