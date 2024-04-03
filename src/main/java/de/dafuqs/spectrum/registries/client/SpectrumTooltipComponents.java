package de.dafuqs.spectrum.registries.client;

import de.dafuqs.spectrum.items.tooltip.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class SpectrumTooltipComponents {
    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(CraftingTabletTooltipData.class, CraftingTabletTooltipComponent::new);
        e.register(BottomlessBundleTooltipData.class, BottomlessBundleTooltipComponent::new);
        e.register(PresentTooltipData.class, PresentTooltipComponent::new);
    }
}
