package de.dafuqs.spectrum.explosion.modifier;

import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FireModifier extends DamageChangingModifier {
	
	public FireModifier(ExplosionModifierType type, ParticleOptions effect, int displayColor) {
		super(type, effect, displayColor);
	}
	
	@Override
	public void applyToBlocks(@NotNull Level world, @NotNull Iterable<BlockPos> blocks) {
		for (BlockPos pos : blocks) {
			if (world.getRandom().nextInt(3) == 0 && world.getBlockState(pos).isAir() && world.getBlockState(pos.below()).isSolidRender(world, pos.below())) {
				world.setBlockAndUpdate(pos, FireBlock.getState(world, pos));
			}
		}
		super.applyToBlocks(world, blocks);
	}

	@Override
	public Optional<DamageSource> getDamageSource(@Nullable LivingEntity owner) {
		if (owner == null) {
			return Optional.empty();
		} else {
			return Optional.of(owner.damageSources().inFire());
		}
	}

	@Override
	public void applyToEntity(@NotNull Entity entity, double distance) {
		entity.setRemainingFireTicks(20);
	}
	
}
