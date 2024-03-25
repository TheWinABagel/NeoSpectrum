package de.dafuqs.spectrum.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;

public class SpectrumMusicType extends Musics {
	
	public static Music SPECTRUM_THEME;
	public static Music DEEPER_DOWN_THEME;
	
	public static void register() {
		SPECTRUM_THEME = new Music(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SpectrumSoundEvents.SPECTRUM_THEME), 6000, 24000, false);
		DEEPER_DOWN_THEME = new Music(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SpectrumSoundEvents.DEEPER_DOWN_THEME), 6000, 24000, false);
	}
	
}
