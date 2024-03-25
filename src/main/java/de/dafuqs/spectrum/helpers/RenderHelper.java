package de.dafuqs.spectrum.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderHelper {

	public static final int GREEN_COLOR = 3289650;
    protected static final BufferBuilder builder = Tesselator.getInstance().getBuilder();
	
	/**
	 * Draws a filled triangle
	 * Attention: The points specified have to be ordered in counter-clockwise order, or will now show up at all
	 */
	public static void fillTriangle(PoseStack matrices, int p1x, int p1y, int p2x, int p2y, int p3x, int p3y, Vector3f color) {
		Matrix4f matrix = matrices.last().pose();
		float red = color.x();
		float green = color.y();
		float blue = color.z();
		float alpha = 1.0F;
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		builder.vertex(matrix, p1x, p1y, 0F).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, p2x, p2y, 0F).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, p3x, p3y, 0F).color(red, green, blue, alpha).endVertex();
		BufferUploader.drawWithShader(builder.end());
		RenderSystem.disableBlend();
	}
	
	/**
	 * Draws a filled square
	 */
	public static void fillQuad(PoseStack matrices, int x, int y, int height, int width, Vector3f color) {
		Matrix4f matrix = matrices.last().pose();
		float red = color.x();
		float green = color.y();
		float blue = color.z();
		float alpha = 1.0F;
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		builder.vertex(matrix, x, y, 0F).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, x, y + height, 0F).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, x + width, y + height, 0F).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, x + width, y, 0F).color(red, green, blue, alpha).endVertex();
		BufferUploader.drawWithShader(builder.end());
		RenderSystem.disableBlend();
	}
	
}