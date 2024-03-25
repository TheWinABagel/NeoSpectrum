package de.dafuqs.spectrum.progression;

import de.dafuqs.spectrum.progression.advancement.*;
import net.minecraft.advancements.CriteriaTriggers;

public class SpectrumAdvancementCriteria {

	public static PedestalRecipeCalculatedCriterion PEDESTAL_RECIPE_CALCULATED;
	public static PedestalCraftingCriterion PEDESTAL_CRAFTING;
	public static FusionShrineCraftingCriterion FUSION_SHRINE_CRAFTING;
	public static CompletedMultiblockCriterion COMPLETED_MULTIBLOCK;
	public static BlockBrokenCriterion BLOCK_BROKEN;
	public static TreasureHunterDropCriterion TREASURE_HUNTER_DROP;
	public static NaturesStaffUseCriterion NATURES_STAFF_USE;
	public static EnchanterCraftingCriterion ENCHANTER_CRAFTING;
	public static EnchanterEnchantingCriterion ENCHANTER_ENCHANTING;
	public static EnchantmentUpgradedCriterion ENCHANTER_UPGRADING;
	public static InertiaUsedCriterion INERTIA_USED;
	public static AzureDikeChargeCriterion AZURE_DIKE_CHARGE;
	public static TrinketChangeCriterion TRINKET_CHANGE;
	public static PotionWorkshopBrewingCriterion POTION_WORKSHOP_BREWING;
	public static PotionWorkshopCraftingCriterion POTION_WORKSHOP_CRAFTING;
	public static TakeOffBeltJumpCriterion TAKE_OFF_BELT_JUMP;
	public static InkContainerInteractionCriterion INK_CONTAINER_INTERACTION;
	public static JeopardantKillCriterion JEOPARDANT_KILL;
	public static MemoryManifestingCriterion MEMORY_MANIFESTING;
	public static SpiritInstillerCraftingCriterion SPIRIT_INSTILLER_CRAFTING;
	public static SlimeSizingCriterion SLIME_SIZING;
	public static CrystalApothecaryCollectingCriterion CRYSTAL_APOTHECARY_COLLECTING;
	public static UpgradePlaceCriterion UPGRADE_PLACING;
	public static CrystallarieumGrownCriterion CRYSTALLARIEUM_GROWING;
	public static CinderhearthSmeltingCriterion CINDERHEARTH_SMELTING;
	public static InkProjectileKillingCriterion KILLED_BY_INK_PROJECTILE;
	public static SpectrumFishingRodHookedCriterion FISHING_ROD_HOOKED;
	public static TitrationBarrelTappingCriterion TITRATION_BARREL_TAPPING;
	public static ConfirmationButtonPressedCriterion CONFIRMATION_BUTTON_PRESSED;
	public static BloodOrchidPluckingCriterion BLOOD_ORCHID_PLUCKING;
	public static DivinityTickCriterion DIVINITY_TICK;
	public static ConsumedTeaWithSconeCriterion CONSUMED_TEA_WITH_SCONE;
	public static HummingstoneHymnCriterion CREATE_HUMMINGSTONE_HYMN;
	public static PastelNetworkCreatingCriterion PASTEL_NETWORK_CREATING;
	public static PreservationCheckCriterion PRESERVATION_CHECK;

	public static void register() {
		PEDESTAL_RECIPE_CALCULATED = CriteriaTriggers.register(new PedestalRecipeCalculatedCriterion());
		PEDESTAL_CRAFTING = CriteriaTriggers.register(new PedestalCraftingCriterion());
		FUSION_SHRINE_CRAFTING = CriteriaTriggers.register(new FusionShrineCraftingCriterion());
		COMPLETED_MULTIBLOCK = CriteriaTriggers.register(new CompletedMultiblockCriterion());
		BLOCK_BROKEN = CriteriaTriggers.register(new BlockBrokenCriterion());
		TREASURE_HUNTER_DROP = CriteriaTriggers.register(new TreasureHunterDropCriterion());
		NATURES_STAFF_USE = CriteriaTriggers.register(new NaturesStaffUseCriterion());
		ENCHANTER_CRAFTING = CriteriaTriggers.register(new EnchanterCraftingCriterion());
		ENCHANTER_ENCHANTING = CriteriaTriggers.register(new EnchanterEnchantingCriterion());
		ENCHANTER_UPGRADING = CriteriaTriggers.register(new EnchantmentUpgradedCriterion());
		INERTIA_USED = CriteriaTriggers.register(new InertiaUsedCriterion());
		AZURE_DIKE_CHARGE = CriteriaTriggers.register(new AzureDikeChargeCriterion());
		TRINKET_CHANGE = CriteriaTriggers.register(new TrinketChangeCriterion());
		POTION_WORKSHOP_BREWING = CriteriaTriggers.register(new PotionWorkshopBrewingCriterion());
		POTION_WORKSHOP_CRAFTING = CriteriaTriggers.register(new PotionWorkshopCraftingCriterion());
		TAKE_OFF_BELT_JUMP = CriteriaTriggers.register(new TakeOffBeltJumpCriterion());
		INK_CONTAINER_INTERACTION = CriteriaTriggers.register(new InkContainerInteractionCriterion());
		JEOPARDANT_KILL = CriteriaTriggers.register(new JeopardantKillCriterion());
		MEMORY_MANIFESTING = CriteriaTriggers.register(new MemoryManifestingCriterion());
		SPIRIT_INSTILLER_CRAFTING = CriteriaTriggers.register(new SpiritInstillerCraftingCriterion());
		SLIME_SIZING = CriteriaTriggers.register(new SlimeSizingCriterion());
		CRYSTAL_APOTHECARY_COLLECTING = CriteriaTriggers.register(new CrystalApothecaryCollectingCriterion());
		UPGRADE_PLACING = CriteriaTriggers.register(new UpgradePlaceCriterion());
		CRYSTALLARIEUM_GROWING = CriteriaTriggers.register(new CrystallarieumGrownCriterion());
		CINDERHEARTH_SMELTING = CriteriaTriggers.register(new CinderhearthSmeltingCriterion());
		KILLED_BY_INK_PROJECTILE = CriteriaTriggers.register(new InkProjectileKillingCriterion());
		FISHING_ROD_HOOKED = CriteriaTriggers.register(new SpectrumFishingRodHookedCriterion());
		TITRATION_BARREL_TAPPING = CriteriaTriggers.register(new TitrationBarrelTappingCriterion());
		CONFIRMATION_BUTTON_PRESSED = CriteriaTriggers.register(new ConfirmationButtonPressedCriterion());
		BLOOD_ORCHID_PLUCKING = CriteriaTriggers.register(new BloodOrchidPluckingCriterion());
		DIVINITY_TICK = CriteriaTriggers.register(new DivinityTickCriterion());
		CONSUMED_TEA_WITH_SCONE = CriteriaTriggers.register(new ConsumedTeaWithSconeCriterion());
		CREATE_HUMMINGSTONE_HYMN = CriteriaTriggers.register(new HummingstoneHymnCriterion());
		PASTEL_NETWORK_CREATING = CriteriaTriggers.register(new PastelNetworkCreatingCriterion());
		PRESERVATION_CHECK = CriteriaTriggers.register(new PreservationCheckCriterion());
	}
	
}