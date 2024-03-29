package de.dafuqs.spectrum.api.energy.storage;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.ElementalColor;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.dafuqs.spectrum.helpers.Support.getShortenedNumberString;

public class TotalCappedInkStorage implements InkStorage {
	
	protected final long maxEnergyTotal;
	protected final Map<InkColor, Long> storedEnergy;
	protected long currentTotal; // This is a cache for quick lookup. Can be recalculated anytime using the values in storedEnergy.
	
	public TotalCappedInkStorage(long maxEnergyTotal) {
		this.maxEnergyTotal = maxEnergyTotal;
		this.currentTotal = 0;
		
		this.storedEnergy = new HashMap<>();
		for (InkColor color : InkColor.all()) {
			this.storedEnergy.put(color, 0L);
		}
	}
	
	public TotalCappedInkStorage(long maxEnergyTotal, Map<InkColor, Long> colors) {
		this.maxEnergyTotal = maxEnergyTotal;

		this.currentTotal = 0;
		this.storedEnergy = colors;
		for (Map.Entry<InkColor, Long> color : colors.entrySet()) {
			this.currentTotal += color.getValue();
		}
	}
	
	public static @Nullable TotalCappedInkStorage fromNbt(@NotNull CompoundTag compound) {
		if (compound.contains("MaxEnergyTotal", Tag.TAG_LONG)) {
			long maxEnergyTotal = compound.getLong("MaxEnergyTotal");
			
			Map<InkColor, Long> colors = new HashMap<>();
			for (InkColor color : InkColor.all()) {
				colors.put(color, compound.getLong(color.toString()));
			}
			return new TotalCappedInkStorage(maxEnergyTotal, colors);
		}
		return null;
	}
	
	@Override
	public boolean accepts(InkColor color) {
		return color instanceof ElementalColor;
	}
	
	@Override
	public long addEnergy(InkColor color, long amount) {
		long overflow = Math.max(0, amount + this.currentTotal - this.maxEnergyTotal);
		long amountToAdd = amount - overflow;
		this.currentTotal += amountToAdd;
		this.storedEnergy.put(color, this.storedEnergy.get(color) + amountToAdd);
		return overflow;
	}
	
	@Override
	public boolean requestEnergy(InkColor color, long amount) {
		long storedAmount = this.storedEnergy.get(color);
		if (storedAmount < amount) {
			return false;
		} else {
			this.currentTotal -= amount;
			this.storedEnergy.put(color, storedAmount - amount);
			return true;
		}
	}
	
	@Override
	public long drainEnergy(InkColor color, long amount) {
		long storedAmount = this.storedEnergy.get(color);
		long drainedAmount = Math.min(storedAmount, amount);
		this.storedEnergy.put(color, storedAmount - drainedAmount);
		this.currentTotal -= drainedAmount;
		return drainedAmount;
	}
	
	@Override
	public long getEnergy(InkColor color) {
		return this.storedEnergy.get(color);
	}
	
	@Override
	@Deprecated
	public Map<InkColor, Long> getEnergy() {
		return this.storedEnergy;
	}
	
	@Override
	@Deprecated
	public void setEnergy(Map<InkColor, Long> colors, long total) {
		this.storedEnergy.putAll(colors);
		this.currentTotal = total;
	}
	
	@Override
	public long getMaxTotal() {
		return this.maxEnergyTotal;
	}
	
	@Override
	public long getMaxPerColor() {
		return this.maxEnergyTotal;
	}
	
	@Override
	public long getCurrentTotal() {
		return this.currentTotal;
	}
	
	@Override
	public boolean isEmpty() {
		return this.currentTotal == 0;
	}
	
	@Override
	public boolean isFull() {
		return this.currentTotal >= this.maxEnergyTotal;
	}
	
	public CompoundTag toNbt() {
		CompoundTag compound = new CompoundTag();
		compound.putLong("MaxEnergyTotal", this.maxEnergyTotal);
		for (Map.Entry<InkColor, Long> color : this.storedEnergy.entrySet()) {
			compound.putLong(color.getKey().toString(), color.getValue());
		}
		return compound;
	}
	
	@Override
	public long getRoom(InkColor color) {
		return this.maxEnergyTotal - this.currentTotal;
	}
	
	@Override
	public void fillCompletely() {
		long energyPerColor = this.maxEnergyTotal / this.storedEnergy.size();
		this.storedEnergy.replaceAll((c, v) -> energyPerColor);
		this.currentTotal = this.maxEnergyTotal;
	}
	
	@Override
	public void clear() {
		this.storedEnergy.replaceAll((c, v) -> 0L);
		this.currentTotal = 0;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addTooltip(List<Component> tooltip, boolean includeHeader) {
		if (includeHeader) {
			tooltip.add(Component.translatable("item.spectrum.total_capped_simple_pigment_energy_storage.tooltip", getShortenedNumberString(maxEnergyTotal)));
		}
		for (Map.Entry<InkColor, Long> color : this.storedEnergy.entrySet()) {
			if (color.getValue() > 0) {
				tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.bullet." + color.getKey().toString().toLowerCase(Locale.ROOT), getShortenedNumberString(color.getValue())));
			}
		}
	}
	
}