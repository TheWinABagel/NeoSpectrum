package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.entity.entity.MagicProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MagicProjectileEntityRenderer extends EntityRenderer<MagicProjectileEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/experience_orb.png");
	private static final RenderType LAYER = RenderType.itemEntityTranslucentCull(TEXTURE);

	public MagicProjectileEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(MagicProjectileEntity magicProjectileEntity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		matrixStack.pushPose();
		Vector3f starColor = InkColor.of(magicProjectileEntity.getDyeColor()).getColor();
		
		double time = (magicProjectileEntity.level().getGameTime() % 24000) + tickDelta + RandomSource.create(magicProjectileEntity.getId()).nextInt(200);
		float scale = 0.75F + 0.1F * (float) Math.sin(time / 10);
		matrixStack.scale(scale, scale, scale);
		
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
		
		float h = 0.75F;
		float k = 1F;
		float l = 0F;
		float m = 0.25F;
		int s = (int) (starColor.x() * 255.0F);
		int t = (int) (starColor.y() * 255.0F);
		int u = (int) (starColor.z() * 255.0F);
		PoseStack.Pose entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();
		Matrix3f matrix3f = entry.normal();
		
		matrixStack.translate(0.0D, 0.10000000149011612D, 0.0D);
		matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		
		vertex(vertexConsumer, matrix4f, matrix3f, -0.5F, -0.25F, s, t, u, h, m, light);
		vertex(vertexConsumer, matrix4f, matrix3f, 0.5F, -0.25F, s, t, u, k, m, light);
		vertex(vertexConsumer, matrix4f, matrix3f, 0.5F, 0.75F, s, t, u, k, l, light);
		vertex(vertexConsumer, matrix4f, matrix3f, -0.5F, 0.75F, s, t, u, h, l, light);
		matrixStack.popPose();
	}
	
	private static void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
		vertexConsumer.vertex(positionMatrix, x, y, 0.0F).color(red, green, blue, 128).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(MagicProjectileEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
	
}
