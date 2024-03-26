package de.dafuqs.spectrum.mixin.accessors;

import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(MapItemSavedData.class)
public interface MapStateAccessor {

    @Accessor(value = "trackingPosition")
    boolean getShowIcons();

    @Accessor(value = "unlimitedTracking")
    boolean getUnlimitedTracking();

    @Accessor(value = "bannerMarkers")
    Map<String, MapBanner> getBanners();

    @Accessor(value = "decorations")
    Map<String, MapDecoration> getIcons();

    @Accessor(value = "trackedDecorationCount")
    int getIconCount();

    @Accessor(value = "trackedDecorationCount")
    void setIconCount(int iconCount);

    @Invoker("setDecorationsDirty")
    void invokeMarkIconsDirty();

    @Invoker("removeDecoration")
    void invokeRemoveIcon(String id);

}
