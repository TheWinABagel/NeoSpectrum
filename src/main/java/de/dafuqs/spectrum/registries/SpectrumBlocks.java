package de.dafuqs.spectrum.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.color.ItemColors;
import de.dafuqs.spectrum.blocks.*;
import de.dafuqs.spectrum.blocks.amphora.AmphoraBlock;
import de.dafuqs.spectrum.blocks.block_flooder.BlockFlooderBlock;
import de.dafuqs.spectrum.blocks.boom.*;
import de.dafuqs.spectrum.blocks.bottomless_bundle.BottomlessBundleBlock;
import de.dafuqs.spectrum.blocks.chests.BlackHoleChestBlock;
import de.dafuqs.spectrum.blocks.chests.CompactingChestBlock;
import de.dafuqs.spectrum.blocks.chests.HeartboundChestBlock;
import de.dafuqs.spectrum.blocks.chests.RestockingChestBlock;
import de.dafuqs.spectrum.blocks.cinderhearth.CinderhearthBlock;
import de.dafuqs.spectrum.blocks.conditional.*;
import de.dafuqs.spectrum.blocks.conditional.amaranth.AmaranthBushelBlock;
import de.dafuqs.spectrum.blocks.conditional.amaranth.AmaranthCropBlock;
import de.dafuqs.spectrum.blocks.conditional.amaranth.PottedAmaranthBushelBlock;
import de.dafuqs.spectrum.blocks.conditional.blood_orchid.BloodOrchidBlock;
import de.dafuqs.spectrum.blocks.conditional.blood_orchid.PottedBloodOrchidBlock;
import de.dafuqs.spectrum.blocks.conditional.colored_tree.*;
import de.dafuqs.spectrum.blocks.conditional.resonant_lily.PottedResonantLilyBlock;
import de.dafuqs.spectrum.blocks.conditional.resonant_lily.ResonantLilyBlock;
import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumBlock;
import de.dafuqs.spectrum.blocks.crystallarieum.CrystallarieumGrowableBlock;
import de.dafuqs.spectrum.blocks.dd_deco.*;
import de.dafuqs.spectrum.blocks.decay.*;
import de.dafuqs.spectrum.blocks.decoration.*;
import de.dafuqs.spectrum.blocks.enchanter.EnchanterBlock;
import de.dafuqs.spectrum.blocks.ender.EnderDropperBlock;
import de.dafuqs.spectrum.blocks.ender.EnderHopperBlock;
import de.dafuqs.spectrum.blocks.energy.ColorPickerBlock;
import de.dafuqs.spectrum.blocks.energy.CrystalApothecaryBlock;
import de.dafuqs.spectrum.blocks.farming.ExtraTickFarmlandBlock;
import de.dafuqs.spectrum.blocks.farming.TilledShaleClayBlock;
import de.dafuqs.spectrum.blocks.farming.TilledSlushBlock;
import de.dafuqs.spectrum.blocks.fluid.DragonrotFluidBlock;
import de.dafuqs.spectrum.blocks.fluid.LiquidCrystalFluidBlock;
import de.dafuqs.spectrum.blocks.fluid.MidnightSolutionFluidBlock;
import de.dafuqs.spectrum.blocks.fluid.MudFluidBlock;
import de.dafuqs.spectrum.blocks.fusion_shrine.FusionShrineBlock;
import de.dafuqs.spectrum.blocks.gemstone.SpectrumBuddingBlock;
import de.dafuqs.spectrum.blocks.gemstone.SpectrumGemstoneBlock;
import de.dafuqs.spectrum.blocks.gravity.FloatBlock;
import de.dafuqs.spectrum.blocks.gravity.FloatBlockItem;
import de.dafuqs.spectrum.blocks.idols.*;
import de.dafuqs.spectrum.blocks.item_bowl.ItemBowlBlock;
import de.dafuqs.spectrum.blocks.item_roundel.ItemRoundelBlock;
import de.dafuqs.spectrum.blocks.jade_vines.*;
import de.dafuqs.spectrum.blocks.lava_sponge.LavaSpongeBlock;
import de.dafuqs.spectrum.blocks.lava_sponge.WetLavaSpongeBlock;
import de.dafuqs.spectrum.blocks.lava_sponge.WetLavaSpongeItem;
import de.dafuqs.spectrum.blocks.melon.AttachedGlisteringStemBlock;
import de.dafuqs.spectrum.blocks.melon.GlisteringMelonBlock;
import de.dafuqs.spectrum.blocks.melon.GlisteringStemBlock;
import de.dafuqs.spectrum.blocks.memory.MemoryBlock;
import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlock;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockItem;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlockType;
import de.dafuqs.spectrum.blocks.mob_head.SpectrumWallSkullBlock;
import de.dafuqs.spectrum.blocks.particle_spawner.CreativeParticleSpawnerBlock;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerBlock;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlock;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeType;
import de.dafuqs.spectrum.blocks.pedestal.BuiltinPedestalVariant;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlock;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockItem;
import de.dafuqs.spectrum.blocks.potion_workshop.PotionWorkshopBlock;
import de.dafuqs.spectrum.blocks.present.PresentBlock;
import de.dafuqs.spectrum.blocks.present.PresentItem;
import de.dafuqs.spectrum.blocks.redstone.*;
import de.dafuqs.spectrum.blocks.rock_candy.RockCandy;
import de.dafuqs.spectrum.blocks.rock_candy.SugarStickBlock;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStar;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStarBlock;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStarItem;
import de.dafuqs.spectrum.blocks.spirit_instiller.SpiritInstillerBlock;
import de.dafuqs.spectrum.blocks.spirit_sallow.*;
import de.dafuqs.spectrum.blocks.structure.*;
import de.dafuqs.spectrum.blocks.titration_barrel.TitrationBarrelBlock;
import de.dafuqs.spectrum.blocks.upgrade.UpgradeBlock;
import de.dafuqs.spectrum.blocks.upgrade.UpgradeBlockItem;
import de.dafuqs.spectrum.blocks.upgrade.Upgradeable;
import de.dafuqs.spectrum.blocks.weathering.Weathering;
import de.dafuqs.spectrum.blocks.weathering.WeatheringBlock;
import de.dafuqs.spectrum.blocks.weathering.WeatheringSlabBlock;
import de.dafuqs.spectrum.blocks.weathering.WeatheringStairsBlock;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.entity.entity.LivingMarkerEntity;
import de.dafuqs.spectrum.items.conditional.FourLeafCloverItem;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.recipe.pedestal.BuiltinGemstoneColor;
import de.dafuqs.spectrum.registries.SpectrumItems.IS;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.function.ToIntFunction;

import static de.dafuqs.spectrum.SpectrumCommon.locate;
import static net.minecraft.world.level.block.Blocks.SMALL_AMETHYST_BUD;
import static net.minecraft.world.level.block.Blocks.litBlockEmission;

public class SpectrumBlocks {

	private static Properties settings(MapColor mapColor, SoundType blockSoundGroup, float strength) {
		return FabricBlockSettings.of().mapColor(mapColor).sound(blockSoundGroup).strength(strength);
	}

	private static Properties settings(MapColor mapColor, SoundType blockSoundGroup, float strength, float resistance) {
		return settings(mapColor, blockSoundGroup, strength).explosionResistance(resistance);
	}

	private static Properties craftingBlock(MapColor mapColor, SoundType blockSoundGroup) {
		return settings(mapColor, blockSoundGroup, 5.0F, 8.0F).isRedstoneConductor(SpectrumBlocks::never).isViewBlocking(SpectrumBlocks::never).noOcclusion().requiresCorrectToolForDrops();
	}

	public static final Block PEDESTAL_BASIC_TOPAZ = new PedestalBlock(craftingBlock(MapColor.DIAMOND, SpectrumBlockSoundGroups.TOPAZ_BLOCK), BuiltinPedestalVariant.BASIC_TOPAZ);
	public static final Block PEDESTAL_BASIC_AMETHYST = new PedestalBlock(craftingBlock(MapColor.COLOR_PURPLE, SoundType.AMETHYST), BuiltinPedestalVariant.BASIC_AMETHYST);
	public static final Block PEDESTAL_BASIC_CITRINE = new PedestalBlock(craftingBlock(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.CITRINE_BLOCK), BuiltinPedestalVariant.BASIC_CITRINE);
	public static final Block PEDESTAL_ALL_BASIC = new PedestalBlock(craftingBlock(MapColor.COLOR_PURPLE, SoundType.AMETHYST), BuiltinPedestalVariant.CMY);
	public static final Block PEDESTAL_ONYX = new PedestalBlock(craftingBlock(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.ONYX_BLOCK), BuiltinPedestalVariant.ONYX);
	public static final Block PEDESTAL_MOONSTONE = new PedestalBlock(craftingBlock(MapColor.SNOW, SpectrumBlockSoundGroups.MOONSTONE_BLOCK), BuiltinPedestalVariant.MOONSTONE);

