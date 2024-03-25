package de.dafuqs.spectrum.explosion.modifier;

import de.dafuqs.spectrum.blocks.PrimordialFireBlock;
import de.dafuqs.spectrum.cca.OnPrimordialFireComponent;
import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PrimordialFireModifier extends DamageChangingModifier {
	
	
	public PrimordialFireModifier(ExplosionModifierType type,ParticleOptions effect, int displayColor) {
		super(type, effect, displayColor);
	}
	
	@Override
	public void applyToBlocks(@NotNull Level world, @NotNull Iterable<BlockPos> blocks) {
		for (BlockPos pos : blocks) {
			if (world.getRandom().nextInt(3) == 0 && world.getBlockState(pos).isAir() && world.getBlockState(pos.below()).isSolidRender(world, pos.below())) {
				world.setBlockAndUpdate(pos, PrimordialFireBlock.getState(world, pos));
			}
		}
	}
	
	@Override
	public void applyToEntity(@NotNull Entity entity, double distance) {
		if (entity instanceof LivingEntity livingEntity) {
			OnPrimordialFireComponent.addPrimordialFireTicks(livingEntity, 20);
		}
	}
	
	@Override
	public float getBlastRadiusModifier() {
		return 1.25F;
	}

	@Override
	public Optional<DamageSource> getDamageSource(@Nullable LivingEntity owner) {
		if (owner == null) {
			return Optional.empty();
		}
		return Optional.of(SpectrumDamageTypes.primordialFire(owner.level(), owner));
	}
}
