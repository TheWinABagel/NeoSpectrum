package de.dafuqs.spectrum;

import com.google.common.collect.ImmutableMap;
import de.dafuqs.spectrum.api.color.ColorRegistry;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.item.PrioritizedBlockInteraction;
import de.dafuqs.spectrum.api.item.PrioritizedEntityInteraction;
import de.dafuqs.spectrum.api.item_group.ItemGroupIDs;
import de.dafuqs.spectrum.blocks.chests.CompactingChestBlockEntity;
import de.dafuqs.spectrum.blocks.idols.FirestarterIdolBlock;
import de.dafuqs.spectrum.blocks.pastel_network.Pastel;
import de.dafuqs.spectrum.cca.SpectrumCapabilities;
import de.dafuqs.spectrum.compat.SpectrumIntegrationPacks;
import de.dafuqs.spectrum.compat.reverb.DimensionReverb;
import de.dafuqs.spectrum.config.SpectrumConfig;
import de.dafuqs.spectrum.data_loaders.CrystalApothecarySimulationsDataLoader;
import de.dafuqs.spectrum.data_loaders.EntityFishingDataLoader;
import de.dafuqs.spectrum.data_loaders.NaturesStaffConversionDataLoader;
import de.dafuqs.spectrum.data_loaders.ResonanceDropsDataLoader;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.entity.SpectrumTrackedDataHandlerRegistry;
import de.dafuqs.spectrum.entity.SpectrumTypeSpecificPredicates;
import de.dafuqs.spectrum.entity.spawners.ShootingStarSpawner;
import de.dafuqs.spectrum.events.SpectrumGameEvents;
import de.dafuqs.spectrum.events.SpectrumPositionSources;
import de.dafuqs.spectrum.explosion.ExplosionModifierProviders;
import de.dafuqs.spectrum.explosion.ExplosionModifiers;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.helpers.TimeHelper;
import de.dafuqs.spectrum.inventories.SpectrumScreenHandlerTypes;
import de.dafuqs.spectrum.items.magic_items.ExchangeStaffItem;
import de.dafuqs.spectrum.items.tools.GlassCrestCrossbowItem;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.items.trinkets.WhispyCircletItem;
import de.dafuqs.spectrum.loot.SpectrumLootConditionTypes;
import de.dafuqs.spectrum.loot.SpectrumLootFunctionTypes;
import de.dafuqs.spectrum.loot.SpectrumLootPoolModifiers;
import de.dafuqs.spectrum.mixin.accessors.RecipeManagerAccessor;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketReceiver;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipeSerializer;
import de.dafuqs.spectrum.registries.*;
import de.dafuqs.spectrum.registries.client.SpectrumColorProviders;
import de.dafuqs.spectrum.spells.InkSpellEffects;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Mod(SpectrumCommon.MOD_ID)
public class SpectrumCommon{
	
	public static final String MOD_ID = "spectrum";
	
	public static final Logger LOGGER = LoggerFactory.getLogger("Spectrum");
	public static SpectrumConfig CONFIG;
	
	public static MinecraftServer minecraftServer;
	/**
	 * Caches the luminance states from fluids as int
	 * for blocks that react to the light level of fluids
	 * like the fusion shrine lighting up with lava or liquid crystal
	 */
	public static final HashMap<Fluid, Integer> fluidLuminance = new HashMap<>();
	
	public static void logInfo(String message) {
		LOGGER.info("[Spectrum] " + message);
	}
	
	public static void logWarning(String message) {
		LOGGER.warn("[Spectrum] " + message);
	}
	
	public static void logError(String message) {
		LOGGER.error("[Spectrum] " + message);
	}
	
	public static ResourceLocation locate(String name) {
		return new ResourceLocation(MOD_ID, name);
	}
	
