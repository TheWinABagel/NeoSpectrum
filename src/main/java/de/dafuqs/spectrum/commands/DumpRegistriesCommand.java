package de.dafuqs.spectrum.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class DumpRegistriesCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("spectrum_dump_registries").executes((context) -> execute(context.getSource())));
	}

	private static int execute(CommandSourceStack source) {
		File directory = FabricLoader.getInstance().getGameDir().resolve("registry_dump").toFile();

		source.registryAccess().registries().forEach(entry -> {
			File file = new File(directory, entry.key().location().toString().replace(":", "-") + ".txt");
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				for (ResourceKey<?> e : entry.value().registryKeySet()) {
					writer.write(e.location().toString());
					writer.write(System.lineSeparator());
				}
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		source.sendSystemMessage(Component.literal("Registries exported to directory 'registry_dump'"));

		return 0;
	}

}
