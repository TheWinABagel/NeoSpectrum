package de.dafuqs.spectrum.api.energy.color;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class ElementalColor extends InkColor {
	
	protected final Map<CompoundColor, Float> mixedColors = new HashMap<>(); // colors that can be mixed from this
	
	public ElementalColor(DyeColor dyeColor, Vector3f color, ResourceLocation requiredAdvancement) {
		super(dyeColor, color, requiredAdvancement);
		ELEMENTAL_COLORS.add(this);
	}
	
	public boolean isUsedForMixing(CompoundColor compoundColor) {
		return this.mixedColors.containsKey(compoundColor);
	}
	
	public void addCompoundAmount(CompoundColor compoundColor, float amount) {
		this.mixedColors.put(compoundColor, amount);
	}
	
}