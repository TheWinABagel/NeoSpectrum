package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LagoonFishingBobberEntity extends SpectrumFishingBobberEntity {
	
	public LagoonFishingBobberEntity(EntityType<? extends LagoonFishingBobberEntity> type, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean foundry) {
		super(type, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, foundry);
	}
	
	public LagoonFishingBobberEntity(EntityType<? extends LagoonFishingBobberEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	public LagoonFishingBobberEntity(Player thrower, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean foundry) {
		super(SpectrumEntityTypes.LAGOON_FISHING_BOBBER, thrower, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, foundry);
	}
	
}
