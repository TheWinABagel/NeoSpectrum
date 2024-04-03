package de.dafuqs.spectrum.registries;

import net.minecraft.world.level.block.state.properties.WoodType;

import static de.dafuqs.spectrum.SpectrumCommon.locate;

public class SpectrumWoodTypes {
	public static final WoodType SLATE_NOXWOOD = WoodType.register(new WoodType(locate("slate_noxwood").toString(), SpectrumBlockSetTypes.NOXWOOD));
	public static final WoodType EBONY_NOXWOOD = WoodType.register(new WoodType(locate("ebony_noxwood").toString(), SpectrumBlockSetTypes.NOXWOOD));
	public static final WoodType IVORY_NOXWOOD = WoodType.register(new WoodType(locate("ivory_noxwood").toString(), SpectrumBlockSetTypes.NOXWOOD));
	public static final WoodType CHESTNUT_NOXWOOD = WoodType.register(new WoodType(locate("chestnut_noxwood").toString(), SpectrumBlockSetTypes.NOXWOOD));
	public static final WoodType COLORED_WOOD = WoodType.register(new WoodType(locate("colored_wood").toString(), SpectrumBlockSetTypes.COLORED_WOOD));
}
