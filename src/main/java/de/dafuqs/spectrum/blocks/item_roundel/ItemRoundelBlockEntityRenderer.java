package de.dafuqs.spectrum.blocks.item_roundel;

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

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemRoundelBlockEntityRenderer<T extends ItemRoundelBlockEntity> implements BlockEntityRenderer<T> {
	
	private static final float distance = 0.29F;
	
	public ItemRoundelBlockEntityRenderer(BlockEntityRendererProvider.Context renderContext) {
	
	}
	
	@Override
	public void render(ItemRoundelBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		if (!blockEntity.isEmpty()) {
			// the floating item stacks
			List<ItemStack> inventoryStacks = new ArrayList<>();
			for (int i = 0; i < blockEntity.getContainerSize(); i++) {
				ItemStack stack = blockEntity.getItem(i);
				if (!stack.isEmpty()) {
					if (blockEntity.renderStacksAsIndividualItems()) {
						for (int j = 0; j < stack.getCount(); j++) {
							inventoryStacks.add(stack);
						}
					} else {
						inventoryStacks.add(stack);
					}
				}
			}
			
			float time = blockEntity.getLevel().getGameTime() % 24000 + tickDelta;
			double radiant = Math.toRadians(360.0F / inventoryStacks.size());
			
			for (int i = 0; i < inventoryStacks.size(); i++) {
				matrixStack.pushPose();
				
				double currentRadiant = radiant * i + (radiant * (time / 16.0) / (8.0F / inventoryStacks.size()));
				matrixStack.translate(distance * Math.sin(currentRadiant) + 0.5, 0.6, distance * Math.cos(currentRadiant) + 0.5); // position offset
				matrixStack.mulPose(Axis.YP.rotationDegrees((float) (i * 360 / inventoryStacks.size()) + (time / 16 / 8 * 360))); // item stack rotation; takes 0..360
				
				Minecraft.getInstance().getItemRenderer().renderStatic(inventoryStacks.get(i), ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, blockEntity.getLevel(), 0);
				matrixStack.popPose();
			}
		}
	}
	
}