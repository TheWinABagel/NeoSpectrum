package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.PreservationTurretEntity;
import de.dafuqs.spectrum.entity.models.PreservationTurretEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class PreservationTurretEntityRenderer extends MobRenderer<PreservationTurretEntity, PreservationTurretEntityModel<PreservationTurretEntity>> {
	
	public static final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/entity/preservation_turret/preservation_turret.png");
	
	public PreservationTurretEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new PreservationTurretEntityModel<>(context.bakeLayer(SpectrumModelLayers.PRESERVATION_TURRET)), 0.0F);
	}
	
	@Override
	public ResourceLocation getTextureLocation(PreservationTurretEntity turretEntity) {
		return TEXTURE;
	}
	
	@Override
	protected void setupRotations(PreservationTurretEntity turretEntity, PoseStack matrixStack, float f, float g, float h) {
		super.setupRotations(turretEntity, matrixStack, f, g + 180.0F, h);
		matrixStack.translate(0.0, 0.5, 0.0);
		matrixStack.mulPose(turretEntity.getAttachedFace().getOpposite().getRotation());
		matrixStack.translate(0.0, -0.5, 0.0);
	}
	
}