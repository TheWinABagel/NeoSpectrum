package de.dafuqs.spectrum.blocks.enchanter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.helpers.ExperienceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EnchanterBlockEntityRenderer implements BlockEntityRenderer<de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntity> {

	protected static final double ITEM_STACK_RENDER_HEIGHT = 0.95F;
	
	protected static RenderType layer;
	protected static ResourceLocation texture;
	protected static EntityRenderDispatcher dispatcher;
	
	public EnchanterBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		texture = new ResourceLocation("textures/entity/experience_orb.png");
		layer = RenderType.entityTranslucent(texture);
		dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
	}
	
	private static void vertex(@NotNull VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
		vertexConsumer.vertex(positionMatrix, x, y, 0.0F).color(red, green, blue, 128).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
	}
	
	@Override
	public void render(EnchanterBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		
		// The item lying on top of the enchanter
		ItemStack stack = blockEntity.getItem(0);
		if (!stack.isEmpty() && blockEntity.getItemFacingDirection() != null) {
			Direction itemFacingDirection = blockEntity.getItemFacingDirection();
			
			matrixStack.pushPose();
			// item stack rotation
			switch (itemFacingDirection) {
				case NORTH -> {
					matrixStack.translate(0.5, ITEM_STACK_RENDER_HEIGHT, 0.7);
					matrixStack.mulPose(Axis.XP.rotationDegrees(270));
					matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				}
				case SOUTH -> { // perfect
					matrixStack.translate(0.5, ITEM_STACK_RENDER_HEIGHT, 0.3);
					matrixStack.mulPose(Axis.XP.rotationDegrees(90));
				}
				case EAST -> {
					matrixStack.translate(0.3, ITEM_STACK_RENDER_HEIGHT, 0.5);
					matrixStack.mulPose(Axis.XP.rotationDegrees(90));
					matrixStack.mulPose(Axis.ZP.rotationDegrees(270));
				}
				case WEST -> {
					matrixStack.translate(0.7, ITEM_STACK_RENDER_HEIGHT, 0.5);
					matrixStack.mulPose(Axis.XP.rotationDegrees(270));
					matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
					matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				}
				default -> { }
			}
			
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, blockEntity.getLevel(), 0);
			matrixStack.popPose();
		}
		
		// The Experience Item rendered in the air
		ItemStack experienceItemStack = blockEntity.getItem(1);
		if (!experienceItemStack.isEmpty() && experienceItemStack.getItem() instanceof ExperienceStorageItem) {
			renderExperienceOrb(
					(float) (blockEntity.getLevel().getGameTime() % 50000) + tickDelta,
					ExperienceHelper.getExperienceOrbSizeForExperience(ExperienceStorageItem.getStoredExperience(experienceItemStack)),
					matrixStack, vertexConsumerProvider, LightTexture.FULL_BRIGHT);
		}
	}
	
	public void renderExperienceOrb(float timeWithTickDelta, int orbSize, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
		matrixStack.pushPose();
		
		float h = (float) (orbSize % 4 * 16) / 64.0F;
		float k = (float) (orbSize % 4 * 16 + 16) / 64.0F;
		float l = (float) (orbSize / 4 * 16) / 64.0F;
		float m = (float) (orbSize / 4 * 16 + 16) / 64.0F;
		float r = timeWithTickDelta / 2.0F;
		int s = (int) ((Mth.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F);
		int u = (int) ((Mth.sin(r + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
		
		matrixStack.translate(0.5D, 2.5D, 0.5D);
		matrixStack.mulPose(dispatcher.camera.rotation());
		matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		
		float scale = 0.5F + (float) (Math.sin(timeWithTickDelta / 8.0) / 8.0);
		matrixStack.scale(scale, scale, scale);
		
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);
		PoseStack.Pose entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();
		Matrix3f matrix3f = entry.normal();
		
		vertex(vertexConsumer, matrix4f, matrix3f, -0.5F, -0.25F, s, 255, u, h, m, i);
		vertex(vertexConsumer, matrix4f, matrix3f, 0.5F, -0.25F, s, 255, u, k, m, i);
		vertex(vertexConsumer, matrix4f, matrix3f, 0.5F, 0.75F, s, 255, u, k, l, i);
		vertex(vertexConsumer, matrix4f, matrix3f, -0.5F, 0.75F, s, 255, u, h, l, i);
		
		matrixStack.popPose();
	}

}
