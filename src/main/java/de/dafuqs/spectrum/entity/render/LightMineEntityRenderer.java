package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.entity.entity.LightMineEntity;
import de.dafuqs.spectrum.helpers.ColorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class LightMineEntityRenderer extends EntityRenderer<LightMineEntity> {

    public LightMineEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
    
    @Override
    public void render(LightMineEntity mine, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
	
		var age = mine.tickCount;
        var alpha = Mth.clamp(1 - Mth.lerp(tickDelta, mine.getVanishingProgress(age - 1), mine.getVanishingProgress(age)), 0F, 1F);
        var scaleFactor = Mth.sin((age + tickDelta) / 8F) / 6F + mine.getScaleOffset();
	
		matrices.mulPose(this.entityRenderDispatcher.cameraOrientation());
		matrices.mulPose(Axis.YP.rotationDegrees(180f));
		matrices.scale(scaleFactor, scaleFactor, 1);
		matrices.translate(-0.5F, -0.5F, 0);
	
		var consumer = vertexConsumers.getBuffer(RenderType.entityTranslucentCull(getTexture(mine)));
		var matrix = matrices.last();
		var positions = matrix.pose();
		var normals = matrix.normal();
	
		Vector3f color = ColorHelper.colorIntToVec(mine.getColor());
		consumer.vertex(positions, 0, 0, 0).color(color.x(), color.y(), color.z(), alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normals, 0, 1, 0).endVertex();
		consumer.vertex(positions, 1, 0, 0).color(color.x(), color.y(), color.z(), alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normals, 0, 1, 0).endVertex();
		consumer.vertex(positions, 1, 1, 0).color(color.x(), color.y(), color.z(), alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normals, 0, 1, 0).endVertex();
		consumer.vertex(positions, 0, 1, 0).color(color.x(), color.y(), color.z(), alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normals, 0, 1, 0).endVertex();
	
		matrices.popPose();
	
		super.render(mine, yaw, tickDelta, matrices, vertexConsumers, light);
	}
    
    @Override
    public ResourceLocation getTexture(LightMineEntity entity) {
        return entity.getTexture();
    }
    
}
