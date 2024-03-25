package de.dafuqs.spectrum.api.predicate.world;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandPredicate implements WorldConditionPredicate, CommandSource {
	public static final CommandPredicate ANY = new CommandPredicate(null);
	
	public final String command;
	
	public CommandPredicate(String command) {
		this.command = command;
	}
	
	public static CommandPredicate fromJson(JsonObject json) {
		if (json == null || json.isJsonNull()) return ANY;
		return new CommandPredicate(json.get("command").getAsString());
	}
	
	@Override
	public boolean test(ServerLevel world, BlockPos pos) {
		if (this == ANY) return true;
		MinecraftServer minecraftServer = world.getServer();
		CommandSourceStack serverCommandSource = new CommandSourceStack(this, Vec3.atCenterOf(pos), Vec2.ZERO, world, 2, "FusionShrine", world.getBlockState(pos).getBlock().getName(), minecraftServer, null);
		return minecraftServer.getCommands().performPrefixedCommand(serverCommandSource, command) > 0;
	}
	
	@Override
	public void sendSystemMessage(Component message) {
	
	}
	
	@Override
	public boolean acceptsSuccess() {
		return false;
	}
	
	@Override
	public boolean acceptsFailure() {
		return false;
	}
	
	@Override
	public boolean shouldInformAdmins() {
		return false;
	}
	
}