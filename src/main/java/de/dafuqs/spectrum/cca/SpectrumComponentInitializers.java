package de.dafuqs.spectrum.cca;

import de.dafuqs.spectrum.cca.azure_dike.AzureDikeProvider;
import de.dafuqs.spectrum.cca.azure_dike.DefaultAzureDikeComponent;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import net.minecraft.world.entity.LivingEntity;

public class SpectrumComponentInitializers implements EntityComponentInitializer, LevelComponentInitializer {
	
	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(LivingEntity.class, AzureDikeProvider.AZURE_DIKE_COMPONENT, DefaultAzureDikeComponent::new);
		registry.registerForPlayers(AzureDikeProvider.AZURE_DIKE_COMPONENT, DefaultAzureDikeComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		
		registry.registerFor(LivingEntity.class, EverpromiseRibbonComponent.EVERPROMISE_RIBBON_COMPONENT, EverpromiseRibbonComponent::new);
		
		registry.registerFor(LivingEntity.class, LastKillComponent.LAST_KILL_COMPONENT, LastKillComponent::new);
		registry.registerForPlayers(LastKillComponent.LAST_KILL_COMPONENT, LastKillComponent::new, RespawnCopyStrategy.NEVER_COPY);
		
		registry.registerFor(LivingEntity.class, OnPrimordialFireComponent.ON_PRIMORDIAL_FIRE_COMPONENT, OnPrimordialFireComponent::new);
		registry.registerForPlayers(OnPrimordialFireComponent.ON_PRIMORDIAL_FIRE_COMPONENT, OnPrimordialFireComponent::new, RespawnCopyStrategy.NEVER_COPY);
	}
	
	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(HardcoreDeathComponent.HARDCORE_DEATHS_COMPONENT, e -> new HardcoreDeathComponent());
	}
	
}
