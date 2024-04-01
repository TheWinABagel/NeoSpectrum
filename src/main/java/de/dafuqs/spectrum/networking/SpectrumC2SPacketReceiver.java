package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.api.block.InkColorSelectedPacketReceiver;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.blocks.chests.CompactingChestBlockEntity;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerBlockEntity;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerConfiguration;
import de.dafuqs.spectrum.helpers.InventoryHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.inventories.BedrockAnvilScreenHandler;
import de.dafuqs.spectrum.inventories.CompactingChestScreenHandler;
import de.dafuqs.spectrum.inventories.ParticleSpawnerScreenHandler;
import de.dafuqs.spectrum.inventories.WorkstaffScreenHandler;
import de.dafuqs.spectrum.items.magic_items.EnderSpliceItem;
import de.dafuqs.spectrum.items.tools.WorkstaffItem;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class SpectrumC2SPacketReceiver { //todoforge packets on 1.20.4
	
	public static void registerC2SReceivers() {
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.RENAME_ITEM_IN_BEDROCK_ANVIL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
//			if (player.containerMenu instanceof BedrockAnvilScreenHandler bedrockAnvilScreenHandler) {
//				String name = buf.readUtf();
//				String string = SharedConstants.filterText(name);
//				if (string.length() <= 50) {
//					bedrockAnvilScreenHandler.setNewItemName(string);
//				}
//			}
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.ADD_LORE_IN_BEDROCK_ANVIL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
//			if (player.containerMenu instanceof BedrockAnvilScreenHandler bedrockAnvilScreenHandler) {
//				String lore = buf.readUtf();
//				String string = SharedConstants.filterText(lore);
//				if (string.length() <= 256) {
//					bedrockAnvilScreenHandler.setNewItemLore(string);
//				}
//			}
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.CHANGE_PARTICLE_SPAWNER_SETTINGS_PACKET_ID, (server, player, handler, buf, responseSender) -> {
//			// receive the client packet...
//			if (player.containerMenu instanceof ParticleSpawnerScreenHandler particleSpawnerScreenHandler) {
//				ParticleSpawnerBlockEntity blockEntity = particleSpawnerScreenHandler.getBlockEntity();
//				if (blockEntity != null) {
//					/// ...apply the new settings...
//					ParticleSpawnerConfiguration configuration = ParticleSpawnerConfiguration.fromBuf(buf);
//					blockEntity.applySettings(configuration);
//
//					// ...and distribute it to all clients again
//					FriendlyByteBuf outgoingBuf = PacketByteBufs.create();
//					outgoingBuf.writeBlockPos(blockEntity.getBlockPos());
//					configuration.write(outgoingBuf);
//
//					// Iterate over all players tracking a position in the world and send the packet to each player
//					for (ServerPlayer serverPlayerEntity : PlayerLookup.tracking((ServerLevel) blockEntity.getLevel(), blockEntity.getBlockPos())) {
//						ServerPlayNetworking.send(serverPlayerEntity, SpectrumS2CPackets.CHANGE_PARTICLE_SPAWNER_SETTINGS_CLIENT_PACKET_ID, outgoingBuf);
//					}
//				}
//			}
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.CHANGE_COMPACTING_CHEST_SETTINGS_PACKET_ID, (server, player, handler, buf, responseSender) -> {
//			// receive the client packet...
//			if (player.containerMenu instanceof CompactingChestScreenHandler compactingChestScreenHandler) {
//				BlockEntity blockEntity = compactingChestScreenHandler.getBlockEntity();
//				if (blockEntity instanceof CompactingChestBlockEntity compactingChestBlockEntity) {
//					/// ...apply the new settings...
//					compactingChestBlockEntity.applySettings(buf);
//				}
//			}
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.GUIDEBOOK_HINT_BOUGHT, (server, player, handler, buf, responseSender) -> {
//			// pay cost
//			Ingredient payment = Ingredient.fromNetwork(buf);
//
//			for (ItemStack remainder : InventoryHelper.removeFromInventoryWithRemainders(List.of(payment), player.getInventory())) {
//				InventoryHelper.smartAddToInventory(remainder, player.getInventory(), null);
//			}
//
//			// give the player the hidden "used_tip" advancement and play a sound
//			Support.grantAdvancementCriterion(player, "hidden/used_tip", "used_tip");
//			player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.CONFIRMATION_BUTTON_PRESSED, (server, player, handler, buf, responseSender) -> {
//			String confirmationString = buf.readUtf();
//			SpectrumAdvancementCriteria.CONFIRMATION_BUTTON_PRESSED.trigger(player, confirmationString);
//			player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.BIND_ENDER_SPLICE_TO_PLAYER, (server, player, handler, buf, responseSender) -> {
//			int entityId = buf.readInt();
//			Entity entity = player.level().getEntity(entityId);
//			if (entity instanceof ServerPlayer targetPlayerEntity
//					&& player.distanceTo(targetPlayerEntity) < 8
//					&& player.getMainHandItem().is(SpectrumItems.ENDER_SPLICE)) {
//
//				EnderSpliceItem.setTeleportTargetPlayer(player.getMainHandItem(), targetPlayerEntity);
//
//				player.playNotifySound(SpectrumSoundEvents.ENDER_SPLICE_BOUND, SoundSource.PLAYERS, 1.0F, 1.0F);
//				targetPlayerEntity.playNotifySound(SpectrumSoundEvents.ENDER_SPLICE_BOUND, SoundSource.PLAYERS, 1.0F, 1.0F);
//			}
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.INK_COLOR_SELECTED, (server, player, handler, buf, responseSender) -> {
//			AbstractContainerMenu screenHandler = player.containerMenu;
//			if (screenHandler instanceof InkColorSelectedPacketReceiver inkColorSelectedPacketReceiver) {
//				boolean isSelection = buf.readBoolean();
//
//				InkColor color;
//				if (isSelection) {
//					String inkColorString = buf.readUtf();
//					color = InkColor.of(inkColorString);
//				} else {
//					color = null;
//				}
//
//				// send the newly selected color to all players that have the same gui open
//				// this is minus the player that selected that entry (since they have that info already)
//				inkColorSelectedPacketReceiver.onInkColorSelectedPacket(color);
//				for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
//					if (serverPlayer.containerMenu instanceof InkColorSelectedPacketReceiver receiver && receiver.getBlockEntity() != null && receiver.getBlockEntity() == inkColorSelectedPacketReceiver.getBlockEntity()) {
//						SpectrumS2CPacketSender.sendInkColorSelected(color, serverPlayer);
//					}
//				}
//			}
//		});
//
//		ServerPlayNetworking.registerGlobalReceiver(SpectrumC2SPackets.WORKSTAFF_TOGGLE_SELECTED, (server, player, handler, buf, responseSender) -> {
//			AbstractContainerMenu screenHandler = player.containerMenu;
//			if (screenHandler instanceof WorkstaffScreenHandler workstaffScreenHandler) {
//                WorkstaffItem.GUIToggle toggle = WorkstaffItem.GUIToggle.values()[buf.readInt()];
//				workstaffScreenHandler.onWorkstaffToggleSelectionPacket(toggle);
//			}
//		});
//
	}
	
}
