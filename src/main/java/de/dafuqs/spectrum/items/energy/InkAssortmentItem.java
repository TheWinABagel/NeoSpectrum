package de.dafuqs.spectrum.items.energy;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.storage.IndividualCappedInkStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkAssortmentItem extends Item implements InkStorageItem<IndividualCappedInkStorage> {
	
	private final long maxEnergy;
	
	public InkAssortmentItem(Properties settings, long maxEnergy) {
		super(settings);
		this.maxEnergy = maxEnergy;
	}
	
	@Override
	public Drainability getDrainability() {
		return Drainability.ALWAYS;
	}
	
	@Override
	public IndividualCappedInkStorage getEnergyStorage(ItemStack itemStack) {
		CompoundTag compound = itemStack.getTag();
		if (compound != null && compound.contains("EnergyStore")) {
			return IndividualCappedInkStorage.fromNbt(compound.getCompound("EnergyStore"));
		}
		return new IndividualCappedInkStorage(this.maxEnergy);
	}
	
	// Omitting this would crash outside the dev env o.O
	@Override
	public ItemStack getDefaultInstance() {
		return super.getDefaultInstance();
	}
	
	@Override
	public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
		if (storage instanceof IndividualCappedInkStorage individualCappedInkStorage) {
			CompoundTag compound = itemStack.getOrCreateTag();
			compound.put("EnergyStore", individualCappedInkStorage.toNbt());
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		getEnergyStorage(stack).addTooltip(tooltip, true);
	}
	
}