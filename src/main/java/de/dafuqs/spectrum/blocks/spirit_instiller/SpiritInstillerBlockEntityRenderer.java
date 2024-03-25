package de.dafuqs.spectrum.blocks.spirit_instiller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class SpiritInstillerBlockEntityRenderer implements BlockEntityRenderer<SpiritInstillerBlockEntity> {
	
	protected final double ITEM_STACK_RENDER_HEIGHT = 0.95F;
	
	public SpiritInstillerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
	
	}
	
	@Override
	public void render(SpiritInstillerBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		
		// The item lying on top of the spirit instiller
		ItemStack stack = blockEntity.getItem(0);
		if (!stack.isEmpty() && blockEntity.getMultiblockRotation() != null) {
			Rotation itemFacingDirection = blockEntity.getMultiblockRotation();
			
			matrixStack.pushPose();
			// item stack rotation
			switch (itemFacingDirection) {
				case NONE -> {
					matrixStack.translate(0.5, ITEM_STACK_RENDER_HEIGHT, 0.7);
					matrixStack.mulPose(Axis.XP.rotationDegrees(270));
					matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				}
				case CLOCKWISE_90 -> {
					matrixStack.translate(0.3, ITEM_STACK_RENDER_HEIGHT, 0.5);
					matrixStack.mulPose(Axis.XP.rotationDegrees(90));
					matrixStack.mulPose(Axis.ZP.rotationDegrees(270));
				}
				case CLOCKWISE_180 -> {
					matrixStack.translate(0.5, ITEM_STACK_RENDER_HEIGHT, 0.3);
					matrixStack.mulPose(Axis.XP.rotationDegrees(90));
				}
				case COUNTERCLOCKWISE_90 -> {
					matrixStack.translate(0.7, ITEM_STACK_RENDER_HEIGHT, 0.5);
					matrixStack.mulPose(Axis.XP.rotationDegrees(270));
					matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
					matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				}
			}
			
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, blockEntity.getLevel(), 0);
			matrixStack.popPose();
		}
		
	}
	
	
}
