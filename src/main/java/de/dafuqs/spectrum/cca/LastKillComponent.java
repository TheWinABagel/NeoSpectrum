package de.dafuqs.spectrum.cca;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public class LastKillComponent implements INBTSerializable<CompoundTag> {
	
//	public static final ComponentKey<LastKillComponent> LAST_KILL_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("last_kill"), LastKillComponent.class);
	public static final ResourceLocation ID = SpectrumCommon.locate("last_kill");
	public static final Capability<LastKillComponent> LAST_KILL_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	private long lastKillTick = -1;
	
	// this is not optional
	// removing this empty constructor will make the world not load
	public LastKillComponent() {
	
	}
	
	public LastKillComponent(LivingEntity entity) {
	
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (this.lastKillTick >= 0) {
			tag.putLong("last_kill_tick", this.lastKillTick);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		if (tag.contains("last_kill_tick", Tag.TAG_LONG)) {
			this.lastKillTick = tag.getLong("last_kill_tick");
		}
	}

	public static void rememberKillTick(LivingEntity livingEntity, long tick) {
		LastKillComponent component = CapabilityHelper.getComponent(livingEntity, LAST_KILL_CAPABILITY);
		if (component == null) return;
		component.lastKillTick = tick;
	}
	
	public static long getLastKillTick(LivingEntity livingEntity) {
		LastKillComponent component = CapabilityHelper.getComponent(livingEntity, LAST_KILL_CAPABILITY);
		if (component == null) return 0L;
		return component.lastKillTick;
	}


}
