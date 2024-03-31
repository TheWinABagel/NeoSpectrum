package de.dafuqs.spectrum.cca;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CapabilityHelper {

    public static <T> Optional<T> getComponentOptional(LivingEntity provider, Capability<T> cap) {
        //Can be empty
        return provider.getCapability(cap).resolve();
    }

    //Not entirely sure if this should be able to be null or not
    @Nullable
    public static <T> T getComponent(LivingEntity provider, Capability<T> cap) {
        return getComponentOptional(provider, cap).orElse(null);
    }
}
