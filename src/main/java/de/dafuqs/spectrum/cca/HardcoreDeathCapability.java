package de.dafuqs.spectrum.cca;

import com.mojang.authlib.GameProfile;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HardcoreDeathCapability implements INBTSerializable<CompoundTag> {

	public static final ResourceLocation ID = SpectrumCommon.locate("hardcore_death");
	private final static List<UUID> playersThatDiedInHardcore = new ArrayList<>();
	public static final Capability<HardcoreDeathCapability> HARDCORE_DEATH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
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

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ListTag uuidList = new ListTag();
		for (UUID playerThatDiedInHardcore : playersThatDiedInHardcore) {
			uuidList.add(NbtUtils.createUUID(playerThatDiedInHardcore));
		}
		tag.put("HardcoreDeaths", uuidList);
		return null;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		playersThatDiedInHardcore.clear();
		ListTag uuidList = tag.getList("HardcoreDeaths", Tag.TAG_INT_ARRAY);
		for (Tag listEntry : uuidList) {
			playersThatDiedInHardcore.add(NbtUtils.loadUUID(listEntry));
		}
	}
}
