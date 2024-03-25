package de.dafuqs.spectrum.items.conditional;

import de.dafuqs.spectrum.api.item.GemstoneColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeItem;

public class GemstonePowderItem extends CloakedItem {
	
	protected final GemstoneColor gemstoneColor;
	
	public GemstonePowderItem(Properties settings, ResourceLocation cloakAdvancementIdentifier, GemstoneColor gemstoneColor) {
		super(settings, cloakAdvancementIdentifier, DyeItem.byColor(gemstoneColor.getDyeColor()));
		this.gemstoneColor = gemstoneColor;
	}
	
	public GemstoneColor getGemstoneColor() {
		return gemstoneColor;
	}
	
}
