package de.dafuqs.spectrum.blocks.chests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
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
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CompactingChestBlockEntityRenderer implements BlockEntityRenderer<CompactingChestBlockEntity> {
	
	private static final Material SPRITE_IDENTIFIER = new Material(InventoryMenu.BLOCK_ATLAS, SpectrumCommon.locate("block/compacting_chest"));
	private final ModelPart root;
	private final ModelPart lid;
	private final ModelPart column;
	private final ModelPart lock;
	
	public CompactingChestBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		LayerDefinition texturedModelData = getTexturedModelData();
		root = texturedModelData.bakeRoot();
		lid = root.getChild("lid");
		column = root.getChild("column");
		lock = root.getChild("lock");
	}
	
	public static @NotNull LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		
		modelPartData.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 17).addBox(1.0F, 0.0F, 1.0F, 14.0F, 11.0F, 14.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		modelPartData.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 1.0F, 14.0F, 3.0F, 14.0F), PartPose.offset(0.0F, 13.0F, 0.0F));
		modelPartData.addOrReplaceChild("column", CubeListBuilder.create().texOffs(18, 32).addBox(7.0F, -7.0F, 7.0F, 2.0F, 7.0F, 2.0F), PartPose.offset(0.0F, 13.0F, 0.0F));
		modelPartData.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(34, 4).addBox(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 14.0F, 0.0F));
		
		return LayerDefinition.create(modelData, 64, 64);
	}
	
	@Override
	public void render(CompactingChestBlockEntity entity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light, int overlay) {
		Level world = entity.getLevel();
		boolean bl = world != null;
		BlockState blockState = bl ? entity.getBlockState() : SpectrumBlocks.COMPACTING_CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
		
		matrixStack.pushPose();
		float f = (blockState.getValue(ChestBlock.FACING)).toYRot();
		matrixStack.translate(0.5D, 0.5D, 0.5D);
		matrixStack.mulPose(Axis.YP.rotationDegrees(-f));
		matrixStack.translate(-0.5D, -0.5D, -0.5D);
		
		float openFactor = entity.getOpenNess(tickDelta);
		openFactor = 1.0F - openFactor;
		openFactor = 1.0F - openFactor * openFactor * openFactor;
		
		lid.y = 11 + openFactor * 6;
		column.y = 11 + openFactor * 6;
		lock.y = 11 + openFactor * 6;
		
		VertexConsumer vertexConsumer = SPRITE_IDENTIFIER.buffer(vertexConsumers, RenderType::entityCutout);
		
		root.render(matrixStack, vertexConsumer, light, overlay);
		
		matrixStack.popPose();
	}
	
}