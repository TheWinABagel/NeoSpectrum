package de.dafuqs.spectrum.registries.client;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;
import de.dafuqs.spectrum.api.entity.PlayerEntityAccessor;
import de.dafuqs.spectrum.api.item.ActivatableItem;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.api.item.SlotReservingItem;
import de.dafuqs.spectrum.blocks.present.PresentBlock;
import de.dafuqs.spectrum.helpers.NullableDyeColor;
import de.dafuqs.spectrum.items.MysteriousLocketItem;
import de.dafuqs.spectrum.items.StructureCompassItem;
import de.dafuqs.spectrum.items.energy.InkFlaskItem;
import de.dafuqs.spectrum.items.magic_items.PaintbrushItem;
import de.dafuqs.spectrum.items.magic_items.PipeBombItem;
import de.dafuqs.spectrum.items.tools.MalachiteCrossbowItem;
import de.dafuqs.spectrum.items.tools.SpectrumFishingRodItem;
import de.dafuqs.spectrum.items.trinkets.AshenCircletItem;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.Locale;
import java.util.Optional;

// Vanilla models see: ModelPredicateProviderRegistry
public class SpectrumModelPredicateProviders {
	
	public static ItemDisplayContext currentItemRenderMode;
	
	public static void registerClient() {
		registerBowPredicates(SpectrumItems.BEDROCK_BOW);
		registerCrossbowPredicates(SpectrumItems.BEDROCK_CROSSBOW);
		registerSpectrumFishingRodItemPredicates(SpectrumItems.LAGOON_ROD);
		registerSpectrumFishingRodItemPredicates(SpectrumItems.MOLTEN_ROD);
		registerSpectrumFishingRodItemPredicates(SpectrumItems.BEDROCK_FISHING_ROD);
		registerEnderSplicePredicates(SpectrumItems.ENDER_SPLICE);
		registerAnimatedWandPredicates(SpectrumItems.NATURES_STAFF);
		registerAnimatedWandPredicates(SpectrumItems.RADIANCE_STAFF);
		registerAnimatedWandPredicates(SpectrumItems.STAFF_OF_REMEMBRANCE);
		registerKnowledgeDropPredicates(SpectrumItems.KNOWLEDGE_GEM);
		registerAshenCircletPredicates(SpectrumItems.ASHEN_CIRCLET);
		registerColorPredicate(SpectrumItems.PAINTBRUSH);
		registerInkColorPredicate(SpectrumItems.INK_FLASK);
		registerInkFillStateItemPredicate(SpectrumItems.INK_FLASK);
		registerMoonPhasePredicates(SpectrumItems.CRESCENT_CLOCK);
		registerActivatableItemPredicate(SpectrumItems.DREAMFLAYER);
		registerOversizedItemPredicate(SpectrumItems.DREAMFLAYER);

		registerOversizedItemPredicate(SpectrumItems.DRACONIC_TWINSWORD);
		registerOversizedItemPredicate(SpectrumItems.DRAGON_TALON);
		registerSlotReservingItem(SpectrumItems.DRAGON_TALON);
		registerSlotReservingItem(SpectrumItems.DRACONIC_TWINSWORD);

		registerOversizedItemPredicate(SpectrumItems.MALACHITE_WORKSTAFF);
		registerOversizedItemPredicate(SpectrumItems.MALACHITE_ULTRA_GREATSWORD);
		registerOversizedItemPredicate(SpectrumItems.MALACHITE_CROSSBOW);
		registerOversizedItemPredicate(SpectrumItems.MALACHITE_BIDENT);
		registerOversizedItemPredicate(SpectrumItems.GLASS_CREST_WORKSTAFF);
		registerOversizedItemPredicate(SpectrumItems.GLASS_CREST_ULTRA_GREATSWORD);
		registerOversizedItemPredicate(SpectrumItems.GLASS_CREST_CROSSBOW);
		registerOversizedItemPredicate(SpectrumItems.FEROCIOUS_GLASS_CREST_BIDENT);
		registerOversizedItemPredicate(SpectrumItems.FRACTAL_GLASS_CREST_BIDENT);
		
		registerBidentThrowingItemPredicate(SpectrumItems.MALACHITE_BIDENT);
		registerBidentThrowingItemPredicate(SpectrumItems.FEROCIOUS_GLASS_CREST_BIDENT);
		registerBidentThrowingItemPredicate(SpectrumItems.FRACTAL_GLASS_CREST_BIDENT);
		
		registerMalachiteCrossbowPredicates(SpectrumItems.MALACHITE_CROSSBOW);
		registerMalachiteCrossbowPredicates(SpectrumItems.GLASS_CREST_CROSSBOW);
		
		registerBottomlessBundlePredicates(SpectrumItems.BOTTOMLESS_BUNDLE);
		registerEnchantmentCanvasPrediates(SpectrumItems.ENCHANTMENT_CANVAS);
		registerPresentPredicates(SpectrumBlocks.PRESENT.asItem());
		registerMysteriousLocketPredicates(SpectrumItems.MYSTERIOUS_LOCKET);
		registerStructureCompassPredicates(SpectrumItems.MYSTERIOUS_COMPASS);
		registerNullableDyeColorPredicate(SpectrumBlocks.CRYSTALLARIEUM.asItem());

		registerPipeBombPredicates(SpectrumItems.PIPE_BOMB);
	}
	
