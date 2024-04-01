package de.dafuqs.spectrum.blocks.fusion_shrine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FusionShrineBlockEntityRenderer<T extends FusionShrineBlockEntity> implements BlockEntityRenderer<T> {
	
	public FusionShrineBlockEntityRenderer(Context ctx) {
	
	}
	
	private static void renderFluid(VertexConsumer builder, Matrix4f pos, TextureAtlasSprite sprite, int light, int overlay, float x1, float x2, float y, float z1, float z2, int[] color) {
		// Convert block size to pixel size
		final double px1 = x1 * 16;
		final double px2 = x2 * 16;
		final double pz1 = z1 * 16;
		final double pz2 = z2 * 16;
		
		final float u1 = sprite.getU(px1);
		final float u2 = sprite.getU(px2);
		final float v1 = sprite.getV(pz1);
		final float v2 = sprite.getV(pz2);
		builder.vertex(pos, x1, y, z2).color(color[1], color[2], color[3], color[0]).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(0f, 1f, 0f).endVertex();
		builder.vertex(pos, x2, y, z2).color(color[1], color[2], color[3], color[0]).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(0f, 1f, 0f).endVertex();
		builder.vertex(pos, x2, y, z1).color(color[1], color[2], color[3], color[0]).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(0f, 1f, 0f).endVertex();
		builder.vertex(pos, x1, y, z1).color(color[1], color[2], color[3], color[0]).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(0f, 1f, 0f).endVertex();
	}
	
	public static int[] unpackColor(int color) {
		final int[] colors = new int[4];
		colors[0] = color >> 24 & 0xff; // alpha
		colors[1] = color >> 16 & 0xff; // red
		colors[2] = color >> 8 & 0xff; // green
		colors[3] = color & 0xff; // blue
		return colors;
	}
	
	@Override
	public void render(FusionShrineBlockEntity fusionShrineBlockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		// the fluid in the shrine
		FluidStack fluidVariant = fusionShrineBlockEntity.getFluidVariant();
		if (!fluidVariant.isEmpty()) {
			matrixStack.pushPose();
			//todoforge this works right
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidVariant.getFluid()).getStillTexture());
			int color = IClientFluidTypeExtensions.of(fluidVariant.getFluid()).getTintColor(fluidVariant.getFluid().defaultFluidState(), fusionShrineBlockEntity.getLevel(), fusionShrineBlockEntity.getBlockPos());
			int[] colors = unpackColor(color);

			renderFluid(vertexConsumerProvider.getBuffer(RenderType.translucent()), matrixStack.last().pose(), sprite, light, overlay, 0.125F, 0.875F, 0.9F, 0.125F, 0.875F, colors);
			matrixStack.popPose();
		}
		
		if (!fusionShrineBlockEntity.isEmpty()) {
			// the floating item stacks
			List<ItemStack> inventoryStacks = new ArrayList<>();
			
			for (int i = 0; i < fusionShrineBlockEntity.getContainerSize(); i++) {
				ItemStack stack = fusionShrineBlockEntity.getItem(i);
				if (!stack.isEmpty()) {
					inventoryStacks.add(stack);
				}
			}
			
			float time = fusionShrineBlockEntity.getLevel().getGameTime() % 500000 + tickDelta;
			double radiant = Math.toRadians(360.0F / inventoryStacks.size());
			float distance = 1.2F;
			
			for (int i = 0; i < inventoryStacks.size(); i++) {
				matrixStack.pushPose();
				double currentRadiant = radiant * i + (radiant * (time / 16.0) / (8.0F / inventoryStacks.size()));
				double height = Math.sin((time + currentRadiant) / 8.0) / 3.0; // item height
				matrixStack.translate(distance * Math.sin(currentRadiant) + 0.5, 1.5 + height, distance * Math.cos(currentRadiant) + 0.5); // position offset
				matrixStack.mulPose(Axis.YP.rotationDegrees((time) * 2)); // item stack rotation
				
				Minecraft.getInstance().getItemRenderer().renderStatic(inventoryStacks.get(i), ItemDisplayContext.GROUND, light, overlay, matrixStack, vertexConsumerProvider, fusionShrineBlockEntity.getLevel(), 0);
				matrixStack.popPose();
			}
		}
	}
	
}
