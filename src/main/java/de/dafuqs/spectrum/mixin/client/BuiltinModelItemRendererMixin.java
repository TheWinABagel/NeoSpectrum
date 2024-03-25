package de.dafuqs.spectrum.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlock;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockType;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumWallSkullBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
	
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void getModel(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, CallbackInfo ci) {
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			if (block instanceof SpectrumSkullBlock || block instanceof SpectrumWallSkullBlock) {
				SpectrumSkullBlockType spectrumSkullBlockType = (SpectrumSkullBlockType) ((SpectrumSkullBlock) block).getType();
				RenderType renderLayer = SpectrumSkullBlockEntityRenderer.getRenderLayer(spectrumSkullBlockType);
				SkullModelBase model = SpectrumSkullBlockEntityRenderer.getModel(spectrumSkullBlockType);
				SpectrumSkullBlockEntityRenderer.renderSkull(null, 180.0F, 0.0F, matrices, vertexConsumers, light, model, renderLayer);
				ci.cancel();
			}
		}
	}
	
}
