package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.PlacedItemBlockEntity;
import de.dafuqs.spectrum.blocks.amphora.AmphoraBlockEntity;
import de.dafuqs.spectrum.blocks.block_flooder.BlockFlooderBlockEntity;
import de.dafuqs.spectrum.blocks.bottomless_bundle.BottomlessBundleBlockEntity;
import de.dafuqs.spectrum.blocks.chests.*;
import de.dafuqs.spectrum.blocks.cinderhearth.CinderhearthBlockEntity;
import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumBlockEntity;
import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.dd_deco.HummingstoneBlockEntity;
import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntity;
import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.ender.EnderDropperBlockEntity;
import de.dafuqs.spectrum.blocks.ender.EnderHopperBlockEntity;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntity;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.energy.CrystalApothecaryBlockEntity;
import de.dafuqs.spectrum.blocks.fusion_shrine.FusionShrineBlockEntity;
import de.dafuqs.spectrum.blocks.fusion_shrine.FusionShrineBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.item_bowl.ItemBowlBlockEntity;
import de.dafuqs.spectrum.blocks.item_bowl.ItemBowlBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.item_roundel.ItemRoundelBlockEntity;
import de.dafuqs.spectrum.blocks.item_roundel.ItemRoundelBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.jade_vines.JadeVineRootsBlockEntity;
import de.dafuqs.spectrum.blocks.jade_vines.JadeVineRootsBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.memory.MemoryBlockEntity;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockEntity;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerBlockEntity;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntity;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.potion_workshop.PotionWorkshopBlockEntity;
import de.dafuqs.spectrum.blocks.present.PresentBlockEntity;
import de.dafuqs.spectrum.blocks.redstone.*;
import de.dafuqs.spectrum.blocks.spirit_instiller.SpiritInstillerBlockEntity;
import de.dafuqs.spectrum.blocks.spirit_instiller.SpiritInstillerBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.spirit_sallow.OminousSaplingBlockEntity;
import de.dafuqs.spectrum.blocks.structure.PreservationBlockDetectorBlockEntity;
import de.dafuqs.spectrum.blocks.structure.PreservationControllerBlockEntity;
import de.dafuqs.spectrum.blocks.structure.PreservationRoundelBlockEntity;
import de.dafuqs.spectrum.blocks.structure.TreasureChestBlockEntity;
import de.dafuqs.spectrum.blocks.titration_barrel.TitrationBarrelBlockEntity;
import de.dafuqs.spectrum.blocks.upgrade.UpgradeBlock;
import de.dafuqs.spectrum.blocks.upgrade.UpgradeBlockBlockEntityRenderer;
import de.dafuqs.spectrum.blocks.upgrade.UpgradeBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public class SpectrumBlockEntities {
	
	public static BlockEntityType<OminousSaplingBlockEntity> OMINOUS_SAPLING;
	public static BlockEntityType<PedestalBlockEntity> PEDESTAL;
	public static BlockEntityType<FusionShrineBlockEntity> FUSION_SHRINE;
	public static BlockEntityType<EnchanterBlockEntity> ENCHANTER;

	public static BlockEntityType<ItemBowlBlockEntity> ITEM_BOWL;
	public static BlockEntityType<ItemRoundelBlockEntity> ITEM_ROUNDEL;
	public static BlockEntityType<EnderDropperBlockEntity> ENDER_DROPPER;
	public static BlockEntityType<EnderHopperBlockEntity> ENDER_HOPPER;
	public static BlockEntityType<ParticleSpawnerBlockEntity> PARTICLE_SPAWNER;
	public static BlockEntityType<UpgradeBlockEntity> UPGRADE_BLOCK;
	public static BlockEntityType<SpectrumSkullBlockEntity> SKULL;
	public static BlockEntityType<BottomlessBundleBlockEntity> BOTTOMLESS_BUNDLE;
	public static BlockEntityType<PotionWorkshopBlockEntity> POTION_WORKSHOP;
	public static BlockEntityType<CrystallarieumBlockEntity> CRYSTALLARIEUM;
	public static BlockEntityType<CinderhearthBlockEntity> CINDERHEARTH;
	
	public static BlockEntityType<CrystalApothecaryBlockEntity> CRYSTAL_APOTHECARY;
	public static BlockEntityType<ColorPickerBlockEntity> COLOR_PICKER;
	
	public static BlockEntityType<CompactingChestBlockEntity> COMPACTING_CHEST;
	public static BlockEntityType<RestockingChestBlockEntity> RESTOCKING_CHEST;
	public static BlockEntityType<HeartboundChestBlockEntity> HEARTBOUND_CHEST;
	public static BlockEntityType<BlackHoleChestBlockEntity> BLACK_HOLE_CHEST;
	public static BlockEntityType<TreasureChestBlockEntity> PRESERVATION_CHEST;
	public static BlockEntityType<AmphoraBlockEntity> AMPHORA;

	public static BlockEntityType<PlayerDetectorBlockEntity> PLAYER_DETECTOR;
	public static BlockEntityType<RedstoneCalculatorBlockEntity> REDSTONE_CALCULATOR;
	public static BlockEntityType<RedstoneTransceiverBlockEntity> REDSTONE_TRANSCEIVER;
	public static BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER;
	public static BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER;
	public static BlockEntityType<BlockFlooderBlockEntity> BLOCK_FLOODER;
	public static BlockEntityType<SpiritInstillerBlockEntity> SPIRIT_INSTILLER;
	public static BlockEntityType<MemoryBlockEntity> MEMORY;
	public static BlockEntityType<JadeVineRootsBlockEntity> JADE_VINE_ROOTS;
	public static BlockEntityType<PresentBlockEntity> PRESENT;
	public static BlockEntityType<TitrationBarrelBlockEntity> TITRATION_BARREL;
	public static BlockEntityType<PastelNodeBlockEntity> PASTEL_NODE;
	public static BlockEntityType<HummingstoneBlockEntity> HUMMINGSTONE;
	public static BlockEntityType<PlacedItemBlockEntity> PLACED_ITEM;

	public static BlockEntityType<PreservationControllerBlockEntity> PRESERVATION_CONTROLLER;
	public static BlockEntityType<PreservationRoundelBlockEntity> PRESERVATION_ROUNDEL;
	public static BlockEntityType<PreservationBlockDetectorBlockEntity> PRESERVATION_BLOCK_DETECTOR;
	
	private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpectrumCommon.locate(id), BlockEntityType.Builder.of(factory, blocks).build(null));
	}
	
	public static void register() {
		OMINOUS_SAPLING = register("ominous_sapling_block_entity", OminousSaplingBlockEntity::new, SpectrumBlocks.OMINOUS_SAPLING);
		PEDESTAL = register("pedestal_block_entity", PedestalBlockEntity::new, SpectrumBlocks.PEDESTAL_BASIC_AMETHYST, SpectrumBlocks.PEDESTAL_BASIC_TOPAZ, SpectrumBlocks.PEDESTAL_BASIC_CITRINE, SpectrumBlocks.PEDESTAL_ALL_BASIC, SpectrumBlocks.PEDESTAL_ONYX, SpectrumBlocks.PEDESTAL_MOONSTONE);
		FUSION_SHRINE = register("fusion_shrine_block_entity", FusionShrineBlockEntity::new, SpectrumBlocks.FUSION_SHRINE_BASALT, SpectrumBlocks.FUSION_SHRINE_CALCITE);
		ENCHANTER = register("enchanter_block_entity", EnchanterBlockEntity::new, SpectrumBlocks.ENCHANTER);
		ITEM_BOWL = register("item_bowl_block_entity", ItemBowlBlockEntity::new, SpectrumBlocks.ITEM_BOWL_BASALT, SpectrumBlocks.ITEM_BOWL_CALCITE);
		ITEM_ROUNDEL = register("item_roundel", ItemRoundelBlockEntity::new, SpectrumBlocks.ITEM_ROUNDEL);
		ENDER_DROPPER = register("ender_dropper", EnderDropperBlockEntity::new, SpectrumBlocks.ENDER_DROPPER);
		ENDER_HOPPER = register("ender_hopper", EnderHopperBlockEntity::new, SpectrumBlocks.ENDER_HOPPER);
		PARTICLE_SPAWNER = register("particle_spawner", ParticleSpawnerBlockEntity::new, SpectrumBlocks.PARTICLE_SPAWNER, SpectrumBlocks.CREATIVE_PARTICLE_SPAWNER);
		COMPACTING_CHEST = register("compacting_chest", CompactingChestBlockEntity::new, SpectrumBlocks.COMPACTING_CHEST);
		RESTOCKING_CHEST = register("restocking_chest", RestockingChestBlockEntity::new, SpectrumBlocks.RESTOCKING_CHEST);
		HEARTBOUND_CHEST = register("heartbound_chest", HeartboundChestBlockEntity::new, SpectrumBlocks.HEARTBOUND_CHEST);
		BLACK_HOLE_CHEST = register("black_hole_chest", BlackHoleChestBlockEntity::new, SpectrumBlocks.BLACK_HOLE_CHEST);
		PRESERVATION_CHEST = register("preservation_chest", TreasureChestBlockEntity::new, SpectrumBlocks.PRESERVATION_CHEST);
		AMPHORA = register("amphora", AmphoraBlockEntity::new, SpectrumBlocks.CHESTNUT_NOXWOOD_AMPHORA, SpectrumBlocks.EBONY_NOXWOOD_AMPHORA, SpectrumBlocks.SLATE_NOXWOOD_AMPHORA, SpectrumBlocks.IVORY_NOXWOOD_AMPHORA);
		PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlockEntity::new, SpectrumBlocks.PLAYER_DETECTOR);
		REDSTONE_CALCULATOR = register("redstone_calculator", RedstoneCalculatorBlockEntity::new, SpectrumBlocks.REDSTONE_CALCULATOR);
		REDSTONE_TRANSCEIVER = register("redstone_transceiver", RedstoneTransceiverBlockEntity::new, SpectrumBlocks.REDSTONE_TRANSCEIVER);
		BLOCK_PLACER = register("block_placer", BlockPlacerBlockEntity::new, SpectrumBlocks.BLOCK_PLACER);
		BLOCK_BREAKER = register("block_breaker", BlockBreakerBlockEntity::new, SpectrumBlocks.BLOCK_BREAKER);
		BLOCK_FLOODER = register("block_flooder", BlockFlooderBlockEntity::new, SpectrumBlocks.BLOCK_FLOODER);
		BOTTOMLESS_BUNDLE = register("bottomless_bundle", BottomlessBundleBlockEntity::new, SpectrumBlocks.BOTTOMLESS_BUNDLE);
		POTION_WORKSHOP = register("potion_workshop", PotionWorkshopBlockEntity::new, SpectrumBlocks.POTION_WORKSHOP);
		SPIRIT_INSTILLER = register("spirit_instiller", SpiritInstillerBlockEntity::new, SpectrumBlocks.SPIRIT_INSTILLER);
		MEMORY = register("memory", MemoryBlockEntity::new, SpectrumBlocks.MEMORY);
		JADE_VINE_ROOTS = register("jade_vine_roots", JadeVineRootsBlockEntity::new, SpectrumBlocks.JADE_VINE_ROOTS);
		CRYSTALLARIEUM = register("crystallarieum", CrystallarieumBlockEntity::new, SpectrumBlocks.CRYSTALLARIEUM);
		CRYSTAL_APOTHECARY = register("crystal_apothecary", CrystalApothecaryBlockEntity::new, SpectrumBlocks.CRYSTAL_APOTHECARY);
		COLOR_PICKER = register("color_picker", ColorPickerBlockEntity::new, SpectrumBlocks.COLOR_PICKER);
		CINDERHEARTH = register("cinderhearth", CinderhearthBlockEntity::new, SpectrumBlocks.CINDERHEARTH);
		PRESENT = register("present", PresentBlockEntity::new, SpectrumBlocks.PRESENT);
		TITRATION_BARREL = register("titration_barrel", TitrationBarrelBlockEntity::new, SpectrumBlocks.TITRATION_BARREL);
		PASTEL_NODE = register("pastel_node", PastelNodeBlockEntity::new, SpectrumBlocks.CONNECTION_NODE, SpectrumBlocks.PROVIDER_NODE, SpectrumBlocks.STORAGE_NODE, SpectrumBlocks.SENDER_NODE, SpectrumBlocks.GATHER_NODE);
		HUMMINGSTONE = register("hummingstone", HummingstoneBlockEntity::new, SpectrumBlocks.HUMMINGSTONE);
		PLACED_ITEM = register("placed_item", PlacedItemBlockEntity::new, SpectrumBlocks.INCANDESCENT_AMALGAM, SpectrumBlocks.THREAT_CONFLUX, SpectrumBlocks.PARAMETRIC_MINING_DEVICE);
		PRESERVATION_CONTROLLER = register("preservation_controller", PreservationControllerBlockEntity::new, SpectrumBlocks.PRESERVATION_CONTROLLER);
		PRESERVATION_ROUNDEL = register("preservation_roundel", PreservationRoundelBlockEntity::new, SpectrumBlocks.PRESERVATION_ROUNDEL);
		PRESERVATION_BLOCK_DETECTOR = register("preservation_block_detector", PreservationBlockDetectorBlockEntity::new, SpectrumBlocks.PRESERVATION_BLOCK_DETECTOR);
		
		// All the upgrades
		List<Block> upgradeBlocksList = UpgradeBlock.getUpgradeBlocks();
		Block[] upgradeBlocksArray = new Block[upgradeBlocksList.size()];
		upgradeBlocksArray = upgradeBlocksList.toArray(upgradeBlocksArray);
		UPGRADE_BLOCK = register("upgrade_block", UpgradeBlockEntity::new, upgradeBlocksArray);
		
		// All the skulls
		List<Block> skullBlocksList = new ArrayList<>();
		skullBlocksList.addAll(SpectrumBlocks.getMobHeads());
		skullBlocksList.addAll(SpectrumBlocks.getMobWallHeads());
		
		Block[] skullBlocksArray = new Block[skullBlocksList.size()];
		skullBlocksArray = skullBlocksList.toArray(skullBlocksArray);
		SKULL = register("skull", SpectrumSkullBlockEntity::new, skullBlocksArray);
	}
	
	public static void registerClient() {
		BlockEntityRenderers.register(SpectrumBlockEntities.PEDESTAL, PedestalBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.HEARTBOUND_CHEST, HeartboundChestBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.COMPACTING_CHEST, CompactingChestBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.RESTOCKING_CHEST, RestockingChestBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.PRESERVATION_CHEST, SpectrumChestBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.BLACK_HOLE_CHEST, BlackHoleChestBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.UPGRADE_BLOCK, UpgradeBlockBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.FUSION_SHRINE, FusionShrineBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.ENCHANTER, EnchanterBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.ITEM_BOWL, ItemBowlBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.ITEM_ROUNDEL, ItemRoundelBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.PRESERVATION_ROUNDEL, ItemRoundelBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.SKULL, SpectrumSkullBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.SPIRIT_INSTILLER, SpiritInstillerBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.JADE_VINE_ROOTS, JadeVineRootsBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.CRYSTALLARIEUM, CrystallarieumBlockEntityRenderer::new);
		BlockEntityRenderers.register(SpectrumBlockEntities.COLOR_PICKER, ColorPickerBlockEntityRenderer::new);
	}
	
}
