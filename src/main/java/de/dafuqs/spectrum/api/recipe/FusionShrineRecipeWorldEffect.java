package de.dafuqs.spectrum.api.recipe;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Effects that are played when crafting with the fusion shrine
 */
public interface FusionShrineRecipeWorldEffect {
	
	Map<String, FusionShrineRecipeWorldEffect> TYPES = new HashMap<>();

	FusionShrineRecipeWorldEffect NOTHING = register("nothing", new FusionShrineRecipeWorldEffect.SingleTimeRecipeWorldEffect() {
		@Override
		public void trigger(ServerLevel world, BlockPos pos) { }
	});
	
	static FusionShrineRecipeWorldEffect register(String id, FusionShrineRecipeWorldEffect effect) {
		TYPES.put(id, effect);
		return effect;
	}
	
	static FusionShrineRecipeWorldEffect fromString(String string) {
		if (string == null || string.isBlank()) {
			return NOTHING;
		}
		if (string.startsWith("/")) {
			return new CommandRecipeWorldEffect(string);
		}
		
		FusionShrineRecipeWorldEffect effect = TYPES.get(string);
		if (effect == null) {
			SpectrumCommon.logError("Unknown fusion shrine world effect '" + string + "'. Will be ignored.");
			return NOTHING;
		}
		return effect;
	}
	
	/**
	 * True for all effects that should just play once.
	 * Otherwise, it will be triggered each tick of the recipe
	 */
	boolean isOneTimeEffect();
	
	void trigger(ServerLevel world, BlockPos pos);
	
	abstract class EveryTickRecipeWorldEffect implements FusionShrineRecipeWorldEffect {
		
		public EveryTickRecipeWorldEffect() {
		}
		
		@Override
		public boolean isOneTimeEffect() {
			return false;
		}
		
	}
	
	abstract class SingleTimeRecipeWorldEffect implements FusionShrineRecipeWorldEffect {
		
		public SingleTimeRecipeWorldEffect() {
		}
		
		@Override
		public boolean isOneTimeEffect() {
			return true;
		}
		
	}
	
	class CommandRecipeWorldEffect implements FusionShrineRecipeWorldEffect, CommandSource {
		
		protected final String command;
		
		public CommandRecipeWorldEffect(String command) {
			this.command = command;
		}
		
		public static CommandRecipeWorldEffect fromJson(JsonObject json) {
			return new CommandRecipeWorldEffect(json.getAsString());
		}
		
		@Override
		public boolean isOneTimeEffect() {
			return false;
		}
		
		@Override
		public void trigger(ServerLevel world, BlockPos pos) {
			MinecraftServer minecraftServer = world.getServer();
			CommandSourceStack serverCommandSource = new CommandSourceStack(this, Vec3.atCenterOf(pos), Vec2.ZERO, world, 2, "FusionShrine", world.getBlockState(pos).getBlock().getName(), minecraftServer, null);
			minecraftServer.getCommands().performPrefixedCommand(serverCommandSource, command);
		}
		
		@Override
		public void sendSystemMessage(Component message) { }
		
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
	
}
