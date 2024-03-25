package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.api.block.PedestalVariant;
import de.dafuqs.spectrum.api.color.ColorRegistry;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.memory.MemoryBlockEntity;
import de.dafuqs.spectrum.blocks.pastel_network.network.PastelTransmission;
import de.dafuqs.spectrum.blocks.pastel_network.network.ServerPastelNetwork;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntity;
import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.particle.effect.ColoredTransmission;
import de.dafuqs.spectrum.particle.effect.TypedTransmission;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SpectrumS2CPacketSender {

	/**
	 * Play particle effect
	 *
	 * @param world          the world
	 * @param position       the pos of the particles
	 * @param particleEffect The particle effect to play
	 */
	public static void playParticleWithRandomOffsetAndVelocity(ServerLevel world, Vec3 position, @NotNull ParticleOptions particleEffect, int amount, Vec3 randomOffset, Vec3 randomVelocity) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeDouble(position.x);
		buf.writeDouble(position.y);
		buf.writeDouble(position.z);
		buf.writeResourceLocation(BuiltInRegistries.PARTICLE_TYPE.getKey(particleEffect.getType()));
		buf.writeInt(amount);
		buf.writeDouble(randomOffset.x);
		buf.writeDouble(randomOffset.y);
		buf.writeDouble(randomOffset.z);
		buf.writeDouble(randomVelocity.x);
		buf.writeDouble(randomVelocity.y);
		buf.writeDouble(randomVelocity.z);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(world, BlockPos.containing(position))) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PARTICLE_WITH_RANDOM_OFFSET_AND_VELOCITY, buf);
		}
	}

	/**
	 * Play particle effect
	 *
	 * @param world          the world
	 * @param position       the pos of the particles
	 * @param particleEffect The particle effect to play
	 */
	public static void playParticles(ServerLevel world, BlockPos position, ParticleOptions particleEffect, int amount) {
		playParticleWithExactVelocity(world, Vec3.atCenterOf(position), particleEffect, amount, Vec3.ZERO);
	}

	/**
	 * Play particle effect
	 *
	 * @param world          the world
	 * @param position       the pos of the particles
	 * @param particleEffect The particle effect to play
	 */
	public static void playParticleWithExactVelocity(ServerLevel world, @NotNull Vec3 position, @NotNull ParticleOptions particleEffect, int amount, @NotNull Vec3 velocity) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeDouble(position.x);
		buf.writeDouble(position.y);
		buf.writeDouble(position.z);
		buf.writeResourceLocation(BuiltInRegistries.PARTICLE_TYPE.getKey(particleEffect.getType()));
		buf.writeInt(amount);
		buf.writeDouble(velocity.x);
		buf.writeDouble(velocity.y);
		buf.writeDouble(velocity.z);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(world, BlockPos.containing(position))) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PARTICLE_WITH_EXACT_VELOCITY, buf);
		}
	}

	/**
	 * Play particles matching a spawn pattern
	 *
	 * @param world          the world
	 * @param position       the pos of the particles
	 * @param particleEffect The particle effect to play
	 */
	public static void playParticleWithPatternAndVelocity(@Nullable Player notThisPlayerEntity, ServerLevel world, @NotNull Vec3 position, @NotNull ParticleOptions particleEffect, @NotNull VectorPattern pattern, double velocity) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeDouble(position.x);
		buf.writeDouble(position.y);
		buf.writeDouble(position.z);
		buf.writeResourceLocation(BuiltInRegistries.PARTICLE_TYPE.getKey(particleEffect.getType()));
		buf.writeInt(pattern.ordinal());
		buf.writeDouble(velocity);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(world, BlockPos.containing(position))) {
			if (!player.equals(notThisPlayerEntity)) {
				ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PARTICLE_PACKET_WITH_PATTERN_AND_VELOCITY_ID, buf);
			}
		}
	}

	/**
	 * @param world     the world
	 * @param blockPos  the blockpos of the pedestal
	 * @param itemStack the itemstack that was crafted
	 */
	public static void sendPlayPedestalCraftingFinishedParticle(Level world, BlockPos blockPos, ItemStack itemStack) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(blockPos);
		buf.writeItem(itemStack);
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PEDESTAL_CRAFTING_FINISHED_PARTICLE_PACKET_ID, buf);
		}
	}
	
	public static void sendPlayFusionCraftingInProgressParticles(Level world, BlockPos blockPos) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(blockPos);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_FUSION_CRAFTING_IN_PROGRESS_PARTICLE_PACKET_ID, buf);
		}
	}
	
	public static void sendPlayFusionCraftingFinishedParticles(Level world, BlockPos blockPos, @NotNull ItemStack itemStack) {
		Optional<DyeColor> optionalItemColor = ColorRegistry.ITEM_COLORS.getMapping(itemStack.getItem());
		
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(blockPos);
		
		if (optionalItemColor.isPresent()) {
			buf.writeInt(optionalItemColor.get().ordinal());
		} else {
            buf.writeInt(DyeColor.LIGHT_GRAY.ordinal());
        }

        // Iterate over all players tracking a position in the world and send the packet to each player
        for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, blockPos)) {
            ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_FUSION_CRAFTING_FINISHED_PARTICLE_PACKET_ID, buf);
        }
    }

    public static void sendPastelTransmissionParticle(ServerPastelNetwork network, int travelTime, @NotNull PastelTransmission transmission) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeUUID(network.getUUID());
		buf.writeInt(travelTime);
		PastelTransmission.writeToBuf(buf, transmission);
	
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) network.getWorld(), transmission.getStartPos())) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PASTEL_TRANSMISSION, buf);
		}
	}
	
	public static void playColorTransmissionParticle(ServerLevel world, @NotNull ColoredTransmission transfer) {
		BlockPos blockPos = BlockPos.containing(transfer.getOrigin());
		
		FriendlyByteBuf buf = PacketByteBufs.create();
		ColoredTransmission.writeToBuf(buf, transfer);
		
		for (ServerPlayer player : PlayerLookup.tracking(world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.COLOR_TRANSMISSION, buf);
		}
	}
	
	public static void playTransmissionParticle(ServerLevel world, @NotNull TypedTransmission transmission) {
		BlockPos blockPos = BlockPos.containing(transmission.getOrigin());
		
		FriendlyByteBuf buf = PacketByteBufs.create();
		TypedTransmission.writeToBuf(buf, transmission);
		
		for (ServerPlayer player : PlayerLookup.tracking(world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.TYPED_TRANSMISSION, buf);
		}
	}
	
	public static void sendPlayBlockBoundSoundInstance(SoundEvent soundEvent, @NotNull ServerLevel world, BlockPos blockPos, int maxDurationTicks) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeResourceLocation(BuiltInRegistries.SOUND_EVENT.getKey(soundEvent));
		buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(world.getBlockState(blockPos).getBlock()));
		buf.writeBlockPos(blockPos);
		buf.writeInt(maxDurationTicks);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_BLOCK_BOUND_SOUND_INSTANCE, buf);
		}
	}
	
	public static void sendPlayTakeOffBeltSoundInstance(ServerPlayer playerEntity) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		ServerPlayNetworking.send(playerEntity, SpectrumS2CPackets.PLAY_TAKE_OFF_BELT_SOUND_INSTANCE, buf);
	}
	
	public static void sendCancelBlockBoundSoundInstance(@NotNull ServerLevel world, BlockPos blockPos) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeResourceLocation(new ResourceLocation("stop"));
		buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(world.getBlockState(blockPos).getBlock()));
		buf.writeBlockPos(blockPos);
		buf.writeInt(1);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_BLOCK_BOUND_SOUND_INSTANCE, buf);
		}
	}
	
	public static void spawnPedestalUpgradeParticles(Level world, BlockPos blockPos, @NotNull PedestalVariant newPedestalVariant) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(blockPos);
		buf.writeInt(newPedestalVariant.getRecipeTier().ordinal());
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PEDESTAL_UPGRADED_PARTICLE_PACKET_ID, buf);
		}
	}
	
	public static void spawnPedestalStartCraftingParticles(PedestalBlockEntity pedestalBlockEntity) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(pedestalBlockEntity.getBlockPos());
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) pedestalBlockEntity.getLevel(), pedestalBlockEntity.getBlockPos())) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PEDESTAL_START_CRAFTING_PARTICLE_PACKET_ID, buf);
		}
	}
	
	public static void sendPlayShootingStarParticles(@NotNull ShootingStarEntity shootingStarEntity) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeDouble(shootingStarEntity.position().x());
		buf.writeDouble(shootingStarEntity.position().y());
		buf.writeDouble(shootingStarEntity.position().z());
		buf.writeInt(shootingStarEntity.getShootingStarType().ordinal());
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) shootingStarEntity.level(), shootingStarEntity.blockPosition())) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_SHOOTING_STAR_PARTICLES, buf);
		}
	}
	
	public static void startSkyLerping(@NotNull ServerLevel serverWorld, int additionalTime) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		long timeOfDay = serverWorld.getDayTime();
		buf.writeLong(timeOfDay);
		buf.writeLong(timeOfDay + additionalTime);
		
		for (ServerPlayer player : serverWorld.players()) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.START_SKY_LERPING, buf);
		}
	}
	
	public static void playMemoryManifestingParticles(ServerLevel serverWorld, @NotNull BlockPos blockPos, EntityType<?> entityType, int amount) {
		Tuple<Integer, Integer> eggColors = MemoryBlockEntity.getEggColorsForEntity(entityType);
		
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(blockPos);
		buf.writeInt(eggColors.getA());
		buf.writeInt(eggColors.getB());
		buf.writeInt(amount);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(serverWorld, blockPos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_MEMORY_MANIFESTING_PARTICLES, buf);
		}
	}
	
	public static void sendBossBarUpdatePropertiesPacket(UUID uuid, boolean serpentMusic, Collection<ServerPlayer> players) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeUUID(uuid);
		buf.writeBoolean(serpentMusic);
		
		for (ServerPlayer player : players) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.UPDATE_BOSS_BAR, buf);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void updateBlockEntityInk(BlockPos pos, InkStorage inkStorage, ServerPlayer player) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(pos);
		buf.writeLong(inkStorage.getCurrentTotal());
		
		Map<InkColor, Long> colors = inkStorage.getEnergy();
		buf.writeInt(colors.size());
		for (Map.Entry<InkColor, Long> color : colors.entrySet()) {
			buf.writeUtf(color.getKey().toString());
			buf.writeLong(color.getValue());
		}
		
		ServerPlayNetworking.send(player, SpectrumS2CPackets.UPDATE_BLOCK_ENTITY_INK, buf);
	}
	
	public static void sendInkColorSelected(@Nullable InkColor color, ServerPlayer player) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		if (color == null) {
			packetByteBuf.writeBoolean(false);
		} else {
			packetByteBuf.writeBoolean(true);
			packetByteBuf.writeUtf(color.toString());
		}
		ServerPlayNetworking.send(player, SpectrumS2CPackets.INK_COLOR_SELECTED, packetByteBuf);
	}
	
	public static void playInkEffectParticles(ServerLevel serverWorld, InkColor inkColor, Vec3 effectPos, float potency) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeUtf(inkColor.toString());
		packetByteBuf.writeDouble(effectPos.x);
		packetByteBuf.writeDouble(effectPos.y);
		packetByteBuf.writeDouble(effectPos.z);
		packetByteBuf.writeFloat(potency);
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(serverWorld, BlockPos.containing(effectPos))) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_INK_EFFECT_PARTICLES, packetByteBuf);
		}
	}
	
	public static void playPresentOpeningParticles(ServerLevel serverWorld, BlockPos pos, Map<DyeColor, Integer> colors) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeBlockPos(pos);
		packetByteBuf.writeInt(colors.size());
		for (Map.Entry<DyeColor, Integer> color : colors.entrySet()) {
			packetByteBuf.writeByte(color.getKey().getId());
			packetByteBuf.writeByte(color.getValue());
		}
		
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(serverWorld, pos)) {
			ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_PRESENT_OPENING_PARTICLES, packetByteBuf);
		}
	}
	
	public static void playAscensionAppliedEffects(ServerPlayer player) {
		ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_ASCENSION_APPLIED_EFFECTS, PacketByteBufs.create());
	}
	
	public static void playDivinityAppliedEffects(ServerPlayer player) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeInt(player.getId());
		ServerPlayNetworking.send(player, SpectrumS2CPackets.PLAY_DIVINITY_APPLIED_EFFECTS, buf);
	}

	public static void sendMoonstoneBlast(ServerLevel serverWorld, MoonstoneStrike moonstoneStrike) {
		// Iterate over all players tracking a position in the world and send the packet to each player
		for (ServerPlayer player : PlayerLookup.tracking(serverWorld, BlockPos.containing(moonstoneStrike.getX(), moonstoneStrike.getY(), moonstoneStrike.getZ()))) {
			Vec3 playerVelocity = moonstoneStrike.getAffectedPlayers().getOrDefault(player, Vec3.ZERO);
			
			FriendlyByteBuf buf = PacketByteBufs.create();
			buf.writeDouble(moonstoneStrike.getX());
			buf.writeDouble(moonstoneStrike.getY());
			buf.writeDouble(moonstoneStrike.getZ());
			buf.writeFloat(moonstoneStrike.getPower());
			buf.writeFloat(moonstoneStrike.getKnockbackMod());
			buf.writeDouble(playerVelocity.x);
			buf.writeDouble(playerVelocity.y);
			buf.writeDouble(playerVelocity.z);

			ServerPlayNetworking.send(player, SpectrumS2CPackets.MOONSTONE_BLAST, buf);
		}
	}
	
}