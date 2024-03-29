package de.dafuqs.spectrum.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.KindlingEntity;
import de.dafuqs.spectrum.entity.models.KindlingEntityModel;
import de.dafuqs.spectrum.registries.client.SpectrumModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@OnlyIn(Dist.CLIENT)
public class KindlingEntityArmorFeatureRenderer extends RenderLayer<KindlingEntity, KindlingEntityModel> {
	
	public static final ResourceLocation TEXTURE_DIAMOND = SpectrumCommon.locate("textures/entity/kindling/armor/diamond.png");
	public static final ResourceLocation TEXTURE_GOLD = SpectrumCommon.locate("textures/entity/kindling/armor/gold.png");
	public static final ResourceLocation TEXTURE_IRON = SpectrumCommon.locate("textures/entity/kindling/armor/iron.png");
	public static final ResourceLocation TEXTURE_LEATHER = SpectrumCommon.locate("textures/entity/kindling/armor/leather.png");
	
	private final KindlingEntityModel model;
	
	public KindlingEntityArmorFeatureRenderer(RenderLayerParent<KindlingEntity, KindlingEntityModel> context, EntityModelSet loader) {
		super(context);
		this.model = new KindlingEntityModel(loader.bakeLayer(SpectrumModelLayers.KINDLING_ARMOR));
	}
	
	@Override
	public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, KindlingEntity kindlingEntity, float f, float g, float h, float j, float k, float l) {
		ItemStack itemStack = kindlingEntity.getArmor();
		
		if (itemStack.getItem() instanceof HorseArmorItem horseArmorItem) {
			this.getParentModel().copyPropertiesTo(this.model);
			this.model.prepareMobModel(kindlingEntity, f, g, h);
			this.model.setupAnim(kindlingEntity, f, g, j, k, l);
			float red;
			float green;
			float blue;
			if (horseArmorItem instanceof DyeableHorseArmorItem dyeableHorseArmorItem) {
				int color = dyeableHorseArmorItem.getColor(itemStack);
				red = (float) (color >> 16 & 255) / 255.0F;
				green = (float) (color >> 8 & 255) / 255.0F;
				blue = (float) (color & 255) / 255.0F;
			} else {
				red = 1.0F;
				green = 1.0F;
				blue = 1.0F;
			}
			
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutoutNoCull(getTextureForArmor(horseArmorItem)));
			this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
		}
	}
	
	public static ResourceLocation getTextureForArmor(HorseArmorItem item) {
		if (item == Items.DIAMOND_HORSE_ARMOR) {
			return TEXTURE_DIAMOND;
		}
		if (item == Items.GOLDEN_HORSE_ARMOR) {
			return TEXTURE_GOLD;
		}
		if (item == Items.IRON_HORSE_ARMOR) {
			return TEXTURE_IRON;
		}
		return TEXTURE_LEATHER;
	}
	
}
