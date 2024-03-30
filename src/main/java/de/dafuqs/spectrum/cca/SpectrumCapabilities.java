package de.dafuqs.spectrum.cca;

import de.dafuqs.spectrum.cca.azure_dike.AzureDikeCapability;
import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.cca.azure_dike.DefaultAzureDikeCapability;
import de.dafuqs.spectrum.cca.everpromise_ribbon.DefaultEverpromiseRibbonCapability;
import de.dafuqs.spectrum.cca.hardcore_death.HardcoreDeathComponent;
import de.dafuqs.spectrum.cca.last_kill.LastKillComponent;
import de.dafuqs.spectrum.cca.on_primordial_fire.OnPrimordialFireComponent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpectrumCapabilities { //todoforge caps :(
	
	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(LivingEntity.class, AzureDikeProvider.AZURE_DIKE_COMPONENT, DefaultAzureDikeCapability::new);
		registry.registerForPlayers(AzureDikeProvider.AZURE_DIKE_COMPONENT, DefaultAzureDikeCapability::new, RespawnCopyStrategy.ALWAYS_COPY);
		
		registry.registerFor(LivingEntity.class, DefaultEverpromiseRibbonCapability.EVERPROMISE_RIBBON_COMPONENT, DefaultEverpromiseRibbonCapability::new);
		
		registry.registerFor(LivingEntity.class, LastKillComponent.LAST_KILL_COMPONENT, LastKillComponent::new);
		registry.registerForPlayers(LastKillComponent.LAST_KILL_COMPONENT, LastKillComponent::new, RespawnCopyStrategy.NEVER_COPY);
		
		registry.registerFor(LivingEntity.class, OnPrimordialFireComponent.ON_PRIMORDIAL_FIRE_COMPONENT, OnPrimordialFireComponent::new);
		registry.registerForPlayers(OnPrimordialFireComponent.ON_PRIMORDIAL_FIRE_COMPONENT, OnPrimordialFireComponent::new, RespawnCopyStrategy.NEVER_COPY);
	}
	
	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(HardcoreDeathComponent.HARDCORE_DEATHS_COMPONENT, e -> new HardcoreDeathComponent());
	}

	@SubscribeEvent
	public void attachEntityCaps(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof LivingEntity) {
			e.addCapability(AzureDikeCapability.AZURE_DIKE_ID, new ICapabilitySerializable<CompoundTag>() {
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
	public void attachLevelCaps(AttachCapabilitiesEvent<Level> e) {
		e.addCapability();
	}
}
