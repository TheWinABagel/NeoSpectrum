package de.dafuqs.spectrum.cca;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public interface BaseSpectrumCapability extends INBTSerializable<CompoundTag> {

    void setEntity(LivingEntity entity);
    LivingEntity getEntity();

    default void sync() {
        if (this.getEntity() instanceof ServerPlayer serverPlayer) {
            //todoforge sync stuff
        }
    }

    default void syncToWatching() {
        //todoforge :pain:
    }
}
