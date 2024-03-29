package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.items.tools.WorkstaffItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SpectrumC2SPacketSender {
	
	public static void sendGuidebookHintBoughtPaket(Ingredient ingredient) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		ingredient.toNetwork(packetByteBuf);
		ClientPlayNetworking.send(SpectrumC2SPackets.GUIDEBOOK_HINT_BOUGHT, packetByteBuf);
	}
	
	public static void sendConfirmationButtonPressedPaket(String queryToTrigger) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeUtf(queryToTrigger);
		ClientPlayNetworking.send(SpectrumC2SPackets.CONFIRMATION_BUTTON_PRESSED, packetByteBuf);
	}
	
	public static void sendBindEnderSpliceToPlayer(Player playerEntity) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeInt(playerEntity.getId());
		ClientPlayNetworking.send(SpectrumC2SPackets.BIND_ENDER_SPLICE_TO_PLAYER, packetByteBuf);
	}
	
	public static void sendInkColorSelectedInGUI(@Nullable InkColor color) {
		FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
		if (color == null) {
			packetByteBuf.writeBoolean(false);
		} else {
			packetByteBuf.writeBoolean(true);
			packetByteBuf.writeUtf(color.toString());
		}
		ClientPlayNetworking.send(SpectrumC2SPackets.INK_COLOR_SELECTED, packetByteBuf);
	}

    public static void sendWorkstaffToggle(WorkstaffItem.GUIToggle toggle) {
        FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeInt(toggle.ordinal());
        ClientPlayNetworking.send(SpectrumC2SPackets.WORKSTAFF_TOGGLE_SELECTED, packetByteBuf);
    }
	
}
