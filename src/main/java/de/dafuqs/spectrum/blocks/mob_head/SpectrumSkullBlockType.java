package de.dafuqs.spectrum.blocks.mob_head;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SkullBlock;

public enum SpectrumSkullBlockType implements SkullBlock.Type {
	// Vanilla
	AXOLOTL_BLUE(EntityType.AXOLOTL),
	AXOLOTL_BROWN(EntityType.AXOLOTL),
	AXOLOTL_CYAN(EntityType.AXOLOTL),
	AXOLOTL_GOLD(EntityType.AXOLOTL),
	AXOLOTL_LEUCISTIC(EntityType.AXOLOTL),
	BAT(EntityType.BAT),
	BEE(EntityType.BEE),
	BLAZE(EntityType.BLAZE),
	CAT(EntityType.CAT),
	CAVE_SPIDER(EntityType.CAVE_SPIDER),
	CHICKEN(EntityType.CHICKEN),
	CLOWNFISH(EntityType.TROPICAL_FISH),
	COW(EntityType.COW),
	DONKEY(EntityType.DONKEY),
	DROWNED(EntityType.DROWNED),
	ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
	ENDERMAN(EntityType.ENDERMAN),
	ENDERMITE(EntityType.ENDERMITE),
	EVOKER(EntityType.EVOKER),
	FOX(EntityType.FOX),
	FOX_ARCTIC(EntityType.FOX),
	GHAST(EntityType.GHAST),
	GLOW_SQUID(EntityType.GLOW_SQUID),
	GOAT(EntityType.GOAT),
	GUARDIAN(EntityType.GUARDIAN),
	HOGLIN(EntityType.HOGLIN),
	HORSE(EntityType.HORSE),
	HUSK(EntityType.HUSK),
	ILLUSIONER(EntityType.ILLUSIONER),
	IRON_GOLEM(EntityType.IRON_GOLEM),
	LLAMA(EntityType.LLAMA),
	MAGMA_CUBE(EntityType.MAGMA_CUBE),
	MOOSHROOM_BROWN(EntityType.MOOSHROOM),
	MOOSHROOM_RED(EntityType.MOOSHROOM),
	MULE(EntityType.MULE),
	OCELOT(EntityType.OCELOT),
	PANDA(EntityType.PANDA),
	PARROT_BLUE(EntityType.PARROT),
	PARROT_CYAN(EntityType.PARROT),
	PARROT_GRAY(EntityType.PARROT),
	PARROT_GREEN(EntityType.PARROT),
	PARROT_RED(EntityType.PARROT),
	PHANTOM(EntityType.PHANTOM),
	PIG(EntityType.PIG),
	POLAR_BEAR(EntityType.POLAR_BEAR),
	PUFFERFISH(EntityType.PUFFERFISH),
	RABBIT(EntityType.RABBIT),
	RAVAGER(EntityType.RAVAGER),
	SALMON(EntityType.SALMON),
	SHEEP_BLACK(EntityType.SHEEP),
	SHEEP_BLUE(EntityType.SHEEP),
	SHEEP_BROWN(EntityType.SHEEP),
	SHEEP_CYAN(EntityType.SHEEP),
	SHEEP_GRAY(EntityType.SHEEP),
	SHEEP_GREEN(EntityType.SHEEP),
	SHEEP_LIGHT_BLUE(EntityType.SHEEP),
	SHEEP_LIGHT_GRAY(EntityType.SHEEP),
	SHEEP_LIME(EntityType.SHEEP),
	SHEEP_MAGENTA(EntityType.SHEEP),
	SHEEP_ORANGE(EntityType.SHEEP),
	SHEEP_PINK(EntityType.SHEEP),
	SHEEP_PURPLE(EntityType.SHEEP),
	SHEEP_RED(EntityType.SHEEP),
	SHEEP_WHITE(EntityType.SHEEP),
	SHEEP_YELLOW(EntityType.SHEEP),
	SHULKER(EntityType.SHULKER),
	SHULKER_BLACK(EntityType.SHULKER),
	SHULKER_BLUE(EntityType.SHULKER),
	SHULKER_BROWN(EntityType.SHULKER),
	SHULKER_CYAN(EntityType.SHULKER),
	SHULKER_GRAY(EntityType.SHULKER),
	SHULKER_GREEN(EntityType.SHULKER),
	SHULKER_LIGHT_BLUE(EntityType.SHULKER),
	SHULKER_LIGHT_GRAY(EntityType.SHULKER),
	SHULKER_LIME(EntityType.SHULKER),
	SHULKER_MAGENTA(EntityType.SHULKER),
	SHULKER_ORANGE(EntityType.SHULKER),
	SHULKER_PINK(EntityType.SHULKER),
	SHULKER_PURPLE(EntityType.SHULKER),
	SHULKER_RED(EntityType.SHULKER),
	SHULKER_WHITE(EntityType.SHULKER),
	SHULKER_YELLOW(EntityType.SHULKER),
	SILVERFISH(EntityType.SILVERFISH),
	SLIME(EntityType.SLIME),
	SNOW_GOLEM(EntityType.SNOW_GOLEM),
	SPIDER(EntityType.SPIDER),
	SQUID(EntityType.SQUID),
	STRAY(EntityType.STRAY),
	STRIDER(EntityType.STRIDER),
	TRADER_LLAMA(EntityType.TRADER_LLAMA),
	TURTLE(EntityType.TURTLE),
	VEX(EntityType.VEX),
	VILLAGER(EntityType.VILLAGER),
	VINDICATOR(EntityType.VINDICATOR),
	WANDERING_TRADER(EntityType.WANDERING_TRADER),
	WITCH(EntityType.WITCH),
	WITHER(EntityType.WITHER),
	WOLF(EntityType.WOLF),
	ZOGLIN(EntityType.ZOGLIN),
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER),
	ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN),
	FROG_TEMPERATE(EntityType.FROG),
	FROG_WARM(EntityType.FROG),
	FROG_COLD(EntityType.FROG),
	TADPOLE(EntityType.TADPOLE),
	ALLAY(EntityType.ALLAY),
	WARDEN(EntityType.WARDEN, true),
	
	// Spectrum
	EGG_LAYING_WOOLY_PIG(SpectrumEntityTypes.EGG_LAYING_WOOLY_PIG, true),
	KINDLING(SpectrumEntityTypes.KINDLING, true),
	PRESERVATION_TURRET(SpectrumEntityTypes.PRESERVATION_TURRET, true),
	MONSTROSITY(SpectrumEntityTypes.MONSTROSITY, true),
	LIZARD(SpectrumEntityTypes.LIZARD, true),
	ERASER(SpectrumEntityTypes.ERASER, true);
	
	public final EntityType<?> entityType;
	public final SkullBlock.Type modelType;
	
	// Most mob heads render with the player head renderer using a different texture, but some use unique renderers already.
	// Somewhen in the future hopefully all of them will get their own unique head block model
	SpectrumSkullBlockType(EntityType<?> entityType) {
		this(entityType, false);
	}
	
	// if you use this constructor without crausing a crash you will also need to add that unique Renderer
	// in SpectrumSkullBlockEntityRenderer.getModels()
	SpectrumSkullBlockType(EntityType<?> entityType, boolean useUniqueRenderer) {
		this.entityType = entityType;
		this.modelType = useUniqueRenderer ? this : SkullBlock.Types.PLAYER;
	}
	
	public SkullBlock.Type getModelType() {
		return this.modelType;
	}
	
}
