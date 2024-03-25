package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.blocks.idols.FirestarterIdolBlock;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MoltenFishingBobberEntity extends SpectrumFishingBobberEntity {
	
	public MoltenFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> type, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion) {
		super(type, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, true);
	}
	
	public MoltenFishingBobberEntity(EntityType<? extends SpectrumFishingBobberEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	@Override
	public void tick() {
		Level world = this.level();
		super.tick();
		if (!world.isClientSide && tickCount % 20 == 0 && onGround()) {
			FirestarterIdolBlock.causeFire((ServerLevel) this.level(), blockPosition(), Direction.DOWN);
		}
	}
	
	public MoltenFishingBobberEntity(Player thrower, Level world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, int bigCatchLevel, boolean inventoryInsertion) {
		super(SpectrumEntityTypes.MOLTEN_FISHING_BOBBER, thrower, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, bigCatchLevel, inventoryInsertion, true);
	}
	
	@Override
	public void hookedEntityTick(Entity hookedEntity) {
		hookedEntity.setSecondsOnFire(2);
	}
	
}
