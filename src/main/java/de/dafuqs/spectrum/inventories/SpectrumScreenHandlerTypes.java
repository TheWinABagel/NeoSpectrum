package de.dafuqs.spectrum.inventories;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;

public class SpectrumScreenHandlerTypes {
	
	public static MenuType<PaintbrushScreenHandler> PAINTBRUSH;
	public static MenuType<WorkstaffScreenHandler> WORKSTAFF;

    public static MenuType<PedestalScreenHandler> PEDESTAL;
    public static MenuType<CraftingTabletScreenHandler> CRAFTING_TABLET;
    public static MenuType<RestockingChestScreenHandler> RESTOCKING_CHEST;
    public static MenuType<BedrockAnvilScreenHandler> BEDROCK_ANVIL;
    public static MenuType<ParticleSpawnerScreenHandler> PARTICLE_SPAWNER;
    public static MenuType<CompactingChestScreenHandler> COMPACTING_CHEST;
    public static MenuType<BlackHoleChestScreenHandler> BLACK_HOLE_CHEST;
    public static MenuType<PotionWorkshopScreenHandler> POTION_WORKSHOP;
    public static MenuType<ColorPickerScreenHandler> COLOR_PICKER;
    public static MenuType<CinderhearthScreenHandler> CINDERHEARTH;
    public static MenuType<FilteringScreenHandler> FILTERING;
	public static MenuType<BagOfHoldingScreenHandler> BAG_OF_HOLDING;

    public static MenuType<GenericSpectrumContainerScreenHandler> GENERIC_TIER1_9X3;
    public static MenuType<GenericSpectrumContainerScreenHandler> GENERIC_TIER2_9X3;
    public static MenuType<GenericSpectrumContainerScreenHandler> GENERIC_TIER3_9X3;

    public static MenuType<GenericSpectrumContainerScreenHandler> GENERIC_TIER1_9X6;
    public static MenuType<GenericSpectrumContainerScreenHandler> GENERIC_TIER2_9X6;
    public static MenuType<GenericSpectrumContainerScreenHandler> GENERIC_TIER3_9X6;

    public static MenuType<Spectrum3x3ContainerScreenHandler> GENERIC_TIER1_3X3;
	public static MenuType<Spectrum3x3ContainerScreenHandler> GENERIC_TIER2_3X3;
	public static MenuType<Spectrum3x3ContainerScreenHandler> GENERIC_TIER3_3X3;
	
	public static <T extends AbstractContainerMenu> MenuType<T> registerSimple(ResourceLocation id, MenuType.MenuSupplier<T> factory) {
		MenuType<T> type = new MenuType<>(factory, FeatureFlags.VANILLA_SET);
		return Registry.register(BuiltInRegistries.MENU, id, type);
	}
	
