package de.dafuqs.spectrum.recipe.pedestal;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.GemstoneColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum PedestalRecipeTier {
	BASIC(SpectrumCommon.locate("place_pedestal"), new GemstoneColor[]{BuiltinGemstoneColor.CYAN, BuiltinGemstoneColor.MAGENTA, BuiltinGemstoneColor.YELLOW}),
	SIMPLE(SpectrumCommon.locate("build_basic_pedestal_structure"), new GemstoneColor[]{BuiltinGemstoneColor.CYAN, BuiltinGemstoneColor.MAGENTA, BuiltinGemstoneColor.YELLOW}),
	ADVANCED(SpectrumCommon.locate("midgame/build_advanced_pedestal_structure"), new GemstoneColor[]{BuiltinGemstoneColor.CYAN, BuiltinGemstoneColor.MAGENTA, BuiltinGemstoneColor.YELLOW, BuiltinGemstoneColor.BLACK}),
	COMPLEX(SpectrumCommon.locate("lategame/build_complex_pedestal_structure"), BuiltinGemstoneColor.values());
	
	private final ResourceLocation unlockAdvancementId;
	private final GemstoneColor[] gemstoneColors;
	
	PedestalRecipeTier(ResourceLocation unlockAdvancementId, GemstoneColor[] gemstoneColors) {
		this.unlockAdvancementId = unlockAdvancementId;
		this.gemstoneColors = gemstoneColors;
	}
	
	@Contract(pure = true)
	public int getPowderSlotCount() {
		return this.gemstoneColors.length;
	}
	
	@Contract(pure = true)
	public GemstoneColor[] getAvailableGemstoneColors() {
		return gemstoneColors;
	}
	
	@Contract(pure = true)
	public static Optional<PedestalRecipeTier> getHighestUnlockedRecipeTier(Player playerEntity) {
		if (AdvancementHelper.hasAdvancement(playerEntity, COMPLEX.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.COMPLEX);
		} else if (AdvancementHelper.hasAdvancement(playerEntity, ADVANCED.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.ADVANCED);
		} else if (AdvancementHelper.hasAdvancement(playerEntity, SIMPLE.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.SIMPLE);
		} else if (AdvancementHelper.hasAdvancement(playerEntity, BASIC.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.BASIC);
		}
		return Optional.empty();
	}
	
	public boolean hasUnlocked(Player playerEntity) {
		return AdvancementHelper.hasAdvancement(playerEntity, unlockAdvancementId);
	}
	
	public static Optional<PedestalRecipeTier> hasJustUnlockedANewRecipeTier(@NotNull ResourceLocation advancementIdentifier) {
		if (advancementIdentifier.equals(BASIC.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.BASIC);
		} else if (advancementIdentifier.equals(SIMPLE.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.SIMPLE);
		} else if (advancementIdentifier.equals(ADVANCED.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.ADVANCED);
		} else if (advancementIdentifier.equals(COMPLEX.unlockAdvancementId)) {
			return Optional.of(PedestalRecipeTier.COMPLEX);
		}
		return Optional.empty();
	}
	
}
