package de.dafuqs.spectrum.sound;


import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import de.dafuqs.spectrum.status_effects.AscensionStatusEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class DivinitySoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	
	private static int instances = 0;
	private int time = 0;
	private boolean done;
	
	public DivinitySoundInstance() {
		super(SpectrumSoundEvents.DIVINITY, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.delay = 0;
		this.volume = 0.8F;
		instances++;
		Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
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
		Minecraft client = Minecraft.getInstance();
		time++;
		if (time > AscensionStatusEffect.MUSIC_INTRO_TICKS) {
			this.volume = 0.8F;
		} else {
			this.volume = 0.5F + ((float) time / AscensionStatusEffect.MUSIC_INTRO_TICKS) * 0.2F;
		}
		LocalPlayer player = client.player;
		if (instances > 1 || player == null || !(player.hasEffect(SpectrumStatusEffects.ASCENSION) || player.hasEffect(SpectrumStatusEffects.DIVINITY))) {
			this.setDone();
		} else {
			this.x = ((float) player.getX());
			this.y = ((float) player.getY());
			this.z = ((float) player.getZ());
		}
	}
	
	protected final void setDone() {
		this.done = true;
		this.looping = false;
		instances--;
	}
}
