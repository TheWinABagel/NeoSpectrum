package de.dafuqs.spectrum.explosion;

import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExplosionModifierProviders {
	
	protected static Map<Item, ExplosionModifier> MODIFIERS = new Object2ObjectOpenHashMap<>();
	
	public static @Nullable ExplosionModifier getModifier(ItemStack stack) {
		return MODIFIERS.getOrDefault(stack.getItem(), null);
	}
	
	public static void registerForModifier(ItemLike provider, ExplosionModifier modifier) {
		MODIFIERS.put(provider.asItem(), modifier);
	}
	
	
	protected static Map<Item, ExplosionArchetype> ARCHETYPES = new Object2ObjectOpenHashMap<>();
	
	public static @Nullable ExplosionArchetype getArchetype(ItemStack stack) {
		return ARCHETYPES.getOrDefault(stack.getItem(), null);
	}
	
	public static void registerForArchetype(ItemLike provider, ExplosionArchetype modifier) {
		ARCHETYPES.put(provider.asItem(), modifier);
	}
	
	public static Set<Item> getProviders() {
		Set<Item> set = new HashSet<>();
		set.addAll(ARCHETYPES.keySet());
		set.addAll(MODIFIERS.keySet());
		return set;
	}
	
	public static void register() {
		registerForArchetype(Items.GLOWSTONE_DUST, ExplosionArchetype.DAMAGE_ENTITIES);
		registerForArchetype(Items.GUNPOWDER, ExplosionArchetype.DESTROY_BLOCKS);
		registerForArchetype(SpectrumItems.MIDNIGHT_ABERRATION, ExplosionArchetype.ALL);
		
		registerForModifier(Items.FIRE_CHARGE, ExplosionModifiers.FIRE);
		registerForModifier(Items.TNT, ExplosionModifiers.EXPLOSION_BOOST);
		registerForModifier(SpectrumItems.STORM_STONE, ExplosionModifiers.LIGHTNING);
		registerForModifier(SpectrumItems.NEOLITH, ExplosionModifiers.MAGIC);
		registerForModifier(SpectrumBlocks.INCANDESCENT_AMALGAM, ExplosionModifiers.INCANDESCENCE);
		registerForModifier(SpectrumItems.DOOMBLOOM_SEED, ExplosionModifiers.PRIMORDIAL_FIRE);
		registerForModifier(Items.CHORUS_FRUIT, ExplosionModifiers.STARRY);
		registerForModifier(Items.END_ROD, ExplosionModifiers.KILL_ZONE);
		registerForModifier(SpectrumItems.SHIMMERSTONE_GEM, ExplosionModifiers.LIGHT);
		registerForModifier(SpectrumItems.STRATINE_FRAGMENTS, ExplosionModifiers.SHAPE_SQUARE);
		
		registerForModifier(SpectrumBlocks.FOUR_LEAF_CLOVER, ExplosionModifiers.FORTUNE);
		registerForModifier(SpectrumItems.MERMAIDS_GEM, ExplosionModifiers.SILK_TOUCH);
		registerForModifier(Items.ENDER_PEARL, ExplosionModifiers.INVENTORY_INSERTION);
	}
	
}
