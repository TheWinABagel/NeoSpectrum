package de.dafuqs.spectrum.explosion.modifier;

import de.dafuqs.spectrum.explosion.ExplosionModifierType;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LightPlacingModifier extends ParticleAddingModifier {
	
	public LightPlacingModifier(ExplosionModifierType type, ParticleOptions effect, int displayColor) {
		super(type, effect, displayColor);
	}
	
	@Override
	public void applyToBlocks(@NotNull Level world, @NotNull Iterable<BlockPos> blocks) {
		BlockState lightState = SpectrumBlocks.WAND_LIGHT_BLOCK.defaultBlockState();
		for (BlockPos pos : blocks) {
			if (world.getRandom().nextInt(12) == 0 && world.getBlockState(pos).isAir()) {
				world.setBlockAndUpdate(pos, lightState);
			}
		}
	}
	
}
