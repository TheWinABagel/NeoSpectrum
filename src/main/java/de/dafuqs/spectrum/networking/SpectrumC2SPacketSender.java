package de.dafuqs.spectrum.networking;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.items.tools.WorkstaffItem;
import io.netty.buffer.Unpooled;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class SpectrumC2SPacketSender { //todoforge packets
	
	public static void sendGuidebookHintBoughtPaket(Ingredient ingredient) {
		FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(Unpooled.buffer());
		ingredient.toNetwork(packetByteBuf);
//		ClientPlayNetworking.send(SpectrumC2SPackets.GUIDEBOOK_HINT_BOUGHT, packetByteBuf);
	}
	
	public static void sendConfirmationButtonPressedPaket(String queryToTrigger) {
		FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(Unpooled.buffer());
		packetByteBuf.writeUtf(queryToTrigger);
//		ClientPlayNetworking.send(SpectrumC2SPackets.CONFIRMATION_BUTTON_PRESSED, packetByteBuf);
	}
	
	public static void sendBindEnderSpliceToPlayer(Player playerEntity) {
		FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(Unpooled.buffer());
		packetByteBuf.writeInt(playerEntity.getId());
//		ClientPlayNetworking.send(SpectrumC2SPackets.BIND_ENDER_SPLICE_TO_PLAYER, packetByteBuf);
	}
	
	public static void sendInkColorSelectedInGUI(@Nullable InkColor color) {
		FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(Unpooled.buffer());
		if (color == null) {
			packetByteBuf.writeBoolean(false);
		} else {
			packetByteBuf.writeBoolean(true);
			packetByteBuf.writeUtf(color.toString());
		}
//		ClientPlayNetworking.send(SpectrumC2SPackets.INK_COLOR_SELECTED, packetByteBuf);
	}

    public static void sendWorkstaffToggle(WorkstaffItem.GUIToggle toggle) {
        FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        packetByteBuf.writeInt(toggle.ordinal());
//        ClientPlayNetworking.send(SpectrumC2SPackets.WORKSTAFF_TOGGLE_SELECTED, packetByteBuf);
    }
	
}
