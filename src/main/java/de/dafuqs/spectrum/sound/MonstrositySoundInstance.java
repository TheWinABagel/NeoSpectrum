package de.dafuqs.spectrum.sound;

import de.dafuqs.spectrum.entity.entity.SpectrumBossEntity;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

@OnlyIn(Dist.CLIENT)
public class MonstrositySoundInstance extends AbstractTickableSoundInstance {
	
	private static int instances = 0;
	private final SpectrumBossEntity bossEntity;
	
	private MonstrositySoundInstance(SpectrumBossEntity bossEntity) {
		super(SpectrumSoundEvents.BOSS_THEME, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
		this.bossEntity = bossEntity;
		this.looping = true;
		instances++;
	}
	
	public static void startSoundInstance(SpectrumBossEntity bossEntity) {
		Minecraft.getInstance().getSoundManager().play(new MonstrositySoundInstance(bossEntity));
	}
	
	@Override
	public void tick() {
		Minecraft client = Minecraft.getInstance();
		if (instances > 1 || (!bossEntity.isAlive() || bossEntity.isRemoved())) {
			instances--;
			this.stop();
			return;
		}
		
		Player player = client.player;
		if (player != null) {
			this.x = player.getX();
			this.y = player.getY();
			this.z = player.getZ();
		}
	}
	
}