	static {
		//Set up config
		logInfo("Loading config file...");
		AutoConfig.register(SpectrumConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(SpectrumConfig.class).getConfig();
		logInfo("Finished loading config file.");
	}

	public SpectrumCommon() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
//		MinecraftForge.EVENT_BUS.addListener(this::init);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent e) {
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		logInfo("Starting Common Startup");
		
		// Register internals
		SpectrumRegistries.register();
		InkColors.register();
		
		logInfo("Registering Banner Patterns...");
		SpectrumBannerPatterns.register();
		
		logInfo("Registering Block / Item Color Registries...");
		ColorRegistry.registerColorRegistries();
		
		// Register ALL the stuff
		logInfo("Registering Status Effects...");
		SpectrumStatusEffects.register();
		SpectrumStatusEffectTags.register();
		logInfo("Registering Advancement Criteria...");
		SpectrumAdvancementCriteria.register();
		logInfo("Registering Particle Types...");
		SpectrumParticleTypes.register();
		logInfo("Registering Sound Events...");
		SpectrumSoundEvents.register();
		logInfo("Registering Music...");
		SpectrumMusicType.register();
		logInfo("Registering BlockSound Groups...");
		SpectrumBlockSoundGroups.register();
		logInfo("Registering Fluids...");
		SpectrumFluids.register();
		logInfo("Registering Enchantments...");
		SpectrumEnchantments.register();
		logInfo("Registering Blocks...");
		SpectrumBlocks.register();
		logInfo("Registering Items...");
		SpectrumPotions.register();
		SpectrumItems.register();
//		SpectrumItemGroups.register(); //todoforge itemgroups
		logInfo("Setting up server side Mod Compat...");
		SpectrumIntegrationPacks.register();
		logInfo("Registering Block Entities...");
		SpectrumBlockEntities.register();
		
		// Worldgen
		logInfo("Registering Features...");
		SpectrumFeatures.register();
		logInfo("Registering Biome Modifications...");
		SpectrumPlacedFeatures.addBiomeModifications();
		logInfo("Registering Structure Types...");
		SpectrumStructureTypes.register();
		
		// Dimension
		logInfo("Registering Dimension...");
		SpectrumDimensions.register();
		
		// Dimension effects
		logInfo("Registering Dimension Sound Effects...");
		DimensionReverb.setup();
		
		// Recipes
		logInfo("Registering Recipe Types...");
		SpectrumFusionShrineWorldEffects.register();
		SpectrumRecipeTypes.registerSerializer();
		
		// Loot
		logInfo("Registering Loot Conditions & Functions...");
		SpectrumLootConditionTypes.register();
		SpectrumLootFunctionTypes.register();
		
		// GUI
		logInfo("Registering Screen Handler Types...");
		SpectrumScreenHandlerTypes.register();
		
		logInfo("Registering Default Item Stack Damage Immunities...");
		SpectrumItemDamageImmunities.registerDefaultItemStackImmunities();
		logInfo("Registering Enchantment Drops...");
		SpectrumLootPoolModifiers.setup();
		logInfo("Registering Type Specific Predicates...");
		SpectrumTypeSpecificPredicates.register();
		logInfo("Registering Omni Accelerator Projectiles & Behaviors...");
		SpectrumOmniAcceleratorProjectiles.register();
		SpectrumItemProjectileBehaviors.register();
		
		logInfo("Registering Entities...");
		SpectrumTrackedDataHandlerRegistry.register();

		modBus.addListener(SpectrumEntityTypes::registerAttributes);
		
		logInfo("Registering Commands...");
		forgeBus.addListener(SpectrumCommands::register);
		
		logInfo("Registering Client To ServerPackage Receivers...");
		SpectrumC2SPacketReceiver.registerC2SReceivers();
		
		logInfo("Registering compostables...");
		SpectrumCompostableBlocks.register();
		
		logInfo("Registering Game Events...");
		SpectrumGameEvents.register();
		SpectrumPositionSources.register();
		
		logInfo("Registering Spell Effects...");
		InkSpellEffects.register();
		
		logInfo("Registering Explosion Effects & Providers...");
		ExplosionModifiers.register();
		ExplosionModifierProviders.register();
		logInfo("Registering Special Recipes...");
		SpectrumCustomRecipeSerializers.registerRecipeSerializers();
		
		logInfo("Registering Dispenser, Resonance & Present Unwrap Behaviors...");
		SpectrumDispenserBehaviors.register();
		SpectrumPresentUnpackBehaviors.register();
		SpectrumResonanceProcessors.register();
		
		logInfo("Registering Resource Conditions...");
		SpectrumResourceConditions.register();
		logInfo("Registering Structure Pool Element Types...");
		SpectrumStructurePoolElementTypes.register();

		logInfo("Registering Capabilities...");
		SpectrumCapabilities.initCapEvents();

		//todoforge port arrowhead
//		CrossbowShootingCallback.register((world, shooter, hand, crossbow, projectile, projectileEntity) -> {
//			if (crossbow.getItem() instanceof GlassCrestCrossbowItem && GlassCrestCrossbowItem.isOvercharged(crossbow)) {
//				if (!world.isClientSide) { // only fired on the client, but making sure mods aren't doing anything weird
//					Vec3 particleVelocity = projectileEntity.getDeltaMovement().scale(0.05);
//
//					if (GlassCrestCrossbowItem.getOvercharge(crossbow) > 0.99F) {
//						SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world,
//								projectileEntity.position(), ParticleTypes.SCRAPE, 5,
//								Vec3.ZERO, particleVelocity);
//						SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world,
//								projectileEntity.position(), ParticleTypes.WAX_OFF, 5,
//								Vec3.ZERO, particleVelocity);
//						SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world,
//								projectileEntity.position(), ParticleTypes.WAX_ON, 5,
//								Vec3.ZERO, particleVelocity);
//						SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world,
//								projectileEntity.position(), ParticleTypes.GLOW, 5,
//								Vec3.ZERO, particleVelocity);
//
//						if (shooter instanceof ServerPlayer serverPlayerEntity) {
//							Support.grantAdvancementCriterion(serverPlayerEntity,
//									SpectrumCommon.locate("lategame/shoot_fully_overcharged_crossbow"),
//									"shot_fully_overcharged_crossbow");
//						}
//						if (projectileEntity instanceof AbstractArrow persistentProjectileEntity) {
//							persistentProjectileEntity.setBaseDamage(persistentProjectileEntity.getBaseDamage() * 1.5);
//						}
//					}
//
//					SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world,
//							projectileEntity.position(), ParticleTypes.FIREWORK, 10,
//							Vec3.ZERO, particleVelocity);
//
//					GlassCrestCrossbowItem.unOvercharge(crossbow);
//				}
//			}
//		});
		//todoforge port fractal
//		ItemSubGroupEvents.modifyEntriesEvent(ItemGroupIDs.SUBTAB_BLOCKS).register(new ItemSubGroupEvents.ModifyEntries() {
//			@Override
//			public void modifyEntries(FabricItemGroupEntries entries) {
//				entries.accept(new ItemStack(Items.APPLE));
//			}
//		});

//		ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.storage, SpectrumBlockEntities.BOTTOMLESS_BUNDLE);
//		FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.fluidStorage, SpectrumBlockEntities.FUSION_SHRINE);
//		FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.fluidStorage, SpectrumBlockEntities.TITRATION_BARREL);
		
		// Builtin Resource Packs
//		Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(SpectrumCommon.MOD_ID);
//		if (modContainer.isPresent()) {
//			// ResourceManagerHelper.registerBuiltinResourcePack(locate("spectrum_style_amethyst"), modContainer.get(), Text.of("Spectrum Style Amethyst"), ResourcePackActivationType.NORMAL); // TODO: retexture
//			ResourceManagerHelper.registerBuiltinResourcePack(locate("spectrum_programmer_art"), modContainer.get(), Component.nullToEmpty("Spectrum's Programmer Art"), ResourcePackActivationType.NORMAL);
//			ResourceManagerHelper.registerBuiltinResourcePack(locate("jinc"), modContainer.get(), Component.nullToEmpty("Alternate Spectrum textures"), ResourcePackActivationType.NORMAL);
//		}
		//todoforge built in resource packs
		
		logInfo("Common startup completed!");
	}

