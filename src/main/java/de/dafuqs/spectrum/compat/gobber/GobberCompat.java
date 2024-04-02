package de.dafuqs.spectrum.compat.gobber;

import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumGrowableBlock;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import static de.dafuqs.spectrum.registries.SpectrumBlocks.registerBlockWithItem;

public class GobberCompat extends SpectrumIntegrationPacks.ModIntegrationPack {
	
	public static Block SMALL_GLOBETTE_BUD;
	public static Block LARGE_GLOBETTE_BUD;
	public static Block GLOBETTE_CLUSTER;
	public static Block SMALL_GLOBETTE_NETHER_BUD;
	public static Block LARGE_GLOBETTE_NETHER_BUD;
	public static Block GLOBETTE_NETHER_CLUSTER;
	public static Block SMALL_GLOBETTE_END_BUD;
	public static Block LARGE_GLOBETTE_END_BUD;
	public static Block GLOBETTE_END_CLUSTER;
	
	public static Block PURE_GLOBETTE_BLOCK;
	public static Block PURE_GLOBETTE_NETHER_BLOCK;
	public static Block PURE_GLOBETTE_END_BLOCK;
	
	public static Item PURE_GLOBETTE;
	public static Item PURE_GLOBETTE_NETHER;
	public static Item PURE_GLOBETTE_END;
	
	@Override
	public void register() {
		// BLOCKS
		SMALL_GLOBETTE_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(Blocks.BLUE_CONCRETE.defaultMapColor()).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_GLOBETTE_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_GLOBETTE_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		GLOBETTE_CLUSTER = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_GLOBETTE_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		SMALL_GLOBETTE_NETHER_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(Blocks.RED_CONCRETE.defaultMapColor()).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_GLOBETTE_NETHER_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_GLOBETTE_NETHER_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		GLOBETTE_NETHER_CLUSTER = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_GLOBETTE_NETHER_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		SMALL_GLOBETTE_END_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).destroyTime(1.0f).mapColor(Blocks.GREEN_CONCRETE.defaultMapColor()).requiresCorrectToolForDrops().noOcclusion(), CrystallarieumGrowableBlock.GrowthStage.SMALL);
		LARGE_GLOBETTE_END_BUD = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_GLOBETTE_END_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
		GLOBETTE_END_CLUSTER = new CrystallarieumGrowableBlock(BlockBehaviour.Properties.copy(SMALL_GLOBETTE_END_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
		
		PURE_GLOBETTE_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK));
		PURE_GLOBETTE_NETHER_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK));
		PURE_GLOBETTE_END_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK));
		
		Item.Properties settings = SpectrumItems.IS.of();
		registerBlockWithItem("small_globette_bud", SMALL_GLOBETTE_BUD, settings, DyeColor.BLUE);
		registerBlockWithItem("large_globette_bud", LARGE_GLOBETTE_BUD, settings, DyeColor.BLUE);
		registerBlockWithItem("globette_cluster", GLOBETTE_CLUSTER, settings, DyeColor.BLUE);
		
		registerBlockWithItem("small_globette_nether_bud", SMALL_GLOBETTE_NETHER_BUD, settings, DyeColor.RED);
		registerBlockWithItem("large_globette_nether_bud", LARGE_GLOBETTE_NETHER_BUD, settings, DyeColor.RED);
		registerBlockWithItem("globette_nether_cluster", GLOBETTE_NETHER_CLUSTER, settings, DyeColor.RED);
		
		registerBlockWithItem("small_globette_end_bud", SMALL_GLOBETTE_END_BUD, settings, DyeColor.GREEN);
		registerBlockWithItem("large_globette_end_bud", LARGE_GLOBETTE_END_BUD, settings, DyeColor.GREEN);
		registerBlockWithItem("globette_end_cluster", GLOBETTE_END_CLUSTER, settings, DyeColor.GREEN);
		
		registerBlockWithItem("pure_globette_block", PURE_GLOBETTE_BLOCK, settings, DyeColor.BLUE);
		registerBlockWithItem("pure_globette_nether_block", PURE_GLOBETTE_NETHER_BLOCK, settings, DyeColor.RED);
		registerBlockWithItem("pure_globette_end_block", PURE_GLOBETTE_END_BLOCK, settings, DyeColor.GREEN);
		
		// ITEMS
		PURE_GLOBETTE = new Item(SpectrumItems.IS.of());
		PURE_GLOBETTE_NETHER = new Item(SpectrumItems.IS.of());
		PURE_GLOBETTE_END = new Item(SpectrumItems.IS.of());
		
		SpectrumItems.register("pure_globette", PURE_GLOBETTE, DyeColor.BLUE);
		SpectrumItems.register("pure_globette_nether", PURE_GLOBETTE_NETHER, DyeColor.RED);
		SpectrumItems.register("pure_globette_end", PURE_GLOBETTE_END, DyeColor.GREEN);
	}
	
	@Override
	public void registerClient() {
		ItemBlockRenderTypes.setRenderLayer(SMALL_GLOBETTE_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(LARGE_GLOBETTE_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(GLOBETTE_CLUSTER, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(SMALL_GLOBETTE_END_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(LARGE_GLOBETTE_END_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(GLOBETTE_END_CLUSTER, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(SMALL_GLOBETTE_NETHER_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(LARGE_GLOBETTE_NETHER_BUD, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(GLOBETTE_NETHER_CLUSTER, RenderType.cutout());
	}
}
