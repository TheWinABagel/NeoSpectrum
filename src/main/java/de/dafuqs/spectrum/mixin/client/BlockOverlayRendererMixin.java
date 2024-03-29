package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.dafuqs.spectrum.registries.SpectrumFluidTags;
import de.dafuqs.spectrum.registries.SpectrumFluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public abstract class BlockOverlayRendererMixin {

	@Inject(method = "renderScreenEffect", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void spectrum$renderFluidOverlay(Minecraft minecraftClient, PoseStack matrixStack, CallbackInfo ci) {
		if (!minecraftClient.player.isSpectator()) {
			if (minecraftClient.player.isEyeInFluid(SpectrumFluidTags.LIQUID_CRYSTAL)) {
				spectrum$renderOverlay(minecraftClient, matrixStack, SpectrumFluids.LIQUID_CRYSTAL_OVERLAY_TEXTURE, SpectrumFluids.LIQUID_CRYSTAL_OVERLAY_ALPHA);
			} else if (minecraftClient.player.isEyeInFluid(SpectrumFluidTags.MUD)) {
				spectrum$renderOverlay(minecraftClient, matrixStack, SpectrumFluids.MUD_OVERLAY_TEXTURE, SpectrumFluids.MUD_OVERLAY_ALPHA);
			} else if (minecraftClient.player.isEyeInFluid(SpectrumFluidTags.MIDNIGHT_SOLUTION)) {
				spectrum$renderOverlay(minecraftClient, matrixStack, SpectrumFluids.MIDNIGHT_SOLUTION_OVERLAY_TEXTURE, SpectrumFluids.MIDNIGHT_SOLUTION_OVERLAY_ALPHA);
			} else if (minecraftClient.player.isEyeInFluid(SpectrumFluidTags.DRAGONROT)) {
				spectrum$renderOverlay(minecraftClient, matrixStack, SpectrumFluids.DRAGONROT_OVERLAY_TEXTURE, SpectrumFluids.DRAGONROT_OVERLAY_ALPHA);
			}
		}
	}

	@Unique
	private static void spectrum$renderOverlay(Minecraft client, PoseStack matrixStack, ResourceLocation textureIdentifier, float alpha) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, textureIdentifier);
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		BlockPos blockPos = BlockPos.containing(client.player.getX(), client.player.getEyeY(), client.player.getZ());
		float f = LightTexture.getBrightness(client.player.level().dimensionType(), client.player.level().getMaxLocalRawBrightness(blockPos));
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(f, f, f, alpha);
		
		float m = -client.player.getYRot() / 64.0F;
		float n = client.player.getXRot() / 64.0F;
		Matrix4f matrix4f = matrixStack.last().pose();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).uv(4.0F + m, 4.0F + n).endVertex();
		bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).uv(0.0F + m, 4.0F + n).endVertex();
		bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).uv(0.0F + m, 0.0F + n).endVertex();
		bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).uv(4.0F + m, 0.0F + n).endVertex();
		BufferUploader.drawWithShader(bufferBuilder.end());
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
	}
	
}