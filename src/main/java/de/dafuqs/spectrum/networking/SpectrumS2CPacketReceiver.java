package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.SpectrumClient;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.block.InkColorSelectedPacketReceiver;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.fusion_shrine.FusionShrineBlockEntity;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerBlockEntity;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerConfiguration;
import de.dafuqs.spectrum.blocks.pastel_network.network.PastelTransmission;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlock;
import de.dafuqs.spectrum.blocks.pedestal.PedestalBlockEntity;
import de.dafuqs.spectrum.blocks.present.PresentBlock;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStar;
import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.helpers.ParticleHelper;
import de.dafuqs.spectrum.items.map.ArtisansAtlasState;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.particle.effect.*;
import de.dafuqs.spectrum.recipe.pedestal.PedestalRecipeTier;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.CraftingBlockSoundInstance;
import de.dafuqs.spectrum.sound.DivinitySoundInstance;
import de.dafuqs.spectrum.sound.TakeOffBeltSoundInstance;
import de.dafuqs.spectrum.spells.InkSpellEffects;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class SpectrumS2CPacketReceiver {
	
	@SuppressWarnings("deprecation")
	public static void registerS2CReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PARTICLE_WITH_RANDOM_OFFSET_AND_VELOCITY, (client, handler, buf, responseSender) -> {
			Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(buf.readResourceLocation());
			int amount = buf.readInt();
			Vec3 randomOffset = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			Vec3 randomVelocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			if (particleType instanceof ParticleOptions particleEffect) {
				client.execute(() -> {
					// Everything in this lambda is running on the render thread
					
					RandomSource random = client.level.random;
					
					for (int i = 0; i < amount; i++) {
						double randomOffsetX = randomOffset.x - random.nextDouble() * randomOffset.x * 2;
						double randomOffsetY = randomOffset.y - random.nextDouble() * randomOffset.y * 2;
						double randomOffsetZ = randomOffset.z - random.nextDouble() * randomOffset.z * 2;
						double randomVelocityX = randomVelocity.x - random.nextDouble() * randomVelocity.x * 2;
						double randomVelocityY = randomVelocity.y - random.nextDouble() * randomVelocity.y * 2;
						double randomVelocityZ = randomVelocity.z - random.nextDouble() * randomVelocity.z * 2;
						
						client.level.addParticle(particleEffect,
								position.x() + randomOffsetX, position.y() + randomOffsetY, position.z() + randomOffsetZ,
								randomVelocityX, randomVelocityY, randomVelocityZ);
					}
				});
			}
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PARTICLE_WITH_EXACT_VELOCITY, (client, handler, buf, responseSender) -> {
			double posX = buf.readDouble();
			double posY = buf.readDouble();
			double posZ = buf.readDouble();
			ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(buf.readResourceLocation());
			int amount = buf.readInt();
			double velocityX = buf.readDouble();
			double velocityY = buf.readDouble();
			double velocityZ = buf.readDouble();
			if (particleType instanceof ParticleOptions particleEffect) {
				client.execute(() -> {
					// Everything in this lambda is running on the render thread
					for (int i = 0; i < amount; i++) {
						client.level.addParticle(particleEffect,
								posX, posY, posZ,
								velocityX, velocityY, velocityZ);
					}
				});
			}
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PARTICLE_PACKET_WITH_PATTERN_AND_VELOCITY_ID, (client, handler, buf, responseSender) -> {
			Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(buf.readResourceLocation());
			VectorPattern pattern = VectorPattern.values()[buf.readInt()];
			double velocity = buf.readDouble();
			if (particleType instanceof ParticleOptions particleEffect) {
				client.execute(() -> {
					// Everything in this lambda is running on the render thread
					ParticleHelper.playParticleWithPatternAndVelocityClient(client.level, position, particleEffect, pattern, velocity);
				});
			}
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.START_SKY_LERPING, (client, handler, buf, responseSender) -> {
			DimensionType dimensionType = client.level.dimensionType();
			long sourceTime = buf.readLong();
			long targetTime = buf.readLong();
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				SpectrumClient.skyLerper.trigger(dimensionType, sourceTime, client.getFrameTime(), targetTime);
				if (client.level.canSeeSky(client.player.blockPosition())) {
					client.level.playSound(null, client.player.blockPosition(), SpectrumSoundEvents.CELESTIAL_POCKET_WATCH_FLY_BY, SoundSource.NEUTRAL, 0.15F, 1.0F);
				}
			});
			
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PEDESTAL_CRAFTING_FINISHED_PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos position = buf.readBlockPos(); // the block pos of the pedestal
			ItemStack itemStack = buf.readItem(); // the item stack that was crafted
			client.execute(() -> {
				RandomSource random = client.level.random;
				// Everything in this lambda is running on the render thread
				for (int i = 0; i < 10; i++) {
					client.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), position.getX() + 0.5, position.getY() + 1, position.getZ() + 0.5, 0.15 - random.nextFloat() * 0.3, random.nextFloat() * 0.15 + 0.1, 0.15 - random.nextFloat() * 0.3);
				}
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_SHOOTING_STAR_PARTICLES, (client, handler, buf, responseSender) -> {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			ShootingStar.Type shootingStarType = ShootingStar.Type.getType(buf.readInt());
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				ShootingStarEntity.playHitParticles(client.level, x, y, z, shootingStarType, 25);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_FUSION_CRAFTING_IN_PROGRESS_PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos position = buf.readBlockPos();
			client.execute(() -> {
				BlockEntity blockEntity = client.level.getBlockEntity(position);
				if (blockEntity instanceof FusionShrineBlockEntity fusionShrineBlockEntity) {
					fusionShrineBlockEntity.spawnCraftingParticles();
				}
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_FUSION_CRAFTING_FINISHED_PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos position = buf.readBlockPos();
			DyeColor dyeColor = DyeColor.values()[buf.readInt()];
			client.execute(() -> {
				Vec3 sourcePos = new Vec3(position.getX() + 0.5, position.getY() + 1, position.getZ() + 0.5);
				
				Vector3f color = ColorHelper.getRGBVec(dyeColor);
				float velocityModifier = 0.25F;
				for (Vec3 velocity : VectorPattern.SIXTEEN.getVectors()) {
					client.level.addParticle(
							new DynamicParticleEffect(SpectrumParticleTypes.WHITE_CRAFTING, 0.0F, color, 1.5F, 40, false, true),
							sourcePos.x, sourcePos.y, sourcePos.z,
							velocity.x * velocityModifier, 0.0F, velocity.z * velocityModifier
					);
				}
				
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_MEMORY_MANIFESTING_PARTICLES, (client, handler, buf, responseSender) -> {
			BlockPos position = buf.readBlockPos();
			int color1 = buf.readInt();
			int color2 = buf.readInt();
			int amount = buf.readInt();
			
			client.execute(() -> {
				RandomSource random = client.level.random;
				
				Vector3f colorVec1 = ColorHelper.colorIntToVec(color1);
				Vector3f colorVec2 = ColorHelper.colorIntToVec(color2);
				
				for (int i = 0; i < amount; i++) {
					int randomLifetime = 30 + random.nextInt(20);
					
					// color1
					client.level.addParticle(
							new DynamicParticleEffect(SpectrumParticleTypes.WHITE_CRAFTING, 0.5F, colorVec1, 1.0F, randomLifetime, false, true),
							position.getX() + 0.5, position.getY() + 0.5, position.getZ(),
							0.15 - random.nextFloat() * 0.3, random.nextFloat() * 0.15 + 0.1, 0.15 - random.nextFloat() * 0.3
					);
					
					// color2
					client.level.addParticle(
							new DynamicParticleEffect(SpectrumParticleTypes.WHITE_CRAFTING, 0.5F, colorVec2, 1.0F, randomLifetime, false, true),
							position.getX() + 0.5, position.getY(), position.getZ() + 0.5,
							0.15 - random.nextFloat() * 0.3, random.nextFloat() * 0.15 + 0.1, 0.15 - random.nextFloat() * 0.3
					);
				}
				
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PEDESTAL_UPGRADED_PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos position = buf.readBlockPos(); // the block pos of the pedestal
			PedestalRecipeTier tier = PedestalRecipeTier.values()[buf.readInt()]; // the item stack that was crafted
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				PedestalBlock.spawnUpgradeParticleEffectsForTier(position, tier);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PEDESTAL_START_CRAFTING_PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos position = buf.readBlockPos(); // the block pos of the pedestal
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				PedestalBlockEntity.spawnCraftingStartParticles(client.level, position);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.CHANGE_PARTICLE_SPAWNER_SETTINGS_CLIENT_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			ParticleSpawnerConfiguration configuration = ParticleSpawnerConfiguration.fromBuf(buf);

			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				if (client.level.getBlockEntity(pos) instanceof ParticleSpawnerBlockEntity particleSpawnerBlockEntity) {
					particleSpawnerBlockEntity.applySettings(configuration);
				}
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PASTEL_TRANSMISSION, (client, handler, buf, responseSender) -> {
			UUID networkUUID = buf.readUUID();
			int travelTime = buf.readInt();
			PastelTransmission transmission = PastelTransmission.fromPacket(buf);
			BlockPos spawnPos = transmission.getStartPos();
			int color = ColorHelper.getRandomColor(networkUUID.hashCode());
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				client.level.addParticle(new PastelTransmissionParticleEffect(transmission.getNodePositions(), transmission.getVariant().toStack(), travelTime, color), spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, 0, 0, 0);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.TYPED_TRANSMISSION, (client, handler, buf, responseSender) -> {
			TypedTransmission transmission = TypedTransmission.readFromBuf(buf);
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				switch (transmission.getVariant()) {
					case BLOCK_POS -> client.level.addAlwaysVisibleParticle(new BlockPosEventTransmissionParticleEffect(transmission.getDestination(), transmission.getArrivalInTicks()), true, transmission.getOrigin().x(), transmission.getOrigin().y(), transmission.getOrigin().z(), 0.0D, 0.0D, 0.0D);
					case ITEM -> client.level.addAlwaysVisibleParticle(new ItemTransmissionParticleEffect(transmission.getDestination(), transmission.getArrivalInTicks()), true, transmission.getOrigin().x(), transmission.getOrigin().y(), transmission.getOrigin().z(), 0.0D, 0.0D, 0.0D);
					case EXPERIENCE -> client.level.addAlwaysVisibleParticle(new ExperienceTransmissionParticleEffect(transmission.getDestination(), transmission.getArrivalInTicks()), true, transmission.getOrigin().x(), transmission.getOrigin().y(), transmission.getOrigin().z(), 0.0D, 0.0D, 0.0D);
					case HUMMINGSTONE -> client.level.addAlwaysVisibleParticle(new HummingstoneTransmissionParticleEffect(transmission.getDestination(), transmission.getArrivalInTicks()), true, transmission.getOrigin().x(), transmission.getOrigin().y(), transmission.getOrigin().z(), 0.0D, 0.0D, 0.0D);
					case REDSTONE -> client.level.addAlwaysVisibleParticle(new WirelessRedstoneTransmissionParticleEffect(transmission.getDestination(), transmission.getArrivalInTicks()), true, transmission.getOrigin().x(), transmission.getOrigin().y(), transmission.getOrigin().z(), 0.0D, 0.0D, 0.0D);
				}
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.COLOR_TRANSMISSION, (client, handler, buf, responseSender) -> {
			ColoredTransmission transmission = ColoredTransmission.readFromBuf(buf);
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				client.level.addAlwaysVisibleParticle(new ColoredTransmissionParticleEffect(transmission.getDestination(), transmission.getArrivalInTicks(), transmission.getDyeColor()), true, transmission.getOrigin().x(), transmission.getOrigin().y(), transmission.getOrigin().z(), 0.0D, 0.0D, 0.0D);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_BLOCK_BOUND_SOUND_INSTANCE, (client, handler, buf, responseSender) -> {
			if (SpectrumCommon.CONFIG.BlockSoundVolume > 0) {
				ResourceLocation soundEffectIdentifier = buf.readResourceLocation();
				ResourceLocation blockIdentifier = buf.readResourceLocation();
				BlockPos blockPos = buf.readBlockPos();
				int maxDurationTicks = buf.readInt();
				
				client.execute(() -> {
					if (soundEffectIdentifier.getPath().equals("stop")) {
						CraftingBlockSoundInstance.stopPlayingOnPos(blockPos);
					} else {
						SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundEffectIdentifier);
						Block block = BuiltInRegistries.BLOCK.get(blockIdentifier);
						
						CraftingBlockSoundInstance.startSoundInstance(soundEvent, blockPos, block, maxDurationTicks);
					}
				});
			}
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_TAKE_OFF_BELT_SOUND_INSTANCE, (client, handler, buf, responseSender) -> client.execute(TakeOffBeltSoundInstance::startSoundInstance));
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.UPDATE_BLOCK_ENTITY_INK, (client, handler, buf, responseSender) -> {
			BlockPos blockPos = buf.readBlockPos();
			long colorTotal = buf.readLong();
			
			int colorEntries = buf.readInt();
			Map<InkColor, Long> colors = new HashMap<>();
			for (int i = 0; i < colorEntries; i++) {
				colors.put(InkColor.of(buf.readUtf()), buf.readLong());
			}
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				BlockEntity blockEntity = client.level.getBlockEntity(blockPos);
				if (blockEntity instanceof InkStorageBlockEntity<?> inkStorageBlockEntity) {
					inkStorageBlockEntity.getEnergyStorage().setEnergy(colors, colorTotal);
				}
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.INK_COLOR_SELECTED, (client, handler, buf, responseSender) -> {
			AbstractContainerMenu screenHandler = client.player.containerMenu;
			if (screenHandler instanceof InkColorSelectedPacketReceiver inkColorSelectedPacketReceiver) {
				boolean isSelection = buf.readBoolean();
				
				InkColor color;
				if (isSelection) {
					String inkColorString = buf.readUtf();
					color = InkColor.of(inkColorString);
				} else {
					color = null;
				}
				inkColorSelectedPacketReceiver.onInkColorSelectedPacket(color);
			}
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_INK_EFFECT_PARTICLES, (client, handler, buf, responseSender) -> {
			InkColor inkColor = InkColor.of(buf.readUtf());
			double posX = buf.readDouble();
			double posY = buf.readDouble();
			double posZ = buf.readDouble();
			float potency = buf.readFloat();
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				InkSpellEffects.getEffect(inkColor).playEffects(client.level, new Vec3(posX, posY, posZ), potency);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_PRESENT_OPENING_PARTICLES, (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			int colorCount = buf.readInt();
			
			Map<DyeColor, Integer> colors = new HashMap<>();
			for (int i = 0; i < colorCount; i++) {
				DyeColor dyeColor = DyeColor.byId(buf.readByte());
				int amount = buf.readByte();
				colors.put(dyeColor, amount);
			}
			
			client.execute(() -> {
				// Everything in this lambda is running on the render thread
				PresentBlock.spawnParticles(client.level, pos, colors);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_ASCENSION_APPLIED_EFFECTS, (client, handler, buf, responseSender) -> client.execute(() -> {
			// Everything in this lambda is running on the render thread
			client.level.playSound(null, client.player.blockPosition(), SpectrumSoundEvents.FADING_PLACED, SoundSource.PLAYERS, 1.0F, 1.0F);
			client.getSoundManager().play(new DivinitySoundInstance());
		}));
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.PLAY_DIVINITY_APPLIED_EFFECTS, (client, handler, buf, responseSender) -> client.execute(() -> {
			// Everything in this lambda is running on the render thread
			LocalPlayer player = client.player;
			client.particleEngine.createTrackingEmitter(player, SpectrumParticleTypes.DIVINITY, 30);
			client.gameRenderer.displayItemActivation(SpectrumItems.DIVINATION_HEART.getDefaultInstance());
			client.level.playSound(null, player.blockPosition(), SpectrumSoundEvents.FAILING_PLACED, SoundSource.PLAYERS, 1.0F, 1.0F);
			
			ParticleHelper.playParticleWithPatternAndVelocityClient(player.level(), player.position(), SpectrumParticleTypes.WHITE_CRAFTING, VectorPattern.SIXTEEN, 0.4);
			ParticleHelper.playParticleWithPatternAndVelocityClient(player.level(), player.position(), SpectrumParticleTypes.RED_CRAFTING, VectorPattern.SIXTEEN, 0.4);
		}));
		
		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.MOONSTONE_BLAST, (client, handler, buf, responseSender) -> {
			LocalPlayer player = client.player;
			
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			float power = buf.readFloat();
			float knockback = buf.readFloat();
			double playerVelocityX = buf.readDouble();
			double playerVelocityY = buf.readDouble();
			double playerVelocityZ = buf.readDouble();
			
			client.execute(() -> {
				MoonstoneStrike.create(client.level, null, null, x, y, z, power, knockback);
				player.setDeltaMovement(player.getDeltaMovement().add(playerVelocityX, playerVelocityY, playerVelocityZ));
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(SpectrumS2CPackets.SYNC_ARTISANS_ATLAS, (client, handler, buf, responseSender) -> {
			String targetIdStr = buf.readUtf();
			ResourceLocation targetId = targetIdStr.length() == 0 ? null : new ResourceLocation(targetIdStr);

			ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket(buf);

			client.execute(() -> {
				PacketUtils.ensureRunningOnSameThread(packet, handler, client);
				MapRenderer mapRenderer = client.gameRenderer.getMapRenderer();
				int i = packet.getMapId();
				String string = MapItem.makeKey(i);

				if (client.level != null) {
					MapItemSavedData mapState = client.level.getMapData(string);

					if (mapState == null) {
						mapState = new ArtisansAtlasState(packet.getScale(), packet.isLocked(), client.level.dimension());
						client.level.overrideMapData(string, mapState);
					}

					if (mapState instanceof ArtisansAtlasState artisansAtlasState) {
						artisansAtlasState.setTargetId(targetId);
						packet.applyToMap(mapState);
						mapRenderer.update(i, mapState);
					}
				}
			});
		});
	}
	
}
