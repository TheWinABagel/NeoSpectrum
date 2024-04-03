package de.dafuqs.spectrum;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dafuqs.revelationary.api.advancements.ClientAdvancementPacketCallback;
import de.dafuqs.revelationary.api.revelations.RevealingCallback;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.render.DynamicItemRenderer;
import de.dafuqs.spectrum.api.render.DynamicRenderModel;
import de.dafuqs.spectrum.blocks.bottomless_bundle.BottomlessBundleItem;
import de.dafuqs.spectrum.blocks.pastel_network.Pastel;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.compat.ears.EarsCompat;
import de.dafuqs.spectrum.compat.idwtialsimmoedm.IdwtialsimmoedmCompat;
import de.dafuqs.spectrum.compat.patchouli.PatchouliFlags;
import de.dafuqs.spectrum.compat.patchouli.PatchouliPages;
import de.dafuqs.spectrum.data_loaders.ParticleSpawnerParticlesDataLoader;
import de.dafuqs.spectrum.entity.SpectrumEntityRenderers;
import de.dafuqs.spectrum.helpers.BuildingHelper;
import de.dafuqs.spectrum.helpers.TooltipHelper;
import de.dafuqs.spectrum.inventories.SpectrumScreenHandlerTypes;
import de.dafuqs.spectrum.items.magic_items.BuildingStaffItem;
import de.dafuqs.spectrum.items.magic_items.ConstructorsStaffItem;
import de.dafuqs.spectrum.items.magic_items.ExchangeStaffItem;
import de.dafuqs.spectrum.items.tools.OmniAcceleratorItem;
import de.dafuqs.spectrum.mixin.accessors.WorldRendererAccessor;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketReceiver;
import de.dafuqs.spectrum.particle.SpectrumParticleFactories;
import de.dafuqs.spectrum.particle.render.ExtendedParticleManager;
import de.dafuqs.spectrum.progression.UnlockToastManager;
import de.dafuqs.spectrum.progression.toast.RevelationToast;
import de.dafuqs.spectrum.registries.*;
import de.dafuqs.spectrum.registries.client.*;
import de.dafuqs.spectrum.render.HudRenderers;
import de.dafuqs.spectrum.render.SkyLerper;
import de.dafuqs.spectrum.render.capes.WorthinessChecker;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Triplet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static de.dafuqs.spectrum.SpectrumCommon.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
public class SpectrumClient implements RevealingCallback, ClientAdvancementPacketCallback {

	@OnlyIn(Dist.CLIENT)
	public static final SkyLerper skyLerper = new SkyLerper();
	public static final boolean foodEffectsTooltipsModLoaded = ModList.get().isLoaded("foodeffecttooltips");

	// initial impl
	public static final ObjectOpenHashSet<ModelResourceLocation> CUSTOM_ITEM_MODELS = new ObjectOpenHashSet<>();

