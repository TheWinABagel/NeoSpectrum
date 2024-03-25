package de.dafuqs.spectrum.items;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumStructureTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MysteriousCompassItem extends StructureCompassItem {

	private static final ResourceLocation REQUIRED_ADVANCEMENT = SpectrumCommon.locate("unlocks/mysterious_locket_socketing");

	public MysteriousCompassItem(Properties settings) {
		super(settings, SpectrumStructureTags.MYSTERIOUS_COMPASS_LOCATED);
	}
	
	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide && world.getGameTime() % 200 == 0 && entity instanceof Player player)
			if(AdvancementHelper.hasAdvancement(player, REQUIRED_ADVANCEMENT)) {
				locateStructure(stack, world, entity);
			} else {
				removeStructurePos(stack);
		}
	}

}
