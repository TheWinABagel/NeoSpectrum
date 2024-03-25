package de.dafuqs.spectrum.explosion;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum ExplosionArchetype {
	COSMETIC("cosmetic", false, false),
	DESTROY_BLOCKS("destroy_blocks", true, false),
	DAMAGE_ENTITIES("damage_entities", false, true),
	ALL("all", true, true);
	
	private final Component name;
	public final boolean affectsBlocks;
	public final boolean affectsEntities;
	
	ExplosionArchetype(String id, boolean affectsBlocks, boolean affectsEntities) {
		this.affectsBlocks = affectsBlocks;
		this.affectsEntities = affectsEntities;
		this.name = Component.translatable("explosion_archetype.spectrum." + id);
	}
	
	public static ExplosionArchetype tryParse(String name) {
		try {
			return ExplosionArchetype.valueOf(name.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			return COSMETIC;
		}
	}
	
	public static ExplosionArchetype get(boolean affectsBlocks, boolean affectsEntities) {
		if (affectsBlocks) {
			return affectsEntities ? ALL : DESTROY_BLOCKS;
		}
		return affectsEntities ? DAMAGE_ENTITIES : COSMETIC;
	}
	
	public Component getName() {
		return name;
	}
	
}
