package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.OnPrimordialFireComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {
    
    @Inject(method = "renderScreenEffect", at = @At(value = "HEAD"))
    private static void spectrum$renderPrimordialFire(Minecraft client, PoseStack matrices, CallbackInfo ci) {
        if (!client.player.isSpectator()) {
            if (OnPrimordialFireComponent.isOnPrimordialFire(client.player)) {
                renderPrimordialFireOverlay(client, matrices);
            }
        }
    }
    
    @Inject(method = "renderScreenEffect", at = @At(value = "HEAD"), cancellable = true)
    private static void spectrum$cancelFireOverlayWithPrimordialFire(Minecraft client, PoseStack matrices, CallbackInfo ci) {
        if (OnPrimordialFireComponent.isOnPrimordialFire(client.player)) {
            ci.cancel();
        }
    }

    // [VanillaCopy] uses different texture for fire overlay
    @Unique
    private static void renderPrimordialFireOverlay(Minecraft client, PoseStack matrices) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        //RenderSystem.enableTexture();
        TextureAtlasSprite sprite = client.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SpectrumCommon.locate("block/primordial_fire_1"));
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        float f = sprite.getU0();
        float g = sprite.getU1();
        float h = (f + g) / 2.0F;
        float i = sprite.getV0();
        float j = sprite.getV1();
        float k = (i + j) / 2.0F;
        float l = sprite.uvShrinkRatio();
        float m = Mth.lerp(l, f, h);
        float n = Mth.lerp(l, g, h);
        float o = Mth.lerp(l, i, k);
        float p = Mth.lerp(l, j, k);
        
        for (int r = 0; r < 2; ++r) {
            matrices.pushPose();
            matrices.translate(((float) (-(r * 2 - 1)) * 0.24F), -0.3, 0.0);
            matrices.mulPose(Axis.YP.rotationDegrees((float) (r * 2 - 1) * 10.0F));
            Matrix4f matrix4f = matrices.last().pose();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferBuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(n, p).endVertex();
            bufferBuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(m, p).endVertex();
            bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(m, o).endVertex();
            bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(n, o).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
            matrices.popPose();
        }
        
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
    
}
