package de.dafuqs.spectrum.cca;

import de.dafuqs.spectrum.cca.azure_dike.AzureDikeCapability;
import de.dafuqs.spectrum.cca.azure_dike.DefaultAzureDikeCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpectrumCapabilities { //todoforge all of this needs to be rewritten for 1.20.4 its all kinds of a mess rn

	public static void initCapEvents() {
		MinecraftForge.EVENT_BUS.register(new DefaultAzureDikeCapability());
		MinecraftForge.EVENT_BUS.register(new OnPrimordialFireComponent());
		MinecraftForge.EVENT_BUS.register(SpectrumCapabilities.class);
	}

	@SubscribeEvent
	public static void attachEntityCaps(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof LivingEntity) {
			e.addCapability(AzureDikeCapability.ID, new ICapabilitySerializable<CompoundTag>() {
				final LazyOptional<AzureDikeCapability> inst = LazyOptional.of(DefaultAzureDikeCapability::new);
				@Override
				public CompoundTag serializeNBT() {
					return inst.orElseThrow(NullPointerException::new).serializeNBT();
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					inst.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
				}

				@Override
				public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
					return DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY.orEmpty(cap, inst);
				}
			});
		}
	}

	@SubscribeEvent
	public static void attachLevelCaps(AttachCapabilitiesEvent<Level> e) {
		e.addCapability(HardcoreDeathCapability.ID, new ICapabilitySerializable<CompoundTag>() {
			final LazyOptional<HardcoreDeathCapability> inst = LazyOptional.of(HardcoreDeathCapability::new);
			@Override
			public CompoundTag serializeNBT() {
				return inst.orElseThrow(NullPointerException::new).serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundTag nbt) {
				inst.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
			}

			@Override
			public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
				return HardcoreDeathCapability.HARDCORE_DEATH_CAPABILITY.orEmpty(cap, inst);
			}
		});
	}
}