	private static void registerNullableDyeColorPredicate(Item item) {
		ItemProperties.register(item, new ResourceLocation("color"), (itemStack, clientWorld, livingEntity, i) -> {
			NullableDyeColor color = NullableDyeColor.get(itemStack.getTag());
			return color.getId() / 16F;
		});
	}
	
	private static void registerMysteriousLocketPredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("socketed"), (itemStack, clientWorld, livingEntity, i) -> MysteriousLocketItem.isSocketed(itemStack) ? 1.0F : 0.0F);
	}
	
	private static void registerStructureCompassPredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("angle"), new CompassItemPropertyFunction((world, stack, entity) -> StructureCompassItem.getStructurePos(stack)));
	}
	
	private static void registerMalachiteCrossbowPredicates(Item crossbowItem) {
		ItemProperties.register(crossbowItem, new ResourceLocation("pull"), (itemStack, clientWorld, livingEntity, i) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				return CrossbowItem.isCharged(itemStack) ? 0.0F : (float) (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(itemStack);
			}
		});
		ItemProperties.register(crossbowItem, new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity, i) ->
				livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F
		);
		ItemProperties.register(crossbowItem, new ResourceLocation("charged"), (itemStack, clientWorld, livingEntity, i) ->
				livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F
		);
		ItemProperties.register(crossbowItem, new ResourceLocation("projectile"), (itemStack, world, entity, seed) -> {
			if (itemStack == null) {
				return 0F;
			}
			ItemStack projectile = MalachiteCrossbowItem.getFirstProjectile(itemStack);
			if(projectile.isEmpty()) {
				return 0F;
			}
			
			// Well, this is awkward
			if (projectile.is(Items.FIREWORK_ROCKET)) {
				return 0.1F;
			} else if (projectile.is(SpectrumItems.MALACHITE_GLASS_ARROW)) {
				return 0.2F;
			} else if (projectile.is(SpectrumItems.TOPAZ_GLASS_ARROW)) {
				return 0.3F;
			} else if (projectile.is(SpectrumItems.AMETHYST_GLASS_ARROW)) {
				return 0.4F;
			} else if (projectile.is(SpectrumItems.CITRINE_GLASS_ARROW)) {
				return 0.5F;
			} else if (projectile.is(SpectrumItems.ONYX_GLASS_ARROW)) {
				return 0.6F;
			} else if (projectile.is(SpectrumItems.MOONSTONE_GLASS_ARROW)) {
				return 0.7F;
			}
			return 0F;
		});
	}
	
	/**
	 * 0.0: not throwing
	 * 0.5: throwing in hand
	 * 1.0: as projectile
	 */
	private static void registerBidentThrowingItemPredicate(Item item) {
		ItemProperties.register(item, new ResourceLocation("bident_throwing"), (itemStack, clientWorld, livingEntity, i) -> {
			if (currentItemRenderMode == ItemDisplayContext.NONE) {
				return 1.0F;
			}
			return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 0.5F : 0.0F;
		});
	}
	
	private static void registerColorPredicate(Item item) {
		ItemProperties.register(item, new ResourceLocation("color"), (itemStack, clientWorld, livingEntity, i) -> {
			Optional<InkColor> color = PaintbrushItem.getColor(itemStack);
			return color.map(inkColor -> (1F + inkColor.getDyeColor().getId()) / 100F).orElse(0.0F);
		});
	}
	
	private static void registerPresentPredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("variant"), (itemStack, clientWorld, livingEntity, i) -> {
			CompoundTag compound = itemStack.getTag();
			if (compound == null || !compound.contains("Variant", Tag.TAG_STRING))
				return 0.0F;
			
			PresentBlock.WrappingPaper wrappingPaper = PresentBlock.WrappingPaper.valueOf(compound.getString("Variant").toUpperCase(Locale.ROOT));
			return wrappingPaper.ordinal() / 10F;
		});
	}
	
	private static void registerBottomlessBundlePredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("locked"), (itemStack, clientWorld, livingEntity, i) -> {
			CompoundTag compound = itemStack.getTag();
			if (compound == null)
				return 0.0F;
			return compound.contains("Locked") ? 1.0F : 0.0F;
		});
		ItemProperties.register(SpectrumItems.BOTTOMLESS_BUNDLE, new ResourceLocation("filled"), (itemStack, clientWorld, livingEntity, i) -> {
			CompoundTag compound = itemStack.getTag();
			if (compound == null)
				return 0.0F;
			return compound.contains("StoredStack") ? 1.0F : 0.0F;
		});
	}
	
	private static void registerMoonPhasePredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("phase"), (itemStack, clientWorld, livingEntity, i) -> {
			Entity entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();
			if (entity == null) {
				return 0.0F;
			} else {
				Level world = entity.level();
				if (clientWorld == null && world instanceof ClientLevel) {
					clientWorld = (ClientLevel) world;
				}
				
				if (clientWorld == null) {
					return 0.0F;
				} else if (!clientWorld.dimensionType().natural()) {
					return 1.0F;
				} else {
					return clientWorld.getMoonPhase() / 8F;
				}
			}
		});
	}
	
	private static void registerActivatableItemPredicate(Item item) {
		ItemProperties.register(item, new ResourceLocation(ActivatableItem.NBT_STRING), (itemStack, clientWorld, livingEntity, i) -> {
			if (ActivatableItem.isActivated(itemStack)) {
				return 1.0F;
			} else {
				return 0.0F;
			}
		});
	}

	private static void registerSlotReservingItem(Item item) {
		ItemProperties.register(item, new ResourceLocation(SlotReservingItem.NBT_STRING), (itemStack, clientWorld, livingEntity, i) -> {
			if (itemStack.getItem() instanceof SlotReservingItem reserver && reserver.isReservingSlot(itemStack)) {
				return 1.0F;
			} else {
				return 0.0F;
			}
		});
	}
	
	private static void registerOversizedItemPredicate(Item item) {
		ItemProperties.register(item, new ResourceLocation("in_world"), (itemStack, world, livingEntity, i) -> {
			if (world == null && livingEntity == null && i == 0) { // REIs 'fast batch' render mode. Without mixin' into REI there is no better way to catch this, I am afraid
				return 0.0F;
			}
			return currentItemRenderMode == ItemDisplayContext.GUI || currentItemRenderMode == ItemDisplayContext.GROUND || currentItemRenderMode == ItemDisplayContext.FIXED ? 0.0F : 1.0F;
		});
	}
	
	private static void registerBowPredicates(Item bowItem) {
		ItemProperties.register(bowItem, new ResourceLocation("pull"), (itemStack, world, livingEntity, i) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				return livingEntity.getUseItem() != itemStack ? 0.0F : (float) (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
			}
		});
		ItemProperties.register(bowItem, new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
	}
	
	private static void registerCrossbowPredicates(Item crossbowItem) {
		ItemProperties.register(crossbowItem, new ResourceLocation("pull"), (itemStack, clientWorld, livingEntity, i) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				return CrossbowItem.isCharged(itemStack) ? 0.0F : (float) (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(itemStack);
			}
		});
		
		ItemProperties.register(crossbowItem, new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity, i) ->
				livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F
		);
		
		ItemProperties.register(crossbowItem, new ResourceLocation("charged"), (itemStack, clientWorld, livingEntity, i) ->
				livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F
		);
		
		ItemProperties.register(crossbowItem, new ResourceLocation("firework"), (itemStack, clientWorld, livingEntity, i) ->
				livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
		);
	}

	private static void registerPipeBombPredicates(Item pipeBombItem) {
		ItemProperties.register(pipeBombItem, new ResourceLocation("armed"), PipeBombItem::isArmed);
	}
	
	private static void registerSpectrumFishingRodItemPredicates(Item fishingRodItem) {
		ItemProperties.register(fishingRodItem, new ResourceLocation("cast"), (itemStack, clientWorld, livingEntity, i) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				boolean isInMainHand = livingEntity.getMainHandItem() == itemStack;
				boolean isInOffhand = livingEntity.getOffhandItem() == itemStack;
				if (livingEntity.getMainHandItem().getItem() instanceof SpectrumFishingRodItem) {
					isInOffhand = false;
				}
				return (isInMainHand || isInOffhand) && livingEntity instanceof Player && ((PlayerEntityAccessor) livingEntity).getSpectrumBobber() != null ? 1.0F : 0.0F;
			}
		});
	}
	
	private static void registerEnderSplicePredicates(Item enderSpliceItem) {
		ItemProperties.register(enderSpliceItem, new ResourceLocation("bound"), (itemStack, clientWorld, livingEntity, i) -> {
			CompoundTag compoundTag = itemStack.getTag();
			if (compoundTag != null && (compoundTag.contains("PosX") || compoundTag.contains("TargetPlayerUUID"))) {
				return 1.0F;
			} else {
				return 0.0F;
			}
		});
	}
	
	private static void registerAshenCircletPredicates(Item ashenCircletItem) {
		ItemProperties.register(ashenCircletItem, new ResourceLocation("cooldown"), (itemStack, clientWorld, livingEntity, i) -> {
			if (livingEntity != null && AshenCircletItem.getCooldownTicks(itemStack, livingEntity.level()) == 0) {
				return 0.0F;
			} else {
				return 1.0F;
			}
		});
	}
	
	private static void registerAnimatedWandPredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("in_use"), (itemStack, clientWorld, livingEntity, i) ->
				(livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack) ? 1.0F : 0.0F
		);
	}
	
	private static void registerKnowledgeDropPredicates(Item item) {
		ItemProperties.register(item, new ResourceLocation("stored_experience_10000"), (itemStack, clientWorld, livingEntity, i) -> {
			if (SpectrumItems.KNOWLEDGE_GEM instanceof ExperienceStorageItem) {
				return ExperienceStorageItem.getStoredExperience(itemStack) / 10000F;
			} else {
				return 0;
			}
		});
	}
	
	private static void registerInkColorPredicate(InkFlaskItem item) {
		ItemProperties.register(SpectrumItems.INK_FLASK, new ResourceLocation("color"), (itemStack, clientWorld, livingEntity, i) -> {
			SingleInkStorage storage = SpectrumItems.INK_FLASK.getEnergyStorage(itemStack);
			InkColor color = storage.getStoredColor();
			return (1F + color.getDyeColor().getId()) / 100F;
		});
	}
	
	private static void registerInkFillStateItemPredicate(InkFlaskItem item) {
		ItemProperties.register(SpectrumItems.INK_FLASK, new ResourceLocation("fill_state"), (itemStack, world, livingEntity, i) -> {
			SingleInkStorage storage = SpectrumItems.INK_FLASK.getEnergyStorage(itemStack);
			long current = storage.getCurrentTotal();
			if (current == 0) {
				return 0.0F;
			} else {
				long max = storage.getMaxTotal();
				return (float) Math.max(0.01, (double) current / (double) max);
			}
		});
	}
	
	private static void registerEnchantmentCanvasPrediates(Item item) {
		ItemProperties.register(item, new ResourceLocation("bound"), (itemStack, world, livingEntity, i) -> {
			CompoundTag nbt = itemStack.getTag();
			if (nbt != null && nbt.contains("BoundItem", Tag.TAG_STRING)) {
				return 1;
			}
			return 0;
		});
	}
	
}
