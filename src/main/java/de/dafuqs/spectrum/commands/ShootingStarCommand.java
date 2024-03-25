package de.dafuqs.spectrum.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.dafuqs.spectrum.entity.spawners.ShootingStarSpawner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ShootingStarCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("spectrum_spawn_shooting_star")
			.requires((source) -> source.hasPermission(2))
			.then(Commands.argument("targets", EntityArgument.players())
				.executes((context) -> execute(context.getSource(), EntityArgument.getPlayers(context, "targets"), 1))
			.then(Commands.argument("amount", IntegerArgumentType.integer(1))
				.executes((context) -> execute(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"))))));
	}

	private static int execute(CommandSourceStack source, Collection<? extends ServerPlayer> targets, int amount) {
		for (ServerPlayer entity : targets) {
			for (int i = 0; i < amount; i++) {
				ShootingStarSpawner.spawnShootingStar((ServerLevel) entity.level(), entity);
			}
		}
		source.sendSuccess(() -> Component.translatable("commands.spectrum.spawn_shooting_star.success", amount), false);
		return amount;
	}

}
