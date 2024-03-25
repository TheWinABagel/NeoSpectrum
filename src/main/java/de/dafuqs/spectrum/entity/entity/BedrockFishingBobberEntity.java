package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BedrockFishingBobberEntity extends SpectrumFishingBobberEntity {
	
	public BedrockFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> type, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean foundry) {
		super(type, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, foundry);
	}
	
	public BedrockFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	public BedrockFishingBobberEntity(Player thrower, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion, boolean foundry) {
		super(SpectrumEntityTypes.BEDROCK_FISHING_BOBBER, thrower, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, foundry);
	}
	
	
}
