package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.FixedSingleInkStorage;
import de.dafuqs.spectrum.api.render.ExtendedItemBars;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkDrainTrinketItem extends SpectrumTrinketItem implements InkStorageItem<FixedSingleInkStorage>, ExtendedItemBars {
	
	/**
	 * TODO: set to the original value again, once ink networking is in. Currently the original max value cannot be achieved.
	 * Players WILL grind out that amount of pigment in some way and will then complain
	 * <p>
	 * lmao trueee ~ Azzyypaaras.
	 */
	public static final int MAX_INK = 3276800; // 1677721600;
	public final InkColor inkColor;
	
	public InkDrainTrinketItem(Properties settings, ResourceLocation unlockIdentifier, InkColor inkColor) {
		super(settings, unlockIdentifier);
		this.inkColor = inkColor;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		
		FixedSingleInkStorage inkStorage = getEnergyStorage(stack);
		long storedInk = inkStorage.getEnergy(inkStorage.getStoredColor());
		
		if (storedInk >= MAX_INK) {
			tooltip.add(Component.translatable("spectrum.tooltip.ink_drain.tooltip.maxed_out").withStyle(ChatFormatting.GRAY));
		} else {
			long nextStepInk;
			int pow = 0;
			do {
				nextStepInk = (long) (100 * Math.pow(8, pow));
				pow++;
			} while (storedInk >= nextStepInk);
			
			tooltip.add(Component.translatable("spectrum.tooltip.ink_drain.tooltip.ink_for_next_step." + inkStorage.getStoredColor().toString(), Support.getShortenedNumberString(nextStepInk - storedInk)).withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return isMaxedOut(stack);
	}
	
	private boolean isMaxedOut(ItemStack stack) {
		FixedSingleInkStorage inkStorage = getEnergyStorage(stack);
		long storedInk = inkStorage.getEnergy(inkStorage.getStoredColor());
		return storedInk >= MAX_INK;
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return isMaxedOut(stack) ? Rarity.EPIC : super.getRarity(stack);
	}
	
	// Omitting this would crash outside the dev env o.O
	@Override
	public ItemStack getDefaultInstance() {
		return super.getDefaultInstance();
	}
	
	@Override
	public Drainability getDrainability() {
		return Drainability.NEVER;
	}
	
	@Override
	public FixedSingleInkStorage getEnergyStorage(ItemStack itemStack) {
		CompoundTag compound = itemStack.getTag();
		if (compound != null && compound.contains("EnergyStore")) {
			return FixedSingleInkStorage.fromNbt(compound.getCompound("EnergyStore"));
		}
		return new FixedSingleInkStorage(MAX_INK, inkColor);
	}
	
	@Override
	public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
		if (storage instanceof FixedSingleInkStorage fixedSingleInkStorage) {
			CompoundTag compound = itemStack.getOrCreateTag();
			compound.put("EnergyStore", fixedSingleInkStorage.toNbt());
		}
	}
	
	@Override
	public ItemStack getFullStack() {
		return InkStorageItem.super.getFullStack();
	}

	@Override
	public int barCount(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean allowVanillaDurabilityBarRendering(@Nullable Player player, ItemStack stack) {
		return false;
	}

	@Override
	public BarSignature getSignature(@Nullable Player player, @NotNull ItemStack stack, int index) {
		var inkTank = getEnergyStorage(stack);
		var progress = (int) Math.round(Mth.clampedLerp(0, 13, Math.log(inkTank.getEnergy(inkColor) / 100.0f) / Math.log(8) / 5.0F));

		if (progress == 0)
			return PASS;

		return new BarSignature(2, 13, 13, progress, 1, ColorHelper.colorVecToRGB(inkColor.getColor()), 2, ExtendedItemBars.DEFAULT_BACKGROUND_COLOR);
	}
}