	public static <T extends AbstractContainerMenu> MenuType<T> registerExtended(ResourceLocation id, IContainerFactory<T> factory) {
		MenuType<T> type = new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS);
		return Registry.register(BuiltInRegistries.MENU, id, type);
	}
	
	public static void register() {
        PAINTBRUSH = registerSimple(SpectrumScreenHandlerIDs.PAINTBRUSH, PaintbrushScreenHandler::new);
        WORKSTAFF = registerSimple(SpectrumScreenHandlerIDs.WORKSTAFF, WorkstaffScreenHandler::new);

        PEDESTAL = registerExtended(SpectrumScreenHandlerIDs.PEDESTAL, PedestalScreenHandler::new);
        PARTICLE_SPAWNER = registerExtended(SpectrumScreenHandlerIDs.PARTICLE_SPAWNER, ParticleSpawnerScreenHandler::new);
        COMPACTING_CHEST = registerExtended(SpectrumScreenHandlerIDs.COMPACTING_CHEST, CompactingChestScreenHandler::new);
        BLACK_HOLE_CHEST = registerExtended(SpectrumScreenHandlerIDs.BLACK_HOLE_CHEST, BlackHoleChestScreenHandler::new);
        COLOR_PICKER = registerExtended(SpectrumScreenHandlerIDs.COLOR_PICKER, ColorPickerScreenHandler::new);
        CINDERHEARTH = registerExtended(SpectrumScreenHandlerIDs.CINDERHEARTH, CinderhearthScreenHandler::new);
		FILTERING = registerExtended(SpectrumScreenHandlerIDs.FILTERING, FilteringScreenHandler::new);
		BAG_OF_HOLDING = registerSimple(SpectrumScreenHandlerIDs.BAG_OF_HOLDING, BagOfHoldingScreenHandler::new);

        CRAFTING_TABLET = registerSimple(SpectrumScreenHandlerIDs.CRAFTING_TABLET, CraftingTabletScreenHandler::new);
        RESTOCKING_CHEST = registerSimple(SpectrumScreenHandlerIDs.RESTOCKING_CHEST, RestockingChestScreenHandler::new);
        BEDROCK_ANVIL = registerSimple(SpectrumScreenHandlerIDs.BEDROCK_ANVIL, BedrockAnvilScreenHandler::new);
        POTION_WORKSHOP = registerSimple(SpectrumScreenHandlerIDs.POTION_WORKSHOP, PotionWorkshopScreenHandler::new);

        GENERIC_TIER1_9X3 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER1_9x3, GenericSpectrumContainerScreenHandler::createGeneric9x3_Tier1);
        GENERIC_TIER2_9X3 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER2_9x3, GenericSpectrumContainerScreenHandler::createGeneric9x3_Tier2);
        GENERIC_TIER3_9X3 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER3_9x3, GenericSpectrumContainerScreenHandler::createGeneric9x3_Tier3);
		
		GENERIC_TIER1_9X6 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER1_9x6, GenericSpectrumContainerScreenHandler::createGeneric9x6_Tier1);
		GENERIC_TIER2_9X6 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER2_9x6, GenericSpectrumContainerScreenHandler::createGeneric9x6_Tier2);
		GENERIC_TIER3_9X6 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER3_9x6, GenericSpectrumContainerScreenHandler::createGeneric9x6_Tier3);
		
		GENERIC_TIER1_3X3 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER1_3X3, Spectrum3x3ContainerScreenHandler::createTier1);
		GENERIC_TIER2_3X3 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER2_3X3, Spectrum3x3ContainerScreenHandler::createTier2);
		GENERIC_TIER3_3X3 = registerSimple(SpectrumScreenHandlerIDs.GENERIC_TIER3_3X3, Spectrum3x3ContainerScreenHandler::createTier3);
	}
	
	public static void registerClient() {
		MenuScreens.register(SpectrumScreenHandlerTypes.PAINTBRUSH, PaintbrushScreen::new);
		MenuScreens.register(SpectrumScreenHandlerTypes.WORKSTAFF, WorkstaffScreen::new);

        MenuScreens.register(SpectrumScreenHandlerTypes.PEDESTAL, PedestalScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.CRAFTING_TABLET, CraftingTabletScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.RESTOCKING_CHEST, RestockingChestScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.BEDROCK_ANVIL, BedrockAnvilScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.PARTICLE_SPAWNER, ParticleSpawnerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.COMPACTING_CHEST, CompactingChestScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.BLACK_HOLE_CHEST, BlackHoleChestScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.POTION_WORKSHOP, PotionWorkshopScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.COLOR_PICKER, ColorPickerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.CINDERHEARTH, CinderhearthScreen::new);
		MenuScreens.register(SpectrumScreenHandlerTypes.FILTERING, FilteringScreen::new);
		MenuScreens.register(SpectrumScreenHandlerTypes.BAG_OF_HOLDING, ContainerScreen::new);

        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER1_9X3, SpectrumGenericContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER2_9X3, SpectrumGenericContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER3_9X3, SpectrumGenericContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER1_9X6, SpectrumGenericContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER2_9X6, SpectrumGenericContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER3_9X6, SpectrumGenericContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER1_3X3, Spectrum3x3ContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER2_3X3, Spectrum3x3ContainerScreen::new);
        MenuScreens.register(SpectrumScreenHandlerTypes.GENERIC_TIER3_3X3, Spectrum3x3ContainerScreen::new);
	}
	
}
