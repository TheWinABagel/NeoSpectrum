package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.items.map.ArtisansAtlasState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MapItemSavedData.class)
public class MapStateMixin {

    // Caches the created state between the two mixins
    @Nullable
    private static ArtisansAtlasState atlasState = null;

    @Inject(method = "fromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;<init>(IIBZZZLnet/minecraft/registry/RegistryKey;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void spectrum$fromNbt_newMapState(CompoundTag nbt, CallbackInfoReturnable<MapItemSavedData> cir, ResourceKey<Level> dimension, int centerX, int centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked) {
        if (nbt.contains("isArtisansAtlas", Tag.TAG_BYTE) && nbt.getBoolean("isArtisansAtlas")) {
            atlasState = new ArtisansAtlasState(centerX, centerZ, scale, showIcons, unlimitedTracking, locked, dimension, nbt);
        }
    }

    @ModifyVariable(
            method = "fromNbt",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;<init>(IIBZZZLnet/minecraft/registry/RegistryKey;)V"),
                    to = @At(value = "TAIL")
            ),
            at = @At(value = "STORE")
    )
    private static MapItemSavedData spectrum$fromNbt_storeMapState(MapItemSavedData vanillaState) {
        if (atlasState != null) {
            ArtisansAtlasState state = atlasState;
            atlasState = null;
            return state;
        }
        return vanillaState;
    }

}