	public static final Block FUSION_SHRINE_BASALT = new FusionShrineBlock(craftingBlock(MapColor.COLOR_BLACK, SoundType.BASALT).lightLevel(value -> value.getValue(FusionShrineBlock.LIGHT_LEVEL)));
	public static final Block FUSION_SHRINE_CALCITE = new FusionShrineBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE).lightLevel(value -> value.getValue(FusionShrineBlock.LIGHT_LEVEL)));
	
	public static final Block ENCHANTER = new EnchanterBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final Block ITEM_BOWL_BASALT = new ItemBowlBlock(craftingBlock(MapColor.COLOR_BLACK, SoundType.BASALT));
	public static final Block ITEM_BOWL_CALCITE = new ItemBowlBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final Block ITEM_ROUNDEL = new ItemRoundelBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final Block POTION_WORKSHOP = new PotionWorkshopBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final Block SPIRIT_INSTILLER = new SpiritInstillerBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final CrystallarieumBlock CRYSTALLARIEUM = new CrystallarieumBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final Block CINDERHEARTH = new CinderhearthBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	
	public static final Block COLOR_PICKER = new ColorPickerBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	public static final Block CRYSTAL_APOTHECARY = new CrystalApothecaryBlock(craftingBlock(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE));
	
	private static Properties gemstone(MapColor mapColor, SoundType blockSoundGroup, int luminance) {
		return settings(mapColor, blockSoundGroup, 1.5F).forceSolidOn().noOcclusion().lightLevel((state) -> luminance).pushReaction(PushReaction.DESTROY);
	}
	
	private static Properties gemstoneBlock(MapColor mapColor, SoundType blockSoundGroup) {
		return settings(mapColor, blockSoundGroup, 1.5F).requiresCorrectToolForDrops();
	}
	
	public static final Block TOPAZ_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.TOPAZ_CLUSTER, 8));
	public static final Block LARGE_TOPAZ_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.LARGE_TOPAZ_BUD, 6));
	public static final Block MEDIUM_TOPAZ_BUD = new AmethystClusterBlock(4, 3, gemstone(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.MEDIUM_TOPAZ_BUD, 4));
	public static final Block SMALL_TOPAZ_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.SMALL_TOPAZ_BUD, 2));
	public static final Block BUDDING_TOPAZ = new SpectrumBuddingBlock(gemstoneBlock(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.TOPAZ_BLOCK).pushReaction(PushReaction.DESTROY).randomTicks(), SMALL_TOPAZ_BUD, MEDIUM_TOPAZ_BUD, LARGE_TOPAZ_BUD, TOPAZ_CLUSTER, SpectrumSoundEvents.BLOCK_TOPAZ_BLOCK_HIT, SpectrumSoundEvents.BLOCK_TOPAZ_BLOCK_CHIME);
	public static final Block TOPAZ_BLOCK = new SpectrumGemstoneBlock(gemstoneBlock(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.TOPAZ_BLOCK), SpectrumSoundEvents.BLOCK_TOPAZ_BLOCK_HIT, SpectrumSoundEvents.BLOCK_TOPAZ_BLOCK_CHIME);

	public static final Block CITRINE_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.CITRINE_CLUSTER, 9));
	public static final Block LARGE_CITRINE_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.LARGE_CITRINE_BUD, 7));
	public static final Block MEDIUM_CITRINE_BUD = new AmethystClusterBlock(4, 3, gemstone(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.MEDIUM_CITRINE_BUD, 5));
	public static final Block SMALL_CITRINE_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.SMALL_CITRINE_BUD, 3));
	public static final Block BUDDING_CITRINE = new SpectrumBuddingBlock(gemstoneBlock(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.CITRINE_BLOCK).pushReaction(PushReaction.DESTROY).randomTicks(), SMALL_CITRINE_BUD, MEDIUM_CITRINE_BUD, LARGE_CITRINE_BUD, CITRINE_CLUSTER, SpectrumSoundEvents.BLOCK_CITRINE_BLOCK_HIT, SpectrumSoundEvents.BLOCK_CITRINE_BLOCK_CHIME);
	public static final Block CITRINE_BLOCK = new SpectrumGemstoneBlock(gemstoneBlock(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.CITRINE_BLOCK), SpectrumSoundEvents.BLOCK_CITRINE_BLOCK_HIT, SpectrumSoundEvents.BLOCK_CITRINE_BLOCK_CHIME);
	public static final Block ONYX_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.ONYX_CLUSTER, 6));
	public static final Block LARGE_ONYX_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.LARGE_ONYX_BUD, 5));
	public static final Block MEDIUM_ONYX_BUD = new AmethystClusterBlock(4, 3, gemstone(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.MEDIUM_ONYX_BUD, 3));
	public static final Block SMALL_ONYX_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.SMALL_ONYX_BUD, 1));
	public static final Block BUDDING_ONYX = new SpectrumBuddingBlock(gemstoneBlock(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.ONYX_BLOCK).pushReaction(PushReaction.DESTROY).randomTicks(), SMALL_ONYX_BUD, MEDIUM_ONYX_BUD, LARGE_ONYX_BUD, ONYX_CLUSTER, SpectrumSoundEvents.BLOCK_ONYX_BLOCK_HIT, SpectrumSoundEvents.BLOCK_ONYX_BLOCK_CHIME);
	public static final Block ONYX_BLOCK = new SpectrumGemstoneBlock(gemstoneBlock(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.ONYX_BLOCK), SpectrumSoundEvents.BLOCK_ONYX_BLOCK_HIT, SpectrumSoundEvents.BLOCK_ONYX_BLOCK_CHIME);
	public static final Block MOONSTONE_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.SNOW, SpectrumBlockSoundGroups.MOONSTONE_CLUSTER, 15));
	public static final Block LARGE_MOONSTONE_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.SNOW, SpectrumBlockSoundGroups.LARGE_MOONSTONE_BUD, 12));
	public static final Block MEDIUM_MOONSTONE_BUD = new AmethystClusterBlock(4, 3, gemstone(MapColor.SNOW, SpectrumBlockSoundGroups.MEDIUM_MOONSTONE_BUD, 9));
	public static final Block SMALL_MOONSTONE_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.SNOW, SpectrumBlockSoundGroups.SMALL_MOONSTONE_BUD, 6));
	public static final Block BUDDING_MOONSTONE = new SpectrumBuddingBlock(gemstoneBlock(MapColor.SNOW, SpectrumBlockSoundGroups.MOONSTONE_BLOCK).pushReaction(PushReaction.DESTROY).randomTicks(), SMALL_MOONSTONE_BUD, MEDIUM_MOONSTONE_BUD, LARGE_MOONSTONE_BUD, MOONSTONE_CLUSTER, SpectrumSoundEvents.BLOCK_MOONSTONE_BLOCK_HIT, SpectrumSoundEvents.BLOCK_MOONSTONE_BLOCK_CHIME);
	public static final Block MOONSTONE_BLOCK = new SpectrumGemstoneBlock(gemstoneBlock(MapColor.SNOW, SpectrumBlockSoundGroups.MOONSTONE_BLOCK), SpectrumSoundEvents.BLOCK_MOONSTONE_BLOCK_HIT, SpectrumSoundEvents.BLOCK_MOONSTONE_BLOCK_CHIME);

	public static final Block TOPAZ_POWDER_BLOCK = new SandBlock(DyeColor.CYAN.getFireworkColor(), FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.COLOR_CYAN));
	public static final Block AMETHYST_POWDER_BLOCK = new SandBlock(DyeColor.MAGENTA.getFireworkColor(), FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.COLOR_MAGENTA));
	public static final Block CITRINE_POWDER_BLOCK = new SandBlock(DyeColor.YELLOW.getFireworkColor(), FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.COLOR_YELLOW));
	public static final Block ONYX_POWDER_BLOCK = new SandBlock(DyeColor.BLACK.getFireworkColor(), FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.COLOR_BLACK));
	public static final Block MOONSTONE_POWDER_BLOCK = new SandBlock(DyeColor.WHITE.getFireworkColor(), FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.SNOW));

	public static final Block VEGETAL_BLOCK = new Block(settings(MapColor.GRASS, SoundType.FUNGUS, 2.0F));
	public static final Block NEOLITH_BLOCK = new Block(settings(MapColor.COLOR_PURPLE, SoundType.COPPER, 6.0F).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM));
	public static final Block BEDROCK_STORAGE_BLOCK = new BlockWithTooltip(settings(MapColor.STONE, SoundType.STONE, 100.0F, 3600.0F).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM), Component.translatable("spectrum.tooltip.dragon_and_wither_immune"));
	
	public static final AmethystClusterBlock BISMUTH_CLUSTER = new BismuthClusterBlock(9, 3, null, gemstone(MapColor.WARPED_STEM, SoundType.CHAIN, 8));
	public static final AmethystClusterBlock LARGE_BISMUTH_BUD = new BismuthClusterBlock(5, 3, BISMUTH_CLUSTER, gemstone(MapColor.WARPED_STEM, SoundType.CHAIN, 6));
	public static final AmethystClusterBlock SMALL_BISMUTH_BUD = new BismuthClusterBlock(3, 4, LARGE_BISMUTH_BUD, gemstone(MapColor.WARPED_STEM, SoundType.CHAIN, 4));
	public static final Block BISMUTH_BLOCK = new Block(gemstoneBlock(MapColor.WARPED_STEM, SoundType.CHAIN));

	// DD BLOCKS
	private static final float BLACKSLAG_HARDNESS = 5.0F;
	private static final float BLACKSLAG_RESISTANCE = 7.0F;
	private static Properties blackslag(SoundType blockSoundGroup) {
		return settings(MapColor.COLOR_GRAY, blockSoundGroup, BLACKSLAG_HARDNESS, BLACKSLAG_RESISTANCE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops();
	}

	public static final Block BLACKSLAG = new BlackslagBlock(blackslag(SoundType.DEEPSLATE));
	public static final Block BLACKSLAG_STAIRS = new StairBlock(BLACKSLAG.defaultBlockState(), blackslag(SoundType.DEEPSLATE));
	public static final Block BLACKSLAG_SLAB = new SlabBlock(blackslag(SoundType.DEEPSLATE));
	public static final Block BLACKSLAG_WALL = new WallBlock(blackslag(SoundType.DEEPSLATE));
	public static final Block INFESTED_BLACKSLAG = new InfestedBlock(BLACKSLAG, blackslag(SoundType.DEEPSLATE));
	public static final Block COBBLED_BLACKSLAG = new Block(blackslag(SoundType.DEEPSLATE));
	public static final Block COBBLED_BLACKSLAG_STAIRS = new StairBlock(COBBLED_BLACKSLAG.defaultBlockState(), blackslag(SoundType.DEEPSLATE));
	public static final Block COBBLED_BLACKSLAG_SLAB = new SlabBlock(blackslag(SoundType.DEEPSLATE));
	public static final Block COBBLED_BLACKSLAG_WALL = new WallBlock(blackslag(SoundType.DEEPSLATE));
	public static final Block POLISHED_BLACKSLAG = new Block(blackslag(SoundType.POLISHED_DEEPSLATE));
	public static final Block POLISHED_BLACKSLAG_STAIRS = new StairBlock(POLISHED_BLACKSLAG.defaultBlockState(), Properties.copy(POLISHED_BLACKSLAG));
	public static final Block POLISHED_BLACKSLAG_SLAB = new SlabBlock(Properties.copy(POLISHED_BLACKSLAG));
	public static final Block POLISHED_BLACKSLAG_WALL = new WallBlock(Properties.copy(POLISHED_BLACKSLAG));
	public static final Block BLACKSLAG_TILES = new Block(blackslag(SoundType.DEEPSLATE_TILES));
	public static final Block BLACKSLAG_TILE_STAIRS = new StairBlock(BLACKSLAG_TILES.defaultBlockState(), Properties.copy(BLACKSLAG_TILES));
	public static final Block BLACKSLAG_TILE_SLAB = new SlabBlock(Properties.copy(BLACKSLAG_TILES));
	public static final Block BLACKSLAG_TILE_WALL = new WallBlock(Properties.copy(BLACKSLAG_TILES));
	public static final Block BLACKSLAG_BRICKS = new Block(blackslag(SoundType.DEEPSLATE_BRICKS));
	public static final Block BLACKSLAG_BRICK_STAIRS = new StairBlock(BLACKSLAG_BRICKS.defaultBlockState(), Properties.copy(BLACKSLAG_BRICKS));
	public static final Block BLACKSLAG_BRICK_SLAB = new SlabBlock(Properties.copy(BLACKSLAG_BRICKS));
	public static final Block BLACKSLAG_BRICK_WALL = new WallBlock(Properties.copy(BLACKSLAG_BRICKS));

	public static final Block POLISHED_BLACKSLAG_PILLAR = new RotatedPillarBlock(Properties.copy(BLACKSLAG_BRICKS));
	public static final Block CHISELED_POLISHED_BLACKSLAG = new Block(blackslag(SoundType.DEEPSLATE_BRICKS));
	public static final Block ANCIENT_CHISELED_POLISHED_BLACKSLAG = new Block(blackslag(SoundType.DEEPSLATE_BRICKS));

	public static final Block CRACKED_BLACKSLAG_BRICKS = new Block(Properties.copy(BLACKSLAG_BRICKS));
	public static final Block CRACKED_BLACKSLAG_TILES = new Block(Properties.copy(BLACKSLAG_TILES));
	public static final Block POLISHED_BLACKSLAG_BUTTON = new ButtonBlock(Properties.of().noCollission().strength(0.5F), SpectrumBlockSetTypes.POLISHED_BLACKSLAG, 5, false);
	public static final Block POLISHED_BLACKSLAG_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().noCollission().strength(0.5F), SpectrumBlockSetTypes.POLISHED_BLACKSLAG);

	public static final Block SHALE_CLAY = new WeatheringBlock(Weathering.WeatheringLevel.UNAFFECTED, blackslag(SoundType.MUD_BRICKS));
	public static final Block TILLED_SHALE_CLAY = new TilledShaleClayBlock(Properties.copy(SHALE_CLAY), SHALE_CLAY.defaultBlockState());
	public static final Block POLISHED_SHALE_CLAY = new WeatheringBlock(Weathering.WeatheringLevel.UNAFFECTED, Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_POLISHED_SHALE_CLAY = new WeatheringBlock(Weathering.WeatheringLevel.EXPOSED, Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_POLISHED_SHALE_CLAY = new WeatheringBlock(Weathering.WeatheringLevel.WEATHERED, Properties.copy(SHALE_CLAY));
	public static final Block POLISHED_SHALE_CLAY_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.UNAFFECTED, POLISHED_SHALE_CLAY.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block POLISHED_SHALE_CLAY_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.UNAFFECTED, Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_POLISHED_SHALE_CLAY_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.EXPOSED, EXPOSED_POLISHED_SHALE_CLAY.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_POLISHED_SHALE_CLAY_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.EXPOSED, Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_POLISHED_SHALE_CLAY_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.WEATHERED, WEATHERED_POLISHED_SHALE_CLAY.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_POLISHED_SHALE_CLAY_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.WEATHERED, Properties.copy(SHALE_CLAY));
	
	public static final Block SHALE_CLAY_BRICKS = new WeatheringBlock(Weathering.WeatheringLevel.UNAFFECTED, Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_SHALE_CLAY_BRICKS = new WeatheringBlock(Weathering.WeatheringLevel.EXPOSED, Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_SHALE_CLAY_BRICKS = new WeatheringBlock(Weathering.WeatheringLevel.WEATHERED, Properties.copy(SHALE_CLAY));
	public static final Block SHALE_CLAY_BRICK_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.UNAFFECTED, SHALE_CLAY_BRICKS.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block SHALE_CLAY_BRICK_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.UNAFFECTED, Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_SHALE_CLAY_BRICK_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.EXPOSED, EXPOSED_SHALE_CLAY_BRICKS.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_SHALE_CLAY_BRICK_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.EXPOSED, Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_SHALE_CLAY_BRICK_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.WEATHERED, WEATHERED_SHALE_CLAY_BRICKS.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_SHALE_CLAY_BRICK_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.WEATHERED, Properties.copy(SHALE_CLAY));
	
	public static final Block SHALE_CLAY_TILES = new WeatheringBlock(Weathering.WeatheringLevel.UNAFFECTED, Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_SHALE_CLAY_TILES = new WeatheringBlock(Weathering.WeatheringLevel.EXPOSED, Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_SHALE_CLAY_TILES = new WeatheringBlock(Weathering.WeatheringLevel.WEATHERED, Properties.copy(SHALE_CLAY));
	public static final Block SHALE_CLAY_TILE_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.UNAFFECTED, SHALE_CLAY_TILES.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block SHALE_CLAY_TILE_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.UNAFFECTED, Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_SHALE_CLAY_TILE_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.EXPOSED, EXPOSED_SHALE_CLAY_TILES.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block EXPOSED_SHALE_CLAY_TILE_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.EXPOSED, Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_SHALE_CLAY_TILE_STAIRS = new WeatheringStairsBlock(Weathering.WeatheringLevel.WEATHERED, WEATHERED_SHALE_CLAY_TILES.defaultBlockState(), Properties.copy(SHALE_CLAY));
	public static final Block WEATHERED_SHALE_CLAY_TILE_SLAB = new WeatheringSlabBlock(Weathering.WeatheringLevel.WEATHERED, Properties.copy(SHALE_CLAY));

	public static final Block PYRITE = new RotatedPillarBlock(settings(MapColor.TERRACOTTA_YELLOW, SoundType.CHAIN, 50.0F));
	public static final Block PYRITE_PILE = new RotatedPillarBlock(Properties.copy(PYRITE));
	public static final Block PYRITE_TILE = new Block(Properties.copy(PYRITE));
	public static final Block PYRITE_PLATING = new Block(Properties.copy(PYRITE));
	public static final Block PYRITE_TUBING = new RotatedPillarBlock(Properties.copy(PYRITE));
	public static final Block PYRITE_RELIEF = new RotatedPillarBlock(Properties.copy(PYRITE));
	public static final Block PYRITE_STACK = new Block(Properties.copy(PYRITE));
	public static final Block PYRITE_PANNELING = new Block(Properties.copy(PYRITE));
	public static final Block PYRITE_VENT = new Block(Properties.copy(PYRITE));
	public static final Block PYRITE_RIPPER = new PyriteRipperBlock(Properties.copy(PYRITE).noOcclusion().isValidSpawn(SpectrumBlocks::never).isViewBlocking(SpectrumBlocks::never));
	
	public static final Block DRAGONBONE = new DragonboneBlock(Properties.copy(Blocks.BONE_BLOCK).strength(-1.0F, 22.0F).pushReaction(PushReaction.BLOCK));
	public static final Block CRACKED_DRAGONBONE = new CrackedDragonboneBlock(Properties.copy(Blocks.BONE_BLOCK).strength(100.0F, 1200.0F).pushReaction(PushReaction.BLOCK));
	public static final Block POLISHED_BONE_ASH = new Block(FabricBlockSettings.copyOf(CRACKED_DRAGONBONE).destroyTime(1500.0F).mapColor(DyeColor.WHITE));
	public static final Block POLISHED_BONE_ASH_STAIRS = new StairBlock(POLISHED_BONE_ASH.defaultBlockState(), Properties.copy(POLISHED_BONE_ASH));
	public static final Block POLISHED_BONE_ASH_SLAB = new SlabBlock(Properties.copy(POLISHED_BONE_ASH));
	public static final Block POLISHED_BONE_ASH_WALL = new WallBlock(Properties.copy(POLISHED_BONE_ASH));
	public static final Block POLISHED_BONE_ASH_PILLAR = new RotatedPillarBlock(FabricBlockSettings.copyOf(POLISHED_BONE_ASH));
	public static final Block BONE_ASH_SHINGLES = new ShinglesBlock(FabricBlockSettings.copyOf(POLISHED_BONE_ASH).noOcclusion());
	
	public static final Block BONE_ASH_BRICKS = new Block(FabricBlockSettings.copyOf(POLISHED_BONE_ASH));
	public static final Block BONE_ASH_BRICK_STAIRS = new StairBlock(BONE_ASH_BRICKS.defaultBlockState(), Properties.copy(BONE_ASH_BRICKS));
	public static final Block BONE_ASH_BRICK_SLAB = new SlabBlock(Properties.copy(BONE_ASH_BRICKS));
	public static final Block BONE_ASH_BRICK_WALL = new WallBlock(Properties.copy(BONE_ASH_BRICKS));
	
	public static final Block BONE_ASH_TILES = new Block(FabricBlockSettings.copyOf(POLISHED_BONE_ASH));
	public static final Block BONE_ASH_TILE_STAIRS = new StairBlock(BONE_ASH_TILES.defaultBlockState(), Properties.copy(BONE_ASH_TILES));
	public static final Block BONE_ASH_TILE_SLAB = new SlabBlock(Properties.copy(BONE_ASH_TILES));
	public static final Block BONE_ASH_TILE_WALL = new WallBlock(Properties.copy(BONE_ASH_TILES));

	public static final Block SLUSH = new RotatedPillarBlock(blackslag(SoundType.MUDDY_MANGROVE_ROOTS));
	public static final Block TILLED_SLUSH = new TilledSlushBlock(Properties.copy(SLUSH), SLUSH.defaultBlockState());

	public static final Block BLACK_MATERIA = new BlackMateriaBlock(settings(MapColor.TERRACOTTA_BLACK, SoundType.SAND, 0.0F).instrument(NoteBlockInstrument.SNARE).randomTicks());
	public static final Block BLACK_SLUDGE = new Block(settings(MapColor.TERRACOTTA_BLACK, SoundType.SAND, 0.5F).instrument(NoteBlockInstrument.SNARE));
	public static final Block SAG_LEAF = new BlackSludgePlantBlock(FabricBlockSettings.copyOf(Blocks.GRASS).mapColor(MapColor.TERRACOTTA_BLACK));
	public static final Block SAG_BUBBLE = new BlackSludgePlantBlock(FabricBlockSettings.copyOf(Blocks.GRASS).mapColor(MapColor.TERRACOTTA_BLACK));
	public static final Block SMALL_SAG_BUBBLE = new BlackSludgePlantBlock(FabricBlockSettings.copyOf(Blocks.GRASS).mapColor(MapColor.TERRACOTTA_BLACK));
	
	public static final PrimordialFireBlock PRIMORDIAL_FIRE = new PrimordialFireBlock(Properties.copy(Blocks.FIRE).mapColor(MapColor.COLOR_PURPLE).lightLevel((state) -> 10));

	public static final Block SMOOTH_BASALT_SLAB = new SlabBlock(FabricBlockSettings.copyOf(Blocks.BASALT));
	public static final Block SMOOTH_BASALT_WALL = new WallBlock(FabricBlockSettings.copyOf(Blocks.BASALT));
	public static final Block SMOOTH_BASALT_STAIRS = new StairBlock(Blocks.BASALT.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.BASALT));

	public static final Block POLISHED_BASALT = new Block(settings(MapColor.COLOR_BLACK, SoundType.BASALT, 2.0F, 5.0F).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops());
	public static final Block PLANED_BASALT = new Block(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block POLISHED_BASALT_PILLAR = new RotatedPillarBlock(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block POLISHED_BASALT_CREST = new CardinalFacingBlock(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block CHISELED_POLISHED_BASALT = new Block(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block NOTCHED_POLISHED_BASALT = new Block(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block POLISHED_BASALT_SLAB = new SlabBlock(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block POLISHED_BASALT_WALL = new WallBlock(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block POLISHED_BASALT_STAIRS = new StairBlock(POLISHED_BASALT.defaultBlockState(), FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block BASALT_BRICKS = new Block(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block BASALT_BRICK_SLAB = new SlabBlock(FabricBlockSettings.copyOf(BASALT_BRICKS));
	public static final Block BASALT_BRICK_WALL = new WallBlock(FabricBlockSettings.copyOf(BASALT_BRICKS));
	public static final Block BASALT_BRICK_STAIRS = new StairBlock(BASALT_BRICKS.defaultBlockState(), FabricBlockSettings.copyOf(BASALT_BRICKS));
	public static final Block TOPAZ_CHISELED_BASALT = new Block(FabricBlockSettings.copyOf(BASALT_BRICKS).luminance(6));
	public static final Block AMETHYST_CHISELED_BASALT = new Block(FabricBlockSettings.copyOf(BASALT_BRICKS).luminance(5));
	public static final Block CITRINE_CHISELED_BASALT = new Block(FabricBlockSettings.copyOf(BASALT_BRICKS).luminance(7));
	public static final Block ONYX_CHISELED_BASALT = new Block(FabricBlockSettings.copyOf(BASALT_BRICKS).luminance(3));
	public static final Block MOONSTONE_CHISELED_BASALT = new SpectrumLineFacingBlock(FabricBlockSettings.copyOf(BASALT_BRICKS).luminance(12));
	
	public static final Block BASALT_TILES = new Block(FabricBlockSettings.copyOf(POLISHED_BASALT));
	public static final Block CRACKED_BASALT_TILES = new Block(FabricBlockSettings.copyOf(BASALT_TILES));
	public static final Block BASALT_TILE_STAIRS = new StairBlock(BASALT_TILES.defaultBlockState(), FabricBlockSettings.copyOf(BASALT_TILES));
	public static final Block BASALT_TILE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(BASALT_TILES));
	public static final Block BASALT_TILE_WALL = new WallBlock(FabricBlockSettings.copyOf(BASALT_TILES));
	public static final Block CRACKED_BASALT_BRICKS = new Block(FabricBlockSettings.copyOf(BASALT_BRICKS));
	public static final Block POLISHED_BASALT_BUTTON = new ButtonBlock(Properties.of().noCollission().strength(0.5F), SpectrumBlockSetTypes.POLISHED_BASALT, 5, false);
	public static final Block POLISHED_BASALT_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, Properties.of().mapColor(MapColor.COLOR_BLACK).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), SpectrumBlockSetTypes.POLISHED_BASALT);

	public static final Block CALCITE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(Blocks.CALCITE));
	public static final Block CALCITE_WALL = new WallBlock(FabricBlockSettings.copyOf(Blocks.CALCITE));
	public static final Block CALCITE_STAIRS = new StairBlock(Blocks.CALCITE.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.CALCITE));
	
	public static final Block POLISHED_CALCITE = new Block(settings(MapColor.TERRACOTTA_WHITE, SoundType.CALCITE, 2.0F, 5.0F).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops());
	public static final Block PLANED_CALCITE = new Block(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_STAIRS = new StairBlock(POLISHED_CALCITE.defaultBlockState(), FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_PILLAR = new RotatedPillarBlock(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_CREST = new CardinalFacingBlock(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block CHISELED_POLISHED_CALCITE = new Block(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block NOTCHED_POLISHED_CALCITE = new Block(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_WALL = new WallBlock(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block CALCITE_BRICKS = new Block(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block CALCITE_BRICK_STAIRS = new StairBlock(CALCITE_BRICKS.defaultBlockState(), FabricBlockSettings.copyOf(CALCITE_BRICKS));
	public static final Block CALCITE_BRICK_SLAB = new SlabBlock(FabricBlockSettings.copyOf(CALCITE_BRICKS));
	public static final Block CALCITE_BRICK_WALL = new WallBlock(FabricBlockSettings.copyOf(CALCITE_BRICKS));
	public static final Block TOPAZ_CHISELED_CALCITE = new Block(FabricBlockSettings.copyOf(CALCITE_BRICKS).luminance(6));
	public static final Block AMETHYST_CHISELED_CALCITE = new Block(FabricBlockSettings.copyOf(CALCITE_BRICKS).luminance(5));
	public static final Block CITRINE_CHISELED_CALCITE = new Block(FabricBlockSettings.copyOf(CALCITE_BRICKS).luminance(7));
	public static final Block ONYX_CHISELED_CALCITE = new Block(FabricBlockSettings.copyOf(CALCITE_BRICKS).luminance(3));
	public static final Block MOONSTONE_CHISELED_CALCITE = new SpectrumLineFacingBlock(FabricBlockSettings.copyOf(CALCITE_BRICKS).luminance(12));
	
	public static final Block CALCITE_TILES = new Block(FabricBlockSettings.copyOf(POLISHED_CALCITE));
	public static final Block CALCITE_TILE_STAIRS = new StairBlock(CALCITE_TILES.defaultBlockState(), FabricBlockSettings.copyOf(CALCITE_TILES));
	public static final Block CALCITE_TILE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(CALCITE_TILES));
	public static final Block CALCITE_TILE_WALL = new WallBlock(FabricBlockSettings.copyOf(CALCITE_TILES));
	public static final Block CRACKED_CALCITE_TILES = new Block(FabricBlockSettings.copyOf(CALCITE_TILES));
	public static final Block CRACKED_CALCITE_BRICKS = new Block(FabricBlockSettings.copyOf(CALCITE_BRICKS));
	public static final Block POLISHED_CALCITE_BUTTON = new ButtonBlock(Properties.of().noCollission().strength(0.5F), SpectrumBlockSetTypes.POLISHED_CALCITE, 5, false);
	public static final Block POLISHED_CALCITE_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), SpectrumBlockSetTypes.POLISHED_CALCITE);
	
	// GEMSTONE LAMPS
	private static Properties gemstoneLamp(BlockBehaviour block) {
		return FabricBlockSettings.copyOf(block).luminance(15).noOcclusion().forceSolidOn();
	}
	public static final Block TOPAZ_BASALT_LAMP = new Block(gemstoneLamp(POLISHED_BASALT));
	public static final Block AMETHYST_BASALT_LAMP = new Block(gemstoneLamp(POLISHED_BASALT));
	public static final Block CITRINE_BASALT_LAMP = new Block(gemstoneLamp(POLISHED_BASALT));
	public static final Block ONYX_BASALT_LAMP = new Block(gemstoneLamp(POLISHED_BASALT));
	public static final Block MOONSTONE_BASALT_LAMP = new Block(gemstoneLamp(POLISHED_BASALT));
	public static final Block TOPAZ_CALCITE_LAMP = new Block(gemstoneLamp(POLISHED_CALCITE));
	public static final Block AMETHYST_CALCITE_LAMP = new Block(gemstoneLamp(POLISHED_CALCITE));
	public static final Block CITRINE_CALCITE_LAMP = new Block(gemstoneLamp(POLISHED_CALCITE));
	public static final Block ONYX_CALCITE_LAMP = new Block(gemstoneLamp(POLISHED_CALCITE));
	public static final Block MOONSTONE_CALCITE_LAMP = new Block(gemstoneLamp(POLISHED_CALCITE));
	
	// GLASS
	private static Properties gemstoneGlass(SoundType soundGroup, MapColor mapColor) {
		return FabricBlockSettings.copyOf(Blocks.GLASS).sound(soundGroup).mapColor(mapColor);
	}
	public static final Block TOPAZ_GLASS = new GemstoneGlassBlock(gemstoneGlass(SpectrumBlockSoundGroups.TOPAZ_CLUSTER, MapColor.COLOR_CYAN), BuiltinGemstoneColor.CYAN);
	public static final Block AMETHYST_GLASS = new GemstoneGlassBlock(gemstoneGlass(SoundType.AMETHYST_CLUSTER, MapColor.COLOR_MAGENTA), BuiltinGemstoneColor.MAGENTA);
	public static final Block CITRINE_GLASS = new GemstoneGlassBlock(gemstoneGlass(SpectrumBlockSoundGroups.CITRINE_CLUSTER, MapColor.COLOR_YELLOW), BuiltinGemstoneColor.YELLOW);
	public static final Block ONYX_GLASS = new GemstoneGlassBlock(gemstoneGlass(SpectrumBlockSoundGroups.ONYX_CLUSTER, MapColor.COLOR_BLACK), BuiltinGemstoneColor.BLACK);
	public static final Block MOONSTONE_GLASS = new GemstoneGlassBlock(gemstoneGlass(SpectrumBlockSoundGroups.MOONSTONE_CLUSTER, MapColor.SNOW), BuiltinGemstoneColor.WHITE);
	public static final Block RADIANT_GLASS = new RadiantGlassBlock(gemstoneGlass(SoundType.GLASS, MapColor.SAND).lightLevel(value -> 12));
	
	public static final Block ETHEREAL_PLATFORM = new EtherealGlassBlock(gemstoneGlass(SoundType.AMETHYST, MapColor.NONE).pushReaction(PushReaction.NORMAL));
	public static final Block UNIVERSE_SPYHOLE = new GlassBlock(settings(MapColor.NONE, SpectrumBlockSoundGroups.CITRINE_BLOCK, 1.5F).requiresCorrectToolForDrops().isViewBlocking(SpectrumBlocks::never));

	private static Properties chime(BlockBehaviour block) {
		return FabricBlockSettings.copyOf(block).pushReaction(PushReaction.DESTROY).destroyTime(1.0F).noOcclusion();
	}
	public static final Block TOPAZ_CHIME = new GemstoneChimeBlock(chime(TOPAZ_CLUSTER), SpectrumSoundEvents.BLOCK_TOPAZ_BLOCK_CHIME, SpectrumParticleTypes.CYAN_SPARKLE_RISING);
	public static final Block AMETHYST_CHIME = new GemstoneChimeBlock(chime(Blocks.AMETHYST_CLUSTER), SoundEvents.AMETHYST_BLOCK_CHIME, SpectrumParticleTypes.MAGENTA_SPARKLE_RISING);
	public static final Block CITRINE_CHIME = new GemstoneChimeBlock(chime(CITRINE_CLUSTER), SpectrumSoundEvents.BLOCK_CITRINE_BLOCK_CHIME, SpectrumParticleTypes.YELLOW_SPARKLE_RISING);
	public static final Block ONYX_CHIME = new GemstoneChimeBlock(chime(ONYX_CLUSTER), SpectrumSoundEvents.BLOCK_ONYX_BLOCK_CHIME, SpectrumParticleTypes.BLACK_SPARKLE_RISING);
	public static final Block MOONSTONE_CHIME = new GemstoneChimeBlock(chime(MOONSTONE_CLUSTER), SpectrumSoundEvents.BLOCK_MOONSTONE_BLOCK_CHIME, SpectrumParticleTypes.WHITE_SPARKLE_RISING);

	private static Properties decostone(BlockBehaviour block) {
		return FabricBlockSettings.copyOf(block).noOcclusion();
	}
	public static final Block TOPAZ_DECOSTONE = new DecoStoneBlock(decostone(TOPAZ_BLOCK));
	public static final Block AMETHYST_DECOSTONE = new DecoStoneBlock(decostone(Blocks.AMETHYST_BLOCK));
	public static final Block CITRINE_DECOSTONE = new DecoStoneBlock(decostone(CITRINE_BLOCK));
	public static final Block ONYX_DECOSTONE = new DecoStoneBlock(decostone(ONYX_BLOCK));
	public static final Block MOONSTONE_DECOSTONE = new DecoStoneBlock(decostone(MOONSTONE_BLOCK));
	
	public static final Block SEMI_PERMEABLE_GLASS = new AlternatePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS), Blocks.GLASS, false);
	public static final Block TINTED_SEMI_PERMEABLE_GLASS = new AlternatePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(Blocks.TINTED_GLASS), Blocks.TINTED_GLASS, true);
	public static final Block RADIANT_SEMI_PERMEABLE_GLASS = new AlternatePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(SpectrumBlocks.RADIANT_GLASS), SpectrumBlocks.RADIANT_GLASS, false);
	public static final Block TOPAZ_SEMI_PERMEABLE_GLASS = new GemstonePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(SpectrumBlocks.TOPAZ_GLASS), BuiltinGemstoneColor.CYAN);
	public static final Block AMETHYST_SEMI_PERMEABLE_GLASS = new GemstonePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(SpectrumBlocks.AMETHYST_GLASS), BuiltinGemstoneColor.MAGENTA);
	public static final Block CITRINE_SEMI_PERMEABLE_GLASS = new GemstonePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(SpectrumBlocks.CITRINE_GLASS), BuiltinGemstoneColor.YELLOW);
	public static final Block ONYX_SEMI_PERMEABLE_GLASS = new GemstonePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(SpectrumBlocks.ONYX_GLASS), BuiltinGemstoneColor.BLACK);
	public static final Block MOONSTONE_SEMI_PERMEABLE_GLASS = new GemstonePlayerOnlyGlassBlock(FabricBlockSettings.copyOf(SpectrumBlocks.MOONSTONE_GLASS), BuiltinGemstoneColor.WHITE);
	
	// MELON
	public static final Block GLISTERING_MELON = new GlisteringMelonBlock(FabricBlockSettings.copyOf(Blocks.MELON));
	public static final Block GLISTERING_MELON_STEM = new GlisteringStemBlock((StemGrownBlock) GLISTERING_MELON, () -> SpectrumItems.GLISTERING_MELON_SEEDS, FabricBlockSettings.copyOf(Blocks.MELON_STEM));
	public static final Block ATTACHED_GLISTERING_MELON_STEM = new AttachedGlisteringStemBlock((StemGrownBlock) GLISTERING_MELON, () -> SpectrumItems.GLISTERING_MELON_SEEDS, FabricBlockSettings.copyOf(Blocks.ATTACHED_MELON_STEM));
	
	public static final Block OMINOUS_SAPLING = new OminousSaplingBlock(FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));
	public static final Block PRESENT = new PresentBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL));
	public static final Block TITRATION_BARREL = new TitrationBarrelBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_RED));
	
	public static final Block PARAMETRIC_MINING_DEVICE = new ParametricMiningDeviceBlock(FabricBlockSettings.copyOf(BLACKSLAG).noOcclusion().instabreak());
	public static final Block THREAT_CONFLUX = new ThreatConfluxBlock(FabricBlockSettings.copyOf(BLACKSLAG).noOcclusion().instabreak());
	
	public static final Block BLOCK_FLOODER = new BlockFlooderBlock(settings(MapColor.CLAY, SoundType.ROOTED_DIRT, 0.0F));
	public static final Block BOTTOMLESS_BUNDLE = new BottomlessBundleBlock(settings(MapColor.ICE, SoundType.WOOL, 1.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
	public static final Block WAND_LIGHT_BLOCK = new WandLightBlock(FabricBlockSettings.copyOf(Blocks.LIGHT).sound(SpectrumBlockSoundGroups.WAND_LIGHT).instabreak());
	public static final Block DECAYING_LIGHT_BLOCK = new DecayingLightBlock(FabricBlockSettings.copyOf(WAND_LIGHT_BLOCK).randomTicks());
	
	private static Properties decay(MapColor mapColor, SoundType soundGroup, float strength, float resistance, PushReaction pistonBehavior) {
		return settings(mapColor, soundGroup, strength, resistance).pushReaction(pistonBehavior).randomTicks().isValidSpawn((state, world, pos, type) -> false);
	}
	
	public static final Block FADING = new FadingBlock(decay(MapColor.PLANT, SoundType.GRASS, 0.5F, 0.5F, PushReaction.DESTROY));
	public static final Block FAILING = new FailingBlock(decay(MapColor.COLOR_BLACK, SoundType.STONE, 20.0F, 50.0F, PushReaction.BLOCK));
	public static final Block RUIN = new RuinBlock(decay(MapColor.COLOR_BLACK, SoundType.STONE, 100.0F, 3600000.0F, PushReaction.BLOCK));
	public static final Block FORFEITURE = new ForfeitureBlock(decay(MapColor.COLOR_BLACK, SoundType.STONE, 100.0F, 3600000.0F, PushReaction.BLOCK));
	public static final Block DECAY_AWAY = new DecayAwayBlock(FabricBlockSettings.copyOf(Blocks.DIRT).pushReaction(PushReaction.DESTROY));
	
	// FLUIDS
	private static Properties fluid(MapColor mapColor) {
		return settings(mapColor, SoundType.EMPTY, 100.0F).replaceable().noCollission().pushReaction(PushReaction.DESTROY).noLootTable().liquid();
	}
	
	public static final Block LIQUID_CRYSTAL = new LiquidCrystalFluidBlock(SpectrumFluids.LIQUID_CRYSTAL, fluid(MapColor.CRIMSON_STEM).lightLevel((state) -> LiquidCrystalFluidBlock.LUMINANCE).replaceable());
	public static final Block MUD = new MudFluidBlock(SpectrumFluids.MUD, fluid(MapColor.TERRACOTTA_BROWN).replaceable());
	public static final Block MIDNIGHT_SOLUTION = new MidnightSolutionFluidBlock(SpectrumFluids.MIDNIGHT_SOLUTION, fluid(MapColor.WARPED_STEM).replaceable());
	public static final Block DRAGONROT = new DragonrotFluidBlock(SpectrumFluids.DRAGONROT, fluid(MapColor.ICE).lightLevel((state) -> 15).replaceable());
	
	
	// ROCK CANDY
	private static Properties rockCandy(BlockBehaviour block) {
		return FabricBlockSettings.copyOf(block).pushReaction(PushReaction.DESTROY).destroyTime(0.5F).lightLevel(ROCK_CANDY_LUMINANCE).randomTicks();
	}
	
	private static final ToIntFunction<BlockState> ROCK_CANDY_LUMINANCE = state -> Math.max(15, state.getValue(BlockStateProperties.AGE_2) * 3 + (state.getValue(SugarStickBlock.LOGGED) == FluidLogging.State.LIQUID_CRYSTAL ? LiquidCrystalFluidBlock.LUMINANCE : 8));
	public static final Block SUGAR_STICK = new SugarStickBlock(rockCandy(SMALL_AMETHYST_BUD), RockCandy.RockCandyVariant.SUGAR);
	public static final Block TOPAZ_SUGAR_STICK = new SugarStickBlock(rockCandy(SpectrumBlocks.SMALL_TOPAZ_BUD), RockCandy.RockCandyVariant.TOPAZ);
	public static final Block AMETHYST_SUGAR_STICK = new SugarStickBlock(rockCandy(Blocks.SMALL_AMETHYST_BUD), RockCandy.RockCandyVariant.AMETHYST);
	public static final Block CITRINE_SUGAR_STICK = new SugarStickBlock(rockCandy(SpectrumBlocks.SMALL_CITRINE_BUD), RockCandy.RockCandyVariant.CITRINE);
	public static final Block ONYX_SUGAR_STICK = new SugarStickBlock(rockCandy(SpectrumBlocks.SMALL_ONYX_BUD), RockCandy.RockCandyVariant.ONYX);
	public static final Block MOONSTONE_SUGAR_STICK = new SugarStickBlock(rockCandy(SpectrumBlocks.SMALL_MOONSTONE_BUD), RockCandy.RockCandyVariant.MOONSTONE);
	
	// PASTEL NETWORK
	private static Properties pastelNode(SoundType soundGroup) {
		return settings(MapColor.NONE, soundGroup, 1.5F).noOcclusion().requiresCorrectToolForDrops();
	}
	
	public static final Block CONNECTION_NODE = new PastelNodeBlock(pastelNode(SoundType.AMETHYST_CLUSTER), PastelNodeType.CONNECTION);
	public static final Block PROVIDER_NODE = new PastelNodeBlock(pastelNode(SoundType.AMETHYST_CLUSTER), PastelNodeType.PROVIDER);
	public static final Block STORAGE_NODE = new PastelNodeBlock(pastelNode(SpectrumBlockSoundGroups.TOPAZ_CLUSTER), PastelNodeType.STORAGE);
	public static final Block SENDER_NODE = new PastelNodeBlock(pastelNode(SpectrumBlockSoundGroups.CITRINE_CLUSTER), PastelNodeType.SENDER);
	public static final Block GATHER_NODE = new PastelNodeBlock(pastelNode(SpectrumBlockSoundGroups.ONYX_CLUSTER), PastelNodeType.GATHER);
	
	public static final Block BLACK_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLACK_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLACK_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLACK_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLACK_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLACK_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLACK_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BLUE_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BLUE_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BLUE_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BLUE_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BLUE_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BLUE_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block BROWN_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block BROWN_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block BROWN_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block BROWN_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block BROWN_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block BROWN_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block CYAN_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block CYAN_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block CYAN_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block CYAN_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block CYAN_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block CYAN_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GRAY_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GRAY_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GRAY_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GRAY_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GRAY_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GRAY_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block GREEN_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block GREEN_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block GREEN_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block GREEN_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block GREEN_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block GREEN_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_BLUE_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_BLUE_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_BLUE_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_BLUE_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_BLUE_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_BLUE_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIGHT_GRAY_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIGHT_GRAY_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIGHT_GRAY_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIGHT_GRAY_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIGHT_GRAY_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIGHT_GRAY_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block LIME_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block LIME_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block LIME_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block LIME_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block LIME_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block LIME_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block MAGENTA_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block MAGENTA_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block MAGENTA_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block MAGENTA_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block MAGENTA_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block MAGENTA_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block ORANGE_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block ORANGE_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block ORANGE_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block ORANGE_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block ORANGE_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block ORANGE_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PINK_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PINK_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PINK_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PINK_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PINK_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PINK_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block PURPLE_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block PURPLE_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block PURPLE_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block PURPLE_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block PURPLE_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block PURPLE_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block RED_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block RED_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block RED_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block RED_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block RED_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block RED_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block WHITE_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block WHITE_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block WHITE_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block WHITE_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block WHITE_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block WHITE_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_PLANKS = new ColoredPlankBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	public static final Block YELLOW_STAIRS = new ColoredStairsBlock(BLACK_PLANKS.defaultBlockState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	public static final Block YELLOW_PRESSURE_PLATE = new ColoredPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	public static final Block YELLOW_FENCE = new ColoredFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	public static final Block YELLOW_FENCE_GATE = new ColoredFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	public static final Block YELLOW_BUTTON = new ColoredWoodenButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	public static final Block YELLOW_SLAB = new ColoredSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	
	//DD FLORA
	public static Properties overgrownBlackslag(MapColor color, SoundType soundGroup) {
		return settings(color, soundGroup, BLACKSLAG_HARDNESS, BLACKSLAG_RESISTANCE).randomTicks();
	}
	
	public static final Block SAWBLADE_GRASS = new BlackslagVegetationBlock(overgrownBlackslag(MapColor.SAND, SoundType.AZALEA_LEAVES));
	public static final Block SHIMMEL = new BlackslagVegetationBlock(overgrownBlackslag(MapColor.TERRACOTTA_GRAY, SoundType.WART_BLOCK));
	public static final Block OVERGROWN_BLACKSLAG = new BlackslagVegetationBlock(overgrownBlackslag(MapColor.PLANT, SoundType.VINE).speedFactor(0.925F));
	public static final Block ROTTEN_GROUND = new RottenGroundBlock(Properties.copy(Blocks.MUD).mapColor(MapColor.ICE).sound(SoundType.HONEY_BLOCK).speedFactor(0.775F).jumpFactor(0.9F));
	
	public static Properties noxshroom(MapColor color) {
		return settings(color, SoundType.FUNGUS, 0.0F).noCollission();
	}
	
	public static final Block SLATE_NOXSHROOM = new FungusBlock(noxshroom(MapColor.COLOR_GRAY), SpectrumConfiguredFeatures.SLATE_NOXFUNGUS, SHIMMEL);
	public static final Block EBONY_NOXSHROOM = new FungusBlock(noxshroom(MapColor.TERRACOTTA_BLACK), SpectrumConfiguredFeatures.EBONY_NOXFUNGUS, SHIMMEL);
	public static final Block IVORY_NOXSHROOM = new FungusBlock(noxshroom(MapColor.QUARTZ), SpectrumConfiguredFeatures.IVORY_NOXFUNGUS, SHIMMEL);
	public static final Block CHESTNUT_NOXSHROOM = new FungusBlock(noxshroom(MapColor.CRIMSON_NYLIUM), SpectrumConfiguredFeatures.CHESTNUT_NOXFUNGUS, SHIMMEL);
	
	public static Properties pottedPlant() {
		return Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
	}
	
	public static final Block POTTED_SLATE_NOXSHROOM = new FlowerPotBlock(SLATE_NOXSHROOM, pottedPlant());
	public static final Block POTTED_EBONY_NOXSHROOM = new FlowerPotBlock(EBONY_NOXSHROOM, pottedPlant());
	public static final Block POTTED_IVORY_NOXSHROOM = new FlowerPotBlock(IVORY_NOXSHROOM, pottedPlant());
	public static final Block POTTED_CHESTNUT_NOXSHROOM = new FlowerPotBlock(CHESTNUT_NOXSHROOM, pottedPlant());
	
	public static final ToIntFunction<BlockState> LANTERN_LIGHT_PROVIDER = (state -> state.getValue(RedstoneLampBlock.LIT) ? 15 : 0);
	
	public static Properties noxcap(MapColor color) {
		return settings(color, SoundType.STEM, 4.0F).instrument(NoteBlockInstrument.BASS);
	}
	private static final int NOXCAP_BUTTON_BLOCK_PRESS_TIME_TICKS = 30;
	
	public static final RotatedPillarBlock STRIPPED_SLATE_NOXCAP_STEM = new RotatedPillarBlock(noxcap(MapColor.COLOR_GRAY));
	public static final RotatedPillarBlock SLATE_NOXCAP_STEM = new StrippingLootPillarBlock(noxcap(MapColor.COLOR_GRAY), STRIPPED_SLATE_NOXCAP_STEM, SpectrumCommon.locate("gameplay/stripping/slate_noxcap_stripping"));
	public static final Block STRIPPED_SLATE_NOXCAP_HYPHAE = new RotatedPillarBlock(noxcap(MapColor.COLOR_GRAY));
	public static final Block SLATE_NOXCAP_HYPHAE = new StrippingLootPillarBlock(noxcap(MapColor.COLOR_GRAY), STRIPPED_SLATE_NOXCAP_HYPHAE, SpectrumCommon.locate("gameplay/stripping/slate_noxcap_stripping"));
	public static final Block SLATE_NOXCAP_BLOCK = new Block(noxcap(MapColor.COLOR_GRAY));
	public static final RotatedPillarBlock SLATE_NOXCAP_GILLS = new RotatedPillarBlock(noxcap(MapColor.DIAMOND).lightLevel(state -> 12).emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always));
	public static final Block SLATE_NOXWOOD_PLANKS = new Block(noxcap(MapColor.COLOR_GRAY));
	public static final StairBlock SLATE_NOXWOOD_STAIRS = new StairBlock(SLATE_NOXWOOD_PLANKS.defaultBlockState(), noxcap(MapColor.COLOR_GRAY));
	public static final SlabBlock SLATE_NOXWOOD_SLAB = new SlabBlock(noxcap(MapColor.COLOR_GRAY));
	public static final FenceBlock SLATE_NOXWOOD_FENCE = new FenceBlock(noxcap(MapColor.COLOR_GRAY));
	public static final FenceGateBlock SLATE_NOXWOOD_FENCE_GATE = new FenceGateBlock(noxcap(MapColor.COLOR_GRAY), SpectrumWoodTypes.SLATE_NOXWOOD);
	public static final Block SLATE_NOXWOOD_DOOR = new DoorBlock(noxcap(MapColor.COLOR_GRAY), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block SLATE_NOXWOOD_TRAPDOOR = new TrapDoorBlock(noxcap(MapColor.COLOR_GRAY), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block SLATE_NOXWOOD_BUTTON = new ButtonBlock(noxcap(MapColor.COLOR_GRAY), SpectrumBlockSetTypes.NOXWOOD, NOXCAP_BUTTON_BLOCK_PRESS_TIME_TICKS, true);
	public static final Block SLATE_NOXWOOD_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, noxcap(MapColor.COLOR_GRAY), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block SLATE_NOXWOOD_BEAM = new RotatedPillarBlock(noxcap(MapColor.COLOR_GRAY));
	public static final Block SLATE_NOXWOOD_AMPHORA = new AmphoraBlock(noxcap(MapColor.COLOR_GRAY));
	public static final Block SLATE_NOXWOOD_LANTERN = new RedstoneLampBlock(noxcap(MapColor.COLOR_GRAY).lightLevel(LANTERN_LIGHT_PROVIDER));
	public static final Block SLATE_NOXWOOD_LIGHT = new RotatedPillarBlock(noxcap(MapColor.COLOR_GRAY).lightLevel(state -> 15));
	public static final Block SLATE_NOXWOOD_LAMP = new FlexLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).luminance(13).pushReaction(PushReaction.DESTROY));
	
	public static final RotatedPillarBlock STRIPPED_EBONY_NOXCAP_STEM = new RotatedPillarBlock(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final RotatedPillarBlock EBONY_NOXCAP_STEM = new StrippingLootPillarBlock(noxcap(MapColor.TERRACOTTA_BLACK), STRIPPED_EBONY_NOXCAP_STEM, SpectrumCommon.locate("gameplay/stripping/ebony_noxcap_stripping"));
	public static final Block STRIPPED_EBONY_NOXCAP_HYPHAE = new RotatedPillarBlock(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final Block EBONY_NOXCAP_HYPHAE = new StrippingLootPillarBlock(noxcap(MapColor.TERRACOTTA_BLACK), STRIPPED_EBONY_NOXCAP_HYPHAE, SpectrumCommon.locate("gameplay/stripping/ebony_noxcap_stripping"));
	public static final Block EBONY_NOXCAP_BLOCK = new Block(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final RotatedPillarBlock EBONY_NOXCAP_GILLS = new RotatedPillarBlock(noxcap(MapColor.DIAMOND).lightLevel(state -> 12).emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always));
	public static final Block EBONY_NOXWOOD_PLANKS = new Block(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final StairBlock EBONY_NOXWOOD_STAIRS = new StairBlock(EBONY_NOXWOOD_PLANKS.defaultBlockState(), noxcap(MapColor.TERRACOTTA_BLACK));
	public static final SlabBlock EBONY_NOXWOOD_SLAB = new SlabBlock(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final FenceBlock EBONY_NOXWOOD_FENCE = new FenceBlock(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final FenceGateBlock EBONY_NOXWOOD_FENCE_GATE = new FenceGateBlock(noxcap(MapColor.TERRACOTTA_BLACK), SpectrumWoodTypes.EBONY_NOXWOOD);
	public static final Block EBONY_NOXWOOD_DOOR = new DoorBlock(noxcap(MapColor.TERRACOTTA_BLACK), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block EBONY_NOXWOOD_TRAPDOOR = new TrapDoorBlock(noxcap(MapColor.TERRACOTTA_BLACK), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block EBONY_NOXWOOD_BUTTON = new ButtonBlock(noxcap(MapColor.TERRACOTTA_BLACK), SpectrumBlockSetTypes.NOXWOOD, NOXCAP_BUTTON_BLOCK_PRESS_TIME_TICKS, true);
	public static final Block EBONY_NOXWOOD_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, noxcap(MapColor.TERRACOTTA_BLACK), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block EBONY_NOXWOOD_BEAM = new RotatedPillarBlock(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final Block EBONY_NOXWOOD_AMPHORA = new AmphoraBlock(noxcap(MapColor.TERRACOTTA_BLACK));
	public static final Block EBONY_NOXWOOD_LANTERN = new RedstoneLampBlock(noxcap(MapColor.TERRACOTTA_BLACK).lightLevel(LANTERN_LIGHT_PROVIDER));
	public static final Block EBONY_NOXWOOD_LIGHT = new RotatedPillarBlock(noxcap(MapColor.TERRACOTTA_BLACK).lightLevel(state -> 15));
	public static final Block EBONY_NOXWOOD_LAMP = new FlexLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).luminance(13).pushReaction(PushReaction.DESTROY));
	
	public static final RotatedPillarBlock STRIPPED_IVORY_NOXCAP_STEM = new RotatedPillarBlock(noxcap(MapColor.QUARTZ));
	public static final RotatedPillarBlock IVORY_NOXCAP_STEM = new StrippingLootPillarBlock(noxcap(MapColor.QUARTZ), STRIPPED_IVORY_NOXCAP_STEM, SpectrumCommon.locate("gameplay/stripping/ivory_noxcap_stripping"));
	public static final Block STRIPPED_IVORY_NOXCAP_HYPHAE = new RotatedPillarBlock(noxcap(MapColor.QUARTZ));
	public static final Block IVORY_NOXCAP_HYPHAE = new StrippingLootPillarBlock(noxcap(MapColor.QUARTZ), STRIPPED_IVORY_NOXCAP_HYPHAE, SpectrumCommon.locate("gameplay/stripping/ivory_noxcap_stripping"));
	public static final Block IVORY_NOXCAP_BLOCK = new Block(noxcap(MapColor.QUARTZ));
	public static final RotatedPillarBlock IVORY_NOXCAP_GILLS = new RotatedPillarBlock(noxcap(MapColor.DIAMOND).lightLevel(state -> 12).emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always));
	public static final Block IVORY_NOXWOOD_PLANKS = new Block(noxcap(MapColor.QUARTZ));
	public static final StairBlock IVORY_NOXWOOD_STAIRS = new StairBlock(IVORY_NOXWOOD_PLANKS.defaultBlockState(), noxcap(MapColor.QUARTZ));
	public static final SlabBlock IVORY_NOXWOOD_SLAB = new SlabBlock(noxcap(MapColor.QUARTZ));
	public static final FenceBlock IVORY_NOXWOOD_FENCE = new FenceBlock(noxcap(MapColor.QUARTZ));
	public static final FenceGateBlock IVORY_NOXWOOD_FENCE_GATE = new FenceGateBlock(noxcap(MapColor.QUARTZ), SpectrumWoodTypes.CHESTNUT_NOXWOOD);
	public static final Block IVORY_NOXWOOD_DOOR = new DoorBlock(noxcap(MapColor.QUARTZ), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block IVORY_NOXWOOD_TRAPDOOR = new TrapDoorBlock(noxcap(MapColor.QUARTZ), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block IVORY_NOXWOOD_BUTTON = new ButtonBlock(noxcap(MapColor.QUARTZ), SpectrumBlockSetTypes.NOXWOOD, NOXCAP_BUTTON_BLOCK_PRESS_TIME_TICKS, true);
	public static final Block IVORY_NOXWOOD_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, noxcap(MapColor.QUARTZ), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block IVORY_NOXWOOD_BEAM = new RotatedPillarBlock(noxcap(MapColor.QUARTZ));
	public static final Block IVORY_NOXWOOD_AMPHORA = new AmphoraBlock(noxcap(MapColor.QUARTZ));
	public static final Block IVORY_NOXWOOD_LANTERN = new RedstoneLampBlock(noxcap(MapColor.QUARTZ).lightLevel(LANTERN_LIGHT_PROVIDER));
	public static final Block IVORY_NOXWOOD_LIGHT = new RotatedPillarBlock(noxcap(MapColor.QUARTZ).lightLevel(state -> 15));
	public static final Block IVORY_NOXWOOD_LAMP = new FlexLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).luminance(13).pushReaction(PushReaction.DESTROY));
	
	public static final RotatedPillarBlock STRIPPED_CHESTNUT_NOXCAP_STEM = new RotatedPillarBlock(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final RotatedPillarBlock CHESTNUT_NOXCAP_STEM = new StrippingLootPillarBlock(noxcap(MapColor.CRIMSON_NYLIUM), STRIPPED_CHESTNUT_NOXCAP_STEM, SpectrumCommon.locate("gameplay/stripping/chestnut_noxcap_stripping"));
	public static final Block STRIPPED_CHESTNUT_NOXCAP_HYPHAE = new RotatedPillarBlock(noxcap(MapColor.QUARTZ));
	public static final Block CHESTNUT_NOXCAP_HYPHAE = new StrippingLootPillarBlock(noxcap(MapColor.QUARTZ), STRIPPED_CHESTNUT_NOXCAP_HYPHAE, SpectrumCommon.locate("gameplay/stripping/chestnut_noxcap_stripping"));
	public static final Block CHESTNUT_NOXCAP_BLOCK = new Block(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final RotatedPillarBlock CHESTNUT_NOXCAP_GILLS = new RotatedPillarBlock(noxcap(MapColor.DIAMOND).lightLevel(state -> 12).emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always));
	public static final Block CHESTNUT_NOXWOOD_PLANKS = new Block(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final StairBlock CHESTNUT_NOXWOOD_STAIRS = new StairBlock(CHESTNUT_NOXWOOD_PLANKS.defaultBlockState(), noxcap(MapColor.CRIMSON_NYLIUM));
	public static final SlabBlock CHESTNUT_NOXWOOD_SLAB = new SlabBlock(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final FenceBlock CHESTNUT_NOXWOOD_FENCE = new FenceBlock(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final FenceGateBlock CHESTNUT_NOXWOOD_FENCE_GATE = new FenceGateBlock(noxcap(MapColor.CRIMSON_NYLIUM), SpectrumWoodTypes.IVORY_NOXWOOD);
	public static final Block CHESTNUT_NOXWOOD_DOOR = new DoorBlock(noxcap(MapColor.CRIMSON_NYLIUM), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block CHESTNUT_NOXWOOD_TRAPDOOR = new TrapDoorBlock(noxcap(MapColor.CRIMSON_NYLIUM), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block CHESTNUT_NOXWOOD_BUTTON = new ButtonBlock(noxcap(MapColor.CRIMSON_NYLIUM), SpectrumBlockSetTypes.NOXWOOD, NOXCAP_BUTTON_BLOCK_PRESS_TIME_TICKS, true);
	public static final Block CHESTNUT_NOXWOOD_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, noxcap(MapColor.CRIMSON_NYLIUM), SpectrumBlockSetTypes.NOXWOOD);
	public static final Block CHESTNUT_NOXWOOD_BEAM = new RotatedPillarBlock(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final Block CHESTNUT_NOXWOOD_AMPHORA = new AmphoraBlock(noxcap(MapColor.CRIMSON_NYLIUM));
	public static final Block CHESTNUT_NOXWOOD_LANTERN = new RedstoneLampBlock(noxcap(MapColor.CRIMSON_NYLIUM).lightLevel(LANTERN_LIGHT_PROVIDER));
	public static final Block CHESTNUT_NOXWOOD_LIGHT = new RotatedPillarBlock(noxcap(MapColor.CRIMSON_NYLIUM).lightLevel(state -> 15));
	public static final Block CHESTNUT_NOXWOOD_LAMP = new FlexLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).luminance(13).pushReaction(PushReaction.DESTROY));
	
	public static Properties dragonjag(MapColor color) {
		return settings(color, SoundType.GRASS, 1.0F);
	}
	
	public static final Block SMALL_RED_DRAGONJAG = new SmallDragonjagBlock(dragonjag(MapColor.NETHER), Dragonjag.Variant.RED);
	public static final Block SMALL_YELLOW_DRAGONJAG = new SmallDragonjagBlock(dragonjag(MapColor.SAND), Dragonjag.Variant.YELLOW);
	public static final Block SMALL_PINK_DRAGONJAG = new SmallDragonjagBlock(dragonjag(MapColor.WARPED_HYPHAE), Dragonjag.Variant.PINK);
	public static final Block SMALL_PURPLE_DRAGONJAG = new SmallDragonjagBlock(dragonjag(MapColor.COLOR_PURPLE), Dragonjag.Variant.PURPLE);
	public static final Block SMALL_BLACK_DRAGONJAG = new SmallDragonjagBlock(dragonjag(MapColor.TERRACOTTA_BLACK), Dragonjag.Variant.BLACK);
	
	public static final Block TALL_RED_DRAGONJAG = new TallDragonjagBlock(dragonjag(MapColor.NETHER), Dragonjag.Variant.RED);
	public static final Block TALL_YELLOW_DRAGONJAG = new TallDragonjagBlock(dragonjag(MapColor.SAND), Dragonjag.Variant.YELLOW);
	public static final Block TALL_PINK_DRAGONJAG = new TallDragonjagBlock(dragonjag(MapColor.WARPED_HYPHAE), Dragonjag.Variant.PINK);
	public static final Block TALL_PURPLE_DRAGONJAG = new TallDragonjagBlock(dragonjag(MapColor.COLOR_PURPLE), Dragonjag.Variant.PURPLE);
	public static final Block TALL_BLACK_DRAGONJAG = new TallDragonjagBlock(dragonjag(MapColor.TERRACOTTA_BLACK), Dragonjag.Variant.BLACK);
	
	public static final Block ALOE = new AloeBlock(settings(MapColor.PLANT, SoundType.GRASS, 1.0F).noCollission().randomTicks().noOcclusion());
	public static final Block SAWBLADE_HOLLY_BUSH = new SawbladeHollyBushBlock(settings(MapColor.TERRACOTTA_GREEN, SoundType.GRASS, 0.0F).noCollission().randomTicks().noOcclusion());
	public static final Block BRISTLE_SPROUTS = new BristleSproutsBlock(settings(MapColor.GRASS, SoundType.GRASS, 0.0F).noCollission().noOcclusion().offsetType(OffsetType.XZ));
	public static final Block DOOMBLOOM = new DoomBloomBlock(settings(MapColor.GRASS, SoundType.GRASS, 0.0F).randomTicks().noCollission().lightLevel((state) -> state.getValue(DoomBloomBlock.AGE) * 2).noOcclusion());
	public static final Block SNAPPING_IVY = new SnappingIvyBlock(settings(MapColor.GRASS, SoundType.GRASS, 3.0F).noCollission().noOcclusion());
	
	public static final Block HUMMINGSTONE_GLASS = new GlassBlock(settings(MapColor.SAND, SoundType.GLASS, 5.0F, 100.0F).noOcclusion().requiresCorrectToolForDrops());
	public static final Block HUMMINGSTONE = new HummingstoneBlock(Properties.copy(HUMMINGSTONE_GLASS).lightLevel((state) -> 14));
	public static final Block CLEAR_HUMMINGSTONE_GLASS = new GlassBlock(Properties.copy(HUMMINGSTONE_GLASS));
	
	public static final Block EFFULGENT_BLOCK = new CushionedFacingBlock(Properties.copy(Blocks.RED_WOOL));
	public static final Block EFFULGENT_CUSHION = new CushionBlock(Properties.copy(EFFULGENT_BLOCK).noOcclusion().isValidSpawn(SpectrumBlocks::never));
	public static final Block EFFULGENT_CARPET = new CushionedCarpetBlock(Properties.copy(Blocks.RED_CARPET));
	
	// JADE VINES
	public static Properties jadeVine() {
		return settings(MapColor.GRASS, SoundType.WOOL, 0.1F).noCollission().noOcclusion();
	}
	
	public static final Block JADE_VINE_ROOTS = new JadeVineRootsBlock(jadeVine().randomTicks().lightLevel((state) -> state.getValue(JadeVineRootsBlock.DEAD) ? 0 : 4));
	public static final Block JADE_VINE_BULB = new JadeVineBulbBlock(jadeVine().lightLevel((state) -> state.getValue(JadeVineBulbBlock.DEAD) ? 0 : 5));
	public static final Block JADE_VINES = new JadeVinePlantBlock(jadeVine().lightLevel((state) -> state.getValue(JadeVinePlantBlock.AGE) == 0 ? 0 : 5));
	public static final Block JADE_VINE_PETAL_BLOCK = new JadeVinePetalBlock(jadeVine().lightLevel(state -> 3));
	public static final Block JADE_VINE_PETAL_CARPET = new CarpetBlock(jadeVine().lightLevel(state -> 3));

	public static final Block NEPHRITE_BLOSSOM_STEM = new NephriteBlossomStemBlock(settings(MapColor.COLOR_PINK, SoundType.WOOL, 2.0F).noOcclusion().noCollission());
	public static final Block NEPHRITE_BLOSSOM_LEAVES = new NephriteBlossomLeavesBlock(settings(MapColor.COLOR_PINK, SoundType.GRASS, 0.2F).randomTicks().lightLevel(state -> 13));
	public static final Block NEPHRITE_BLOSSOM_BULB = new NephriteBlossomBulbBlock(FabricBlockSettings.copyOf(NEPHRITE_BLOSSOM_STEM));

	public static Properties jadeite() {
		return settings(MapColor.WOOL, SoundType.WOOL, 0.1F).noCollission().noOcclusion();
	}
	public static final Block JADEITE_LOTUS_STEM = new JadeiteLotusStemBlock(settings(MapColor.COLOR_BLACK, SoundType.WOOL, 2.0F).noOcclusion().noCollission());
	public static final Block JADEITE_LOTUS_FLOWER = new JadeiteFlowerBlock(settings(MapColor.SNOW, SoundType.WOOL, 2.0F).lightLevel(state -> 14).hasPostProcess(SpectrumBlocks::always).emissiveRendering(SpectrumBlocks::always));
	public static final Block JADEITE_LOTUS_BULB = new JadeiteLotusBulbBlock(FabricBlockSettings.copyOf(JADEITE_LOTUS_STEM));
	public static final Block JADEITE_PETAL_BLOCK = new JadeVinePetalBlock(jadeite());
	public static final Block JADEITE_PETAL_CARPET = new CarpetBlock(jadeite());

	private static Properties ore() {
		return FabricBlockSettings.copyOf(Blocks.IRON_ORE);
	}
	
	private static Properties deepslateOre() {
		return FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE);
	}
	
	private static Properties blackslagOre() {
		return FabricBlockSettings.copyOf(BLACKSLAG).strength(BLACKSLAG_HARDNESS * 1.5F, BLACKSLAG_RESISTANCE * 2F).requiresCorrectToolForDrops();
	}
	
	private static Properties netherrackOre() {
		return FabricBlockSettings.copyOf(Blocks.NETHERRACK).strength(3.0F, 3.0F).sound(SoundType.NETHER_ORE).requiresCorrectToolForDrops();
	}
	
	private static Properties endstoneOre() {
		return FabricBlockSettings.copyOf(Blocks.END_STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops();
	}
	
	public static final Block SHIMMERSTONE_ORE = new CloakedOreBlock(ore(), UniformInt.of(2, 4), locate("milestones/reveal_shimmerstone"), Blocks.STONE.defaultBlockState());
	public static final Block DEEPSLATE_SHIMMERSTONE_ORE = new CloakedOreBlock(deepslateOre(), UniformInt.of(2, 4), locate("milestones/reveal_shimmerstone"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block BLACKSLAG_SHIMMERSTONE_ORE = new CloakedOreBlock(blackslagOre(), UniformInt.of(2, 4), locate("milestones/reveal_shimmerstone"), BLACKSLAG.defaultBlockState());
	public static final Block SHIMMERSTONE_BLOCK = new SparklestoneBlock(settings(MapColor.COLOR_YELLOW, SoundType.GLASS, 2.0F).lightLevel((state) -> 15));
	
	public static final Block AZURITE_ORE = new CloakedOreBlock(ore(), UniformInt.of(4, 7), locate("milestones/reveal_azurite"), Blocks.STONE.defaultBlockState());
	public static final Block DEEPSLATE_AZURITE_ORE = new CloakedOreBlock(deepslateOre(), UniformInt.of(4, 7), locate("milestones/reveal_azurite"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block BLACKSLAG_AZURITE_ORE = new CloakedOreBlock(blackslagOre(), UniformInt.of(4, 7), locate("milestones/reveal_azurite"), SpectrumBlocks.BLACKSLAG.defaultBlockState());
	public static final Block AZURITE_BLOCK = new SpectrumFacingBlock(FabricBlockSettings.copyOf(Blocks.LAPIS_BLOCK).mapColor(MapColor.COLOR_BLUE));
	public static final Block AZURITE_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.COLOR_BLUE, SpectrumBlockSoundGroups.SMALL_ONYX_BUD, 2));
	public static final Block LARGE_AZURITE_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.COLOR_BLUE, SpectrumBlockSoundGroups.LARGE_ONYX_BUD, 3));
	public static final Block SMALL_AZURITE_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.COLOR_BLUE, SpectrumBlockSoundGroups.ONYX_CLUSTER, 5));
	
	public static final Block MALACHITE_ORE = new CloakedOreBlock(ore(), UniformInt.of(7, 11), locate("milestones/reveal_malachite"), Blocks.STONE.defaultBlockState());
	public static final Block DEEPSLATE_MALACHITE_ORE = new CloakedOreBlock(deepslateOre(), UniformInt.of(7, 11), locate("milestones/reveal_malachite"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block BLACKSLAG_MALACHITE_ORE = new CloakedOreBlock(blackslagOre(), UniformInt.of(7, 11), locate("milestones/reveal_malachite"), BLACKSLAG.defaultBlockState());
	public static final Block MALACHITE_BLOCK = new SpectrumFacingBlock(gemstoneBlock(MapColor.EMERALD, SoundType.CHAIN));
	public static final Block MALACHITE_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.EMERALD, SoundType.CHAIN, 9));
	public static final Block LARGE_MALACHITE_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.EMERALD, SoundType.CHAIN, 7));
	public static final Block SMALL_MALACHITE_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.EMERALD, SoundType.CHAIN, 5));
	
	public static final Block BLOODSTONE_BLOCK = new SpectrumFacingBlock(gemstoneBlock(MapColor.COLOR_RED, SpectrumBlockSoundGroups.ONYX_CLUSTER));
	public static final Block BLOODSTONE_CLUSTER = new AmethystClusterBlock(7, 3, gemstone(MapColor.COLOR_RED, SpectrumBlockSoundGroups.SMALL_ONYX_BUD, 6));
	public static final Block LARGE_BLOODSTONE_BUD = new AmethystClusterBlock(5, 3, gemstone(MapColor.COLOR_RED, SpectrumBlockSoundGroups.SMALL_ONYX_BUD, 4));
	public static final Block SMALL_BLOODSTONE_BUD = new AmethystClusterBlock(3, 4, gemstone(MapColor.COLOR_RED, SpectrumBlockSoundGroups.ONYX_CLUSTER, 3));
	
	public static final Block STRATINE_ORE = new CloakedOreBlock(netherrackOre(), UniformInt.of(3, 5), locate("milestones/reveal_stratine"), Blocks.NETHERRACK.defaultBlockState());
	public static final Block PALTAERIA_ORE = new CloakedOreBlock(endstoneOre(), UniformInt.of(2, 4), locate("milestones/reveal_paltaeria"), Blocks.END_STONE.defaultBlockState());
	
	private static Properties gravityBlock(MapColor mapColor) {
		return settings(mapColor, SoundType.METAL, 4.0F, 6.0F).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops();
	}
	public static final FloatBlock PALTAERIA_FRAGMENT_BLOCK = new FloatBlock(gravityBlock(MapColor.COLOR_LIGHT_BLUE), 0.2F);
	public static final FloatBlock STRATINE_FRAGMENT_BLOCK = new FloatBlock(gravityBlock(MapColor.NETHER), -0.2F);
	public static final FloatBlock HOVER_BLOCK = new FloatBlock(gravityBlock(MapColor.DIAMOND), 0.0F);
	
	public static final Block BLACKSLAG_COAL_ORE = new DropExperienceBlock(blackslagOre(), UniformInt.of(0, 2));
	public static final Block BLACKSLAG_COPPER_ORE = new DropExperienceBlock(blackslagOre());
	public static final Block BLACKSLAG_IRON_ORE = new DropExperienceBlock(blackslagOre());
	public static final Block BLACKSLAG_GOLD_ORE = new DropExperienceBlock(blackslagOre());
	public static final Block BLACKSLAG_LAPIS_ORE = new DropExperienceBlock(blackslagOre(), UniformInt.of(2, 5));
	public static final Block BLACKSLAG_DIAMOND_ORE = new DropExperienceBlock(blackslagOre(), UniformInt.of(3, 7));
	public static final Block BLACKSLAG_REDSTONE_ORE = new RedStoneOreBlock(blackslagOre().randomTicks().lightLevel(litBlockEmission(9)));
	public static final Block BLACKSLAG_EMERALD_ORE = new DropExperienceBlock(blackslagOre(), UniformInt.of(3, 7));
	
	// FUNCTIONAL BLOCKS
	public static final Block HEARTBOUND_CHEST = new HeartboundChestBlock(settings(MapColor.TERRACOTTA_WHITE, SoundType.STONE, -1.0F, 3600000.0F).requiresCorrectToolForDrops().noOcclusion());
	public static final Block COMPACTING_CHEST = new CompactingChestBlock(settings(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 4.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
	public static final Block RESTOCKING_CHEST = new RestockingChestBlock(settings(MapColor.COLOR_ORANGE, SoundType.STONE, 4.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
	public static final Block BLACK_HOLE_CHEST = new BlackHoleChestBlock(settings(MapColor.COLOR_BLACK, SoundType.STONE, 4.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
	public static final Block PARTICLE_SPAWNER = new ParticleSpawnerBlock(settings(MapColor.TERRACOTTA_WHITE, SoundType.AMETHYST, 5.0F, 6.0F).requiresCorrectToolForDrops().noOcclusion());
	public static final Block CREATIVE_PARTICLE_SPAWNER = new CreativeParticleSpawnerBlock(FabricBlockSettings.copyOf(SpectrumBlocks.PARTICLE_SPAWNER).strength(-1.0F, 3600000.8F).noLootTable());
	public static final Block BEDROCK_ANVIL = new BedrockAnvilBlock(FabricBlockSettings.copyOf(Blocks.ANVIL).requiresCorrectToolForDrops().strength(8.0F, 8.0F).sound(SoundType.METAL));
	
	// SOLID LIQUID CRYSTAL
	public static final Block FROSTBITE_CRYSTAL = new Block(FabricBlockSettings.copyOf(Blocks.GLOWSTONE).mapColor(MapColor.CLAY));
	public static final Block BLAZING_CRYSTAL = new Block(FabricBlockSettings.copyOf(Blocks.GLOWSTONE).mapColor(MapColor.COLOR_ORANGE));
	
	public static final Block RESONANT_LILY = new ResonantLilyBlock(MobEffects.HEAL, 5, FabricBlockSettings.copyOf(Blocks.POPPY).mapColor(MapColor.SNOW));
	public static final Block QUITOXIC_REEDS = new QuitoxicReedsBlock(settings(MapColor.NONE, SoundType.GRASS, 0.0F).noCollission().offsetType(BlockBehaviour.OffsetType.XYZ).randomTicks().lightLevel(state -> state.getValue(QuitoxicReedsBlock.LOGGED).getLuminance()));
	public static final Block MERMAIDS_BRUSH = new MermaidsBrushBlock(settings(MapColor.NONE, SoundType.WET_GRASS, 0.0F).noCollission().randomTicks().lightLevel(state -> state.getValue(MermaidsBrushBlock.LOGGED).getLuminance()));
	public static final Block RADIATING_ENDER = new RadiatingEnderBlock(FabricBlockSettings.copyOf(Blocks.EMERALD_BLOCK).mapColor(MapColor.COLOR_PURPLE));
	public static final Block AMARANTH = new AmaranthCropBlock(settings(MapColor.NONE, SoundType.CROP, 0.0F).noCollission().randomTicks());
	public static final Block AMARANTH_BUSHEL = new AmaranthBushelBlock(settings(MapColor.NONE, SoundType.CROP, 0.0F).noCollission());
	
	public static final Block MEMORY = new MemoryBlock(settings(MapColor.NONE, SoundType.AMETHYST, 0.0F).isViewBlocking(SpectrumBlocks::never).noOcclusion().randomTicks());
	public static final Block CRACKED_END_PORTAL_FRAME = new CrackedEndPortalFrameBlock(settings(MapColor.ICE, SoundType.GLASS, -1.0F, 3600000.0F).instrument(NoteBlockInstrument.BASEDRUM).lightLevel((state) -> 1));
	public static final Block LAVA_SPONGE = new LavaSpongeBlock(FabricBlockSettings.copyOf(Blocks.SPONGE).mapColor(MapColor.COLOR_ORANGE));
	public static final Block WET_LAVA_SPONGE = new WetLavaSpongeBlock(FabricBlockSettings.copyOf(Blocks.WET_SPONGE).mapColor(MapColor.COLOR_ORANGE).luminance(9).emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always));
	
	public static final Block LIGHT_LEVEL_DETECTOR = new BlockLightDetectorBlock(FabricBlockSettings.copyOf(Blocks.DAYLIGHT_DETECTOR));
	public static final Block WEATHER_DETECTOR = new WeatherDetectorBlock(FabricBlockSettings.copyOf(Blocks.DAYLIGHT_DETECTOR));
	public static final Block ITEM_DETECTOR = new ItemDetectorBlock(FabricBlockSettings.copyOf(Blocks.DAYLIGHT_DETECTOR));
	public static final Block PLAYER_DETECTOR = new PlayerDetectorBlock(FabricBlockSettings.copyOf(Blocks.DAYLIGHT_DETECTOR));
	public static final Block ENTITY_DETECTOR = new EntityDetectorBlock(FabricBlockSettings.copyOf(Blocks.DAYLIGHT_DETECTOR));
	public static final Block REDSTONE_CALCULATOR = new RedstoneCalculatorBlock(FabricBlockSettings.copyOf(Blocks.REPEATER));
	public static final Block REDSTONE_TIMER = new RedstoneTimerBlock(FabricBlockSettings.copyOf(Blocks.REPEATER));
	public static final Block REDSTONE_TRANSCEIVER = new RedstoneTransceiverBlock(FabricBlockSettings.copyOf(Blocks.REPEATER));
	public static final Block BLOCK_PLACER = new BlockPlacerBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER));
	public static final Block BLOCK_DETECTOR = new BlockDetectorBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER));
	public static final Block BLOCK_BREAKER = new BlockBreakerBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER));
	public static final EnderDropperBlock ENDER_DROPPER = new EnderDropperBlock(FabricBlockSettings.copyOf(Blocks.DROPPER).mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(15F, 60.0F));
	public static final Block ENDER_HOPPER = new EnderHopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER).mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(15F, 60.0F));
	
	public static final Block SPIRIT_SALLOW_LEAVES = new SpiritSallowLeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).mapColor(MapColor.QUARTZ).lightLevel((state) -> 8));
	public static final Block SPIRIT_SALLOW_LOG = new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_GRAY));
	public static final Block SPIRIT_SALLOW_ROOTS = new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_GRAY));
	public static final Block SPIRIT_SALLOW_HEART = new Block(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_GRAY).luminance(11));
	public static final Block SACRED_SOIL = new ExtraTickFarmlandBlock(FabricBlockSettings.copyOf(Blocks.FARMLAND).mapColor(MapColor.CLAY), Blocks.DIRT.defaultBlockState());

	private static Properties spiritVines(MapColor mapColor) {
		return settings(mapColor, SoundType.CAVE_VINES, 0.0F).noCollission();
	}
	public static final Block CYAN_SPIRIT_SALLOW_VINES_BODY = new SpiritVinesBodyBlock(spiritVines(MapColor.COLOR_CYAN), BuiltinGemstoneColor.CYAN);
	public static final Block CYAN_SPIRIT_SALLOW_VINES_HEAD = new SpiritVinesHeadBlock(spiritVines(MapColor.COLOR_CYAN), BuiltinGemstoneColor.CYAN);
	public static final Block MAGENTA_SPIRIT_SALLOW_VINES_BODY = new SpiritVinesBodyBlock(spiritVines(MapColor.COLOR_MAGENTA), BuiltinGemstoneColor.MAGENTA);
	public static final Block MAGENTA_SPIRIT_SALLOW_VINES_HEAD = new SpiritVinesHeadBlock(spiritVines(MapColor.COLOR_MAGENTA), BuiltinGemstoneColor.MAGENTA);
	public static final Block YELLOW_SPIRIT_SALLOW_VINES_BODY = new SpiritVinesBodyBlock(spiritVines(MapColor.COLOR_YELLOW), BuiltinGemstoneColor.YELLOW);
	public static final Block YELLOW_SPIRIT_SALLOW_VINES_HEAD = new SpiritVinesHeadBlock(spiritVines(MapColor.COLOR_YELLOW), BuiltinGemstoneColor.YELLOW);
	public static final Block BLACK_SPIRIT_SALLOW_VINES_BODY = new SpiritVinesBodyBlock(spiritVines(MapColor.TERRACOTTA_BLACK), BuiltinGemstoneColor.BLACK);
	public static final Block BLACK_SPIRIT_SALLOW_VINES_HEAD = new SpiritVinesHeadBlock(spiritVines(MapColor.TERRACOTTA_BLACK), BuiltinGemstoneColor.BLACK);
	public static final Block WHITE_SPIRIT_SALLOW_VINES_BODY = new SpiritVinesBodyBlock(spiritVines(MapColor.TERRACOTTA_WHITE), BuiltinGemstoneColor.WHITE);
	public static final Block WHITE_SPIRIT_SALLOW_VINES_HEAD = new SpiritVinesHeadBlock(spiritVines(MapColor.TERRACOTTA_WHITE), BuiltinGemstoneColor.WHITE);
	
	public static final Block STUCK_STORM_STONE = new StuckStormStoneBlock(settings(MapColor.NONE, SoundType.SMALL_AMETHYST_BUD, 0.0F).noCollission().noOcclusion().isSuffocating(SpectrumBlocks::never).noParticlesOnBreak().isViewBlocking(SpectrumBlocks::never).replaceable());
	public static final Block DEEPER_DOWN_PORTAL = new DeeperDownPortalBlock(settings(MapColor.COLOR_BLACK, SoundType.EMPTY, -1.0F, 3600000.0F).pushReaction(PushReaction.BLOCK).lightLevel(state -> 8).noLootTable());
	
	private static Properties upgrade() {
		return FabricBlockSettings.copyOf(SpectrumBlocks.POLISHED_BASALT).forceSolidOn();
	}
	public static final Block UPGRADE_SPEED = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.SPEED, 1, DyeColor.MAGENTA);
	public static final Block UPGRADE_SPEED2 = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.SPEED, 2, DyeColor.MAGENTA);
	public static final Block UPGRADE_SPEED3 = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.SPEED, 8, DyeColor.MAGENTA);
	public static final Block UPGRADE_EFFICIENCY = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.EFFICIENCY, 1, DyeColor.YELLOW);
	public static final Block UPGRADE_EFFICIENCY2 = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.EFFICIENCY, 4, DyeColor.YELLOW);
	public static final Block UPGRADE_YIELD = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.YIELD, 1, DyeColor.CYAN);
	public static final Block UPGRADE_YIELD2 = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.YIELD, 4, DyeColor.CYAN);
	public static final Block UPGRADE_EXPERIENCE = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.EXPERIENCE, 1, DyeColor.PURPLE);
	public static final Block UPGRADE_EXPERIENCE2 = new UpgradeBlock(upgrade(), Upgradeable.UpgradeType.EXPERIENCE, 4, DyeColor.PURPLE);
	
	public static final Block REDSTONE_SAND = new RedstoneGravityBlock(FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.FIRE));
	public static final Block ENDER_GLASS = new EnderGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).mapColor(MapColor.COLOR_PURPLE).noOcclusion().isRedstoneConductor(SpectrumBlocks::never)
			.isValidSpawn((state, world, pos, entityType) -> state.getValue(EnderGlassBlock.TRANSPARENCY_STATE) == EnderGlassBlock.TransparencyState.SOLID)
			.isSuffocating((state, world, pos) -> state.getValue(EnderGlassBlock.TRANSPARENCY_STATE) == EnderGlassBlock.TransparencyState.SOLID)
			.isViewBlocking((state, world, pos) -> state.getValue(EnderGlassBlock.TRANSPARENCY_STATE) == EnderGlassBlock.TransparencyState.SOLID));
	public static final Block CLOVER = new CloverBlock(FabricBlockSettings.copyOf(Blocks.GRASS).offsetType(BlockBehaviour.OffsetType.XZ));
	public static final Block FOUR_LEAF_CLOVER = new FourLeafCloverBlock(FabricBlockSettings.copyOf(Blocks.GRASS).offsetType(BlockBehaviour.OffsetType.XZ));
	public static final Block BLOOD_ORCHID = new BloodOrchidBlock(SpectrumStatusEffects.FRENZY, 10, FabricBlockSettings.copyOf(Blocks.POPPY).offsetType(BlockBehaviour.OffsetType.NONE).randomTicks());
	
	private static final UniformInt gemOreExperienceProvider = UniformInt.of(1, 4);
	public static final Block TOPAZ_ORE = new GemstoneOreBlock(ore(), gemOreExperienceProvider, BuiltinGemstoneColor.CYAN, locate("hidden/collect_shards/collect_topaz_shard"), Blocks.STONE.defaultBlockState());
	public static final Block AMETHYST_ORE = new GemstoneOreBlock(ore(), gemOreExperienceProvider, BuiltinGemstoneColor.MAGENTA, locate("hidden/collect_shards/collect_amethyst_shard"), Blocks.STONE.defaultBlockState());
	public static final Block CITRINE_ORE = new GemstoneOreBlock(ore(), gemOreExperienceProvider, BuiltinGemstoneColor.YELLOW, locate("hidden/collect_shards/collect_citrine_shard"), Blocks.STONE.defaultBlockState());
	public static final Block ONYX_ORE = new GemstoneOreBlock(ore(), gemOreExperienceProvider, BuiltinGemstoneColor.BLACK, locate("create_onyx_shard"), Blocks.STONE.defaultBlockState());
	public static final Block MOONSTONE_ORE = new GemstoneOreBlock(ore(), gemOreExperienceProvider, BuiltinGemstoneColor.WHITE, locate("lategame/collect_moonstone_shard"), Blocks.STONE.defaultBlockState());
	
	public static final Block DEEPSLATE_TOPAZ_ORE = new GemstoneOreBlock(deepslateOre(), gemOreExperienceProvider, BuiltinGemstoneColor.CYAN, locate("hidden/collect_shards/collect_topaz_shard"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block DEEPSLATE_AMETHYST_ORE = new GemstoneOreBlock(deepslateOre(), gemOreExperienceProvider, BuiltinGemstoneColor.MAGENTA, locate("hidden/collect_shards/collect_amethyst_shard"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block DEEPSLATE_CITRINE_ORE = new GemstoneOreBlock(deepslateOre(), gemOreExperienceProvider, BuiltinGemstoneColor.YELLOW, locate("hidden/collect_shards/collect_citrine_shard"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block DEEPSLATE_ONYX_ORE = new GemstoneOreBlock(deepslateOre(), gemOreExperienceProvider, BuiltinGemstoneColor.BLACK, locate("create_onyx_shard"), Blocks.DEEPSLATE.defaultBlockState());
	public static final Block DEEPSLATE_MOONSTONE_ORE = new GemstoneOreBlock(deepslateOre(), gemOreExperienceProvider, BuiltinGemstoneColor.WHITE, locate("lategame/collect_moonstone_shard"), Blocks.DEEPSLATE.defaultBlockState());
	
	public static final Block BLACKSLAG_TOPAZ_ORE = new GemstoneOreBlock(blackslagOre(), gemOreExperienceProvider, BuiltinGemstoneColor.CYAN, locate("hidden/collect_shards/collect_topaz_shard"), SpectrumBlocks.BLACKSLAG.defaultBlockState());
	public static final Block BLACKSLAG_AMETHYST_ORE = new GemstoneOreBlock(blackslagOre(), gemOreExperienceProvider, BuiltinGemstoneColor.MAGENTA, locate("hidden/collect_shards/collect_amethyst_shard"), SpectrumBlocks.BLACKSLAG.defaultBlockState());
	public static final Block BLACKSLAG_CITRINE_ORE = new GemstoneOreBlock(blackslagOre(), gemOreExperienceProvider, BuiltinGemstoneColor.YELLOW, locate("hidden/collect_shards/collect_citrine_shard"), SpectrumBlocks.BLACKSLAG.defaultBlockState());
	public static final Block BLACKSLAG_ONYX_ORE = new GemstoneOreBlock(blackslagOre(), gemOreExperienceProvider, BuiltinGemstoneColor.BLACK, locate("create_onyx_shard"), SpectrumBlocks.BLACKSLAG.defaultBlockState());
	public static final Block BLACKSLAG_MOONSTONE_ORE = new GemstoneOreBlock(blackslagOre(), gemOreExperienceProvider, BuiltinGemstoneColor.WHITE, locate("lategame/collect_moonstone_shard"), SpectrumBlocks.BLACKSLAG.defaultBlockState());
	
	private static Properties gemStorageBlock(MapColor mapColor, SoundType soundGroup) {
		return settings(mapColor, soundGroup, 5.0F, 6.0F);
	}
	
	public static final Block TOPAZ_STORAGE_BLOCK = new Block(gemStorageBlock(MapColor.COLOR_CYAN, SpectrumBlockSoundGroups.TOPAZ_BLOCK));
	public static final Block AMETHYST_STORAGE_BLOCK = new Block(gemStorageBlock(MapColor.COLOR_MAGENTA, SoundType.AMETHYST));
	public static final Block CITRINE_STORAGE_BLOCK = new Block(gemStorageBlock(MapColor.COLOR_YELLOW, SpectrumBlockSoundGroups.CITRINE_BLOCK));
	public static final Block ONYX_STORAGE_BLOCK = new Block(gemStorageBlock(MapColor.COLOR_BLACK, SpectrumBlockSoundGroups.ONYX_BLOCK));
	public static final Block MOONSTONE_STORAGE_BLOCK = new Block(gemStorageBlock(MapColor.SNOW, SpectrumBlockSoundGroups.MOONSTONE_BLOCK));
	//public static final Block SPECTRAL_SHARD_BLOCK = new SpectrumGemstoneBlock(gemstoneBlock(MapColor.DIAMOND_BLUE, SpectrumBlockSoundGroups.SPECTRAL_BLOCK), SpectrumSoundEvents.SPECTRAL_BLOCK_HIT, SpectrumSoundEvents.SPECTRAL_BLOCK_CHIME);
	//public static final Block SPECTRAL_SHARD_STORAGE_BLOCK = new Block(gemStorageBlock(MapColor.OFF_WHITE, SpectrumBlockSoundGroups.SPECTRAL_BLOCK));
	
	// COLORED TREES
	private static Properties coloredBlock(Block baseBlock, MapColor color) {
		return FabricBlockSettings.copyOf(baseBlock).mapColor(color);
	}
	
	public static final Block BLACK_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_SAPLING = new ColoredSaplingBlock(coloredBlock(Blocks.OAK_SAPLING, MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	public static final Block BLACK_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_LEAVES = new ColoredLeavesBlock(coloredBlock(Blocks.OAK_LEAVES, MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	public static final Block BLACK_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_LOG = new ColoredLogBlock(coloredBlock(Blocks.OAK_LOG, MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	public static final Block STRIPPED_BLACK_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block STRIPPED_BLUE_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block STRIPPED_BROWN_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block STRIPPED_CYAN_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block STRIPPED_GRAY_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block STRIPPED_GREEN_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block STRIPPED_LIGHT_BLUE_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block STRIPPED_LIGHT_GRAY_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block STRIPPED_LIME_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block STRIPPED_MAGENTA_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block STRIPPED_ORANGE_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block STRIPPED_PINK_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block STRIPPED_PURPLE_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block STRIPPED_RED_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_RED), DyeColor.RED);
	public static final Block STRIPPED_WHITE_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.SNOW), DyeColor.WHITE);
	public static final Block STRIPPED_YELLOW_LOG = new ColoredStrippedLogBlock(coloredBlock(Blocks.STRIPPED_OAK_LOG, MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	public static final Block BLACK_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_WOOD = new ColoredWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	public static final Block STRIPPED_BLACK_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.STRIPPED_OAK_WOOD, MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block STRIPPED_BLUE_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block STRIPPED_BROWN_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block STRIPPED_CYAN_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block STRIPPED_GRAY_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block STRIPPED_GREEN_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block STRIPPED_LIGHT_BLUE_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block STRIPPED_LIGHT_GRAY_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block STRIPPED_LIME_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block STRIPPED_MAGENTA_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block STRIPPED_ORANGE_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block STRIPPED_PINK_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block STRIPPED_PURPLE_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block STRIPPED_RED_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_RED), DyeColor.RED);
	public static final Block STRIPPED_WHITE_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.SNOW), DyeColor.WHITE);
	public static final Block STRIPPED_YELLOW_WOOD = new ColoredStrippedWoodBlock(coloredBlock(Blocks.OAK_WOOD, MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	public static final Block POTTED_BLACK_SAPLING = new PottedColoredSaplingBlock(BLACK_SAPLING, pottedPlant(), DyeColor.BLACK);
	public static final Block POTTED_BLUE_SAPLING = new PottedColoredSaplingBlock(BLUE_SAPLING, pottedPlant(), DyeColor.BLUE);
	public static final Block POTTED_BROWN_SAPLING = new PottedColoredSaplingBlock(BROWN_SAPLING, pottedPlant(), DyeColor.BROWN);
	public static final Block POTTED_CYAN_SAPLING = new PottedColoredSaplingBlock(CYAN_SAPLING, pottedPlant(), DyeColor.CYAN);
	public static final Block POTTED_GRAY_SAPLING = new PottedColoredSaplingBlock(GRAY_SAPLING, pottedPlant(), DyeColor.GRAY);
	public static final Block POTTED_GREEN_SAPLING = new PottedColoredSaplingBlock(GREEN_SAPLING, pottedPlant(), DyeColor.GREEN);
	public static final Block POTTED_LIGHT_BLUE_SAPLING = new PottedColoredSaplingBlock(LIGHT_BLUE_SAPLING, pottedPlant(), DyeColor.LIGHT_BLUE);
	public static final Block POTTED_LIGHT_GRAY_SAPLING = new PottedColoredSaplingBlock(LIGHT_GRAY_SAPLING, pottedPlant(), DyeColor.LIGHT_GRAY);
	public static final Block POTTED_LIME_SAPLING = new PottedColoredSaplingBlock(LIME_SAPLING, pottedPlant(), DyeColor.LIME);
	public static final Block POTTED_MAGENTA_SAPLING = new PottedColoredSaplingBlock(MAGENTA_SAPLING, pottedPlant(), DyeColor.MAGENTA);
	public static final Block POTTED_ORANGE_SAPLING = new PottedColoredSaplingBlock(ORANGE_SAPLING, pottedPlant(), DyeColor.ORANGE);
	public static final Block POTTED_PINK_SAPLING = new PottedColoredSaplingBlock(PINK_SAPLING, pottedPlant(), DyeColor.PINK);
	public static final Block POTTED_PURPLE_SAPLING = new PottedColoredSaplingBlock(PURPLE_SAPLING, pottedPlant(), DyeColor.PURPLE);
	public static final Block POTTED_RED_SAPLING = new PottedColoredSaplingBlock(RED_SAPLING, pottedPlant(), DyeColor.RED);
	public static final Block POTTED_WHITE_SAPLING = new PottedColoredSaplingBlock(WHITE_SAPLING, pottedPlant(), DyeColor.WHITE);
	public static final Block POTTED_YELLOW_SAPLING = new PottedColoredSaplingBlock(YELLOW_SAPLING, pottedPlant(), DyeColor.YELLOW);
	
	public static final Block POTTED_AMARANTH_BUSHEL = new PottedAmaranthBushelBlock(AMARANTH_BUSHEL, pottedPlant());
	public static final Block POTTED_BLOOD_ORCHID = new PottedBloodOrchidBlock(BLOOD_ORCHID, pottedPlant());
	public static final Block POTTED_RESONANT_LILY = new PottedResonantLilyBlock(RESONANT_LILY, pottedPlant());
	
	private static Properties glowBlock(MapColor color) {
		return settings(color, SoundType.BASALT, 2.5F).requiresCorrectToolForDrops().lightLevel(state -> 1).hasPostProcess(SpectrumBlocks::always).emissiveRendering(SpectrumBlocks::always);
	}
	public static final Block BLACK_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_GLOWBLOCK = new GlowBlock(glowBlock(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	private static Properties coloredLamp(MapColor color) {
		return FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP).mapColor(color);
	}
	public static final Block BLACK_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_LAMP = new ColoredLightBlock(coloredLamp(MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_LAMP = new ColoredLightBlock(coloredLamp(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	private static Properties pigmentBlock(MapColor color) {
		return settings(color, SoundType.WOOL, 1.0F);
	}
	public static final Block BLACK_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_BLOCK = new PigmentBlock(pigmentBlock(MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_BLOCK = new PigmentBlock(pigmentBlock(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	private static Properties sporeBlossom(MapColor color) {
		return FabricBlockSettings.copyOf(Blocks.SPORE_BLOSSOM).mapColor(color);
	}
	public static final Block BLACK_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_BLACK), DyeColor.BLACK);
	public static final Block BLUE_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_BLUE), DyeColor.BLUE);
	public static final Block BROWN_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_BROWN), DyeColor.BROWN);
	public static final Block CYAN_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_CYAN), DyeColor.CYAN);
	public static final Block GRAY_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_GRAY), DyeColor.GRAY);
	public static final Block GREEN_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_GREEN), DyeColor.GREEN);
	public static final Block LIGHT_BLUE_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE);
	public static final Block LIGHT_GRAY_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY);
	public static final Block LIME_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME);
	public static final Block MAGENTA_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA);
	public static final Block ORANGE_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_ORANGE), DyeColor.ORANGE);
	public static final Block PINK_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_PINK), DyeColor.PINK);
	public static final Block PURPLE_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_PURPLE), DyeColor.PURPLE);
	public static final Block RED_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_RED), DyeColor.RED);
	public static final Block WHITE_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.SNOW), DyeColor.WHITE);
	public static final Block YELLOW_SPORE_BLOSSOM = new ColoredSporeBlossomBlock(sporeBlossom(MapColor.COLOR_YELLOW), DyeColor.YELLOW);
	
	private static Properties shimmerstoneLight(SoundType soundGroup) {
		return settings(MapColor.NONE, soundGroup, 1.0F).noOcclusion().requiresCorrectToolForDrops().lightLevel(state -> 15);
	}
	public static final Block BASALT_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.BASALT));
	public static final Block CALCITE_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.CALCITE));
	public static final Block STONE_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.STONE));
	public static final Block GRANITE_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.STONE));
	public static final Block DIORITE_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.STONE));
	public static final Block ANDESITE_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.STONE));
	public static final Block DEEPSLATE_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.DEEPSLATE));
	public static final Block BLACKSLAG_SHIMMERSTONE_LIGHT = new ShimmerstoneLightBlock(shimmerstoneLight(SoundType.DEEPSLATE));
	
	// CRYSTALLARIEUM
	private static Properties crystallarieumGrowable(Block baseBlock) {
		return settings(baseBlock.defaultMapColor(), baseBlock.getSoundType(baseBlock.defaultBlockState()), 1.5F).forceSolidOn().pushReaction(PushReaction.DESTROY).requiresCorrectToolForDrops().noOcclusion();
	}
	public static final Block SMALL_COAL_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.COAL_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_COAL_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_COAL_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block COAL_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_COAL_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_COPPER_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.COPPER_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_COPPER_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_COPPER_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block COPPER_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_COPPER_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_DIAMOND_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.DIAMOND_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_DIAMOND_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_DIAMOND_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block DIAMOND_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_DIAMOND_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_EMERALD_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.EMERALD_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_EMERALD_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_EMERALD_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block EMERALD_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_EMERALD_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_GLOWSTONE_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.GLOWSTONE).lightLevel(state -> 4), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_GLOWSTONE_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_GLOWSTONE_BUD).lightLevel(state -> 8), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block GLOWSTONE_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_GLOWSTONE_BUD).lightLevel(state -> 14), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_GOLD_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.GOLD_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_GOLD_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_GOLD_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block GOLD_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_GOLD_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_IRON_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.IRON_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_IRON_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_IRON_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block IRON_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_IRON_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_LAPIS_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.LAPIS_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_LAPIS_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_LAPIS_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block LAPIS_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_LAPIS_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_NETHERITE_SCRAP_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.ANCIENT_DEBRIS), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_NETHERITE_SCRAP_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_NETHERITE_SCRAP_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block NETHERITE_SCRAP_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_NETHERITE_SCRAP_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_ECHO_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.SCULK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_ECHO_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_ECHO_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block ECHO_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_ECHO_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_PRISMARINE_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.SCULK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_PRISMARINE_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_PRISMARINE_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block PRISMARINE_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_PRISMARINE_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_QUARTZ_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.QUARTZ_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_QUARTZ_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_QUARTZ_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block QUARTZ_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_QUARTZ_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	public static final Block SMALL_REDSTONE_BUD = new CrystallarieumGrowableBlock(crystallarieumGrowable(Blocks.REDSTONE_BLOCK), CrystallarieumGrowableBlock.GrowthStage.SMALL);
	public static final Block LARGE_REDSTONE_BUD = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_REDSTONE_BUD), CrystallarieumGrowableBlock.GrowthStage.LARGE);
	public static final Block REDSTONE_CLUSTER = new CrystallarieumGrowableBlock(FabricBlockSettings.copyOf(SMALL_REDSTONE_BUD), CrystallarieumGrowableBlock.GrowthStage.CLUSTER);
	
	public static final Block PURE_COAL_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK));
	public static final Block PURE_IRON_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK));
	public static final Block PURE_GOLD_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK));
	public static final Block PURE_DIAMOND_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK));
	public static final Block PURE_EMERALD_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.EMERALD_BLOCK));
	public static final Block PURE_REDSTONE_BLOCK = new PureRedstoneBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK));
	public static final Block PURE_LAPIS_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.LAPIS_BLOCK));
	public static final Block PURE_COPPER_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK));
	public static final Block PURE_QUARTZ_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.QUARTZ_BLOCK));
	public static final Block PURE_NETHERITE_SCRAP_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.ANCIENT_DEBRIS));
	public static final Block PURE_ECHO_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK));
	public static final Block PURE_GLOWSTONE_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.GLOWSTONE));
	public static final Block PURE_PRISMARINE_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.PRISMARINE));
	
	private static Properties preservationBlock() {
		return settings(MapColor.CLAY, SoundType.STONE, -1.0F, 3600000.0F).instrument(NoteBlockInstrument.BASEDRUM).noLootTable().isValidSpawn(SpectrumBlocks::never);
	}
	public static final Block PRESERVATION_CONTROLLER = new PreservationControllerBlock(preservationBlock().lightLevel(state -> 1).emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always));
	public static final Block DIKE_GATE = new DikeGateBlock(preservationBlock().lightLevel(state -> 3).sound(SoundType.GLASS).noOcclusion().emissiveRendering(SpectrumBlocks::always).hasPostProcess(SpectrumBlocks::always).isRedstoneConductor(SpectrumBlocks::never).isSuffocating(SpectrumBlocks::never).isViewBlocking(SpectrumBlocks::never));
	public static final Block INVISIBLE_WALL = new InvisibleWallBlock(preservationBlock().lightLevel(state -> 3).sound(SoundType.GLASS).noOcclusion().isViewBlocking(SpectrumBlocks::never));
	public static final Block PRESERVATION_CHEST = new TreasureChestBlock(preservationBlock());
	
	public static final Block DOWNSTONE = new Block(preservationBlock()); // "raw" preservation stone, used in the Deeper Down bottom in place of bedrock
	public static final Block PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block PRESERVATION_STAIRS = new StairBlock(PRESERVATION_STONE.defaultBlockState(), preservationBlock());
	public static final Block PRESERVATION_SLAB = new SlabBlock(preservationBlock());
	public static final Block POWDER_CHISELED_PRESERVATION_STONE = new Block(preservationBlock().lightLevel(state -> 2));
	public static final Block DIKE_CHISELED_PRESERVATION_STONE = new Block(preservationBlock().lightLevel(state -> 6));
	public static final Block DIKE_GATE_FOUNTAIN = new SpectrumFacingBlock(preservationBlock());
	public static final Block PRESERVATION_BRICKS = new Block(preservationBlock());
	public static final Block SHIMMERING_PRESERVATION_BRICKS = new Block(FabricBlockSettings.copyOf(preservationBlock()).luminance(5));
	public static final Block COURIER_STATUE = new StatueBlock(preservationBlock());
	
	public static final Block BLACK_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block BLUE_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block BROWN_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block CYAN_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block GRAY_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block GREEN_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block LIGHT_BLUE_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block LIGHT_GRAY_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block LIME_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block MAGENTA_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block ORANGE_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block PINK_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block PURPLE_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block RED_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block WHITE_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	public static final Block YELLOW_CHISELED_PRESERVATION_STONE = new Block(preservationBlock());
	
	public static final Block PRESERVATION_GLASS = new GlassBlock(preservationBlock().sound(SoundType.GLASS).noOcclusion().isRedstoneConductor(SpectrumBlocks::never).isSuffocating(SpectrumBlocks::never).isViewBlocking(SpectrumBlocks::never));
	public static final Block TINTED_PRESERVATION_GLASS = new GlassBlock(FabricBlockSettings.copyOf(PRESERVATION_GLASS).luminance(12).strength(Float.MAX_VALUE, 3600000.0F));
	public static final Block PRESERVATION_ROUNDEL = new PreservationRoundelBlock(preservationBlock().noOcclusion());
	public static final Block PRESERVATION_BLOCK_DETECTOR = new PreservationBlockDetectorBlock(preservationBlock());
	
	private static Properties shootingStar() {
		return FabricBlockSettings.copyOf(Blocks.STONE).noOcclusion();
	}
	public static final ShootingStarBlock GLISTERING_SHOOTING_STAR = new ShootingStarBlock(shootingStar(), ShootingStar.Type.GLISTERING);
	public static final ShootingStarBlock FIERY_SHOOTING_STAR = new ShootingStarBlock(shootingStar(), ShootingStar.Type.FIERY);
	public static final ShootingStarBlock COLORFUL_SHOOTING_STAR = new ShootingStarBlock(shootingStar(), ShootingStar.Type.COLORFUL);
	public static final ShootingStarBlock PRISTINE_SHOOTING_STAR = new ShootingStarBlock(shootingStar(), ShootingStar.Type.PRISTINE);
	public static final ShootingStarBlock GEMSTONE_SHOOTING_STAR = new ShootingStarBlock(shootingStar(), ShootingStar.Type.GEMSTONE);
	public static final Block STARDUST_BLOCK = new SandBlock(DyeColor.PURPLE.getFireworkColor(), FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.COLOR_PURPLE));

	public static final Block INCANDESCENT_AMALGAM = new IncandescentAmalgamBlock(FabricBlockSettings.of().instabreak().noOcclusion());
	
	private static Properties idol(SoundType soundGroup) {
		return settings(MapColor.TERRACOTTA_WHITE, soundGroup, 3.0F).requiresCorrectToolForDrops().noOcclusion();
	}
	public static final Block AXOLOTL_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.AXOLOTL_IDOL), ParticleTypes.HEART, MobEffects.REGENERATION, 0, 100); // heals 2 hp / 1 heart
	public static final Block BAT_IDOL = new AoEStatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.BAT_IDOL), ParticleTypes.INSTANT_EFFECT, MobEffects.GLOWING, 0, 200, 8);
	public static final Block BEE_IDOL = new BonemealingIdolBlock(idol(SpectrumBlockSoundGroups.BEE_IDOL), ParticleTypes.DRIPPING_HONEY);
	public static final Block BLAZE_IDOL = new FirestarterIdolBlock(idol(SpectrumBlockSoundGroups.BLAZE_IDOL), ParticleTypes.FLAME);
	public static final Block CAT_IDOL = new FallDamageNegatingIdolBlock(idol(SpectrumBlockSoundGroups.CAT_IDOL), ParticleTypes.ENCHANTED_HIT);
	public static final Block CHICKEN_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.CHICKEN_IDOL), ParticleTypes.ENCHANTED_HIT, MobEffects.SLOW_FALLING, 0, 100);
	public static final Block COW_IDOL = new MilkingIdolBlock(idol(SpectrumBlockSoundGroups.COW_IDOL), ParticleTypes.ENCHANTED_HIT, 6);
	public static final Block CREEPER_IDOL = new ExplosionIdolBlock(idol(SpectrumBlockSoundGroups.CREEPER_IDOL), ParticleTypes.EXPLOSION, 3, false, Explosion.BlockInteraction.DESTROY);
	public static final Block ENDER_DRAGON_IDOL = new ProjectileIdolBlock(idol(SpectrumBlockSoundGroups.ENDER_DRAGON_IDOL), ParticleTypes.DRAGON_BREATH, EntityType.DRAGON_FIREBALL, SoundEvents.ENDER_DRAGON_SHOOT, 6.0F, 1.1F) {
		@Override
		public Projectile createProjectile(ServerLevel world, BlockPos mobBlockPos, Position position, Direction side) {
			LivingMarkerEntity markerEntity = new LivingMarkerEntity(SpectrumEntityTypes.LIVING_MARKER, world);
			markerEntity.setPosRaw(position.x(), position.y(), position.z());
			
			Vec3 targetPosition = Vec3.atCenterOf(mobBlockPos.relative(side, 50));
			double f = targetPosition.x() - markerEntity.getX();
			double g = targetPosition.y() - markerEntity.getY();
			double h = targetPosition.z() - markerEntity.getZ();
			
			DragonFireball entity = new DragonFireball(world, markerEntity, f, g, h);
			
			markerEntity.discard();
			return entity;
		}
	};
	public static final Block ENDERMAN_IDOL = new RandomTeleportingIdolBlock(idol(SpectrumBlockSoundGroups.ENDERMAN_IDOL), ParticleTypes.REVERSE_PORTAL, 16, 16);
	public static final Block ENDERMITE_IDOL = new LineTeleportingIdolBlock(idol(SpectrumBlockSoundGroups.ENDERMITE_IDOL), ParticleTypes.REVERSE_PORTAL, 16);
	public static final Block EVOKER_IDOL = new EntitySummoningIdolBlock(idol(SpectrumBlockSoundGroups.EVOKER_IDOL), ParticleTypes.ANGRY_VILLAGER, EntityType.VEX) {
		@Override
		public void afterSummon(ServerLevel world, Entity entity) {
			((Vex) entity).setLimitedLife(20 * (30 + world.random.nextInt(90)));
		}
	};
	public static final Block FISH_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.FISH_IDOL), ParticleTypes.SPLASH, MobEffects.WATER_BREATHING, 0, 200);
	public static final Block FOX_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.FOX_IDOL), ParticleTypes.ENCHANTED_HIT, MobEffects.DIG_SPEED, 0, 200);
	public static final Block GHAST_IDOL = new ProjectileIdolBlock(idol(SpectrumBlockSoundGroups.GHAST_IDOL), ParticleTypes.SMOKE, EntityType.FIREBALL, SoundEvents.GHAST_SHOOT, 6.0F, 1.1F) {
		@Override
		public Projectile createProjectile(ServerLevel world, BlockPos mobBlockPos, Position position, Direction side) {
			LivingMarkerEntity markerEntity = new LivingMarkerEntity(SpectrumEntityTypes.LIVING_MARKER, world);
			markerEntity.setPosRaw(position.x(), position.y(), position.z());
			
			Vec3 targetPosition = Vec3.atCenterOf(mobBlockPos.relative(side, 50));
			double f = targetPosition.x() - markerEntity.getX();
			double g = targetPosition.y() - markerEntity.getY();
			double h = targetPosition.z() - markerEntity.getZ();
			
			LargeFireball entity = new LargeFireball(world, markerEntity, f, g, h, 1);
			
			markerEntity.discard();
			return entity;
		}
	};
	public static final Block GLOW_SQUID_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.GLOW_SQUID_IDOL), ParticleTypes.GLOW_SQUID_INK, MobEffects.GLOWING, 0, 200);
	public static final Block GOAT_IDOL = new KnockbackIdolBlock(idol(SpectrumBlockSoundGroups.GOAT_IDOL), ParticleTypes.ENCHANTED_HIT, 5.0F, 0.5F); // knocks mostly sideways
	public static final Block GUARDIAN_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.GUARDIAN_IDOL), ParticleTypes.BUBBLE, MobEffects.DIG_SLOWDOWN, 2, 200);
	public static final Block HORSE_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.HORSE_IDOL), ParticleTypes.INSTANT_EFFECT, MobEffects.DAMAGE_BOOST, 0, 100);
	public static final Block ILLUSIONER_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.ILLUSIONER_IDOL), ParticleTypes.ANGRY_VILLAGER, MobEffects.INVISIBILITY, 0, 100);
	public static final Block OCELOT_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.OCELOT_IDOL), ParticleTypes.INSTANT_EFFECT, MobEffects.NIGHT_VISION, 0, 100);
	public static final Block PARROT_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.PARROT_IDOL), ParticleTypes.INSTANT_EFFECT, MobEffects.ABSORPTION, 0, 100);
	public static final Block PHANTOM_IDOL = new InsomniaIdolBlock(idol(SpectrumBlockSoundGroups.PHANTOM_IDOL), ParticleTypes.POOF, 24000); // +1 ingame day without sleep
	public static final Block PIG_IDOL = new FeedingIdolBlock(idol(SpectrumBlockSoundGroups.PIG_IDOL), ParticleTypes.INSTANT_EFFECT, 6);
	public static final Block PIGLIN_IDOL = new PiglinTradeIdolBlock(idol(SpectrumBlockSoundGroups.PIGLIN_IDOL), ParticleTypes.HEART);
	public static final Block POLAR_BEAR_IDOL = new FreezingIdolBlock(idol(SpectrumBlockSoundGroups.POLAR_BEAR_IDOL), ParticleTypes.SNOWFLAKE);
	public static final Block PUFFERFISH_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.PUFFERFISH_IDOL), ParticleTypes.SPLASH, MobEffects.CONFUSION, 0, 200);
	public static final Block RABBIT_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.RABBIT_IDOL), ParticleTypes.INSTANT_EFFECT, MobEffects.JUMP, 3, 100);
	public static final Block SHEEP_IDOL = new ShearingIdolBlock(idol(SpectrumBlockSoundGroups.SHEEP_IDOL), ParticleTypes.ENCHANTED_HIT, 6);
	public static final Block SHULKER_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.SHULKER_IDOL), ParticleTypes.END_ROD, MobEffects.LEVITATION, 0, 100);
	public static final Block SILVERFISH_IDOL = new SilverfishInsertingIdolBlock(idol(SpectrumBlockSoundGroups.SILVERFISH_IDOL), ParticleTypes.EXPLOSION);
	public static final Block SKELETON_IDOL = new ProjectileIdolBlock(idol(SpectrumBlockSoundGroups.SKELETON_IDOL), ParticleTypes.INSTANT_EFFECT, EntityType.ARROW, SoundEvents.ARROW_SHOOT, 6.0F, 1.1F) {
		@Override
		public Projectile createProjectile(ServerLevel world, BlockPos mobBlockPos, Position position, Direction side) {
			Arrow arrowEntity = new Arrow(world, position.x(), position.y(), position.z());
			arrowEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
			return arrowEntity;
		}
	};
	public static final Block SLIME_IDOL = new SlimeSizingIdolBlock(idol(SpectrumBlockSoundGroups.SLIME_IDOL), ParticleTypes.ITEM_SLIME, 6, 8);
	public static final Block SNOW_GOLEM_IDOL = new ProjectileIdolBlock(idol(SpectrumBlockSoundGroups.SNOW_GOLEM_IDOL), ParticleTypes.SNOWFLAKE, EntityType.SNOWBALL, SoundEvents.ARROW_SHOOT, 3.0F, 1.1F) {
		@Override
		public Projectile createProjectile(ServerLevel world, BlockPos mobBlockPos, Position position, Direction side) {
			world.playSound(null, mobBlockPos.getX(), mobBlockPos.getY(), mobBlockPos.getZ(), SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.BLOCKS, 1.0F, 0.4F / world.random.nextFloat() * 0.4F + 0.8F);
			return new Snowball(world, position.x(), position.y(), position.z());
		}
	};
	public static final Block SPIDER_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.SPIDER_IDOL), ParticleTypes.ENCHANTED_HIT, MobEffects.POISON, 0, 100);
	public static final Block SQUID_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.SQUID_IDOL), ParticleTypes.SQUID_INK, MobEffects.BLINDNESS, 0, 200);
	public static final Block STRAY_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.STRAY_IDOL), ParticleTypes.ENCHANTED_HIT, MobEffects.MOVEMENT_SLOWDOWN, 2, 100);
	public static final Block STRIDER_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.STRIDER_IDOL), ParticleTypes.DRIPPING_LAVA, MobEffects.FIRE_RESISTANCE, 0, 200);
	public static final Block TURTLE_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.TURTLE_IDOL), ParticleTypes.DRIPPING_WATER, MobEffects.DAMAGE_RESISTANCE, 1, 200);
	public static final Block WITCH_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.WITCH_IDOL), ParticleTypes.ENCHANTED_HIT, MobEffects.WEAKNESS, 0, 200);
	public static final Block WITHER_IDOL = new ExplosionIdolBlock(idol(SpectrumBlockSoundGroups.WITHER_IDOL), ParticleTypes.EXPLOSION, 7.0F, true, Explosion.BlockInteraction.DESTROY);
	public static final Block WITHER_SKELETON_IDOL = new StatusEffectIdolBlock(idol(SpectrumBlockSoundGroups.WITHER_SKELETON_IDOL), ParticleTypes.ENCHANTED_HIT, MobEffects.WITHER, 0, 100);
	public static final Block ZOMBIE_IDOL = new VillagerConvertingIdolBlock(idol(SpectrumBlockSoundGroups.ZOMBIE_IDOL), ParticleTypes.ENCHANTED_HIT);
	
	public static BiMap<SpectrumSkullBlockType, Block> MOB_HEADS;
	public static BiMap<SpectrumSkullBlockType, Block> MOB_WALL_HEADS;
	
	static boolean never(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
		return false;
	}
	
	static boolean always(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}
	
	static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
	}
	
	static void registerBlock(String name, Block block) {
		Registry.register(BuiltInRegistries.BLOCK, locate(name), block);
	}
	
	static void registerBlockItem(String name, BlockItem blockItem, DyeColor dyeColor) {
		Registry.register(BuiltInRegistries.ITEM, locate(name), blockItem);
		ItemColors.ITEM_COLORS.registerColorMapping(blockItem, dyeColor);
	}
	
	public static void registerBlockWithItem(String name, Block block, Item.Properties itemSettings, DyeColor dyeColor) {
		Registry.register(BuiltInRegistries.BLOCK, locate(name), block);
		BlockItem blockItem = new BlockItem(block, itemSettings);
		Registry.register(BuiltInRegistries.ITEM, locate(name), blockItem);
		ItemColors.ITEM_COLORS.registerColorMapping(blockItem, dyeColor);
	}
	
	static void registerBlockWithItem(String name, Block block, BlockItem blockItem, DyeColor dyeColor) {
		Registry.register(BuiltInRegistries.BLOCK, locate(name), block);
		Registry.register(BuiltInRegistries.ITEM, locate(name), blockItem);
		ItemColors.ITEM_COLORS.registerColorMapping(blockItem, dyeColor);
	}
	
	public static void register() {
		registerBlockWithItem("pedestal_basic_topaz", PEDESTAL_BASIC_TOPAZ, new PedestalBlockItem(PEDESTAL_BASIC_TOPAZ, IS.of(1), BuiltinPedestalVariant.BASIC_TOPAZ, "item.spectrum.pedestal.tooltip.basic_topaz"), DyeColor.WHITE);
		registerBlockWithItem("pedestal_basic_amethyst", PEDESTAL_BASIC_AMETHYST, new PedestalBlockItem(PEDESTAL_BASIC_AMETHYST, IS.of(1), BuiltinPedestalVariant.BASIC_AMETHYST, "item.spectrum.pedestal.tooltip.basic_amethyst"), DyeColor.WHITE);
		registerBlockWithItem("pedestal_basic_citrine", PEDESTAL_BASIC_CITRINE, new PedestalBlockItem(PEDESTAL_BASIC_CITRINE, IS.of(1), BuiltinPedestalVariant.BASIC_CITRINE, "item.spectrum.pedestal.tooltip.basic_citrine"), DyeColor.WHITE);
		registerBlockWithItem("pedestal_all_basic", PEDESTAL_ALL_BASIC, new PedestalBlockItem(PEDESTAL_ALL_BASIC, IS.of(1), BuiltinPedestalVariant.CMY, "item.spectrum.pedestal.tooltip.all_basic"), DyeColor.WHITE);
		registerBlockWithItem("pedestal_onyx", PEDESTAL_ONYX, new PedestalBlockItem(PEDESTAL_ONYX, IS.of(1), BuiltinPedestalVariant.ONYX, "item.spectrum.pedestal.tooltip.onyx"), DyeColor.WHITE);
		registerBlockWithItem("pedestal_moonstone", PEDESTAL_MOONSTONE, new PedestalBlockItem(PEDESTAL_MOONSTONE, IS.of(1), BuiltinPedestalVariant.MOONSTONE, "item.spectrum.pedestal.tooltip.moonstone"), DyeColor.WHITE);
		registerBlockWithItem("fusion_shrine_basalt", FUSION_SHRINE_BASALT, IS.of(1), DyeColor.GRAY);
		registerBlockWithItem("fusion_shrine_calcite", FUSION_SHRINE_CALCITE, IS.of(1), DyeColor.GRAY);
		registerBlockWithItem("enchanter", ENCHANTER, IS.of(1), DyeColor.PURPLE);
		registerBlockWithItem("item_bowl_basalt", ITEM_BOWL_BASALT, IS.of(16), DyeColor.PINK);
		registerBlockWithItem("item_bowl_calcite", ITEM_BOWL_CALCITE, IS.of(16), DyeColor.PINK);
		registerBlockWithItem("item_roundel", ITEM_ROUNDEL, IS.of(16), DyeColor.PINK);
		registerBlockWithItem("potion_workshop", POTION_WORKSHOP, IS.of(1), DyeColor.PURPLE);
		registerBlockWithItem("spirit_instiller", SPIRIT_INSTILLER, IS.of(1), DyeColor.WHITE);
		registerBlockWithItem("crystallarieum", CRYSTALLARIEUM, IS.of(1), DyeColor.BROWN);
		registerBlockWithItem("cinderhearth", CINDERHEARTH, IS.of(1).fireResistant(), DyeColor.ORANGE);
		registerBlockWithItem("crystal_apothecary", CRYSTAL_APOTHECARY, IS.of(8), DyeColor.GREEN);
		registerBlockWithItem("color_picker", COLOR_PICKER, IS.of(8), DyeColor.GREEN);
		
		registerBlockWithItem("upgrade_speed", UPGRADE_SPEED, new UpgradeBlockItem(UPGRADE_SPEED, IS.of(8), "upgrade_speed"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_speed2", UPGRADE_SPEED2, new UpgradeBlockItem(UPGRADE_SPEED2, IS.of(8, Rarity.UNCOMMON), "upgrade_speed2"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_speed3", UPGRADE_SPEED3, new UpgradeBlockItem(UPGRADE_SPEED3, IS.of(8, Rarity.RARE), "upgrade_speed3"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_efficiency", UPGRADE_EFFICIENCY, new UpgradeBlockItem(UPGRADE_EFFICIENCY, IS.of(8, Rarity.UNCOMMON), "upgrade_efficiency"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_efficiency2", UPGRADE_EFFICIENCY2, new UpgradeBlockItem(UPGRADE_EFFICIENCY2, IS.of(8, Rarity.RARE), "upgrade_efficiency2"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_yield", UPGRADE_YIELD, new UpgradeBlockItem(UPGRADE_YIELD, IS.of(8, Rarity.UNCOMMON), "upgrade_yield"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_yield2", UPGRADE_YIELD2, new UpgradeBlockItem(UPGRADE_YIELD2, IS.of(8, Rarity.RARE), "upgrade_yield2"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_experience", UPGRADE_EXPERIENCE, new UpgradeBlockItem(UPGRADE_EXPERIENCE, IS.of(8), "upgrade_experience"), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("upgrade_experience2", UPGRADE_EXPERIENCE2, new UpgradeBlockItem(UPGRADE_EXPERIENCE2, IS.of(8, Rarity.UNCOMMON), "upgrade_experience2"), DyeColor.LIGHT_GRAY);
		
		registerPastelNetworkNodes(IS.of(16));
		registerStoneBlocks(IS.of());
		registerGemBlocks(IS.of());
		
		registerShootingStarBlocks(IS.of(1, Rarity.UNCOMMON));
		
		registerGemOreBlocks(IS.of());
		registerOreBlocks(IS.of(), IS.of().fireResistant());
		registerOreStorageBlocks(IS.of(), IS.of().fireResistant());
		registerGemstoneLamps(IS.of());
		registerShimmerstoneLights(IS.of());
		registerRunes(IS.of());
		registerGemstoneGlass(IS.of());
		registerPlayerOnlyGlass(IS.of());
		registerGemstoneChimes(IS.of());
		registerDecoStones(IS.of());
		registerPigmentStorageBlocks(IS.of());
		registerColoredLamps(IS.of());
		registerGlowBlocks(IS.of());
		registerSporeBlossoms(IS.of());
		registerColoredWood(IS.of());
		registerDDFlora(IS.of());
		registerRedstone(IS.of());
		registerMagicalBlocks(IS.of());
		registerMobBlocks(IS.of());
		registerCrystallarieumGrowingBlocks(IS.of());
		registerPureOreBlocks(IS.of());
		registerJadeVineBlocks(IS.of());
		registerSugarSticks(IS.of());
		registerStructureBlocks(IS.of());
		registerSpiritTree(IS.of());
		
		// Decay
		registerBlock("fading", FADING);
		registerBlock("failing", FAILING);
		registerBlock("ruin", RUIN);
		registerBlock("forfeiture", FORFEITURE);
		registerBlock("decay_away", DECAY_AWAY);
		
		// Fluids + Products
		registerBlock("mud", MUD);
		registerBlock("liquid_crystal", LIQUID_CRYSTAL);
		registerBlock("midnight_solution", MIDNIGHT_SOLUTION);
		registerBlock("dragonrot", DRAGONROT);
		
		registerBlockWithItem("black_materia", BLACK_MATERIA, IS.of(), DyeColor.GRAY);
		registerBlockWithItem("frostbite_crystal", FROSTBITE_CRYSTAL, IS.of(), DyeColor.LIGHT_BLUE);
		registerBlockWithItem("blazing_crystal", BLAZING_CRYSTAL, IS.of(), DyeColor.ORANGE);
		registerBlockWithItem("resonant_lily", RESONANT_LILY, IS.of(), DyeColor.GREEN);
		registerBlockWithItem("clover", CLOVER, IS.of(), DyeColor.LIME);
		registerBlockWithItem("four_leaf_clover", FOUR_LEAF_CLOVER, new FourLeafCloverItem(FOUR_LEAF_CLOVER, IS.of(), locate("milestones/reveal_four_leaf_clover"), CLOVER.asItem()), DyeColor.LIME);
		registerBlockWithItem("incandescent_amalgam", INCANDESCENT_AMALGAM, new IncandescentAmalgamItem(INCANDESCENT_AMALGAM, IS.of(16).food(SpectrumFoodComponents.INCANDESCENT_AMALGAM)), DyeColor.RED);
		
		registerBlockWithItem("blood_orchid", BLOOD_ORCHID, IS.of(), DyeColor.RED);
		registerBlock("potted_blood_orchid", POTTED_BLOOD_ORCHID);
		registerBlock("potted_resonant_lily", POTTED_RESONANT_LILY);
		
		// Worldgen
		registerBlockWithItem("quitoxic_reeds", QUITOXIC_REEDS, IS.of(), DyeColor.PURPLE);
		registerBlockWithItem("radiating_ender", RADIATING_ENDER, IS.of(), DyeColor.PURPLE);
		
		registerBlock("amaranth", AMARANTH);
		registerBlockWithItem("amaranth_bushel", AMARANTH_BUSHEL, IS.of(), DyeColor.RED);
		registerBlock("potted_amaranth_bushel", POTTED_AMARANTH_BUSHEL);
		
		registerBlockWithItem("bedrock_anvil", BEDROCK_ANVIL, IS.of(), DyeColor.BLACK);
		registerBlockWithItem("cracked_end_portal_frame", CRACKED_END_PORTAL_FRAME, IS.of(), DyeColor.PURPLE);
		
		registerBlockWithItem("memory", MEMORY, new MemoryItem(MEMORY, IS.of(Rarity.UNCOMMON)), DyeColor.LIGHT_GRAY);
		
		// Technical Blocks without items
		registerBlock("mermaids_brush", MERMAIDS_BRUSH);
		registerBlock("sag_leaf", SAG_LEAF);
		registerBlock("sag_bubble", SAG_BUBBLE);
		registerBlock("small_sag_bubble", SMALL_SAG_BUBBLE);
		
		registerBlock("primordial_fire", PRIMORDIAL_FIRE);
		registerBlock("deeper_down_portal", DEEPER_DOWN_PORTAL);
		registerBlock("glistering_melon_stem", GLISTERING_MELON_STEM);
		registerBlock("attached_glistering_melon_stem", ATTACHED_GLISTERING_MELON_STEM);
		registerBlock("stuck_storm_stone", STUCK_STORM_STONE);
		registerBlock("wand_light", WAND_LIGHT_BLOCK);
		registerBlock("decaying_light", DECAYING_LIGHT_BLOCK);
		registerBlock("block_flooder", BLOCK_FLOODER);
		registerBlock("bottomless_bundle", BOTTOMLESS_BUNDLE);
		
		registerMobHeads(IS.of());
	}
	
	private static void registerDDFlora(Item.Properties settings) {
		registerBlockWithItem("sawblade_grass", SAWBLADE_GRASS, settings, DyeColor.LIME);
		registerBlockWithItem("overgrown_blackslag", OVERGROWN_BLACKSLAG, settings, DyeColor.LIME);
		registerBlockWithItem("shimmel", SHIMMEL, settings, DyeColor.LIME);
		registerBlockWithItem("rotten_ground", ROTTEN_GROUND, settings, DyeColor.LIME);
		
		registerBlockWithItem("slate_noxshroom", SLATE_NOXSHROOM, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxcap_block", SLATE_NOXCAP_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxcap_stem", SLATE_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_slate_noxcap_stem", STRIPPED_SLATE_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxcap_hyphae", SLATE_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_slate_noxcap_hyphae", STRIPPED_SLATE_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxcap_gills", SLATE_NOXCAP_GILLS, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_planks", SLATE_NOXWOOD_PLANKS, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_stairs", SLATE_NOXWOOD_STAIRS, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_slab", SLATE_NOXWOOD_SLAB, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_fence", SLATE_NOXWOOD_FENCE, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_fence_gate", SLATE_NOXWOOD_FENCE_GATE, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_door", SLATE_NOXWOOD_DOOR, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_trapdoor", SLATE_NOXWOOD_TRAPDOOR, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_pressure_plate", SLATE_NOXWOOD_PRESSURE_PLATE, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_button", SLATE_NOXWOOD_BUTTON, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_beam", SLATE_NOXWOOD_BEAM, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_amphora", SLATE_NOXWOOD_AMPHORA, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_lantern", SLATE_NOXWOOD_LANTERN, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_light", SLATE_NOXWOOD_LIGHT, settings, DyeColor.LIME);
		registerBlockWithItem("slate_noxwood_lamp", SLATE_NOXWOOD_LAMP, settings, DyeColor.LIME);
		
		registerBlockWithItem("ebony_noxshroom", EBONY_NOXSHROOM, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxcap_block", EBONY_NOXCAP_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxcap_stem", EBONY_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_ebony_noxcap_stem", STRIPPED_EBONY_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxcap_hyphae", EBONY_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_ebony_noxcap_hyphae", STRIPPED_EBONY_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxcap_gills", EBONY_NOXCAP_GILLS, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_planks", EBONY_NOXWOOD_PLANKS, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_stairs", EBONY_NOXWOOD_STAIRS, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_slab", EBONY_NOXWOOD_SLAB, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_fence", EBONY_NOXWOOD_FENCE, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_fence_gate", EBONY_NOXWOOD_FENCE_GATE, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_door", EBONY_NOXWOOD_DOOR, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_trapdoor", EBONY_NOXWOOD_TRAPDOOR, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_pressure_plate", EBONY_NOXWOOD_PRESSURE_PLATE, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_button", EBONY_NOXWOOD_BUTTON, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_beam", EBONY_NOXWOOD_BEAM, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_amphora", EBONY_NOXWOOD_AMPHORA, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_lantern", EBONY_NOXWOOD_LANTERN, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_light", EBONY_NOXWOOD_LIGHT, settings, DyeColor.LIME);
		registerBlockWithItem("ebony_noxwood_lamp", EBONY_NOXWOOD_LAMP, settings, DyeColor.LIME);
		
		registerBlockWithItem("ivory_noxshroom", IVORY_NOXSHROOM, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxcap_block", IVORY_NOXCAP_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxcap_stem", IVORY_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_ivory_noxcap_stem", STRIPPED_IVORY_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxcap_hyphae", IVORY_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_ivory_noxcap_hyphae", STRIPPED_IVORY_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxcap_gills", IVORY_NOXCAP_GILLS, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_planks", IVORY_NOXWOOD_PLANKS, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_stairs", IVORY_NOXWOOD_STAIRS, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_slab", IVORY_NOXWOOD_SLAB, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_fence", IVORY_NOXWOOD_FENCE, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_fence_gate", IVORY_NOXWOOD_FENCE_GATE, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_door", IVORY_NOXWOOD_DOOR, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_trapdoor", IVORY_NOXWOOD_TRAPDOOR, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_pressure_plate", IVORY_NOXWOOD_PRESSURE_PLATE, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_button", IVORY_NOXWOOD_BUTTON, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_beam", IVORY_NOXWOOD_BEAM, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_amphora", IVORY_NOXWOOD_AMPHORA, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_lantern", IVORY_NOXWOOD_LANTERN, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_light", IVORY_NOXWOOD_LIGHT, settings, DyeColor.LIME);
		registerBlockWithItem("ivory_noxwood_lamp", IVORY_NOXWOOD_LAMP, settings, DyeColor.LIME);
		
		registerBlockWithItem("chestnut_noxshroom", CHESTNUT_NOXSHROOM, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxcap_block", CHESTNUT_NOXCAP_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxcap_stem", CHESTNUT_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_chestnut_noxcap_stem", STRIPPED_CHESTNUT_NOXCAP_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxcap_hyphae", CHESTNUT_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_chestnut_noxcap_hyphae", STRIPPED_CHESTNUT_NOXCAP_HYPHAE, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxcap_gills", CHESTNUT_NOXCAP_GILLS, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_planks", CHESTNUT_NOXWOOD_PLANKS, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_stairs", CHESTNUT_NOXWOOD_STAIRS, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_slab", CHESTNUT_NOXWOOD_SLAB, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_fence", CHESTNUT_NOXWOOD_FENCE, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_fence_gate", CHESTNUT_NOXWOOD_FENCE_GATE, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_door", CHESTNUT_NOXWOOD_DOOR, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_trapdoor", CHESTNUT_NOXWOOD_TRAPDOOR, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_pressure_plate", CHESTNUT_NOXWOOD_PRESSURE_PLATE, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_button", CHESTNUT_NOXWOOD_BUTTON, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_beam", CHESTNUT_NOXWOOD_BEAM, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_amphora", CHESTNUT_NOXWOOD_AMPHORA, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_lantern", CHESTNUT_NOXWOOD_LANTERN, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_light", CHESTNUT_NOXWOOD_LIGHT, settings, DyeColor.LIME);
		registerBlockWithItem("chestnut_noxwood_lamp", CHESTNUT_NOXWOOD_LAMP, settings, DyeColor.LIME);
		
		registerBlock("potted_slate_noxshroom", POTTED_SLATE_NOXSHROOM);
		registerBlock("potted_ebony_noxshroom", POTTED_EBONY_NOXSHROOM);
		registerBlock("potted_ivory_noxshroom", POTTED_IVORY_NOXSHROOM);
		registerBlock("potted_chestnut_noxshroom", POTTED_CHESTNUT_NOXSHROOM);
		
		registerBlockWithItem("small_red_dragonjag", SMALL_RED_DRAGONJAG, settings, DyeColor.LIME);
		registerBlockWithItem("small_yellow_dragonjag", SMALL_YELLOW_DRAGONJAG, settings, DyeColor.LIME);
		registerBlockWithItem("small_pink_dragonjag", SMALL_PINK_DRAGONJAG, settings, DyeColor.LIME);
		registerBlockWithItem("small_purple_dragonjag", SMALL_PURPLE_DRAGONJAG, settings, DyeColor.LIME);
		registerBlockWithItem("small_black_dragonjag", SMALL_BLACK_DRAGONJAG, settings, DyeColor.LIME);
		registerBlock("tall_red_dragonjag", TALL_RED_DRAGONJAG);
		registerBlock("tall_yellow_dragonjag", TALL_YELLOW_DRAGONJAG);
		registerBlock("tall_pink_dragonjag", TALL_PINK_DRAGONJAG);
		registerBlock("tall_purple_dragonjag", TALL_PURPLE_DRAGONJAG);
		registerBlock("tall_black_dragonjag", TALL_BLACK_DRAGONJAG);
		
		registerBlock("aloe", ALOE);
		registerBlock("sawblade_holly_bush", SAWBLADE_HOLLY_BUSH);
		registerBlockWithItem("bristle_sprouts", BRISTLE_SPROUTS, settings, DyeColor.LIME);
		registerBlock("doombloom", DOOMBLOOM);
		registerBlockWithItem("snapping_ivy", SNAPPING_IVY, settings, DyeColor.RED);
		
		registerBlockWithItem("hummingstone", HUMMINGSTONE, settings, DyeColor.LIME);
		registerBlockWithItem("hummingstone_glass", HUMMINGSTONE_GLASS, settings, DyeColor.LIME);
		registerBlockWithItem("clear_hummingstone_glass", CLEAR_HUMMINGSTONE_GLASS, settings, DyeColor.LIME);
	}
	
	private static void registerCrystallarieumGrowingBlocks(Item.Properties settings) {
		// vanilla
		registerBlockWithItem("small_coal_bud", SMALL_COAL_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_coal_bud", LARGE_COAL_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("coal_cluster", COAL_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_iron_bud", SMALL_IRON_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_iron_bud", LARGE_IRON_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("iron_cluster", IRON_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_gold_bud", SMALL_GOLD_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_gold_bud", LARGE_GOLD_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("gold_cluster", GOLD_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_diamond_bud", SMALL_DIAMOND_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("large_diamond_bud", LARGE_DIAMOND_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("diamond_cluster", DIAMOND_CLUSTER, settings, DyeColor.CYAN);
		
		registerBlockWithItem("small_emerald_bud", SMALL_EMERALD_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("large_emerald_bud", LARGE_EMERALD_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("emerald_cluster", EMERALD_CLUSTER, settings, DyeColor.CYAN);
		
		registerBlockWithItem("small_redstone_bud", SMALL_REDSTONE_BUD, settings, DyeColor.RED);
		registerBlockWithItem("large_redstone_bud", LARGE_REDSTONE_BUD, settings, DyeColor.RED);
		registerBlockWithItem("redstone_cluster", REDSTONE_CLUSTER, settings, DyeColor.RED);
		
		registerBlockWithItem("small_lapis_bud", SMALL_LAPIS_BUD, settings, DyeColor.PURPLE);
		registerBlockWithItem("large_lapis_bud", LARGE_LAPIS_BUD, settings, DyeColor.PURPLE);
		registerBlockWithItem("lapis_cluster", LAPIS_CLUSTER, settings, DyeColor.PURPLE);
		
		registerBlockWithItem("small_copper_bud", SMALL_COPPER_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_copper_bud", LARGE_COPPER_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("copper_cluster", COPPER_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_quartz_bud", SMALL_QUARTZ_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_quartz_bud", LARGE_QUARTZ_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("quartz_cluster", QUARTZ_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_netherite_scrap_bud", SMALL_NETHERITE_SCRAP_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_netherite_scrap_bud", LARGE_NETHERITE_SCRAP_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("netherite_scrap_cluster", NETHERITE_SCRAP_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_echo_bud", SMALL_ECHO_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("large_echo_bud", LARGE_ECHO_BUD, settings, DyeColor.BROWN);
		registerBlockWithItem("echo_cluster", ECHO_CLUSTER, settings, DyeColor.BROWN);
		
		registerBlockWithItem("small_glowstone_bud", SMALL_GLOWSTONE_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("large_glowstone_bud", LARGE_GLOWSTONE_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("glowstone_cluster", GLOWSTONE_CLUSTER, settings, DyeColor.YELLOW);
		
		registerBlockWithItem("small_prismarine_bud", SMALL_PRISMARINE_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("large_prismarine_bud", LARGE_PRISMARINE_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("prismarine_cluster", PRISMARINE_CLUSTER, settings, DyeColor.CYAN);
	}
	
	private static void registerRedstone(Item.Properties settings) {
		registerBlockWithItem("light_level_detector", LIGHT_LEVEL_DETECTOR, settings, DyeColor.RED);
		registerBlockWithItem("weather_detector", WEATHER_DETECTOR, settings, DyeColor.RED);
		registerBlockWithItem("item_detector", ITEM_DETECTOR, settings, DyeColor.RED);
		registerBlockWithItem("player_detector", PLAYER_DETECTOR, settings, DyeColor.RED);
		registerBlockWithItem("entity_detector", ENTITY_DETECTOR, settings, DyeColor.RED);
		
		registerBlockWithItem("redstone_timer", REDSTONE_TIMER, settings, DyeColor.RED);
		registerBlockWithItem("redstone_calculator", REDSTONE_CALCULATOR, settings, DyeColor.RED);
		registerBlockWithItem("redstone_transceiver", REDSTONE_TRANSCEIVER, settings, DyeColor.RED);
		
		registerBlockWithItem("redstone_sand", REDSTONE_SAND, settings, DyeColor.RED);
		registerBlockWithItem("ender_glass", ENDER_GLASS, settings, DyeColor.PURPLE);

		registerBlockWithItem("block_placer", BLOCK_PLACER, settings, DyeColor.CYAN);
		registerBlockWithItem("block_detector", BLOCK_DETECTOR, settings, DyeColor.CYAN);
		registerBlockWithItem("block_breaker", BLOCK_BREAKER, settings, DyeColor.CYAN);
	}
	
	private static void registerMagicalBlocks(Item.Properties settings) {
		registerBlockWithItem("heartbound_chest", HEARTBOUND_CHEST, settings, DyeColor.BLUE);
		registerBlockWithItem("compacting_chest", COMPACTING_CHEST, settings, DyeColor.YELLOW);
		registerBlockWithItem("restocking_chest", RESTOCKING_CHEST, settings, DyeColor.YELLOW);
		registerBlockWithItem("black_hole_chest", BLACK_HOLE_CHEST, settings, DyeColor.LIGHT_GRAY);
		
		registerBlockWithItem("ender_hopper", ENDER_HOPPER, settings, DyeColor.PURPLE);
		registerBlockWithItem("ender_dropper", ENDER_DROPPER, settings, DyeColor.PURPLE);
		registerBlockWithItem("particle_spawner", PARTICLE_SPAWNER, settings, DyeColor.PINK);
		registerBlockWithItem("creative_particle_spawner", CREATIVE_PARTICLE_SPAWNER, new BlockItem(CREATIVE_PARTICLE_SPAWNER, IS.of(Rarity.EPIC)), DyeColor.PINK);
		
		registerBlockWithItem("glistering_melon", GLISTERING_MELON, settings, DyeColor.LIME);
		
		registerBlockWithItem("lava_sponge", LAVA_SPONGE, settings, DyeColor.ORANGE);
		registerBlockWithItem("wet_lava_sponge", WET_LAVA_SPONGE, new WetLavaSpongeItem(WET_LAVA_SPONGE, IS.of(1).craftRemainder(LAVA_SPONGE.asItem())), DyeColor.ORANGE);
		
		registerBlockWithItem("ethereal_platform", ETHEREAL_PLATFORM, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("universe_spyhole", UNIVERSE_SPYHOLE, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("present", PRESENT, new PresentItem(PRESENT, IS.of(1)), DyeColor.LIGHT_GRAY);
		registerBlockWithItem("titration_barrel", TITRATION_BARREL, settings, DyeColor.MAGENTA);

		registerBlockWithItem("parametric_mining_device", PARAMETRIC_MINING_DEVICE, new ParametricMiningDeviceItem(PARAMETRIC_MINING_DEVICE, IS.of(8)), DyeColor.RED);
		registerBlockWithItem("threat_conflux", THREAT_CONFLUX, new ThreatConfluxItem(THREAT_CONFLUX, IS.of(8)), DyeColor.RED);
	}
	
	private static void registerPigmentStorageBlocks(Item.Properties settings) {
		registerBlockWithItem("white_block", WHITE_BLOCK, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_block", ORANGE_BLOCK, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_block", MAGENTA_BLOCK, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_block", LIGHT_BLUE_BLOCK, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_block", YELLOW_BLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_block", LIME_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("pink_block", PINK_BLOCK, settings, DyeColor.PINK);
		registerBlockWithItem("gray_block", GRAY_BLOCK, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_block", LIGHT_GRAY_BLOCK, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_block", CYAN_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_block", PURPLE_BLOCK, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_block", BLUE_BLOCK, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_block", BROWN_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("green_block", GREEN_BLOCK, settings, DyeColor.GREEN);
		registerBlockWithItem("red_block", RED_BLOCK, settings, DyeColor.RED);
		registerBlockWithItem("black_block", BLACK_BLOCK, settings, DyeColor.BLACK);
	}
	
	private static void registerSpiritTree(Item.Properties settings) {
		registerBlockWithItem("ominous_sapling", OMINOUS_SAPLING, new OminousSaplingBlockItem(OMINOUS_SAPLING, settings), DyeColor.GREEN);
		
		registerBlockWithItem("spirit_sallow_roots", SPIRIT_SALLOW_ROOTS, settings, DyeColor.GREEN);
		registerBlockWithItem("spirit_sallow_log", SPIRIT_SALLOW_LOG, settings, DyeColor.GREEN);
		registerBlockWithItem("spirit_sallow_leaves", SPIRIT_SALLOW_LEAVES, settings, DyeColor.GREEN);
		registerBlockWithItem("spirit_sallow_heart", SPIRIT_SALLOW_HEART, settings, DyeColor.GREEN);
		
		registerBlock("cyan_spirit_sallow_vines_head", CYAN_SPIRIT_SALLOW_VINES_HEAD);
		registerBlock("magenta_spirit_sallow_vines_head", MAGENTA_SPIRIT_SALLOW_VINES_HEAD);
		registerBlock("yellow_spirit_sallow_vines_head", YELLOW_SPIRIT_SALLOW_VINES_HEAD);
		registerBlock("black_spirit_sallow_vines_head", BLACK_SPIRIT_SALLOW_VINES_HEAD);
		registerBlock("white_spirit_sallow_vines_head", WHITE_SPIRIT_SALLOW_VINES_HEAD);
		
		registerBlock("cyan_spirit_sallow_vines_body", CYAN_SPIRIT_SALLOW_VINES_BODY);
		registerBlock("magenta_spirit_sallow_vines_body", MAGENTA_SPIRIT_SALLOW_VINES_BODY);
		registerBlock("yellow_spirit_sallow_vines_body", YELLOW_SPIRIT_SALLOW_VINES_BODY);
		registerBlock("black_spirit_sallow_vines_body", BLACK_SPIRIT_SALLOW_VINES_BODY);
		registerBlock("white_spirit_sallow_vines_body", WHITE_SPIRIT_SALLOW_VINES_BODY);
		
		registerBlockWithItem("sacred_soil", SACRED_SOIL, settings, DyeColor.LIME);
	}
	
	private static void registerOreBlocks(Item.Properties settings, Item.Properties settingsFireproof) {
		registerBlockWithItem("shimmerstone_ore", SHIMMERSTONE_ORE, settings, DyeColor.YELLOW);
		registerBlockWithItem("deepslate_shimmerstone_ore", DEEPSLATE_SHIMMERSTONE_ORE, settings, DyeColor.YELLOW);
		registerBlockWithItem("blackslag_shimmerstone_ore", BLACKSLAG_SHIMMERSTONE_ORE, settings, DyeColor.YELLOW);
		
		registerBlockWithItem("azurite_ore", AZURITE_ORE, settings, DyeColor.BLUE);
		registerBlockWithItem("deepslate_azurite_ore", DEEPSLATE_AZURITE_ORE, settings, DyeColor.BLUE);
		registerBlockWithItem("blackslag_azurite_ore", BLACKSLAG_AZURITE_ORE, settings, DyeColor.BLUE);
		
		registerBlockWithItem("stratine_ore", STRATINE_ORE, new FloatBlockItem(STRATINE_ORE, settingsFireproof, -0.01F), DyeColor.RED);
		registerBlockWithItem("paltaeria_ore", PALTAERIA_ORE, new FloatBlockItem(PALTAERIA_ORE, settings, 0.01F), DyeColor.CYAN);
		
		registerBlockWithItem("small_bismuth_bud", SMALL_BISMUTH_BUD, IS.of(Rarity.UNCOMMON), DyeColor.CYAN);
		registerBlockWithItem("large_bismuth_bud", LARGE_BISMUTH_BUD, IS.of(Rarity.UNCOMMON), DyeColor.CYAN);
		registerBlockWithItem("bismuth_cluster", BISMUTH_CLUSTER, IS.of(Rarity.UNCOMMON), DyeColor.CYAN);
		registerBlockWithItem("bismuth_block", BISMUTH_BLOCK, IS.of(Rarity.UNCOMMON), DyeColor.CYAN);
		
		registerBlockWithItem("malachite_ore", MALACHITE_ORE, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		registerBlockWithItem("deepslate_malachite_ore", DEEPSLATE_MALACHITE_ORE, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		registerBlockWithItem("blackslag_malachite_ore", BLACKSLAG_MALACHITE_ORE, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		registerBlockWithItem("small_malachite_bud", SMALL_MALACHITE_BUD, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		registerBlockWithItem("large_malachite_bud", LARGE_MALACHITE_BUD, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		registerBlockWithItem("malachite_cluster", MALACHITE_CLUSTER, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		registerBlockWithItem("malachite_block", MALACHITE_BLOCK, IS.of(Rarity.UNCOMMON), DyeColor.GREEN);
		
		
		registerBlockWithItem("blackslag_coal_ore", BLACKSLAG_COAL_ORE, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_copper_ore", BLACKSLAG_COPPER_ORE, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_iron_ore", BLACKSLAG_IRON_ORE, settings, DyeColor.BROWN);
		registerBlockWithItem("blackslag_gold_ore", BLACKSLAG_GOLD_ORE, settings, DyeColor.YELLOW);
		registerBlockWithItem("blackslag_diamond_ore", BLACKSLAG_DIAMOND_ORE, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("blackslag_redstone_ore", BLACKSLAG_REDSTONE_ORE, settings, DyeColor.RED);
		registerBlockWithItem("blackslag_lapis_ore", BLACKSLAG_LAPIS_ORE, settings, DyeColor.BLUE);
		registerBlockWithItem("blackslag_emerald_ore", BLACKSLAG_EMERALD_ORE, settings, DyeColor.LIME);
		
		registerBlockWithItem("azurite_cluster", AZURITE_CLUSTER, IS.of(Rarity.UNCOMMON), DyeColor.BLUE);
		registerBlockWithItem("large_azurite_bud", LARGE_AZURITE_BUD, IS.of(Rarity.UNCOMMON), DyeColor.BLUE);
		registerBlockWithItem("small_azurite_bud", SMALL_AZURITE_BUD, IS.of(Rarity.UNCOMMON), DyeColor.BLUE);
		
		registerBlockWithItem("small_bloodstone_bud", SMALL_BLOODSTONE_BUD, settings.rarity(Rarity.UNCOMMON), DyeColor.RED);
		registerBlockWithItem("large_bloodstone_bud", LARGE_BLOODSTONE_BUD, settings.rarity(Rarity.UNCOMMON), DyeColor.RED);
		registerBlockWithItem("bloodstone_cluster", BLOODSTONE_CLUSTER, settings.rarity(Rarity.UNCOMMON), DyeColor.RED);
		registerBlockWithItem("bloodstone_block", BLOODSTONE_BLOCK, settings.rarity(Rarity.UNCOMMON), DyeColor.RED);
		registerBlockWithItem("effulgent_block", EFFULGENT_BLOCK, settings.rarity(Rarity.UNCOMMON), DyeColor.YELLOW);
		registerBlockWithItem("effulgent_cushion", EFFULGENT_CUSHION, settings.rarity(Rarity.UNCOMMON), DyeColor.YELLOW);
		registerBlockWithItem("effulgent_carpet", EFFULGENT_CARPET, settings.rarity(Rarity.UNCOMMON), DyeColor.YELLOW);
	}
	
	private static void registerOreStorageBlocks(Item.Properties settings, Item.Properties settingsFireproof) {
		registerBlockWithItem("topaz_storage_block", TOPAZ_STORAGE_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_storage_block", AMETHYST_STORAGE_BLOCK, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_storage_block", CITRINE_STORAGE_BLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_storage_block", ONYX_STORAGE_BLOCK, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_storage_block", MOONSTONE_STORAGE_BLOCK, settings, DyeColor.WHITE);
		//registerBlockWithItem("spectral_shard_storage_block", SPECTRAL_SHARD_STORAGE_BLOCK, IS.of(Rarity.RARE), DyeColor.WHITE);

		registerBlockWithItem("topaz_powder_block", TOPAZ_POWDER_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_powder_block", AMETHYST_POWDER_BLOCK, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_powder_block", CITRINE_POWDER_BLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_powder_block", ONYX_POWDER_BLOCK, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_powder_block", MOONSTONE_POWDER_BLOCK, settings, DyeColor.WHITE);

		registerBlockWithItem("vegetal_block", VEGETAL_BLOCK, IS.of(), DyeColor.GREEN);
		registerBlockWithItem("neolith_block", NEOLITH_BLOCK, IS.of(), DyeColor.BROWN);
		registerBlockWithItem("bedrock_storage_block", BEDROCK_STORAGE_BLOCK, IS.of(Rarity.UNCOMMON), DyeColor.BLACK);
		//registerBlockWithItem("spectral_shard_block", SPECTRAL_SHARD_BLOCK, IS.of(Rarity.RARE), DyeColor.WHITE);
		
		registerBlockWithItem("azurite_block", AZURITE_BLOCK, IS.of(), DyeColor.BLUE);
		registerBlockWithItem("shimmerstone_block", SHIMMERSTONE_BLOCK, IS.of(), DyeColor.YELLOW);
		registerBlockWithItem("stratine_fragment_block", STRATINE_FRAGMENT_BLOCK, new FloatBlockItem(STRATINE_FRAGMENT_BLOCK, settingsFireproof, -0.02F), DyeColor.RED);
		registerBlockWithItem("paltaeria_fragment_block", PALTAERIA_FRAGMENT_BLOCK, new FloatBlockItem(PALTAERIA_FRAGMENT_BLOCK, settings, 0.02F), DyeColor.CYAN);
		registerBlockWithItem("hover_block", HOVER_BLOCK, new FloatBlockItem(HOVER_BLOCK, settings, 0F) {
			@Override
			public double applyGravity(ItemStack stack, Level world, Entity entity) {
				return 0;
			}
			
			@Override
			public void applyGravity(ItemStack stack, Level world, ItemEntity itemEntity) {
				itemEntity.setNoGravity(true);
			}
		}, DyeColor.GREEN);
	}
	
	private static void registerColoredLamps(Item.Properties settings) {
		registerBlockWithItem("white_lamp", WHITE_LAMP, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_lamp", ORANGE_LAMP, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_lamp", MAGENTA_LAMP, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_lamp", LIGHT_BLUE_LAMP, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_lamp", YELLOW_LAMP, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_lamp", LIME_LAMP, settings, DyeColor.LIME);
		registerBlockWithItem("pink_lamp", PINK_LAMP, settings, DyeColor.PINK);
		registerBlockWithItem("gray_lamp", GRAY_LAMP, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_lamp", LIGHT_GRAY_LAMP, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_lamp", CYAN_LAMP, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_lamp", PURPLE_LAMP, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_lamp", BLUE_LAMP, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_lamp", BROWN_LAMP, settings, DyeColor.BROWN);
		registerBlockWithItem("green_lamp", GREEN_LAMP, settings, DyeColor.GREEN);
		registerBlockWithItem("red_lamp", RED_LAMP, settings, DyeColor.RED);
		registerBlockWithItem("black_lamp", BLACK_LAMP, settings, DyeColor.BLACK);
	}
	
	private static void registerGemstoneGlass(Item.Properties settings) {
		registerBlockWithItem("topaz_glass", TOPAZ_GLASS, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_glass", AMETHYST_GLASS, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_glass", CITRINE_GLASS, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_glass", ONYX_GLASS, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_glass", MOONSTONE_GLASS, settings, DyeColor.WHITE);
		
		registerBlockWithItem("radiant_glass", RADIANT_GLASS, settings, DyeColor.WHITE);
	}
	
	private static void registerPlayerOnlyGlass(Item.Properties settings) {
		registerBlockWithItem("semi_permeable_glass", SEMI_PERMEABLE_GLASS, settings, DyeColor.WHITE);
		registerBlockWithItem("tinted_semi_permeable_glass", TINTED_SEMI_PERMEABLE_GLASS, settings, DyeColor.BLACK);
		registerBlockWithItem("radiant_semi_permeable_glass", RADIANT_SEMI_PERMEABLE_GLASS, settings, DyeColor.YELLOW);
		
		registerBlockWithItem("topaz_semi_permeable_glass", TOPAZ_SEMI_PERMEABLE_GLASS, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_semi_permeable_glass", AMETHYST_SEMI_PERMEABLE_GLASS, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_semi_permeable_glass", CITRINE_SEMI_PERMEABLE_GLASS, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_semi_permeable_glass", ONYX_SEMI_PERMEABLE_GLASS, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_semi_permeable_glass", MOONSTONE_SEMI_PERMEABLE_GLASS, settings, DyeColor.WHITE);
	}
	
	private static void registerGemstoneChimes(Item.Properties settings) {
		registerBlockWithItem("topaz_chime", TOPAZ_CHIME, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_chime", AMETHYST_CHIME, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_chime", CITRINE_CHIME, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_chime", ONYX_CHIME, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_chime", MOONSTONE_CHIME, settings, DyeColor.WHITE);
	}
	
	private static void registerDecoStones(Item.Properties settings) {
		registerBlockWithItem("amethyst_decostone", AMETHYST_DECOSTONE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("topaz_decostone", TOPAZ_DECOSTONE, settings, DyeColor.CYAN);
		registerBlockWithItem("citrine_decostone", CITRINE_DECOSTONE, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_decostone", ONYX_DECOSTONE, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_decostone", MOONSTONE_DECOSTONE, settings, DyeColor.WHITE);
	}
	
	private static void registerStoneBlocks(Item.Properties settings) {
		registerBlockWithItem("smooth_basalt_slab", SMOOTH_BASALT_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("smooth_basalt_wall", SMOOTH_BASALT_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("smooth_basalt_stairs", SMOOTH_BASALT_STAIRS, settings, DyeColor.BROWN);

		registerBlockWithItem("polished_basalt", POLISHED_BASALT, settings, DyeColor.BROWN);
		registerBlockWithItem("planed_basalt", PLANED_BASALT, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_basalt_pillar", POLISHED_BASALT_PILLAR, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_basalt_crest", POLISHED_BASALT_CREST, settings, DyeColor.BROWN);
		registerBlockWithItem("chiseled_polished_basalt", CHISELED_POLISHED_BASALT, settings, DyeColor.BROWN);
		registerBlockWithItem("notched_polished_basalt", NOTCHED_POLISHED_BASALT, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_basalt_slab", POLISHED_BASALT_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_basalt_wall", POLISHED_BASALT_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_basalt_stairs", POLISHED_BASALT_STAIRS, settings, DyeColor.BROWN);
		
		registerBlockWithItem("basalt_bricks", BASALT_BRICKS, settings, DyeColor.BROWN);
		registerBlockWithItem("basalt_brick_slab", BASALT_BRICK_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("basalt_brick_wall", BASALT_BRICK_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("basalt_brick_stairs", BASALT_BRICK_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("cracked_basalt_bricks", CRACKED_BASALT_BRICKS, settings, DyeColor.BROWN);
		
		registerBlockWithItem("basalt_tiles", BASALT_TILES, settings, DyeColor.BROWN);
		registerBlockWithItem("basalt_tile_stairs", BASALT_TILE_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("basalt_tile_slab", BASALT_TILE_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("basalt_tile_wall", BASALT_TILE_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("cracked_basalt_tiles", CRACKED_BASALT_TILES, settings, DyeColor.BROWN);
		
		registerBlockWithItem("polished_basalt_button", POLISHED_BASALT_BUTTON, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_basalt_pressure_plate", POLISHED_BASALT_PRESSURE_PLATE, settings, DyeColor.BROWN);
		
		registerBlockWithItem("calcite_slab", CALCITE_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_wall", CALCITE_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_stairs", CALCITE_STAIRS, settings, DyeColor.BROWN);

		registerBlockWithItem("polished_calcite", POLISHED_CALCITE, settings, DyeColor.BROWN);
		registerBlockWithItem("planed_calcite", PLANED_CALCITE, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_calcite_pillar", POLISHED_CALCITE_PILLAR, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_calcite_crest", POLISHED_CALCITE_CREST, settings, DyeColor.BROWN);
		registerBlockWithItem("chiseled_polished_calcite", CHISELED_POLISHED_CALCITE, settings, DyeColor.BROWN);
		registerBlockWithItem("notched_polished_calcite", NOTCHED_POLISHED_CALCITE, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_calcite_slab", POLISHED_CALCITE_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_calcite_wall", POLISHED_CALCITE_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_calcite_stairs", POLISHED_CALCITE_STAIRS, settings, DyeColor.BROWN);
		
		registerBlockWithItem("calcite_bricks", CALCITE_BRICKS, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_brick_slab", CALCITE_BRICK_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_brick_wall", CALCITE_BRICK_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_brick_stairs", CALCITE_BRICK_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("cracked_calcite_bricks", CRACKED_CALCITE_BRICKS, settings, DyeColor.BROWN);
		
		registerBlockWithItem("calcite_tiles", CALCITE_TILES, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_tile_stairs", CALCITE_TILE_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_tile_slab", CALCITE_TILE_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("calcite_tile_wall", CALCITE_TILE_WALL, settings, DyeColor.BROWN);
		registerBlockWithItem("cracked_calcite_tiles", CRACKED_CALCITE_TILES, settings, DyeColor.BROWN);
		
		registerBlockWithItem("polished_calcite_button", POLISHED_CALCITE_BUTTON, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_calcite_pressure_plate", POLISHED_CALCITE_PRESSURE_PLATE, settings, DyeColor.BROWN);
		
		registerBlockWithItem("blackslag", BLACKSLAG, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_stairs", BLACKSLAG_STAIRS, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_slab", BLACKSLAG_SLAB, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_wall", BLACKSLAG_WALL, settings, DyeColor.BLACK);
		registerBlockWithItem("infested_blackslag", INFESTED_BLACKSLAG, settings, DyeColor.BLACK);
		registerBlockWithItem("cobbled_blackslag", COBBLED_BLACKSLAG, settings, DyeColor.BLACK);
		registerBlockWithItem("cobbled_blackslag_stairs", COBBLED_BLACKSLAG_STAIRS, settings, DyeColor.BLACK);
		registerBlockWithItem("cobbled_blackslag_slab", COBBLED_BLACKSLAG_SLAB, settings, DyeColor.BLACK);
		registerBlockWithItem("cobbled_blackslag_wall", COBBLED_BLACKSLAG_WALL, settings, DyeColor.BLACK);
		registerBlockWithItem("polished_blackslag", POLISHED_BLACKSLAG, settings, DyeColor.BLACK);
		registerBlockWithItem("polished_blackslag_stairs", POLISHED_BLACKSLAG_STAIRS, settings, DyeColor.BLACK);
		registerBlockWithItem("polished_blackslag_slab", POLISHED_BLACKSLAG_SLAB, settings, DyeColor.BLACK);
		registerBlockWithItem("polished_blackslag_wall", POLISHED_BLACKSLAG_WALL, settings, DyeColor.BLACK);
		
		registerBlockWithItem("blackslag_tiles", BLACKSLAG_TILES, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_tile_stairs", BLACKSLAG_TILE_STAIRS, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_tile_slab", BLACKSLAG_TILE_SLAB, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_tile_wall", BLACKSLAG_TILE_WALL, settings, DyeColor.BLACK);
		registerBlockWithItem("cracked_blackslag_tiles", CRACKED_BLACKSLAG_TILES, settings, DyeColor.BLACK);
		
		registerBlockWithItem("blackslag_bricks", BLACKSLAG_BRICKS, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_brick_stairs", BLACKSLAG_BRICK_STAIRS, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_brick_slab", BLACKSLAG_BRICK_SLAB, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_brick_wall", BLACKSLAG_BRICK_WALL, settings, DyeColor.BLACK);
		registerBlockWithItem("cracked_blackslag_bricks", CRACKED_BLACKSLAG_BRICKS, settings, DyeColor.BLACK);
		
		registerBlockWithItem("polished_blackslag_pillar", POLISHED_BLACKSLAG_PILLAR, settings, DyeColor.BLACK);
		registerBlockWithItem("chiseled_polished_blackslag", CHISELED_POLISHED_BLACKSLAG, settings, DyeColor.BLACK);
		registerBlockWithItem("ancient_chiseled_polished_blackslag", ANCIENT_CHISELED_POLISHED_BLACKSLAG, settings, DyeColor.BLACK);
		registerBlockWithItem("polished_blackslag_button", POLISHED_BLACKSLAG_BUTTON, settings, DyeColor.BLACK);
		registerBlockWithItem("polished_blackslag_pressure_plate", POLISHED_BLACKSLAG_PRESSURE_PLATE, settings, DyeColor.BLACK);


		registerBlockWithItem("shale_clay", SHALE_CLAY, settings, DyeColor.BROWN);
		registerBlockWithItem("tilled_shale_clay", TILLED_SHALE_CLAY, settings, DyeColor.BROWN);

		registerBlockWithItem("polished_shale_clay", POLISHED_SHALE_CLAY, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_polished_shale_clay", EXPOSED_POLISHED_SHALE_CLAY, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_polished_shale_clay", WEATHERED_POLISHED_SHALE_CLAY, settings, DyeColor.BROWN);
		
		registerBlockWithItem("polished_shale_clay_stairs", POLISHED_SHALE_CLAY_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("polished_shale_clay_slab", POLISHED_SHALE_CLAY_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_polished_shale_clay_stairs", EXPOSED_POLISHED_SHALE_CLAY_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_polished_shale_clay_slab", EXPOSED_POLISHED_SHALE_CLAY_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_polished_shale_clay_stairs", WEATHERED_POLISHED_SHALE_CLAY_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_polished_shale_clay_slab", WEATHERED_POLISHED_SHALE_CLAY_SLAB, settings, DyeColor.BROWN);
		
		registerBlockWithItem("shale_clay_bricks", SHALE_CLAY_BRICKS, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_shale_clay_bricks", EXPOSED_SHALE_CLAY_BRICKS, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_shale_clay_bricks", WEATHERED_SHALE_CLAY_BRICKS, settings, DyeColor.BROWN);
		
		registerBlockWithItem("shale_clay_brick_stairs", SHALE_CLAY_BRICK_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("shale_clay_brick_slab", SHALE_CLAY_BRICK_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_shale_clay_brick_stairs", EXPOSED_SHALE_CLAY_BRICK_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_shale_clay_brick_slab", EXPOSED_SHALE_CLAY_BRICK_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_shale_clay_brick_stairs", WEATHERED_SHALE_CLAY_BRICK_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_shale_clay_brick_slab", WEATHERED_SHALE_CLAY_BRICK_SLAB, settings, DyeColor.BROWN);
		
		registerBlockWithItem("shale_clay_tiles", SHALE_CLAY_TILES, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_shale_clay_tiles", EXPOSED_SHALE_CLAY_TILES, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_shale_clay_tiles", WEATHERED_SHALE_CLAY_TILES, settings, DyeColor.BROWN);
		
		registerBlockWithItem("shale_clay_tile_stairs", SHALE_CLAY_TILE_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("shale_clay_tile_slab", SHALE_CLAY_TILE_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_shale_clay_tile_stairs", EXPOSED_SHALE_CLAY_TILE_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("exposed_shale_clay_tile_slab", EXPOSED_SHALE_CLAY_TILE_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_shale_clay_tile_stairs", WEATHERED_SHALE_CLAY_TILE_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("weathered_shale_clay_tile_slab", WEATHERED_SHALE_CLAY_TILE_SLAB, settings, DyeColor.BROWN);
		
		registerBlockWithItem("polished_bone_ash", POLISHED_BONE_ASH, settings, DyeColor.CYAN);
		registerBlockWithItem("polished_bone_ash_slab", POLISHED_BONE_ASH_SLAB, settings, DyeColor.CYAN);
		registerBlockWithItem("polished_bone_ash_stairs", POLISHED_BONE_ASH_STAIRS, settings, DyeColor.CYAN);
		registerBlockWithItem("polished_bone_ash_wall", POLISHED_BONE_ASH_WALL, settings, DyeColor.CYAN);
		
		registerBlockWithItem("bone_ash_bricks", BONE_ASH_BRICKS, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_brick_slab", BONE_ASH_BRICK_SLAB, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_brick_stairs", BONE_ASH_BRICK_STAIRS, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_brick_wall", BONE_ASH_BRICK_WALL, settings, DyeColor.CYAN);
		
		registerBlockWithItem("bone_ash_tiles", BONE_ASH_TILES, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_tile_slab", BONE_ASH_TILE_SLAB, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_tile_stairs", BONE_ASH_TILE_STAIRS, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_tile_wall", BONE_ASH_TILE_WALL, settings, DyeColor.CYAN);
		
		registerBlockWithItem("polished_bone_ash_pillar", POLISHED_BONE_ASH_PILLAR, settings, DyeColor.CYAN);
		registerBlockWithItem("bone_ash_shingles", BONE_ASH_SHINGLES, settings, DyeColor.CYAN);
		
		registerBlockWithItem("slush", SLUSH, settings, DyeColor.BROWN);
		registerBlockWithItem("tilled_slush", TILLED_SLUSH, settings, DyeColor.BROWN);
		registerBlockWithItem("black_sludge", BLACK_SLUDGE, IS.of(), DyeColor.GRAY);
		
		registerBlockWithItem("pyrite", PYRITE, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_pile", PYRITE_PILE, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_tile", PYRITE_TILE, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_plating", PYRITE_PLATING, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_tubing", PYRITE_TUBING, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_relief", PYRITE_RELIEF, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_stack", PYRITE_STACK, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_panneling", PYRITE_PANNELING, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_vent", PYRITE_VENT, settings, DyeColor.PURPLE);
		registerBlockWithItem("pyrite_ripper", PYRITE_RIPPER, settings, DyeColor.RED);
		
		registerBlockWithItem("dragonbone", DRAGONBONE, settings, DyeColor.GREEN);
		registerBlockWithItem("cracked_dragonbone", CRACKED_DRAGONBONE, settings, DyeColor.GREEN);
	}
	
	private static void registerRunes(Item.Properties settings) {
		registerBlockWithItem("topaz_chiseled_basalt", TOPAZ_CHISELED_BASALT, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_chiseled_basalt", AMETHYST_CHISELED_BASALT, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_chiseled_basalt", CITRINE_CHISELED_BASALT, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_chiseled_basalt", ONYX_CHISELED_BASALT, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_chiseled_basalt", MOONSTONE_CHISELED_BASALT, settings, DyeColor.WHITE);
		
		registerBlockWithItem("topaz_chiseled_calcite", TOPAZ_CHISELED_CALCITE, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_chiseled_calcite", AMETHYST_CHISELED_CALCITE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_chiseled_calcite", CITRINE_CHISELED_CALCITE, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_chiseled_calcite", ONYX_CHISELED_CALCITE, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_chiseled_calcite", MOONSTONE_CHISELED_CALCITE, settings, DyeColor.WHITE);
	}
	
	private static void registerGemstoneLamps(Item.Properties settings) {
		registerBlockWithItem("topaz_calcite_lamp", TOPAZ_CALCITE_LAMP, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_calcite_lamp", AMETHYST_CALCITE_LAMP, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_calcite_lamp", CITRINE_CALCITE_LAMP, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_calcite_lamp", ONYX_CALCITE_LAMP, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_calcite_lamp", MOONSTONE_CALCITE_LAMP, settings, DyeColor.WHITE);
		
		registerBlockWithItem("topaz_basalt_lamp", TOPAZ_BASALT_LAMP, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_basalt_lamp", AMETHYST_BASALT_LAMP, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_basalt_lamp", CITRINE_BASALT_LAMP, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_basalt_lamp", ONYX_BASALT_LAMP, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_basalt_lamp", MOONSTONE_BASALT_LAMP, settings, DyeColor.WHITE);
	}
	
	private static void registerColoredWood(Item.Properties settings) {
		registerBlockWithItem("white_log", WHITE_LOG, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_log", ORANGE_LOG, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_log", MAGENTA_LOG, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_log", LIGHT_BLUE_LOG, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_log", YELLOW_LOG, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_log", LIME_LOG, settings, DyeColor.LIME);
		registerBlockWithItem("pink_log", PINK_LOG, settings, DyeColor.PINK);
		registerBlockWithItem("gray_log", GRAY_LOG, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_log", LIGHT_GRAY_LOG, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_log", CYAN_LOG, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_log", PURPLE_LOG, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_log", BLUE_LOG, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_log", BROWN_LOG, settings, DyeColor.BROWN);
		registerBlockWithItem("green_log", GREEN_LOG, settings, DyeColor.GREEN);
		registerBlockWithItem("red_log", RED_LOG, settings, DyeColor.RED);
		registerBlockWithItem("black_log", BLACK_LOG, settings, DyeColor.BLACK);
		
		registerBlockWithItem("stripped_white_log", STRIPPED_WHITE_LOG, settings, DyeColor.WHITE);
		registerBlockWithItem("stripped_orange_log", STRIPPED_ORANGE_LOG, settings, DyeColor.ORANGE);
		registerBlockWithItem("stripped_magenta_log", STRIPPED_MAGENTA_LOG, settings, DyeColor.MAGENTA);
		registerBlockWithItem("stripped_light_blue_log", STRIPPED_LIGHT_BLUE_LOG, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("stripped_yellow_log", STRIPPED_YELLOW_LOG, settings, DyeColor.YELLOW);
		registerBlockWithItem("stripped_lime_log", STRIPPED_LIME_LOG, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_pink_log", STRIPPED_PINK_LOG, settings, DyeColor.PINK);
		registerBlockWithItem("stripped_gray_log", STRIPPED_GRAY_LOG, settings, DyeColor.GRAY);
		registerBlockWithItem("stripped_light_gray_log", STRIPPED_LIGHT_GRAY_LOG, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("stripped_cyan_log", STRIPPED_CYAN_LOG, settings, DyeColor.CYAN);
		registerBlockWithItem("stripped_purple_log", STRIPPED_PURPLE_LOG, settings, DyeColor.PURPLE);
		registerBlockWithItem("stripped_blue_log", STRIPPED_BLUE_LOG, settings, DyeColor.BLUE);
		registerBlockWithItem("stripped_brown_log", STRIPPED_BROWN_LOG, settings, DyeColor.BROWN);
		registerBlockWithItem("stripped_green_log", STRIPPED_GREEN_LOG, settings, DyeColor.GREEN);
		registerBlockWithItem("stripped_red_log", STRIPPED_RED_LOG, settings, DyeColor.RED);
		registerBlockWithItem("stripped_black_log", STRIPPED_BLACK_LOG, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_wood", WHITE_WOOD, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_wood", ORANGE_WOOD, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_wood", MAGENTA_WOOD, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_wood", LIGHT_BLUE_WOOD, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_wood", YELLOW_WOOD, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_wood", LIME_WOOD, settings, DyeColor.LIME);
		registerBlockWithItem("pink_wood", PINK_WOOD, settings, DyeColor.PINK);
		registerBlockWithItem("gray_wood", GRAY_WOOD, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_wood", LIGHT_GRAY_WOOD, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_wood", CYAN_WOOD, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_wood", PURPLE_WOOD, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_wood", BLUE_WOOD, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_wood", BROWN_WOOD, settings, DyeColor.BROWN);
		registerBlockWithItem("green_wood", GREEN_WOOD, settings, DyeColor.GREEN);
		registerBlockWithItem("red_wood", RED_WOOD, settings, DyeColor.RED);
		registerBlockWithItem("black_wood", BLACK_WOOD, settings, DyeColor.BLACK);
		
		registerBlockWithItem("stripped_white_wood", STRIPPED_WHITE_WOOD, settings, DyeColor.WHITE);
		registerBlockWithItem("stripped_orange_wood", STRIPPED_ORANGE_WOOD, settings, DyeColor.ORANGE);
		registerBlockWithItem("stripped_magenta_wood", STRIPPED_MAGENTA_WOOD, settings, DyeColor.MAGENTA);
		registerBlockWithItem("stripped_light_blue_wood", STRIPPED_LIGHT_BLUE_WOOD, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("stripped_yellow_wood", STRIPPED_YELLOW_WOOD, settings, DyeColor.YELLOW);
		registerBlockWithItem("stripped_lime_wood", STRIPPED_LIME_WOOD, settings, DyeColor.LIME);
		registerBlockWithItem("stripped_pink_wood", STRIPPED_PINK_WOOD, settings, DyeColor.PINK);
		registerBlockWithItem("stripped_gray_wood", STRIPPED_GRAY_WOOD, settings, DyeColor.GRAY);
		registerBlockWithItem("stripped_light_gray_wood", STRIPPED_LIGHT_GRAY_WOOD, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("stripped_cyan_wood", STRIPPED_CYAN_WOOD, settings, DyeColor.CYAN);
		registerBlockWithItem("stripped_purple_wood", STRIPPED_PURPLE_WOOD, settings, DyeColor.PURPLE);
		registerBlockWithItem("stripped_blue_wood", STRIPPED_BLUE_WOOD, settings, DyeColor.BLUE);
		registerBlockWithItem("stripped_brown_wood", STRIPPED_BROWN_WOOD, settings, DyeColor.BROWN);
		registerBlockWithItem("stripped_green_wood", STRIPPED_GREEN_WOOD, settings, DyeColor.GREEN);
		registerBlockWithItem("stripped_red_wood", STRIPPED_RED_WOOD, settings, DyeColor.RED);
		registerBlockWithItem("stripped_black_wood", STRIPPED_BLACK_WOOD, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_leaves", WHITE_LEAVES, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_leaves", ORANGE_LEAVES, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_leaves", MAGENTA_LEAVES, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_leaves", LIGHT_BLUE_LEAVES, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_leaves", YELLOW_LEAVES, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_leaves", LIME_LEAVES, settings, DyeColor.LIME);
		registerBlockWithItem("pink_leaves", PINK_LEAVES, settings, DyeColor.PINK);
		registerBlockWithItem("gray_leaves", GRAY_LEAVES, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_leaves", LIGHT_GRAY_LEAVES, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_leaves", CYAN_LEAVES, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_leaves", PURPLE_LEAVES, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_leaves", BLUE_LEAVES, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_leaves", BROWN_LEAVES, settings, DyeColor.BROWN);
		registerBlockWithItem("green_leaves", GREEN_LEAVES, settings, DyeColor.GREEN);
		registerBlockWithItem("red_leaves", RED_LEAVES, settings, DyeColor.RED);
		registerBlockWithItem("black_leaves", BLACK_LEAVES, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_sapling", WHITE_SAPLING, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_sapling", ORANGE_SAPLING, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_sapling", MAGENTA_SAPLING, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_sapling", LIGHT_BLUE_SAPLING, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_sapling", YELLOW_SAPLING, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_sapling", LIME_SAPLING, settings, DyeColor.LIME);
		registerBlockWithItem("pink_sapling", PINK_SAPLING, settings, DyeColor.PINK);
		registerBlockWithItem("gray_sapling", GRAY_SAPLING, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_sapling", LIGHT_GRAY_SAPLING, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_sapling", CYAN_SAPLING, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_sapling", PURPLE_SAPLING, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_sapling", BLUE_SAPLING, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_sapling", BROWN_SAPLING, settings, DyeColor.BROWN);
		registerBlockWithItem("green_sapling", GREEN_SAPLING, settings, DyeColor.GREEN);
		registerBlockWithItem("red_sapling", RED_SAPLING, settings, DyeColor.RED);
		registerBlockWithItem("black_sapling", BLACK_SAPLING, settings, DyeColor.BLACK);
		
		registerBlock("potted_white_sapling", POTTED_WHITE_SAPLING);
		registerBlock("potted_orange_sapling", POTTED_ORANGE_SAPLING);
		registerBlock("potted_magenta_sapling", POTTED_MAGENTA_SAPLING);
		registerBlock("potted_light_blue_sapling", POTTED_LIGHT_BLUE_SAPLING);
		registerBlock("potted_yellow_sapling", POTTED_YELLOW_SAPLING);
		registerBlock("potted_lime_sapling", POTTED_LIME_SAPLING);
		registerBlock("potted_pink_sapling", POTTED_PINK_SAPLING);
		registerBlock("potted_gray_sapling", POTTED_GRAY_SAPLING);
		registerBlock("potted_light_gray_sapling", POTTED_LIGHT_GRAY_SAPLING);
		registerBlock("potted_cyan_sapling", POTTED_CYAN_SAPLING);
		registerBlock("potted_purple_sapling", POTTED_PURPLE_SAPLING);
		registerBlock("potted_blue_sapling", POTTED_BLUE_SAPLING);
		registerBlock("potted_brown_sapling", POTTED_BROWN_SAPLING);
		registerBlock("potted_green_sapling", POTTED_GREEN_SAPLING);
		registerBlock("potted_red_sapling", POTTED_RED_SAPLING);
		registerBlock("potted_black_sapling", POTTED_BLACK_SAPLING);
		
		registerBlockWithItem("white_planks", WHITE_PLANKS, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_planks", ORANGE_PLANKS, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_planks", MAGENTA_PLANKS, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_planks", LIGHT_BLUE_PLANKS, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_planks", YELLOW_PLANKS, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_planks", LIME_PLANKS, settings, DyeColor.LIME);
		registerBlockWithItem("pink_planks", PINK_PLANKS, settings, DyeColor.PINK);
		registerBlockWithItem("gray_planks", GRAY_PLANKS, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_planks", LIGHT_GRAY_PLANKS, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_planks", CYAN_PLANKS, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_planks", PURPLE_PLANKS, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_planks", BLUE_PLANKS, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_planks", BROWN_PLANKS, settings, DyeColor.BROWN);
		registerBlockWithItem("green_planks", GREEN_PLANKS, settings, DyeColor.GREEN);
		registerBlockWithItem("red_planks", RED_PLANKS, settings, DyeColor.RED);
		registerBlockWithItem("black_planks", BLACK_PLANKS, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_stairs", WHITE_STAIRS, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_stairs", ORANGE_STAIRS, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_stairs", MAGENTA_STAIRS, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_stairs", LIGHT_BLUE_STAIRS, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_stairs", YELLOW_STAIRS, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_stairs", LIME_STAIRS, settings, DyeColor.LIME);
		registerBlockWithItem("pink_stairs", PINK_STAIRS, settings, DyeColor.PINK);
		registerBlockWithItem("gray_stairs", GRAY_STAIRS, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_stairs", LIGHT_GRAY_STAIRS, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_stairs", CYAN_STAIRS, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_stairs", PURPLE_STAIRS, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_stairs", BLUE_STAIRS, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_stairs", BROWN_STAIRS, settings, DyeColor.BROWN);
		registerBlockWithItem("green_stairs", GREEN_STAIRS, settings, DyeColor.GREEN);
		registerBlockWithItem("red_stairs", RED_STAIRS, settings, DyeColor.RED);
		registerBlockWithItem("black_stairs", BLACK_STAIRS, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_pressure_plate", WHITE_PRESSURE_PLATE, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_pressure_plate", ORANGE_PRESSURE_PLATE, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_pressure_plate", MAGENTA_PRESSURE_PLATE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_pressure_plate", LIGHT_BLUE_PRESSURE_PLATE, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_pressure_plate", YELLOW_PRESSURE_PLATE, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_pressure_plate", LIME_PRESSURE_PLATE, settings, DyeColor.LIME);
		registerBlockWithItem("pink_pressure_plate", PINK_PRESSURE_PLATE, settings, DyeColor.PINK);
		registerBlockWithItem("gray_pressure_plate", GRAY_PRESSURE_PLATE, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_pressure_plate", LIGHT_GRAY_PRESSURE_PLATE, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_pressure_plate", CYAN_PRESSURE_PLATE, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_pressure_plate", PURPLE_PRESSURE_PLATE, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_pressure_plate", BLUE_PRESSURE_PLATE, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_pressure_plate", BROWN_PRESSURE_PLATE, settings, DyeColor.BROWN);
		registerBlockWithItem("green_pressure_plate", GREEN_PRESSURE_PLATE, settings, DyeColor.GREEN);
		registerBlockWithItem("red_pressure_plate", RED_PRESSURE_PLATE, settings, DyeColor.RED);
		registerBlockWithItem("black_pressure_plate", BLACK_PRESSURE_PLATE, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_fence", WHITE_FENCE, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_fence", ORANGE_FENCE, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_fence", MAGENTA_FENCE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_fence", LIGHT_BLUE_FENCE, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_fence", YELLOW_FENCE, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_fence", LIME_FENCE, settings, DyeColor.LIME);
		registerBlockWithItem("pink_fence", PINK_FENCE, settings, DyeColor.PINK);
		registerBlockWithItem("gray_fence", GRAY_FENCE, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_fence", LIGHT_GRAY_FENCE, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_fence", CYAN_FENCE, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_fence", PURPLE_FENCE, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_fence", BLUE_FENCE, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_fence", BROWN_FENCE, settings, DyeColor.BROWN);
		registerBlockWithItem("green_fence", GREEN_FENCE, settings, DyeColor.GREEN);
		registerBlockWithItem("red_fence", RED_FENCE, settings, DyeColor.RED);
		registerBlockWithItem("black_fence", BLACK_FENCE, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_fence_gate", WHITE_FENCE_GATE, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_fence_gate", ORANGE_FENCE_GATE, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_fence_gate", MAGENTA_FENCE_GATE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_fence_gate", LIGHT_BLUE_FENCE_GATE, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_fence_gate", YELLOW_FENCE_GATE, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_fence_gate", LIME_FENCE_GATE, settings, DyeColor.LIME);
		registerBlockWithItem("pink_fence_gate", PINK_FENCE_GATE, settings, DyeColor.PINK);
		registerBlockWithItem("gray_fence_gate", GRAY_FENCE_GATE, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_fence_gate", LIGHT_GRAY_FENCE_GATE, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_fence_gate", CYAN_FENCE_GATE, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_fence_gate", PURPLE_FENCE_GATE, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_fence_gate", BLUE_FENCE_GATE, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_fence_gate", BROWN_FENCE_GATE, settings, DyeColor.BROWN);
		registerBlockWithItem("green_fence_gate", GREEN_FENCE_GATE, settings, DyeColor.GREEN);
		registerBlockWithItem("red_fence_gate", RED_FENCE_GATE, settings, DyeColor.RED);
		registerBlockWithItem("black_fence_gate", BLACK_FENCE_GATE, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_button", WHITE_BUTTON, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_button", ORANGE_BUTTON, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_button", MAGENTA_BUTTON, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_button", LIGHT_BLUE_BUTTON, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_button", YELLOW_BUTTON, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_button", LIME_BUTTON, settings, DyeColor.LIME);
		registerBlockWithItem("pink_button", PINK_BUTTON, settings, DyeColor.PINK);
		registerBlockWithItem("gray_button", GRAY_BUTTON, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_button", LIGHT_GRAY_BUTTON, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_button", CYAN_BUTTON, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_button", PURPLE_BUTTON, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_button", BLUE_BUTTON, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_button", BROWN_BUTTON, settings, DyeColor.BROWN);
		registerBlockWithItem("green_button", GREEN_BUTTON, settings, DyeColor.GREEN);
		registerBlockWithItem("red_button", RED_BUTTON, settings, DyeColor.RED);
		registerBlockWithItem("black_button", BLACK_BUTTON, settings, DyeColor.BLACK);
		
		registerBlockWithItem("white_slab", WHITE_SLAB, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_slab", ORANGE_SLAB, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_slab", MAGENTA_SLAB, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_slab", LIGHT_BLUE_SLAB, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_slab", YELLOW_SLAB, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_slab", LIME_SLAB, settings, DyeColor.LIME);
		registerBlockWithItem("pink_slab", PINK_SLAB, settings, DyeColor.PINK);
		registerBlockWithItem("gray_slab", GRAY_SLAB, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_slab", LIGHT_GRAY_SLAB, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_slab", CYAN_SLAB, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_slab", PURPLE_SLAB, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_slab", BLUE_SLAB, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_slab", BROWN_SLAB, settings, DyeColor.BROWN);
		registerBlockWithItem("green_slab", GREEN_SLAB, settings, DyeColor.GREEN);
		registerBlockWithItem("red_slab", RED_SLAB, settings, DyeColor.RED);
		registerBlockWithItem("black_slab", BLACK_SLAB, settings, DyeColor.BLACK);
	}
	
	private static void registerGlowBlocks(Item.Properties settings) {
		registerBlockWithItem("white_glowblock", WHITE_GLOWBLOCK, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_glowblock", ORANGE_GLOWBLOCK, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_glowblock", MAGENTA_GLOWBLOCK, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_glowblock", LIGHT_BLUE_GLOWBLOCK, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_glowblock", YELLOW_GLOWBLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_glowblock", LIME_GLOWBLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("pink_glowblock", PINK_GLOWBLOCK, settings, DyeColor.PINK);
		registerBlockWithItem("gray_glowblock", GRAY_GLOWBLOCK, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_glowblock", LIGHT_GRAY_GLOWBLOCK, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_glowblock", CYAN_GLOWBLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_glowblock", PURPLE_GLOWBLOCK, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_glowblock", BLUE_GLOWBLOCK, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_glowblock", BROWN_GLOWBLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("green_glowblock", GREEN_GLOWBLOCK, settings, DyeColor.GREEN);
		registerBlockWithItem("red_glowblock", RED_GLOWBLOCK, settings, DyeColor.RED);
		registerBlockWithItem("black_glowblock", BLACK_GLOWBLOCK, settings, DyeColor.BLACK);
	}
	
	public static void registerShimmerstoneLights(Item.Properties settings) {
		registerBlockWithItem("basalt_shimmerstone_light", BASALT_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("calcite_shimmerstone_light", CALCITE_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("stone_shimmerstone_light", STONE_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("granite_shimmerstone_light", GRANITE_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("diorite_shimmerstone_light", DIORITE_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("andesite_shimmerstone_light", ANDESITE_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("deepslate_shimmerstone_light", DEEPSLATE_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
		registerBlockWithItem("blackslag_shimmerstone_light", BLACKSLAG_SHIMMERSTONE_LIGHT, settings, DyeColor.YELLOW);
	}
	
	public static void registerShootingStarBlocks(Item.Properties settings) {
		registerBlockWithItem("glistering_shooting_star", GLISTERING_SHOOTING_STAR, new ShootingStarItem(GLISTERING_SHOOTING_STAR, settings), DyeColor.PURPLE);
		registerBlockWithItem("fiery_shooting_star", FIERY_SHOOTING_STAR, new ShootingStarItem(FIERY_SHOOTING_STAR, settings), DyeColor.PURPLE);
		registerBlockWithItem("colorful_shooting_star", COLORFUL_SHOOTING_STAR, new ShootingStarItem(COLORFUL_SHOOTING_STAR, settings), DyeColor.PURPLE);
		registerBlockWithItem("pristine_shooting_star", PRISTINE_SHOOTING_STAR, new ShootingStarItem(PRISTINE_SHOOTING_STAR, settings), DyeColor.PURPLE);
		registerBlockWithItem("gemstone_shooting_star", GEMSTONE_SHOOTING_STAR, new ShootingStarItem(GEMSTONE_SHOOTING_STAR, settings), DyeColor.PURPLE);

		registerBlockWithItem("stardust_block", STARDUST_BLOCK, settings, DyeColor.BLACK);
	}
	
	public static void registerPastelNetworkNodes(Item.Properties settings) {
		registerBlockWithItem("connection_node", CONNECTION_NODE, settings, DyeColor.GREEN);
		registerBlockWithItem("provider_node", PROVIDER_NODE, settings, DyeColor.GREEN);
		registerBlockWithItem("storage_node", STORAGE_NODE, settings, DyeColor.GREEN);
		registerBlockWithItem("sender_node", SENDER_NODE, settings, DyeColor.GREEN);
		registerBlockWithItem("gather_node", GATHER_NODE, settings, DyeColor.GREEN);
	}
	
	public static void registerSporeBlossoms(Item.Properties settings) {
		registerBlockWithItem("white_spore_blossom", WHITE_SPORE_BLOSSOM, settings, DyeColor.WHITE);
		registerBlockWithItem("orange_spore_blossom", ORANGE_SPORE_BLOSSOM, settings, DyeColor.ORANGE);
		registerBlockWithItem("magenta_spore_blossom", MAGENTA_SPORE_BLOSSOM, settings, DyeColor.MAGENTA);
		registerBlockWithItem("light_blue_spore_blossom", LIGHT_BLUE_SPORE_BLOSSOM, settings, DyeColor.LIGHT_BLUE);
		registerBlockWithItem("yellow_spore_blossom", YELLOW_SPORE_BLOSSOM, settings, DyeColor.YELLOW);
		registerBlockWithItem("lime_spore_blossom", LIME_SPORE_BLOSSOM, settings, DyeColor.LIME);
		registerBlockWithItem("pink_spore_blossom", PINK_SPORE_BLOSSOM, settings, DyeColor.PINK);
		registerBlockWithItem("gray_spore_blossom", GRAY_SPORE_BLOSSOM, settings, DyeColor.GRAY);
		registerBlockWithItem("light_gray_spore_blossom", LIGHT_GRAY_SPORE_BLOSSOM, settings, DyeColor.LIGHT_GRAY);
		registerBlockWithItem("cyan_spore_blossom", CYAN_SPORE_BLOSSOM, settings, DyeColor.CYAN);
		registerBlockWithItem("purple_spore_blossom", PURPLE_SPORE_BLOSSOM, settings, DyeColor.PURPLE);
		registerBlockWithItem("blue_spore_blossom", BLUE_SPORE_BLOSSOM, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_spore_blossom", BROWN_SPORE_BLOSSOM, settings, DyeColor.BROWN);
		registerBlockWithItem("green_spore_blossom", GREEN_SPORE_BLOSSOM, settings, DyeColor.GREEN);
		registerBlockWithItem("red_spore_blossom", RED_SPORE_BLOSSOM, settings, DyeColor.RED);
		registerBlockWithItem("black_spore_blossom", BLACK_SPORE_BLOSSOM, settings, DyeColor.BLACK);
	}
	
	private static void registerGemBlocks(Item.Properties settings) {
		registerBlockWithItem("topaz_block", TOPAZ_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("budding_topaz", BUDDING_TOPAZ, settings, DyeColor.CYAN);
		registerBlockWithItem("small_topaz_bud", SMALL_TOPAZ_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("medium_topaz_bud", MEDIUM_TOPAZ_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("large_topaz_bud", LARGE_TOPAZ_BUD, settings, DyeColor.CYAN);
		registerBlockWithItem("topaz_cluster", TOPAZ_CLUSTER, settings, DyeColor.CYAN);
		
		registerBlockWithItem("citrine_block", CITRINE_BLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("budding_citrine", BUDDING_CITRINE, settings, DyeColor.YELLOW);
		registerBlockWithItem("small_citrine_bud", SMALL_CITRINE_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("medium_citrine_bud", MEDIUM_CITRINE_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("large_citrine_bud", LARGE_CITRINE_BUD, settings, DyeColor.YELLOW);
		registerBlockWithItem("citrine_cluster", CITRINE_CLUSTER, settings, DyeColor.YELLOW);
		
		registerBlockWithItem("onyx_block", ONYX_BLOCK, settings, DyeColor.BLACK);
		registerBlockWithItem("budding_onyx", BUDDING_ONYX, settings, DyeColor.BLACK);
		registerBlockWithItem("small_onyx_bud", SMALL_ONYX_BUD, settings, DyeColor.BLACK);
		registerBlockWithItem("medium_onyx_bud", MEDIUM_ONYX_BUD, settings, DyeColor.BLACK);
		registerBlockWithItem("large_onyx_bud", LARGE_ONYX_BUD, settings, DyeColor.BLACK);
		registerBlockWithItem("onyx_cluster", ONYX_CLUSTER, settings, DyeColor.BLACK);
		
		registerBlockWithItem("moonstone_block", MOONSTONE_BLOCK, settings, DyeColor.WHITE);
		registerBlockWithItem("budding_moonstone", BUDDING_MOONSTONE, settings, DyeColor.WHITE);
		registerBlockWithItem("small_moonstone_bud", SMALL_MOONSTONE_BUD, settings, DyeColor.WHITE);
		registerBlockWithItem("medium_moonstone_bud", MEDIUM_MOONSTONE_BUD, settings, DyeColor.WHITE);
		registerBlockWithItem("large_moonstone_bud", LARGE_MOONSTONE_BUD, settings, DyeColor.WHITE);
		registerBlockWithItem("moonstone_cluster", MOONSTONE_CLUSTER, settings, DyeColor.WHITE);
	}
	
	private static void registerGemOreBlocks(Item.Properties settings) {
		// stone ores
		registerBlockWithItem("topaz_ore", TOPAZ_ORE, settings, DyeColor.CYAN);
		registerBlockWithItem("amethyst_ore", AMETHYST_ORE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("citrine_ore", CITRINE_ORE, settings, DyeColor.YELLOW);
		registerBlockWithItem("onyx_ore", ONYX_ORE, settings, DyeColor.BLACK);
		registerBlockWithItem("moonstone_ore", MOONSTONE_ORE, settings, DyeColor.WHITE);
		
		// deepslate ores
		registerBlockWithItem("deepslate_topaz_ore", DEEPSLATE_TOPAZ_ORE, settings, DyeColor.CYAN);
		registerBlockWithItem("deepslate_amethyst_ore", DEEPSLATE_AMETHYST_ORE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("deepslate_citrine_ore", DEEPSLATE_CITRINE_ORE, settings, DyeColor.YELLOW);
		registerBlockWithItem("deepslate_onyx_ore", DEEPSLATE_ONYX_ORE, settings, DyeColor.BLACK);
		registerBlockWithItem("deepslate_moonstone_ore", DEEPSLATE_MOONSTONE_ORE, settings, DyeColor.WHITE);
		
		// blackslag ores
		registerBlockWithItem("blackslag_topaz_ore", BLACKSLAG_TOPAZ_ORE, settings, DyeColor.CYAN);
		registerBlockWithItem("blackslag_amethyst_ore", BLACKSLAG_AMETHYST_ORE, settings, DyeColor.MAGENTA);
		registerBlockWithItem("blackslag_citrine_ore", BLACKSLAG_CITRINE_ORE, settings, DyeColor.YELLOW);
		registerBlockWithItem("blackslag_onyx_ore", BLACKSLAG_ONYX_ORE, settings, DyeColor.BLACK);
		registerBlockWithItem("blackslag_moonstone_ore", BLACKSLAG_MOONSTONE_ORE, settings, DyeColor.WHITE);
	}
	
	private static void registerStructureBlocks(Item.Properties settings) {
		registerBlockWithItem("downstone", DOWNSTONE, settings, DyeColor.BLUE);
		
		registerBlockWithItem("preservation_stone", PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_stairs", PRESERVATION_STAIRS, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_slab", PRESERVATION_SLAB, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_bricks", PRESERVATION_BRICKS, settings, DyeColor.BLUE);
		registerBlockWithItem("shimmering_preservation_bricks", SHIMMERING_PRESERVATION_BRICKS, settings, DyeColor.BLUE);
		registerBlockWithItem("powder_chiseled_preservation_stone", POWDER_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("dike_chiseled_preservation_stone", DIKE_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_glass", PRESERVATION_GLASS, settings, DyeColor.BLUE);
		registerBlockWithItem("tinted_preservation_glass", TINTED_PRESERVATION_GLASS, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_roundel", PRESERVATION_ROUNDEL, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_block_detector", PRESERVATION_BLOCK_DETECTOR, settings, DyeColor.BLUE);
		registerBlockWithItem("dike_gate_fountain", DIKE_GATE_FOUNTAIN, settings, DyeColor.BLUE);
		registerBlockWithItem("dike_gate", DIKE_GATE, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_controller", PRESERVATION_CONTROLLER, settings, DyeColor.BLUE);
		
		registerBlockWithItem("black_chiseled_preservation_stone", BLACK_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("blue_chiseled_preservation_stone", BLUE_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("brown_chiseled_preservation_stone", BROWN_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("cyan_chiseled_preservation_stone", CYAN_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("gray_chiseled_preservation_stone", GRAY_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("green_chiseled_preservation_stone", GREEN_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("light_blue_chiseled_preservation_stone", LIGHT_BLUE_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("light_gray_chiseled_preservation_stone", LIGHT_GRAY_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("lime_chiseled_preservation_stone", LIME_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("magenta_chiseled_preservation_stone", MAGENTA_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("orange_chiseled_preservation_stone", ORANGE_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("pink_chiseled_preservation_stone", PINK_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("purple_chiseled_preservation_stone", PURPLE_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("red_chiseled_preservation_stone", RED_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("white_chiseled_preservation_stone", WHITE_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		registerBlockWithItem("yellow_chiseled_preservation_stone", YELLOW_CHISELED_PRESERVATION_STONE, settings, DyeColor.BLUE);
		
		registerBlockWithItem("invisible_wall", INVISIBLE_WALL, settings, DyeColor.BLUE);
		registerBlockWithItem("courier_statue", COURIER_STATUE, settings, DyeColor.BLUE);
		registerBlockWithItem("preservation_chest", PRESERVATION_CHEST, settings, DyeColor.BLUE);
	}
	
	private static void registerJadeVineBlocks(Item.Properties settings) {
		registerBlock("jade_vine_roots", JADE_VINE_ROOTS);
		registerBlock("jade_vine_bulb", JADE_VINE_BULB);
		registerBlock("jade_vines", JADE_VINES);
		registerBlockWithItem("jade_vine_petal_block", JADE_VINE_PETAL_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("jade_vine_petal_carpet", JADE_VINE_PETAL_CARPET, settings, DyeColor.LIME);

		registerBlockWithItem("nephrite_blossom_stem", NEPHRITE_BLOSSOM_STEM, settings, DyeColor.PINK);
		registerBlockWithItem("nephrite_blossom_leaves", NEPHRITE_BLOSSOM_LEAVES, settings, DyeColor.PINK);
		registerBlock("nephrite_blossom_bulb", NEPHRITE_BLOSSOM_BULB);

		registerBlockWithItem("jadeite_lotus_stem", JADEITE_LOTUS_STEM, settings, DyeColor.LIME);
		registerBlockWithItem("jadeite_lotus_flower", JADEITE_LOTUS_FLOWER, IS.of().stacksTo(8), DyeColor.LIME);
		registerBlock("jadeite_lotus_bulb", JADEITE_LOTUS_BULB);
		registerBlockWithItem("jadeite_petal_block", JADEITE_PETAL_BLOCK, settings, DyeColor.LIME);
		registerBlockWithItem("jadeite_petal_carpet", JADEITE_PETAL_CARPET, settings, DyeColor.LIME);
	}
	
	private static void registerSugarSticks(Item.Properties settings) {
		registerBlockWithItem("sugar_stick", SUGAR_STICK, settings, DyeColor.PINK);
		registerBlockWithItem("topaz_sugar_stick", TOPAZ_SUGAR_STICK, settings, DyeColor.PINK);
		registerBlockWithItem("amethyst_sugar_stick", AMETHYST_SUGAR_STICK, settings, DyeColor.PINK);
		registerBlockWithItem("citrine_sugar_stick", CITRINE_SUGAR_STICK, settings, DyeColor.PINK);
		registerBlockWithItem("onyx_sugar_stick", ONYX_SUGAR_STICK, settings, DyeColor.PINK);
		registerBlockWithItem("moonstone_sugar_stick", MOONSTONE_SUGAR_STICK, settings, DyeColor.PINK);
	}
	
	private static void registerPureOreBlocks(Item.Properties settings) {
		registerBlockWithItem("pure_coal_block", PURE_COAL_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("pure_iron_block", PURE_IRON_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("pure_gold_block", PURE_GOLD_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("pure_diamond_block", PURE_DIAMOND_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("pure_emerald_block", PURE_EMERALD_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("pure_redstone_block", PURE_REDSTONE_BLOCK, settings, DyeColor.RED);
		registerBlockWithItem("pure_lapis_block", PURE_LAPIS_BLOCK, settings, DyeColor.PURPLE);
		registerBlockWithItem("pure_copper_block", PURE_COPPER_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("pure_quartz_block", PURE_QUARTZ_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("pure_glowstone_block", PURE_GLOWSTONE_BLOCK, settings, DyeColor.YELLOW);
		registerBlockWithItem("pure_prismarine_block", PURE_PRISMARINE_BLOCK, settings, DyeColor.CYAN);
		registerBlockWithItem("pure_netherite_scrap_block", PURE_NETHERITE_SCRAP_BLOCK, settings, DyeColor.BROWN);
		registerBlockWithItem("pure_echo_block", PURE_ECHO_BLOCK, settings, DyeColor.BROWN);
	}
	
	private static void registerMobBlocks(Item.Properties settings) {
		registerBlockWithItem("axolotl_idol", AXOLOTL_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("bat_idol", BAT_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("bee_idol", BEE_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("blaze_idol", BLAZE_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("cat_idol", CAT_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("chicken_idol", CHICKEN_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("cow_idol", COW_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("creeper_idol", CREEPER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("ender_dragon_idol", ENDER_DRAGON_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("enderman_idol", ENDERMAN_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("endermite_idol", ENDERMITE_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("evoker_idol", EVOKER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("fish_idol", FISH_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("fox_idol", FOX_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("ghast_idol", GHAST_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("glow_squid_idol", GLOW_SQUID_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("goat_idol", GOAT_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("guardian_idol", GUARDIAN_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("horse_idol", HORSE_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("illusioner_idol", ILLUSIONER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("ocelot_idol", OCELOT_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("parrot_idol", PARROT_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("phantom_idol", PHANTOM_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("pig_idol", PIG_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("piglin_idol", PIGLIN_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("polar_bear_idol", POLAR_BEAR_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("pufferfish_idol", PUFFERFISH_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("rabbit_idol", RABBIT_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("sheep_idol", SHEEP_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("shulker_idol", SHULKER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("silverfish_idol", SILVERFISH_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("skeleton_idol", SKELETON_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("slime_idol", SLIME_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("snow_golem_idol", SNOW_GOLEM_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("spider_idol", SPIDER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("squid_idol", SQUID_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("stray_idol", STRAY_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("strider_idol", STRIDER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("turtle_idol", TURTLE_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("witch_idol", WITCH_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("wither_idol", WITHER_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("wither_skeleton_idol", WITHER_SKELETON_IDOL, settings, DyeColor.PINK);
		registerBlockWithItem("zombie_idol", ZOMBIE_IDOL, settings, DyeColor.PINK);
	}
	
	// Most mob heads vanilla is missing (vanilla only has: skeleton, wither skeleton, zombie, player, creeper, ender dragon)
	private static void registerMobHeads(Item.Properties settings) {
		MOB_HEADS = EnumHashBiMap.create(SpectrumSkullBlockType.class);
		MOB_WALL_HEADS = EnumHashBiMap.create(SpectrumSkullBlockType.class);
		
		for (SpectrumSkullBlockType type : SpectrumSkullBlockType.values()) {
			Block head = new SpectrumSkullBlock(type, FabricBlockSettings.copyOf(Blocks.SKELETON_SKULL));
			registerBlock(type.name().toLowerCase(Locale.ROOT) + "_head", head);
			Block wallHead = new SpectrumWallSkullBlock(type, FabricBlockSettings.copyOf(Blocks.SKELETON_SKULL).dropsLike(head));
			registerBlock(type.name().toLowerCase(Locale.ROOT) + "_wall_head", wallHead);
			BlockItem headItem = new SpectrumSkullBlockItem(head, wallHead, (settings), type.entityType);
			registerBlockItem(type.name().toLowerCase(Locale.ROOT) + "_head", headItem, DyeColor.GRAY);
			
			MOB_HEADS.put(type, head);
			MOB_WALL_HEADS.put(type, wallHead);
		}
	}
	
	public static Block getMobHead(SpectrumSkullBlockType skullType) {
		return MOB_HEADS.get(skullType);
	}
	
	public static SpectrumSkullBlockType getSkullType(Block block) {
		if (block instanceof SpectrumWallSkullBlock) {
			return MOB_WALL_HEADS.inverse().get(block);
		} else {
			return MOB_HEADS.inverse().get(block);
		}
	}
	
	public static Block getMobWallHead(SpectrumSkullBlockType skullType) {
		return MOB_WALL_HEADS.get(skullType);
	}
	
	@Contract(pure = true)
	public static @NotNull Collection<Block> getMobHeads() {
		return MOB_HEADS.values();
	}
	
	@Contract(pure = true)
	public static @NotNull Collection<Block> getMobWallHeads() {
		return MOB_WALL_HEADS.values();
	}
	
	public static void registerClient() {
		
		// Crafting Stations
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), PEDESTAL_BASIC_AMETHYST, PEDESTAL_BASIC_CITRINE, PEDESTAL_BASIC_TOPAZ, PEDESTAL_ALL_BASIC, PEDESTAL_ONYX, PEDESTAL_MOONSTONE);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), FUSION_SHRINE_BASALT, FUSION_SHRINE_CALCITE);
		BlockRenderLayerMap.INSTANCE.putBlock(ENCHANTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(POTION_WORKSHOP, RenderType.translucent());
		
		// Gemstones
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_TOPAZ_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MEDIUM_TOPAZ_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_TOPAZ_BUD, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_CITRINE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MEDIUM_CITRINE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_CITRINE_BUD, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_ONYX_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MEDIUM_ONYX_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_ONYX_BUD, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_MOONSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MEDIUM_MOONSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_MOONSTONE_BUD, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BISMUTH_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_BISMUTH_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_BISMUTH_BUD, RenderType.cutout());
		
		// Glass
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMETHYST_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_GLASS, RenderType.translucent());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RADIANT_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RADIANT_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TINTED_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SEMI_PERMEABLE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMETHYST_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_SEMI_PERMEABLE_GLASS, RenderType.translucent());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ENDER_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PARTICLE_SPAWNER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CREATIVE_PARTICLE_SPAWNER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CRYSTALLARIEUM, RenderType.translucent());
		
		// Gemstone Lights
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_CALCITE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMETHYST_CALCITE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_CALCITE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_CALCITE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_CALCITE_LAMP, RenderType.translucent());
		
		// Gemstone Lamps
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_BASALT_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMETHYST_BASALT_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_BASALT_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_BASALT_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_BASALT_LAMP, RenderType.translucent());
		
		// Noxwood
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.IVORY_NOXWOOD_DOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.IVORY_NOXWOOD_TRAPDOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EBONY_NOXWOOD_DOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EBONY_NOXWOOD_TRAPDOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SLATE_NOXWOOD_DOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SLATE_NOXWOOD_TRAPDOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CHESTNUT_NOXWOOD_DOOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CHESTNUT_NOXWOOD_TRAPDOOR, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SLATE_NOXWOOD_LAMP, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EBONY_NOXWOOD_LAMP, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.IVORY_NOXWOOD_LAMP, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CHESTNUT_NOXWOOD_LAMP, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SLATE_NOXWOOD_LIGHT, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EBONY_NOXWOOD_LIGHT, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.IVORY_NOXWOOD_LIGHT, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CHESTNUT_NOXWOOD_LIGHT, RenderType.translucent());
		
		// Saplings
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLACK_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLUE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BROWN_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CYAN_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GRAY_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GREEN_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIGHT_BLUE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIGHT_GRAY_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIME_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MAGENTA_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ORANGE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PINK_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PURPLE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RED_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.WHITE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.YELLOW_SAPLING, RenderType.cutout());
		
		// Potted Saplings
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_BLACK_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_BLUE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_BROWN_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_CYAN_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_GRAY_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_GREEN_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_LIGHT_BLUE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_LIGHT_GRAY_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_LIME_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_MAGENTA_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_ORANGE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_PINK_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_PURPLE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_RED_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_WHITE_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_YELLOW_SAPLING, RenderType.cutout());
		
		// POTTED NOXSHROOMS
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_EBONY_NOXSHROOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_SLATE_NOXSHROOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_IVORY_NOXSHROOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_CHESTNUT_NOXSHROOM, RenderType.cutout());
		
		// Spore Blossoms
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLACK_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLUE_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BROWN_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CYAN_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GRAY_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GREEN_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIGHT_BLUE_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIGHT_GRAY_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIME_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MAGENTA_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ORANGE_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PINK_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PURPLE_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RED_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.WHITE_SPORE_BLOSSOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.YELLOW_SPORE_BLOSSOM, RenderType.cutout());
		
		// Colored lamps
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLACK_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLUE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BROWN_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CYAN_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GRAY_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GREEN_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIGHT_BLUE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIGHT_GRAY_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LIME_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MAGENTA_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ORANGE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PINK_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PURPLE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RED_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.WHITE_LAMP, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.YELLOW_LAMP, RenderType.translucent());
		
		// Decostones
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_DECOSTONE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMETHYST_DECOSTONE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_DECOSTONE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_DECOSTONE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_DECOSTONE, RenderType.translucent());
		
		// Chimes
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TOPAZ_CHIME, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMETHYST_CHIME, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CITRINE_CHIME, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MOONSTONE_CHIME, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ONYX_CHIME, RenderType.translucent());
		
		// Others
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), SpectrumBlocks.PRIMORDIAL_FIRE);
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PRESENT, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GLISTERING_MELON_STEM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ATTACHED_GLISTERING_MELON_STEM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.OMINOUS_SAPLING, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ITEM_BOWL_BASALT, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ITEM_BOWL_CALCITE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ITEM_ROUNDEL, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MEMORY, RenderType.translucent());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.JADE_VINE_ROOTS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.JADE_VINE_BULB, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.JADE_VINES, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.JADE_VINE_PETAL_BLOCK, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.JADE_VINE_PETAL_CARPET, RenderType.cutout());

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), NEPHRITE_BLOSSOM_LEAVES, NEPHRITE_BLOSSOM_BULB, NEPHRITE_BLOSSOM_STEM);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), JADEITE_LOTUS_FLOWER, JADEITE_LOTUS_BULB, JADEITE_LOTUS_STEM, JADEITE_PETAL_BLOCK, JADEITE_PETAL_CARPET);

		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMARANTH, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AMARANTH_BUSHEL, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_AMARANTH_BUSHEL, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLOOD_ORCHID, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_BLOOD_ORCHID, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POTTED_RESONANT_LILY, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.DIKE_GATE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.INVISIBLE_WALL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PRESERVATION_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TINTED_PRESERVATION_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.COURIER_STATUE, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.COLOR_PICKER, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.REDSTONE_TIMER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.REDSTONE_TRANSCEIVER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.REDSTONE_CALCULATOR, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.QUITOXIC_REEDS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MERMAIDS_BRUSH, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RESONANT_LILY, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.STUCK_STORM_STONE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CLOVER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.FOUR_LEAF_CLOVER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ETHEREAL_PLATFORM, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.UNIVERSE_SPYHOLE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BOTTOMLESS_BUNDLE, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SAG_LEAF, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SAG_BUBBLE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_SAG_BUBBLE, RenderType.cutout());
		
		// Mob Blocks
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AXOLOTL_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BAT_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BEE_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLAZE_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CAT_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CHICKEN_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.COW_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CREEPER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ENDER_DRAGON_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ENDERMAN_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ENDERMITE_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EVOKER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.FISH_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.FOX_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GHAST_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GLOW_SQUID_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GOAT_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GUARDIAN_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.HORSE_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ILLUSIONER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.OCELOT_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PARROT_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PHANTOM_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PIG_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PIGLIN_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.POLAR_BEAR_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PUFFERFISH_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.RABBIT_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SHEEP_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SHULKER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SILVERFISH_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SKELETON_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SLIME_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SNOW_GOLEM_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SPIDER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SQUID_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.STRAY_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.STRIDER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TURTLE_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.WITCH_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.WITHER_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.WITHER_SKELETON_IDOL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ZOMBIE_IDOL, RenderType.translucent());
		
		// Shooting stars
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.COLORFUL_SHOOTING_STAR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.FIERY_SHOOTING_STAR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GEMSTONE_SHOOTING_STAR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GLISTERING_SHOOTING_STAR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PRISTINE_SHOOTING_STAR, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.INCANDESCENT_AMALGAM, RenderType.cutout());
		
		// CRYSTALLARIEUM GROWABLES
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_COAL_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_COAL_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.COAL_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_COPPER_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_COPPER_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.COPPER_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_DIAMOND_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_DIAMOND_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.DIAMOND_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_EMERALD_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_EMERALD_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EMERALD_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_GLOWSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_GLOWSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GLOWSTONE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_GOLD_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_GOLD_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.GOLD_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_IRON_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_IRON_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.IRON_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_LAPIS_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_LAPIS_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LAPIS_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_NETHERITE_SCRAP_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_NETHERITE_SCRAP_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.NETHERITE_SCRAP_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_PRISMARINE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_PRISMARINE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PRISMARINE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_QUARTZ_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_QUARTZ_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.QUARTZ_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_REDSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_REDSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.REDSTONE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_ECHO_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_ECHO_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ECHO_CLUSTER, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_AZURITE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_AZURITE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.AZURITE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_MALACHITE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_MALACHITE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.MALACHITE_CLUSTER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_BLOODSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.LARGE_BLOODSTONE_BUD, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BLOODSTONE_CLUSTER, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_YELLOW_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_RED_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_PINK_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_PURPLE_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SMALL_BLACK_DRAGONJAG, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TALL_YELLOW_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TALL_RED_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TALL_PINK_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TALL_PURPLE_DRAGONJAG, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.TALL_BLACK_DRAGONJAG, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.ALOE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SAWBLADE_HOLLY_BUSH, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.BRISTLE_SPROUTS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.DOOMBLOOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SNAPPING_IVY, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.SLATE_NOXSHROOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.IVORY_NOXSHROOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.EBONY_NOXSHROOM, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CHESTNUT_NOXSHROOM, RenderType.cutout());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.PYRITE_RIPPER, RenderType.cutoutMipped());
		
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.HUMMINGSTONE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.HUMMINGSTONE_GLASS, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SpectrumBlocks.CLEAR_HUMMINGSTONE_GLASS, RenderType.translucent());
	}
	
}
