package de.dafuqs.spectrum.items.energy;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.TotalCappedElementalInkStorage;
import de.dafuqs.spectrum.api.item.LoomPatternProvider;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumBannerPatterns;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtistsPaletteItem extends SpectrumTrinketItem implements InkStorageItem<ArtistsPaletteItem.ArtistsPaletteInkStorage>, LoomPatternProvider {
	
	private final long maxEnergyTotal;
	
	public ArtistsPaletteItem(Properties settings, long maxEnergyTotal) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/artists_palette"));
		this.maxEnergyTotal = maxEnergyTotal;
	}
	
	@Override
	public Drainability getDrainability() {
		return Drainability.PLAYER_ONLY;
	}
	
	@Override
	public ArtistsPaletteInkStorage getEnergyStorage(ItemStack itemStack) {
		CompoundTag compound = itemStack.getTag();
		if (compound != null && compound.contains("EnergyStore")) {
			return ArtistsPaletteInkStorage.fromNbt(compound.getCompound("EnergyStore"));
		}
		return new ArtistsPaletteInkStorage(this.maxEnergyTotal);
	}
	
	// Omitting this would crash outside the dev env o.O
	@Override
	public ItemStack getDefaultInstance() {
		return super.getDefaultInstance();
	}
	
	@Override
	public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
		if (storage instanceof ArtistsPaletteInkStorage artistsPaletteInkStorage) {
			CompoundTag compound = itemStack.getOrCreateTag();
			compound.put("EnergyStore", artistsPaletteInkStorage.toNbt());
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.pigment_palette.tooltip.target").withStyle(ChatFormatting.GRAY));
		getEnergyStorage(stack).addTooltip(tooltip, true);
		addBannerPatternProviderTooltip(tooltip);
	}
	
	@Override
	public Holder<BannerPattern> getPattern() {
		return SpectrumBannerPatterns.PALETTE;
	}

	public static class ArtistsPaletteInkStorage extends TotalCappedElementalInkStorage {

		public ArtistsPaletteInkStorage(long maxEnergyTotal) {
			super(maxEnergyTotal);
		}

		public ArtistsPaletteInkStorage(long maxEnergyTotal, long cyan, long magenta, long yellow, long black, long white) {
			super(maxEnergyTotal, cyan, magenta, yellow, black, white);
		}

		public static @Nullable ArtistsPaletteItem.ArtistsPaletteInkStorage fromNbt(@NotNull CompoundTag compound) {
			if (compound.contains("MaxEnergyTotal", Tag.TAG_LONG)) {
				long maxEnergyTotal = compound.getLong("MaxEnergyTotal");
				long cyan = compound.getLong("Cyan");
				long magenta = compound.getLong("Magenta");
				long yellow = compound.getLong("Yellow");
				long black = compound.getLong("Black");
				long white = compound.getLong("White");
				return new ArtistsPaletteInkStorage(maxEnergyTotal, cyan, magenta, yellow, black, white);
			}
			return null;
		}

		public long addEnergy(InkColor color, long amount, ItemStack stack, ServerPlayer serverPlayerEntity) {
			long leftoverEnergy = super.addEnergy(color, amount);
			if (leftoverEnergy != amount) {
				SpectrumAdvancementCriteria.INK_CONTAINER_INTERACTION.trigger(serverPlayerEntity, stack, this, color, amount - leftoverEnergy);
			}
			return leftoverEnergy;
		}

		public boolean requestEnergy(InkColor color, long amount, ItemStack stack, ServerPlayer serverPlayerEntity) {
			boolean success = super.requestEnergy(color, amount);
			if (success) {
				SpectrumAdvancementCriteria.INK_CONTAINER_INTERACTION.trigger(serverPlayerEntity, stack, this, color, -amount);
			}
			return success;
		}

		public long drainEnergy(InkColor color, long amount, ItemStack stack, ServerPlayer serverPlayerEntity) {
			long drainedAmount = super.drainEnergy(color, amount);
			if (drainedAmount != 0) {
				SpectrumAdvancementCriteria.INK_CONTAINER_INTERACTION.trigger(serverPlayerEntity, stack, this, color, -drainedAmount);
			}
			return drainedAmount;
		}

	}
}