	@SubscribeEvent
	public void onAddReloadListener(AddReloadListenerEvent e) {
		logInfo("Registering Data Loaders...");
		e.addListener(NaturesStaffConversionDataLoader.INSTANCE);
		e.addListener(EntityFishingDataLoader.INSTANCE);
		e.addListener(CrystalApothecarySimulationsDataLoader.INSTANCE);
		e.addListener(ResonanceDropsDataLoader.INSTANCE);

		logInfo("Registering RecipeCache reload listener");

		e.addListener((ResourceManagerReloadListener) manager -> {
			CompactingChestBlockEntity.clearCache();

			if (minecraftServer != null) {
				injectEnchantmentUpgradeRecipes(minecraftServer);
				FirestarterIdolBlock.addBlockSmeltingRecipes(minecraftServer);
			}
		});
	}

	@SubscribeEvent
	public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
		Level level = e.getLevel();
		Player player = e.getEntity();
		if (!level.isClientSide && !player.isSpectator()) {

			BlockPos pos = e.getPos();
			ItemStack mainHandStack = player.getMainHandItem();
			if (mainHandStack.getItem() instanceof ExchangeStaffItem exchangeStaffItem) {
				BlockState targetBlockState = level.getBlockState(pos);
				if (exchangeStaffItem.canInteractWith(targetBlockState, level, pos, player)) {
					Optional<Block> storedBlock = ExchangeStaffItem.getStoredBlock(player.getMainHandItem());

					if (storedBlock.isPresent()
							&& storedBlock.get() != targetBlockState.getBlock()
							&& storedBlock.get().asItem() != Items.AIR
							&& ExchangeStaffItem.exchange(level, pos, player, storedBlock.get(), player.getMainHandItem(), true, e.getFace())) {

						e.setCancellationResult(InteractionResult.SUCCESS);
						return;
					}
				}
				level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
				e.setCancellationResult(InteractionResult.FAIL);
			}
		}
	}

	@SubscribeEvent
	public void onTagsUpdated(TagsUpdatedEvent e) {
		if (e.getUpdateCause().equals(TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED)) {
			SpectrumColorProviders.resetToggleableProviders();
			SpectrumMultiblocks.register();
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent e) {
		if (e.getPlayer() instanceof ServerPlayer serverPlayerEntity) {
			SpectrumAdvancementCriteria.BLOCK_BROKEN.trigger(serverPlayerEntity, e.getState());
		}
	}

	@SubscribeEvent
	public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract e) {
		ItemStack handStack = e.getEntity().getItemInHand(e.getHand());
		if (handStack.getItem() instanceof PrioritizedEntityInteraction && e.getTarget() instanceof LivingEntity livingEntity) {
			e.setCancellationResult(handStack.interactLivingEntity(e.getEntity(), livingEntity, e.getHand()));
		}
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		ItemStack handStack = e.getEntity().getItemInHand(e.getHand());
		if (handStack.getItem() instanceof PrioritizedBlockInteraction) {
			e.setCancellationResult(handStack.useOn(new UseOnContext(e.getEntity(), e.getHand(), e.getHitVec())));
		}
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent e) {
		SpectrumCommon.logInfo("Fetching server instance...");
		SpectrumCommon.minecraftServer = e.getServer();

		logInfo("Registering MultiBlocks...");
		SpectrumMultiblocks.register();
	}

	@SubscribeEvent
	public void onServerStopped(ServerStoppedEvent e) {
		Pastel.clearServerInstance();
		SpectrumCommon.minecraftServer = null;
	}

	@SubscribeEvent
	public void onTickServerTick(TickEvent.ServerTickEvent e) {
		if (e.phase.equals(TickEvent.Phase.END)) {
			Pastel.getServerInstance().tick();
		}
	}

	@SubscribeEvent
	public void onTickLevelTick(TickEvent.LevelTickEvent e) {
		// these would actually be nicer to have as Spawners in ServerWorld
		// to have them run in tickSpawners()
		// but getting them in there would require some ugly mixins

		Level level = e.level;
		if (level.getGameTime() % 100 == 0) {
			if (TimeHelper.getTimeOfDay(level).isNight()) { // 90 chances in a night
				if (SpectrumCommon.CONFIG.ShootingStarWorlds.contains(level.dimension().location().toString())) {
					ShootingStarSpawner.INSTANCE.tick((ServerLevel) level, true, true);
				}
			}

				/* TODO: Monstrosity
				if (world.getRegistryKey().equals(SpectrumDimensions.DIMENSION_KEY)) {
					MonstrositySpawner.INSTANCE.spawn(world, true, true);
				}*/
		}
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent e) {
		SpectrumCommon.logInfo("Querying fluid luminance...");
		MinecraftServer server = e.getServer();
		for (Iterator<Block> it = BuiltInRegistries.BLOCK.stream().iterator(); it.hasNext(); ) {
			Block block = it.next();
			if (block instanceof LiquidBlock fluidBlock) {
				fluidLuminance.put(fluidBlock.getFluidState(fluidBlock.defaultBlockState()).getType(), fluidBlock.defaultBlockState().getLightEmission());
			}
		}

		SpectrumCommon.logInfo("Injecting additional recipes...");
		FirestarterIdolBlock.addBlockSmeltingRecipes(server);
		injectEnchantmentUpgradeRecipes(server);
	}

	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent e) {
		// If the player wears a Whispy Circlet and sleeps
		// they get fully healed and all negative status effects removed
		// When the sleep timer reached 100 the player is fully asleep
		Player player = e.getEntity();
		if (player instanceof ServerPlayer serverPlayerEntity
				&& serverPlayerEntity.getSleepTimer() == 100
				&& SpectrumTrinketItem.hasEquipped(player, SpectrumItems.WHISPY_CIRCLET)) {

			player.setHealth(player.getMaxHealth());
			WhispyCircletItem.removeNegativeStatusEffects(player);
		}
	}

	@SubscribeEvent
	public void onLivingEquipmentChange(LivingEquipmentChangeEvent e) {
		var oldInexorable = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INEXORABLE, e.getFrom());
		var newInexorable = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INEXORABLE, e.getTo());

		var effectType = e.getSlot() == EquipmentSlot.CHEST ? SpectrumAttributeTags.INEXORABLE_ARMOR_EFFECTIVE : SpectrumAttributeTags.INEXORABLE_HANDHELD_EFFECTIVE;

		if (oldInexorable > 0 && newInexorable <= 0) {
			LivingEntity livingEntity = e.getEntity();
			livingEntity.getActiveEffects()
					.stream()
					.filter(instance -> {
						var statusEffect = instance.getEffect();
						var attributes = statusEffect.getAttributeModifiers().keySet();
						return attributes.stream()
								.anyMatch(attribute -> {
									var attributeRegistryOptional = BuiltInRegistries.ATTRIBUTE.getTag(effectType);

									return attributeRegistryOptional.map(registryEntries -> registryEntries
											.stream()
											.map(Holder::value)
											.anyMatch(entityAttribute -> {

												if (!statusEffect.getAttributeModifiers().containsKey(entityAttribute))
													return false;

												var value = statusEffect.getAttributeModifiers().get(entityAttribute).getAmount();
												return value < 0;

											})).orElse(false);

								});
					})
					.forEach(instance -> instance.getEffect().addAttributeModifiers(livingEntity, livingEntity.getAttributes(), instance.getAmplifier()));
		}
	}

	@SubscribeEvent
	public void onItemAttributeModifier(ItemAttributeModifierEvent e) {
		if (e.getSlotType() == EquipmentSlot.MAINHAND) {
			int tightGripLevel = e.getItemStack().getEnchantmentLevel(SpectrumEnchantments.TIGHT_GRIP);
			if (tightGripLevel > 0) {
				float attackSpeedBonus = tightGripLevel * SpectrumCommon.CONFIG.TightGripAttackSpeedBonusPercentPerLevel;
				AttributeModifier mod = new AttributeModifier(UUID.fromString("b09d9b57-eefb-4499-9150-5d8d3e644a40"), "Tight Grip modifier", attackSpeedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
				e.addModifier(Attributes.ATTACK_SPEED, mod);
			}
		}
	}

	// It could have been so much easier and performant, but KubeJS overrides the ENTIRE recipe manager
	// and cancels all sorts of functions at HEAD unconditionally, so Spectrum can not mixin into it
	public void injectEnchantmentUpgradeRecipes(MinecraftServer minecraftServer) {
		if (!EnchantmentUpgradeRecipeSerializer.enchantmentUpgradeRecipesToInject.isEmpty()) {
			ImmutableMap<ResourceLocation, Recipe<?>> collectedRecipes = EnchantmentUpgradeRecipeSerializer.enchantmentUpgradeRecipesToInject.stream().collect(ImmutableMap.toImmutableMap(EnchantmentUpgradeRecipe::getId, enchantmentUpgradeRecipe -> enchantmentUpgradeRecipe));
			Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = ((RecipeManagerAccessor) minecraftServer.getRecipeManager()).getRecipes();
			
			ArrayList<Recipe<?>> newList = new ArrayList<>();
			for (Map<ResourceLocation, Recipe<?>> r : recipes.values()) {
				newList.addAll(r.values());
			}
			for (Recipe<?> recipe : collectedRecipes.values()) {
				if (!newList.contains(recipe)) {
					newList.add(recipe);
				}
			}
			
			minecraftServer.getRecipeManager().replaceRecipes(newList);
		}
	}
	
}
