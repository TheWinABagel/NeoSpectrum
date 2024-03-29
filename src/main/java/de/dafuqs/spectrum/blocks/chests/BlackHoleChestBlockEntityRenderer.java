package de.dafuqs.spectrum.blocks.chests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BlackHoleChestBlockEntityRenderer implements BlockEntityRenderer<BlackHoleChestBlockEntity> {
	
	private static final Material spriteIdentifier = new Material(InventoryMenu.BLOCK_ATLAS, SpectrumCommon.locate("block/black_block"));
	private final ModelPart root;
	
	public BlackHoleChestBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		LayerDefinition texturedModelData = getTexturedModelData();
		root = texturedModelData.bakeRoot();
	}
	
	public static @NotNull LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(1, 1).addBox(5.0F, 7.0F, 5.0F, 6.0F, 3.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		modelPartData.addOrReplaceChild("lid2", CubeListBuilder.create().texOffs(1, 1).addBox(7.0F, 4.0F, 7.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}

	@Override
	public void render(BlackHoleChestBlockEntity entity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light, int overlay) {
		matrixStack.pushPose();
		matrixStack.translate(0.5D, 0.5D, 0.5D);
		matrixStack.translate(-0.5D, -0.5D, -0.5D);

		float openFactor = entity.getOpenNess(tickDelta);
		openFactor = 1.0F - openFactor;
		openFactor = 1.0F - openFactor * openFactor * openFactor;

		root.y = openFactor * 5;
		
		VertexConsumer vertexConsumer = spriteIdentifier.buffer(vertexConsumers, RenderType::entityCutout);
		root.render(matrixStack, vertexConsumer, light, overlay);
		
		matrixStack.popPose();
	}
	
}