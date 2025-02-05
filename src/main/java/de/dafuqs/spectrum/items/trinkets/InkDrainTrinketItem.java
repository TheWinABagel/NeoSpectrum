package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.api.render.ExtendedItemBars;
import de.dafuqs.spectrum.helpers.*;
import net.minecraft.client.item.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class InkDrainTrinketItem extends SpectrumTrinketItem implements InkStorageItem<FixedSingleInkStorage>, ExtendedItemBars {
	
	/**
	 * TODO: set to the original value again, once ink networking is in. Currently the original max value cannot be achieved.
	 * Players WILL grind out that amount of pigment in some way and will then complain
	 * <p>
	 * lmao trueee ~ Azzyypaaras.
	 */
	public static final int MAX_INK = 3276800; // 1677721600;
	public final InkColor inkColor;
	
	public InkDrainTrinketItem(Settings settings, Identifier unlockIdentifier, InkColor inkColor) {
		super(settings, unlockIdentifier);
		this.inkColor = inkColor;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		
		FixedSingleInkStorage inkStorage = getEnergyStorage(stack);
		long storedInk = inkStorage.getEnergy(inkStorage.getStoredColor());
		
		if (storedInk >= MAX_INK) {
			tooltip.add(Text.translatable("spectrum.tooltip.ink_drain.tooltip.maxed_out").formatted(Formatting.GRAY));
		} else {
			long nextStepInk;
			int pow = 0;
			do {
				nextStepInk = (long) (100 * Math.pow(8, pow));
				pow++;
			} while (storedInk >= nextStepInk);
			
			tooltip.add(Text.translatable("spectrum.tooltip.ink_drain.tooltip.ink_for_next_step." + inkStorage.getStoredColor().toString(), Support.getShortenedNumberString(nextStepInk - storedInk)).formatted(Formatting.GRAY));
		}
	}
	
	@Override
	public boolean hasGlint(ItemStack stack) {
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
	public ItemStack getDefaultStack() {
		return super.getDefaultStack();
	}
	
	@Override
	public Drainability getDrainability() {
		return Drainability.NEVER;
	}
	
	@Override
	public FixedSingleInkStorage getEnergyStorage(ItemStack itemStack) {
		NbtCompound compound = itemStack.getNbt();
		if (compound != null && compound.contains("EnergyStore")) {
			return FixedSingleInkStorage.fromNbt(compound.getCompound("EnergyStore"));
		}
		return new FixedSingleInkStorage(MAX_INK, inkColor);
	}
	
	@Override
	public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
		if (storage instanceof FixedSingleInkStorage fixedSingleInkStorage) {
			NbtCompound compound = itemStack.getOrCreateNbt();
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
	public boolean allowVanillaDurabilityBarRendering(@Nullable PlayerEntity player, ItemStack stack) {
		return false;
	}

	@Override
	public BarSignature getSignature(@Nullable PlayerEntity player, @NotNull ItemStack stack, int index) {
		var inkTank = getEnergyStorage(stack);
		var progress = (int) Math.round(MathHelper.clampedLerp(0, 13, Math.log(inkTank.getEnergy(inkColor) / 100.0f) / Math.log(8) / 5.0F));

		if (progress == 0)
			return PASS;

		return new BarSignature(2, 13, 13, progress, 1, ColorHelper.colorVecToRGB(inkColor.getColor()), 2, ExtendedItemBars.DEFAULT_BACKGROUND_COLOR);
	}
}