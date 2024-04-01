package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Collection;
import java.util.Objects;

public class SpectrumNetwork {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(SpectrumCommon.locate(SpectrumCommon.MOD_ID))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();

    //copied from fapi
    public static Collection<ServerPlayer> tracking(ServerLevel level, BlockPos pos) {
        Objects.requireNonNull(pos, "BlockPos cannot be null");

        return tracking(level, new ChunkPos(pos));
    }

    public static Collection<ServerPlayer> tracking(ServerLevel level, ChunkPos pos) {
        Objects.requireNonNull(level, "The level cannot be null");
        Objects.requireNonNull(pos, "The chunk pos cannot be null");

        return level.getChunkSource().chunkMap.getPlayers(pos, false);
    }
}
