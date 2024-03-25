package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class BidentEntity extends BidentBaseEntity {
	
	public BidentEntity(Level world) {
		this(SpectrumEntityTypes.BIDENT, world);
	}
	
	public BidentEntity(EntityType<? extends ThrownTrident> entityType, Level world) {
		super(entityType, world);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		MoonstoneStrike.create(entityHitResult.getEntity().level(), this, null, this.getX(), this.getY(), this.getZ(), 2);
	}
	
	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		MoonstoneStrike.create(this.level(), this, null, this.getX(), this.getY(), this.getZ(), 2);
	}
    
}
