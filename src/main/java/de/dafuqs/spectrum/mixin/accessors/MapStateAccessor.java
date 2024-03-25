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

    @Accessor(value = "showIcons")
    boolean getShowIcons();

    @Accessor(value = "unlimitedTracking")
    boolean getUnlimitedTracking();

    @Accessor(value = "banners")
    Map<String, MapBanner> getBanners();

    @Accessor(value = "icons")
    Map<String, MapDecoration> getIcons();

    @Accessor(value = "iconCount")
    int getIconCount();

    @Accessor(value = "iconCount")
    void setIconCount(int iconCount);

    @Invoker("markIconsDirty")
    void invokeMarkIconsDirty();

    @Invoker("removeIcon")
    void invokeRemoveIcon(String id);

}
