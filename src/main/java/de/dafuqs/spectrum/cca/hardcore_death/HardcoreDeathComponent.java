package de.dafuqs.spectrum.cca.hardcore_death;

import com.mojang.authlib.GameProfile;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HardcoreDeathComponent implements Component {
	
	public static final ComponentKey<HardcoreDeathComponent> HARDCORE_DEATHS_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("hardcore_deaths"), HardcoreDeathComponent.class);
	
	private final static List<UUID> playersThatDiedInHardcore = new ArrayList<>();
	
	@Override
	public void writeToNbt(@NotNull CompoundTag tag) {
		ListTag uuidList = new ListTag();
		for (UUID playerThatDiedInHardcore : playersThatDiedInHardcore) {
			uuidList.add(NbtUtils.createUUID(playerThatDiedInHardcore));
		}
		tag.put("HardcoreDeaths", uuidList);
	}
	
	@Override
	public void readFromNbt(CompoundTag tag) {
		playersThatDiedInHardcore.clear();
		ListTag uuidList = tag.getList("HardcoreDeaths", Tag.TAG_INT_ARRAY);
		for (Tag listEntry : uuidList) {
			playersThatDiedInHardcore.add(NbtUtils.loadUUID(listEntry));
		}
	}
	
	public static boolean isInHardcore(Player player) {
		return player.hasEffect(SpectrumStatusEffects.DIVINITY);
	}
	
	public static void addHardcoreDeath(GameProfile profile) {
		addHardcoreDeath(profile.getId());
	}
	
	public static void removeHardcoreDeath(GameProfile profile) {
		removeHardcoreDeath(profile.getId());
	}
	
	public static boolean hasHardcoreDeath(GameProfile profile) {
		return hasHardcoreDeath(profile.getId());
	}
	
	protected static void addHardcoreDeath(UUID uuid) {
		if (!playersThatDiedInHardcore.contains(uuid)) {
			playersThatDiedInHardcore.add(uuid);
		}
		SpectrumCommon.minecraftServer.getPlayerList().getPlayer(uuid).setGameMode(GameType.SPECTATOR);
	}
	
	protected static boolean hasHardcoreDeath(UUID uuid) {
		return playersThatDiedInHardcore.contains(uuid);
	}
	
	protected static void removeHardcoreDeath(UUID uuid) {
		playersThatDiedInHardcore.remove(uuid);
	}
	
}
