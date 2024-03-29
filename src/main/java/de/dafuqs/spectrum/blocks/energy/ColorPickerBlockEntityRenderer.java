package de.dafuqs.spectrum.blocks.energy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class ColorPickerBlockEntityRenderer<T extends ColorPickerBlockEntity> implements BlockEntityRenderer<T> {
	
	public ColorPickerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
	
	}
	
	@Override
	public void render(ColorPickerBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		Minecraft client = Minecraft.getInstance();
		// The item on top
		ItemStack stack = blockEntity.getItem(0);
		ItemStack stack2 = blockEntity.getItem(1);
		// lying on top
		if (!stack.isEmpty()) {
			matrixStack.pushPose();
			matrixStack.translate(0.5, 0.7, 0.6);
			matrixStack.mulPose(Axis.XP.rotationDegrees(270));
			matrixStack.mulPose(Axis.YP.rotationDegrees(180));
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, blockEntity.getLevel(), 0);
			matrixStack.popPose();
		}
		// floating in air
		if (!stack2.isEmpty()) {
			matrixStack.pushPose();
			
			float time = blockEntity.getLevel().getGameTime() % 50000 + tickDelta;
			double height = Math.sin((time) / 8.0) / 6.0; // item height
			
			matrixStack.translate(0.5, 1.0 + height, 0.5);
			matrixStack.mulPose(client.getBlockEntityRenderDispatcher().camera.rotation());
			matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			Minecraft.getInstance().getItemRenderer().renderStatic(stack2, ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, blockEntity.getLevel(), 0);
			matrixStack.popPose();
		}
	}
	
}
