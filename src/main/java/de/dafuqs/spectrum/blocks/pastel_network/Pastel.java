package de.dafuqs.spectrum.blocks.pastel_network;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.pastel_network.network.ClientPastelNetworkManager;
import de.dafuqs.spectrum.blocks.pastel_network.network.PastelNetworkManager;
import de.dafuqs.spectrum.blocks.pastel_network.network.ServerPastelNetworkManager;
import de.dafuqs.spectrum.particle.render.EarlyRenderingParticleContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Pastel {

    @Environment(EnvType.CLIENT)
    private static ClientPastelNetworkManager clientManager;
    private static ServerPastelNetworkManager serverManager;

    @Environment(EnvType.CLIENT)
    public static ClientPastelNetworkManager getClientInstance() {
        if (clientManager == null) {
            clientManager = new ClientPastelNetworkManager();
        }
        return clientManager;
    }

    public static ServerPastelNetworkManager getServerInstance() {
        if (serverManager == null) {
            serverManager = ServerPastelNetworkManager.get(SpectrumCommon.minecraftServer.overworld());
        }
        return serverManager;
    }

    public static PastelNetworkManager getInstance(boolean client) {
        if (client) {
            return getClientInstance();
        } else {
            return getServerInstance();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static void clearClientInstance() {
        clientManager = null;
        EarlyRenderingParticleContainer.clear();
    }

    public static void clearServerInstance() {
        serverManager = null;
    }

}
