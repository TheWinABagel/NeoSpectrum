package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class SpectrumNetwork {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(SpectrumCommon.locate(SpectrumCommon.MOD_ID))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();

}
