package de.dafuqs.spectrum.cca;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EverpromiseRibbonCapability implements BaseSpectrumCapability { //todoforge move to capability on 1.20.4
	public static final ResourceLocation ID = SpectrumCommon.locate("everpromise_ribbon");
	public static final Capability<EverpromiseRibbonCapability> EVERPROMISE_RIBBON_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	private boolean hasRibbon = false;
	private LivingEntity entity;

	public static void attachRibbon(LivingEntity livingEntity) {
		livingEntity.getPersistentData().putBoolean("has_everpromise_ribbon", true);
//		DefaultEverpromiseRibbonCapability component = EVERPROMISE_RIBBON_COMPONENT.get(livingEntity);
//		component.hasRibbon = true;
	}
	
	public static boolean hasRibbon(LivingEntity livingEntity) {
//		DefaultEverpromiseRibbonCapability component = EVERPROMISE_RIBBON_COMPONENT.get(livingEntity);
		return livingEntity.getPersistentData().getBoolean("has_everpromise_ribbon");
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (this.hasRibbon) {
			tag.putBoolean("has_everpromise_ribbon", true);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.hasRibbon = tag.getBoolean("has_everpromise_ribbon");
	}

	@Override
	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	@Override
	public LivingEntity getEntity() {
		return this.entity;
	}
}
