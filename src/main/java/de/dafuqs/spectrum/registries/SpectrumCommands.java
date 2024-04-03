package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.commands.*;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpectrumCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        var dispatcher = e.getDispatcher();
        ShootingStarCommand.register(dispatcher);
        SanityCommand.register(dispatcher);
        PrintConfigCommand.register(dispatcher);
        PrimordialFireCommand.register(dispatcher);
        DumpRegistriesCommand.register(dispatcher);
    }
}
