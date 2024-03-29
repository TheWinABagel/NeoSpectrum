package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import de.dafuqs.spectrum.render.RenderingContext;
import de.dafuqs.spectrum.render.armor.BedrockArmorCapeModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(CapeLayer.class)
public abstract class CapeFeatureRendererMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public CapeFeatureRendererMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> ctx) {
		super(ctx);
	}

    /**
     * Renders a custom flap on the front of the Bedrock Armor, as well as a custom cape render
     */
	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At("HEAD"), cancellable = true)
	public void spectrum$renderBedrockCape(PoseStack ms, MultiBufferSource vertices, int light, AbstractClientPlayer player, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// If the player has disabled their cape from rendering, do not render
		if (!player.isModelPartShown(PlayerModelPart.CAPE)) return;

		// Check for the chestplate, and begin rendering the cape if equipped
		if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() == SpectrumItems.BEDROCK_CHESTPLATE) {

			// Vanilla cape values
			double x = Mth.rotLerp(h / 2, (float) player.xCloakO, (float) player.xCloak)
					- Mth.rotLerp(h / 2, (float) player.xo, (float) player.getX());
			double y = Mth.rotLerp(h / 2, (float) player.yCloakO, (float) player.yCloak)
					- Mth.rotLerp(h / 2, (float) player.yo, (float) player.getY());
			double z = Mth.rotLerp(h / 2, (float) player.zCloakO, (float) player.zCloak)
					- Mth.rotLerp(h / 2, (float) player.zo, (float) player.getZ());
			float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
			double o = Mth.sin(yaw * (float) (Math.PI / 180.0));
			double p = -Mth.cos(yaw * (float) (Math.PI / 180.0));
			float q = (float) y * 10.0F;
			q = Mth.clamp(q, -6.0F, 32.0F);
			float r = (float) (x * o + z * p) * 100.0F;
			r = Mth.clamp(r, 0.0F, 150.0F);
			float capeZOffset = (float) (x * p - z * o) * 100.0F;
			capeZOffset = Mth.clamp(capeZOffset, -20.0F, 20.0F);
			if (r < 0.0F) {
				r = 0.0F;
			}


			float t = Mth.lerp(h, player.oBob, player.bob);
			q += Mth.sin(Mth.lerp(h, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * t;

			if (player.isCrouching()) {
				q += 25.0F;
			}

			float frontCapeRotation = Mth.clamp(-(6.0F + r / 2.0F + q), -25, 0);

            // Transform and render front cloth
            VertexConsumer vertexConsumer = vertices.getBuffer(RenderType.entitySolid(SpectrumModelLayers.BEDROCK_ARMOR_LOCATION));
            ms.pushPose();
            ms.translate(0, 0.35, 0);
			ms.mulPose(Axis.XP.rotationDegrees(frontCapeRotation));
            if (!player.isCrouching()) {
				ms.mulPose(Axis.ZP.rotationDegrees(capeZOffset / 2.0F));
            }

            // Make some space for your legs if crouching
            ms.translate(0, -0.65, -0.15);
            if (player.isCrouching()) {
                ms.translate(0, 0.05, 0.35);
            }
            BedrockArmorCapeModel.FRONT_CLOTH.render(ms, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            ms.popPose();
			
			// Respect the players own cape, Elytras and Fabrics Render Event
			if (player.getCloakTextureLocation() != null || RenderingContext.isElytraRendered || !LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.invoker().allowCapeRender(player)) {
				return;
			}
			
			float backCapeRotation = Mth.clamp(6.0F + r / 2.0F + q, -30, 60);
			
			// Transform and render the custom cape
			ms.pushPose();
			ms.translate(0, -0.05, 0.0); // Push up and backwards, then rotate
			ms.mulPose(Axis.XP.rotationDegrees(backCapeRotation));
			ms.mulPose(Axis.ZP.rotationDegrees(capeZOffset / 2.0F));
			ms.mulPose(Axis.YP.rotationDegrees(180.0F - capeZOffset / 1.25F));
			ms.translate(0, 0.05, -0.325); // Move back down
			if (player.isCrouching()) {
				ms.translate(0, 0.15, 0.125);
			}
			
			BedrockArmorCapeModel.CAPE_MODEL.render(ms, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
			ms.popPose();
			
			// Cancel any other capes from rendering
			ci.cancel();
		}
	}
}
