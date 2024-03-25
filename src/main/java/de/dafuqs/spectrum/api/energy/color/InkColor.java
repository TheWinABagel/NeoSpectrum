package de.dafuqs.spectrum.api.energy.color;

import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3f;

import java.util.*;

public abstract class InkColor {
	
	protected static final Map<DyeColor, InkColor> DYE_TO_COLOR = new HashMap<>();
	protected static final List<InkColor> ALL_COLORS = new ArrayList<>();
	protected static final List<ElementalColor> ELEMENTAL_COLORS = new ArrayList<>();
	
	protected final DyeColor dyeColor;
	protected final Vector3f color;
	
	protected final ResourceLocation requiredAdvancement;
	
	protected InkColor(DyeColor dyeColor, Vector3f color, ResourceLocation requiredAdvancement) {
		this.dyeColor = dyeColor;
		this.color = color;
		this.requiredAdvancement = requiredAdvancement;
		
		ALL_COLORS.add(this);
		DYE_TO_COLOR.put(dyeColor, this);
	}
	
	public static InkColor of(DyeColor dyeColor) {
		return DYE_TO_COLOR.get(dyeColor);
	}
	
	public static InkColor of(String colorString) {
		return DYE_TO_COLOR.get(DyeColor.valueOf(colorString.toUpperCase(Locale.ROOT)));
	}
	
	public static List<InkColor> all() {
		return ALL_COLORS;
	}
	
	public static List<ElementalColor> elementals() {
		return ELEMENTAL_COLORS;
	}
	
	public DyeColor getDyeColor() {
		return this.dyeColor;
	}
	
	@Override
	public String toString() {
		return this.dyeColor.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InkColor that = (InkColor) o;
		return this.dyeColor.equals(that.dyeColor);
	}
	
	// hash table lookup go wheeeeee!
	@Override
	public int hashCode() {
		return dyeColor.getId();
	}
	
	public Component getName() {
		return Component.translatable("spectrum.ink.color." + this);
	}
	
	public Vector3f getColor() {
		return this.color;
	}
	
	public ResourceLocation getRequiredAdvancement() {
		return requiredAdvancement;
	}
	
	public static InkColor getRandomMixedColor(InkColor color1, InkColor color2, net.minecraft.util.RandomSource random) {
		boolean color1Elemental = color1 instanceof ElementalColor;
		boolean color2Elemental = color2 instanceof ElementalColor;

		if (color1Elemental && color2Elemental) {
			List<InkColor> possibleOutcomes = new ArrayList<>();

			for (Holder<InkColor> c : SpectrumRegistries.getEntries(SpectrumRegistries.INK_COLORS, InkColorTags.COMPOUND_COLORS)) {
				if (((CompoundColor) c.value()).isMixedUsing((ElementalColor) color1) && ((CompoundColor) c.value()).isMixedUsing((ElementalColor) color2)) {
					possibleOutcomes.add(c.value());
				}
			}

			if (!possibleOutcomes.isEmpty()) { // this should always be the case, but you never know
				Collections.shuffle(possibleOutcomes);
				return possibleOutcomes.get(0);
			}
			return color1;
		} else if (color1Elemental) {
			return color1;
		} else if (color2Elemental) {
			return color2;
		} else {
			return random.nextBoolean() ? color1 : color2;
		}
	}

}