	@SubscribeEvent
	public void setup(FMLClientSetupEvent e) {
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		logInfo("Starting Client Startup");

		logInfo("Registering Model Layers...");

		modBus.addListener(SpectrumModelLayers::register);

		logInfo("Setting up Block Rendering...");
		SpectrumBlocks.registerClient();

		logInfo("Setting up client side Mod Compat...");
		SpectrumIntegrationPacks.registerClient();

		logInfo("Setting up Fluid Rendering...");
		SpectrumFluids.registerClient();

		logInfo("Setting up GUIs...");
		SpectrumScreenHandlerTypes.registerClient();

		logInfo("Setting up ItemPredicates...");
		SpectrumModelPredicateProviders.registerClient();

		logInfo("Setting up Block Entity Renderers...");
		SpectrumBlockEntities.registerClient();
		logInfo("Setting up Entity Renderers...");
		modBus.addListener(SpectrumEntityRenderers::registerClient);

		registerCustomItemRenderer("bottomless_bundle", SpectrumItems.BOTTOMLESS_BUNDLE, BottomlessBundleItem.Renderer::new);
		registerCustomItemRenderer("omni_accelerator", SpectrumItems.OMNI_ACCELERATOR, OmniAcceleratorItem.Renderer::new);

		logInfo("Registering Server to Client Package Receivers...");
		SpectrumS2CPacketReceiver.registerS2CReceivers();
		logInfo("Registering Particle Factories...");
		modBus.addListener(SpectrumParticleFactories::register);

		logInfo("Registering Overlays...");
		forgeBus.addListener(HudRenderers::register);

		logInfo("Registering Item Tooltips...");
		modBus.addListener(SpectrumTooltipComponents::registerTooltipComponents);

		logInfo("Registering custom Patchouli Pages & Flags...");
		PatchouliPages.register();
		PatchouliFlags.register();

		logInfo("Registering Dimension Effects...");
		SpectrumDimensions.registerClient();

		if (CONFIG.AddItemTooltips) {
			forgeBus.addListener(SpectrumTooltips::register);
		}

		if (ModList.get().isLoaded("ears")) {
			logInfo("Registering Ears Compat...");
			EarsCompat.register();
		}

		if (ModList.get().isLoaded("idwtialsimmoedm")) {
			logInfo("Registering idwtialsimmoedm Compat...");
			IdwtialsimmoedmCompat.register();
		}

		logInfo("Registering Armor Renderers..."); //todoforge armor renderers
		SpectrumArmorRenderers.register();
		WorthinessChecker.init();

		RevealingCallback.register(this);
		ClientAdvancementPacketCallback.registerCallback(this);

		SpectrumCommon.logInfo("Registering Block and Item Color Providers...");
		modBus.register(SpectrumColorProviders.class);
//		SpectrumColorProviders.registerClient();

		logInfo("Client startup completed!");
	}

	@SubscribeEvent
	public void clientReloaders(RegisterClientReloadListenersEvent e) {
		e.registerReloadListener(ParticleSpawnerParticlesDataLoader.INSTANCE);
	}

	@SubscribeEvent
	public void addCustomModels(ModelEvent.ModifyBakingResult e) {
		logInfo("Setting up Item Renderers...");
		var models = e.getModels();
		for (ModelResourceLocation id : CUSTOM_ITEM_MODELS) {
			var old = models.get(id);
			models.replace(id, new DynamicRenderModel(old));
		}
	}

	@SubscribeEvent
	public void clearClient(ClientPlayerNetworkEvent.LoggingOut e) {
		Pastel.clearClientInstance();
	}

	@SubscribeEvent
	public void modifyTooltips(ItemTooltipEvent e) {
		ItemStack stack = e.getItemStack();
		if (!foodEffectsTooltipsModLoaded && stack.isEdible()) {
			if (BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(SpectrumCommon.MOD_ID)) {
				TooltipHelper.addFoodComponentEffectTooltip(stack, e.getToolTip());
			}
		}
		if (stack.is(SpectrumItemTags.COMING_SOON_TOOLTIP)) {
			e.getToolTip().add(Component.translatable("spectrum.tooltip.coming_soon").withStyle(ChatFormatting.RED));
		}
	}

