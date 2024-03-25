package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.entity.entity.SpectrumFishingBobberEntity;
import de.dafuqs.spectrum.items.tools.SpectrumFishingRodItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class SpectrumFishingBobberEntityRenderer extends EntityRenderer<SpectrumFishingBobberEntity> {
	
	public SpectrumFishingBobberEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	public abstract RenderType getLayer(SpectrumFishingBobberEntity bobber);
	
	@Override
	public void render(SpectrumFishingBobberEntity bobber, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
		super.render(bobber, f, g, matrixStack, vertexConsumerProvider, i);
		
		Player playerEntity = bobber.getPlayerOwner();
		if (playerEntity != null) {
			matrixStack.pushPose();
			matrixStack.pushPose();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			PoseStack.Pose entry = matrixStack.last();
			Matrix4f matrix4f = entry.pose();
			Matrix3f matrix3f = entry.normal();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(getLayer(bobber));
			vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 0, 0, 1);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 0, 1, 1);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 1, 1, 0);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 1, 0, 0);
			matrixStack.popPose();
			int j = playerEntity.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
			ItemStack itemStack = playerEntity.getMainHandItem();
			if (!(itemStack.getItem() instanceof SpectrumFishingRodItem)) {
				j = -j;
			}
			
			float h = playerEntity.getAttackAnim(g);
			float k = Mth.sin(Mth.sqrt(h) * 3.1415927F);
			float l = Mth.lerp(g, playerEntity.yBodyRotO, playerEntity.yBodyRot) * 0.017453292F;
			double d = Mth.sin(l);
			double e = Mth.cos(l);
			double m = (double) j * 0.35D;
			double o;
			double p;
			double q;
			float r;
			double s;
			if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && playerEntity == Minecraft.getInstance().player) {
				s = 960.0D / this.entityRenderDispatcher.options.fov().get();
				Vec3 vec3d = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float) j * 0.525F, -0.1F);
				vec3d = vec3d.scale(s);
				vec3d = vec3d.yRot(k * 0.5F);
				vec3d = vec3d.xRot(-k * 0.7F);
				o = Mth.lerp(g, playerEntity.xo, playerEntity.getX()) + vec3d.x;
				p = Mth.lerp(g, playerEntity.yo, playerEntity.getY()) + vec3d.y;
				q = Mth.lerp(g, playerEntity.zo, playerEntity.getZ()) + vec3d.z;
				r = playerEntity.getEyeHeight();
			} else {
				o = Mth.lerp(g, playerEntity.xo, playerEntity.getX()) - e * m - d * 0.8D;
				p = playerEntity.yo + (double) playerEntity.getEyeHeight() + (playerEntity.getY() - playerEntity.yo) * (double) g - 0.45D;
				q = Mth.lerp(g, playerEntity.zo, playerEntity.getZ()) - d * m + e * 0.8D;
				r = playerEntity.isCrouching() ? -0.1875F : 0.0F;
			}
			
			s = Mth.lerp(g, bobber.xo, bobber.getX());
			double t = Mth.lerp(g, bobber.yo, bobber.getY()) + 0.25D;
			double u = Mth.lerp(g, bobber.zo, bobber.getZ());
			float v = (float) (o - s);
			float w = (float) (p - t) + r;
			float x = (float) (q - u);
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderType.lineStrip());
			PoseStack.Pose entry2 = matrixStack.last();
			
			for (int z = 0; z <= 16; ++z) {
				renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16));
			}
			
			matrixStack.popPose();
			super.render(bobber, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}
	
	private static float percentage(int value, int max) {
		return (float) value / (float) max;
	}
	
	private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
		buffer.vertex(matrix, x - 0.5F, (float) y - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float) u, (float) v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
	}
	
	private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, PoseStack.Pose matrices, float segmentStart, float segmentEnd) {
		float f = x * segmentStart;
		float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
		float h = z * segmentStart;
		float i = x * segmentEnd - f;
		float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
		float k = z * segmentEnd - h;
		float l = Mth.sqrt(i * i + j * j + k * k);
		i /= l;
		j /= l;
		k /= l;
		buffer.vertex(matrices.pose(), f, g, h).color(0, 0, 0, 255).normal(matrices.normal(), i, j, k).endVertex();
	}
	
}
