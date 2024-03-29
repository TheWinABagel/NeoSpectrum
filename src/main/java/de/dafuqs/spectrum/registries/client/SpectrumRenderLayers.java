package de.dafuqs.spectrum.registries.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.dafuqs.spectrum.mixin.client.accessors.RenderLayerAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class SpectrumRenderLayers {
	
	/**
	 * Render layer to be used in Spectrum's magical renderers.
	 * Like glow ink, it glows in the dark, always with max brightness
	 */
	public static class GlowInTheDarkRenderLayer extends RenderType {
		
		public GlowInTheDarkRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
			super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
		}
		
		public static RenderType get(ResourceLocation texture) {
			RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TransparencyStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(NO_OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).createCompositeState(true);
			return RenderLayerAccessor.invokeCreate("spectrum_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, multiPhaseParameters);
		}
	}
	
}
