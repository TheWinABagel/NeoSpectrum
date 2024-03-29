package de.dafuqs.spectrum.sound;

import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class HintRevelationSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	
	private final Player player;
	private final int duration;
	private boolean done;
	private int playtime;
	
	public HintRevelationSoundInstance(Player player, int duration) {
		super(SpectrumSoundEvents.TEXT_REVEALED, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.delay = 0;
		this.volume = 1.0F;
		this.player = player;
		this.duration = duration;
		this.x = player.getX();
		this.y = player.getY();
		this.z = player.getZ();
		
		this.playtime = 0;
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
		playtime++;
		
		if (this.player != null) {
			this.x = player.getX();
			this.y = player.getY();
			this.z = player.getZ();
		}
		
		if (player == null || !player.getMainHandItem().is(SpectrumItems.GUIDEBOOK) || playtime > duration) {
			this.setDone();
		}
	}
	
	protected final void setDone() {
		this.done = true;
		this.looping = false;
	}
}