package de.dafuqs.spectrum.sound;

import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class PipeBombChargingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {

	private final Player player;
	private boolean done;

	public PipeBombChargingSoundInstance(Player player) {
		super(SpectrumSoundEvents.INCANDESCENT_CHARGE, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.delay = 0;
		this.volume = 1F;
		this.player = player;
		this.x = player.getX();
		this.y = player.getY();
		this.z = player.getZ();
	}
	
	@Override
	public boolean isStopped() {
		return this.done;
	}
	
	@Override
	public boolean canStartSilent() {
		return true;
	}
	
	@Override
	public void tick() {
		if (player == null || player.getUseItemRemainingTicks() <= 0 || player.getTicksUsingItem() > 54) {
			this.setDone();
		} else {
			this.x = this.player.getX();
			this.y = this.player.getY();
			this.z = this.player.getZ();
			
			showParticles();
		}
	}
	
	private void showParticles() {
		Level world = player.getCommandSenderWorld();
		Vec3 pos = player.position();
		RandomSource random = world.random;
		
		for (int i = 0; i < 2; i++) {
			player.getCommandSenderWorld().addParticle(
					SpectrumParticleTypes.PRIMORDIAL_FLAME,
					pos.x,
					pos.y + 1,
					pos.z,
					random.nextDouble() - 0.5D,
					random.nextDouble() - 0.5D,
					random.nextDouble() - 0.5D);
		}
	}
	
	protected final void setDone() {
		this.done = true;
		this.looping = false;
	}
}
