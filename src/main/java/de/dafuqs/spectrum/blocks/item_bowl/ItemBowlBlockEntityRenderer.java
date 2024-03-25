package de.dafuqs.spectrum.blocks.item_bowl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemBowlBlockEntityRenderer implements BlockEntityRenderer<ItemBowlBlockEntity> {
	
	final double radiant = Math.toRadians(360.0F);
	
	public ItemBowlBlockEntityRenderer(BlockEntityRendererProvider.Context renderContext) {
	
	}
	
	@Override
	public void render(ItemBowlBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		ItemStack stack = blockEntity.getItem(0);
		if (!stack.isEmpty()) {
			float time = blockEntity.getLevel().getGameTime() % 50000 + tickDelta;
			
			matrixStack.pushPose();
			double currentRadiant = radiant + (radiant * (time / 16.0) / 8.0F);
			double height = Math.sin((time + currentRadiant) / 8.0) / 7.0; // item height
			matrixStack.translate(0.5, 0.8 + height, 0.5); // position offset
			matrixStack.mulPose(Axis.YP.rotationDegrees(time * 2)); // item stack rotation
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, blockEntity.getLevel(), 0);
			matrixStack.popPose();
		}
	}
	
}