package de.dafuqs.spectrum.compat.REI.plugins;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.compat.REI.GatedRecipeDisplay;
import de.dafuqs.spectrum.compat.REI.SpectrumPlugins;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class NaturesStaffConversionsDisplay extends BasicDisplay implements GatedRecipeDisplay {
	
	public static final ResourceLocation UNLOCK_ADVANCEMENT_IDENTIFIER = SpectrumCommon.locate("unlocks/items/natures_staff");
	private final @Nullable ResourceLocation requiredAdvancementIdentifier;
	
	public NaturesStaffConversionsDisplay(EntryStack<?> in, EntryStack<?> out, @Nullable ResourceLocation requiredAdvancementIdentifier) {
		super(Collections.singletonList(EntryIngredient.of(in)), Collections.singletonList(EntryIngredient.of(out)));
		this.requiredAdvancementIdentifier = requiredAdvancementIdentifier;
	}
	
	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return SpectrumPlugins.NATURES_STAFF;
	}
	
	@Override
    public boolean isUnlocked() {
		Minecraft client = Minecraft.getInstance();
		return AdvancementHelper.hasAdvancement(client.player, this.requiredAdvancementIdentifier)
				&& AdvancementHelper.hasAdvancement(client.player, UNLOCK_ADVANCEMENT_IDENTIFIER);
	}
	
	@Override
	public boolean isSecret() {
		return false;
	}
	
}
