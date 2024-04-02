package de.dafuqs.spectrum.compat.ae2;

import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumGrowableBlock;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
		SMALL_CERTUS_QUARTZ_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(MapColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_CERTUS_QUARTZ_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_CERTUS_QUARTZ_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		CERTUS_QUARTZ_CLUSTER = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_CERTUS_QUARTZ_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		SMALL_FLUIX_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(Blocks.PURPLE_CONCRETE.defaultMapColor()).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_FLUIX_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_FLUIX_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		FLUIX_CLUSTER = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_FLUIX_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		
		PURE_CERTUS_QUARTZ_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.3F).sound(SoundType.GLASS));
		PURE_FLUIX_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.3F).sound(SoundType.GLASS));
		
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
		ItemBlockRenderTypes.setRenderLayer(SMALL_CERTUS_QUARTZ_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(LARGE_CERTUS_QUARTZ_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CERTUS_QUARTZ_CLUSTER, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(SMALL_FLUIX_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(LARGE_FLUIX_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(FLUIX_CLUSTER, RenderType.cutout());
	}


}
