package de.dafuqs.spectrum.compat.ae2;

import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumGrowableBlock;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import static de.dafuqs.spectrum.registries.SpectrumBlocks.registerBlockWithItem;

public class AE2Compat extends SpectrumIntegrationPacks.ModIntegrationPack {
	
	public static Block SMALL_CERTUS_QUARTZ_BUD;
	public static Block LARGE_CERTUS_QUARTZ_BUD;
	public static Block CERTUS_QUARTZ_CLUSTER;
	public static Block SMALL_FLUIX_BUD;
	public static Block LARGE_FLUIX_BUD;
	public static Block FLUIX_CLUSTER;
	
	public static Block PURE_CERTUS_QUARTZ_BLOCK;
	public static Block PURE_FLUIX_BLOCK;
	
	public static Item PURE_CERTUS_QUARTZ;
	public static Item PURE_FLUIX;
	
	@Override
	public void register() {
		// BLOCKS
		SMALL_CERTUS_QUARTZ_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(MapColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_CERTUS_QUARTZ_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_CERTUS_QUARTZ_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		CERTUS_QUARTZ_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_CERTUS_QUARTZ_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		SMALL_FLUIX_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(Blocks.PURPLE_CONCRETE.defaultMapColor()).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_FLUIX_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_FLUIX_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		FLUIX_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_FLUIX_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		
		PURE_CERTUS_QUARTZ_BLOCK = new Block(FabricBlockSettings.of().mapColor(MapColor.SAND).strength(0.3F).sound(SoundType.GLASS));
		PURE_FLUIX_BLOCK = new Block(FabricBlockSettings.of().mapColor(MapColor.SAND).strength(0.3F).sound(SoundType.GLASS));
		
		Item.Properties settings = SpectrumItems.IS.of();
		registerBlockWithItem("small_certus_quartz_bud", SMALL_CERTUS_QUARTZ_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("large_certus_quartz_bud", LARGE_CERTUS_QUARTZ_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("certus_quartz_cluster", CERTUS_QUARTZ_CLUSTER, settings, DyeColor.YELLOW);
		
		registerBlockWithItem("small_fluix_bud", SMALL_FLUIX_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("large_fluix_bud", LARGE_FLUIX_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("fluix_cluster", FLUIX_CLUSTER, settings, DyeColor.YELLOW);
		
		registerBlockWithItem("pure_certus_quartz_block", PURE_CERTUS_QUARTZ_BLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("pure_fluix_block", PURE_FLUIX_BLOCK, settings, DyeColor.YELLOW);
		
		// ITEMS
		PURE_CERTUS_QUARTZ = new Item(SpectrumItems.IS.of());
		PURE_FLUIX = new Item(SpectrumItems.IS.of());
		SpectrumItems.register("pure_certus_quartz", PURE_CERTUS_QUARTZ, DyeColor.YELLOW);
		SpectrumItems.register("pure_fluix", PURE_FLUIX, DyeColor.YELLOW);
	}
	
	@Override
	public void registerClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(SMALL_CERTUS_QUARTZ_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(LARGE_CERTUS_QUARTZ_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(CERTUS_QUARTZ_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SMALL_FLUIX_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(LARGE_FLUIX_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(FLUIX_CLUSTER, RenderType.cutout());
	}
	
}
