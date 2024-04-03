package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.items.map.ArtisansAtlasState;
import de.dafuqs.spectrum.networking.SpectrumS2CPackets;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

@Mixin(MapItemSavedData.HoldingPlayer.class)
public class MapStatePlayerUpdateTrackerMixin {

    @Shadow
    @Final
    public Player player;

    @Inject(
            method = "nextUpdatePacket",
            at = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ClientboundMapItemDataPacket"), //todoforge Probably breaks, packets stuff
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void spectrum$getArtisansAtlasPacket(int mapId, CallbackInfoReturnable<Packet<?>> cir, MapItemSavedData.MapPatch updateData, Collection<MapDecoration> icons) {
        Level world = player.level();
        if (world != null) {
            String mapStr = MapItem.makeKey(mapId);
            MapItemSavedData state = world.getMapData(mapStr);
            if (state instanceof ArtisansAtlasState artisansAtlasState) {
                ClientboundMapItemDataPacket mapUpdateS2CPacket = new ClientboundMapItemDataPacket(mapId, state.scale, state.locked, icons, updateData);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

                ResourceLocation targetId = artisansAtlasState.getTargetId();
                if (targetId == null) {
                    buf.writeUtf("");
                } else {
                    buf.writeUtf(targetId.toString());
                }

                mapUpdateS2CPacket.write(buf);

//                Packet<?> packet = ServerPlayNetworking.createS2CPacket(SpectrumS2CPackets.SYNC_ARTISANS_ATLAS, buf);
//                cir.setReturnValue(packet);
            }
        }
    }
}
