package de.dafuqs.spectrum.blocks.pedestal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipe;
import de.dafuqs.spectrum.registries.client.SpectrumRenderLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@OnlyIn(Dist.CLIENT)
public class PedestalBlockEntityRenderer<T extends PedestalBlockEntity> implements BlockEntityRenderer<T> {
	
	private final ResourceLocation GROUND_MARK = SpectrumCommon.locate("textures/misc/circle.png");
	private final ModelPart circle;
	
	public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		super();
		this.circle = getTexturedModelData().bakeRoot().getChild("circle");
	}
	
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		
		modelPartData.addOrReplaceChild("circle", CubeListBuilder.create(), PartPose.offset(8.0F, 0.1F, 8.0F));
		modelPartData.getChild("circle").addOrReplaceChild("circle2", CubeListBuilder.create().texOffs(0, 0).addBox(-32.0F, 0.0F, -29F, 64.0F, 0.0F, 64.0F), PartPose.rotation(0.0F, 0.0F, 0.0F));
		
		return LayerDefinition.create(modelData, 256, 256);
	}
	
	@Override
	public void render(T entity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		if (entity.getLevel() == null) {
			return;
		}
		
		// render floating item stacks
		Recipe<?> currentRecipe = entity.getCurrentRecipe();
		if (currentRecipe instanceof PedestalRecipe) {
			float time = entity.getLevel().getGameTime() % 50000 + tickDelta;
			circle.yRot = time / 25.0F;
			circle.render(matrixStack, vertexConsumerProvider.getBuffer(SpectrumRenderLayers.GlowInTheDarkRenderLayer.get(GROUND_MARK)), light, overlay);
			
			ItemStack outputItemStack = entity.getCurrentRecipe().getResultItem(entity.getLevel().registryAccess());
			
			matrixStack.pushPose();
			double height = Math.sin((time) / 8.0) / 6.0; // item height
			matrixStack.translate(0.5F, 1.3 + height, 0.5F); // position offset
			matrixStack.mulPose(Axis.YP.rotationDegrees((time) * 2)); // item stack rotation
			
			// fixed lighting because:
			// 1. light variable would always be 0 anyways (the pedestal is opaque, making the inside black)
			// 2. the floating item looks like a hologram
			Minecraft.getInstance().getItemRenderer().renderStatic(outputItemStack, ItemDisplayContext.GROUND, 15728768, overlay, matrixStack, vertexConsumerProvider, entity.getLevel(), 0);
			matrixStack.popPose();
		}
	}
	
}
