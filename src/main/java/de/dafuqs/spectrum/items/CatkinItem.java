package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.GemstoneColor;
import de.dafuqs.spectrum.items.conditional.GemstonePowderItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CatkinItem extends GemstonePowderItem {
	
	protected final boolean lucid;
	
	public CatkinItem(@NotNull GemstoneColor gemstoneColor, boolean lucid, Properties settings) {
		super(settings, SpectrumCommon.locate("endgame/grow_ominous_sapling"), gemstoneColor);
		this.lucid = lucid;
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return lucid;
	}
	
}
