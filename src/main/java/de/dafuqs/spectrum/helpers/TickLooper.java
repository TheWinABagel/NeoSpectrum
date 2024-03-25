package de.dafuqs.spectrum.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

// TODO: migrate to net.minecraft.world.tick ?
public final class TickLooper {

    private int currentTick;
    private int maxTick;

    public TickLooper(int maxTick) {
        this.maxTick = maxTick;
    }

    public void tick() {
        currentTick++;
    }

    public boolean reachedCap() {
        return currentTick >= maxTick;
    }

    public void reset() {
        currentTick = 0;
    }

    public int getTick() {
        return currentTick;
    }

    public int getMaxTick() {
        return maxTick;
    }

    public float getProgress() {
        return (float) currentTick / (float) maxTick;
    }
    
    @Override
    public String toString() {
        return "TickLooper (" + currentTick + "/" + maxTick + ")";
    }
    
    public void readNbt(CompoundTag nbt) {
        maxTick = nbt.getInt("max");
        currentTick = nbt.getInt("current");
    }
    
    public Tag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("max", maxTick);
        nbt.putInt("current", currentTick);
        return nbt;
    }
}