package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.MonstrosityEntity;
import de.dafuqs.spectrum.entity.models.MonstrosityEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MonstrosityEntityRenderer extends MobRenderer<MonstrosityEntity, MonstrosityEntityModel> {
	
	public static final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/entity/monstrosity.png");
	
	public MonstrosityEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new MonstrosityEntityModel(context.bakeLayer(SpectrumModelLayers.MONSTROSITY)), 1.8F);
	}
	
	@Override
	public void render(MonstrosityEntity entity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
	}
	
	@Override
	public ResourceLocation getTextureLocation(MonstrosityEntity entity) {
		return TEXTURE;
	}
	
}
