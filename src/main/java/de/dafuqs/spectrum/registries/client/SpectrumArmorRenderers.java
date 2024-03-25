package de.dafuqs.spectrum.registries.client;

import de.dafuqs.spectrum.items.armor.BedrockArmorItem;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.world.item.Item;

import java.util.List;

public class SpectrumArmorRenderers {
	
	public static final List<Item> BEDROCK_ARMOR =
			List.of(
					SpectrumItems.BEDROCK_HELMET,
					SpectrumItems.BEDROCK_CHESTPLATE,
					SpectrumItems.BEDROCK_LEGGINGS,
					SpectrumItems.BEDROCK_BOOTS
			);
	
	public static void register() {
		ArmorRenderer renderer = (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
			
			BedrockArmorItem armor = (BedrockArmorItem) stack.getItem();
			var model = armor.getArmorModel();
			var texture = armor.getArmorTexture(stack, slot);
			contextModel.copyPropertiesTo(model);
			
			ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, texture);
			
		};
		ArmorRenderer.register(renderer, BEDROCK_ARMOR.toArray(new Item[0]));
	}
	
}
