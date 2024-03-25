package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.spectrum.entity.entity.EggLayingWoolyPigEntity;
import de.dafuqs.spectrum.entity.models.EggLayingWoolyPigEntityModel;
import de.dafuqs.spectrum.entity.models.EggLayingWoolyPigHatEntityModel;
import de.dafuqs.spectrum.entity.models.EggLayingWoolyPigWoolEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class EggLayingWoolyPigWoolFeatureRenderer extends RenderLayer<EggLayingWoolyPigEntity, EggLayingWoolyPigEntityModel> {
	
	private final EggLayingWoolyPigHatEntityModel hat;
	private final EggLayingWoolyPigWoolEntityModel wool;
	
	public EggLayingWoolyPigWoolFeatureRenderer(EggLayingWoolyPigEntityRenderer context, EntityModelSet loader) {
		super(context);
		this.hat = new EggLayingWoolyPigHatEntityModel(loader.bakeLayer(SpectrumModelLayers.WOOLY_PIG_HAT));
		this.wool = new EggLayingWoolyPigWoolEntityModel(loader.bakeLayer(SpectrumModelLayers.WOOLY_PIG_WOOL));
	}
	
	@Override
	public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, EggLayingWoolyPigEntity entity, float f, float g, float h, float j, float k, float l) {
		if (entity.isInvisible()) {
			Minecraft minecraftClient = Minecraft.getInstance();
			boolean bl = minecraftClient.shouldEntityAppearGlowing(entity);
			if (bl) {
				VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.outline(EggLayingWoolyPigEntityRenderer.TEXTURE));
				if (!entity.isHatless()) {
					this.getParentModel().copyPropertiesTo(this.hat);
					this.hat.animateModel(entity, f, g, h);
					this.hat.setAngles(entity, f, g, j, k, l);
					this.hat.renderToBuffer(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F);
				}
				if (!entity.isSheared()) {
					this.getParentModel().copyPropertiesTo(this.wool);
					this.wool.prepareMobModel(entity, f, g, h);
					this.wool.setAngles(entity, f, g, j, k, l);
					this.wool.renderToBuffer(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F);
				}
			}
		} else {
			float[] rgbColor = EggLayingWoolyPigEntity.getRgbColor(entity.getColor());
			if (!entity.isHatless()) {
				this.getParentModel().copyPropertiesTo(this.hat);
				this.hat.animateModel(entity, f, g, h);
				this.hat.setAngles(entity, f, g, j, k, l);
				coloredCutoutModelCopyLayerRender(this.getParentModel(), this.hat, getTexture(entity), matrixStack, vertexConsumerProvider, i, entity, f, g, j, k, l, h, rgbColor[0], rgbColor[1], rgbColor[2]);
			}
			if (!entity.isSheared()) {
				coloredCutoutModelCopyLayerRender(this.getParentModel(), this.wool, getTexture(entity), matrixStack, vertexConsumerProvider, i, entity, f, g, j, k, l, h, rgbColor[0], rgbColor[1], rgbColor[2]);
			}
		}
	}
	
	@Override
	public ResourceLocation getTextureLocation(EggLayingWoolyPigEntity entity) {
		return EggLayingWoolyPigEntityRenderer.TEXTURE;
	}
	
}