	@SubscribeEvent
	public void render(RenderLevelStageEvent e) { //todoforge no idea if this needs to be multiple listeners, am still rendering noob
		if (e.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SKY)) {
			HudRenderers.clearItemStackOverlay();
		}
		if (e.getStage().equals(RenderLevelStageEvent.Stage.AFTER_ENTITIES)) {
			((ExtendedParticleManager) Minecraft.getInstance().particleEngine).render(e.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), e.getCamera(), e.getRenderTick());
		}
		if (e.getStage().equals(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)) {
			Pastel.getClientInstance().renderLines(e.getPoseStack(), e.getCamera().getPosition(), Minecraft.getInstance().renderBuffers().bufferSource());
		}
	}

	@SubscribeEvent
	public void renderBlockOutline(RenderHighlightEvent.Block e) {
		this.renderExtendedBlockOutline(e.getPoseStack(), e.getCamera(), e.getTarget());

	}

	private void registerCustomItemRenderer(String id, Item item, Supplier<DynamicItemRenderer> renderer) {
		CUSTOM_ITEM_MODELS.add(new ModelResourceLocation(MOD_ID, id, "inventory"));
		DynamicItemRenderer.RENDERERS.put(item, renderer.get());
	}

	private boolean renderExtendedBlockOutline(PoseStack pose, Camera camera, BlockHitResult blockHitResult) {
		boolean shouldCancel = false;
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			for (ItemStack handStack : client.player.getHandSlots()) {
				Item handItem = handStack.getItem();
				Vec3 cameraPos = camera.getPosition();
				if (handItem instanceof ConstructorsStaffItem) {
					if (blockHitResult != null) {
						shouldCancel = renderPlacementStaffOutline(pose, camera, cameraPos.x, cameraPos.y, cameraPos.z, Minecraft.getInstance().renderBuffers().bufferSource(), blockHitResult);
					}
					break;
				} else if (handItem instanceof ExchangeStaffItem) {
					if (blockHitResult != null) {
						shouldCancel = renderExchangeStaffOutline(pose, camera, cameraPos.x, cameraPos.y, cameraPos.z, Minecraft.getInstance().renderBuffers().bufferSource(), handStack, blockHitResult);
					}
					break;
				}
			}
		}

		return !shouldCancel;
	}

	@Override
	public void trigger(Set<ResourceLocation> advancements, Set<Block> blocks, Set<Item> items, boolean isJoinPacket) {
		if (!isJoinPacket) {
			for (Block block : blocks) {
				if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(SpectrumCommon.MOD_ID)) {
					RevelationToast.showRevelationToast(Minecraft.getInstance(), new ItemStack(SpectrumBlocks.PEDESTAL_BASIC_AMETHYST.asItem()), SpectrumSoundEvents.NEW_REVELATION);
					break;
				}
			}
		}
	}

	@Override
	public void onClientAdvancementPacket(Set<ResourceLocation> gottenAdvancements, Set<ResourceLocation> removedAdvancements, boolean isJoinPacket) {
		if (!isJoinPacket) {
			UnlockToastManager.processAdvancements(gottenAdvancements);
		}
	}

	private boolean renderPlacementStaffOutline(PoseStack matrices, Camera camera, double d, double e, double f, MultiBufferSource consumers, @NotNull BlockHitResult hitResult) {
		Minecraft client = Minecraft.getInstance();
		ClientLevel world = client.level;
		LocalPlayer player = client.player;
		if (player == null || world == null) return false;

		BlockPos lookingAtPos = hitResult.getBlockPos();
		BlockState lookingAtState = world.getBlockState(lookingAtPos);

		if (player.getMainHandItem().getItem() instanceof BuildingStaffItem staff && (player.isCreative() || staff.canInteractWith(lookingAtState, world, lookingAtPos, player))) {
			Block lookingAtBlock = lookingAtState.getBlock();
			Item item = lookingAtBlock.asItem();
			VoxelShape shape = Shapes.empty();

			if (item != Items.AIR) {
				int itemCountInInventory = Integer.MAX_VALUE;
				long inkLimit = Long.MAX_VALUE;
				if (!player.isCreative()) {
					Triplet<Block, Item, Integer> inventoryItemAndCount = BuildingHelper.getBuildingItemCountInInventoryIncludingSimilars(player, lookingAtBlock, Integer.MAX_VALUE);
					item = inventoryItemAndCount.getB();
					itemCountInInventory = inventoryItemAndCount.getC();
					inkLimit = InkPowered.getAvailableInk(player, ConstructorsStaffItem.USED_COLOR) / ConstructorsStaffItem.INK_COST_PER_BLOCK;
				}

				boolean sneaking = player.isShiftKeyDown();
				if (itemCountInInventory == 0) {
					HudRenderers.setItemStackToRender(new ItemStack(item), 0, false);
				} else if (inkLimit == 0) {
					HudRenderers.setItemStackToRender(new ItemStack(item), 1, true);
				} else {
					long usableCount = Math.min(itemCountInInventory, inkLimit);
					List<BlockPos> positions = BuildingHelper.calculateBuildingStaffSelection(world, lookingAtPos, hitResult.getDirection(), usableCount, ConstructorsStaffItem.getRange(player), !sneaking);
					if (positions.size() > 0) {
						for (BlockPos newPosition : positions) {
							if (world.getWorldBorder().isWithinBounds(newPosition)) {
								BlockPos testPos = lookingAtPos.subtract(newPosition);
								shape = Shapes.or(shape, lookingAtState.getShape(world, lookingAtPos, CollisionContext.of(camera.getEntity())).move(-testPos.getX(), -testPos.getY(), -testPos.getZ()));
							}
						}

						HudRenderers.setItemStackToRender(new ItemStack(item), positions.size(), false);
						VertexConsumer linesBuffer = consumers.getBuffer(RenderType.lines());
						de.dafuqs.spectrum.mixin.accessors.WorldRendererAccessor.invokeDrawCuboidShapeOutline(matrices, linesBuffer, shape, (double) lookingAtPos.getX() - d, (double) lookingAtPos.getY() - e, (double) lookingAtPos.getZ() - f, 0.0F, 0.0F, 0.0F, 0.4F);
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean renderExchangeStaffOutline(PoseStack matrices, Camera camera, double d, double e, double f, MultiBufferSource consumers, ItemStack exchangeStaffItemStack, BlockHitResult hitResult) {
		Minecraft client = Minecraft.getInstance();
		ClientLevel world = client.level;
		LocalPlayer player = client.player;

		if (player == null || world == null) return false;

		BlockPos lookingAtPos = hitResult.getBlockPos();
		BlockState lookingAtState = world.getBlockState(lookingAtPos);

		if (player.getMainHandItem().getItem() instanceof BuildingStaffItem staff && (player.isCreative() || staff.canInteractWith(lookingAtState, world, lookingAtPos, player))) {
			Block lookingAtBlock = lookingAtState.getBlock();
			Optional<Block> exchangeBlock = ExchangeStaffItem.getStoredBlock(exchangeStaffItemStack);
			if (exchangeBlock.isPresent() && exchangeBlock.get() != lookingAtBlock) {
				Item exchangeBlockItem = exchangeBlock.get().asItem();
				VoxelShape shape = Shapes.empty();

				if (exchangeBlockItem != Items.AIR) {
					int itemCountInInventory = Integer.MAX_VALUE;
					long inkLimit = Integer.MAX_VALUE;
					if (!player.isCreative()) {
						itemCountInInventory = player.getInventory().countItem(exchangeBlockItem);
						inkLimit = InkPowered.getAvailableInk(player, ExchangeStaffItem.USED_COLOR) / ExchangeStaffItem.INK_COST_PER_BLOCK;
					}

					if (itemCountInInventory == 0) {
						HudRenderers.setItemStackToRender(new ItemStack(exchangeBlockItem), 0, false);
					} else if (inkLimit == 0) {
						HudRenderers.setItemStackToRender(new ItemStack(exchangeBlockItem), 1, true);
					} else {
						long usableCount = Math.min(itemCountInInventory, inkLimit);
						List<BlockPos> positions = BuildingHelper.getConnectedBlocks(world, lookingAtPos, usableCount, ExchangeStaffItem.getRange(player));
						for (BlockPos newPosition : positions) {
							if (world.getWorldBorder().isWithinBounds(newPosition)) {
								BlockPos testPos = lookingAtPos.subtract(newPosition);
								shape = Shapes.or(shape, lookingAtState.getShape(world, lookingAtPos, CollisionContext.of(camera.getEntity())).move(-testPos.getX(), -testPos.getY(), -testPos.getZ()));
							}
						}

						HudRenderers.setItemStackToRender(new ItemStack(exchangeBlockItem), positions.size(), false);
						VertexConsumer linesBuffer = consumers.getBuffer(RenderType.lines());
						WorldRendererAccessor.invokeDrawCuboidShapeOutline(matrices, linesBuffer, shape, (double) lookingAtPos.getX() - d, (double) lookingAtPos.getY() - e, (double) lookingAtPos.getZ() - f, 0.0F, 0.0F, 0.0F, 0.4F);
						return true;
					}
				}
			}
		}

		return false;
	}
}
