package de.dafuqs.spectrum.cca.everpromise_ribbon;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DefaultEverpromiseRibbonCapability implements Component {
	
	public static final ComponentKey<DefaultEverpromiseRibbonCapability> EVERPROMISE_RIBBON_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("everpromise_ribbon"), DefaultEverpromiseRibbonCapability.class);
	
	private boolean hasRibbon = false;
	
	// this is not optional
	// removing this empty constructor will make the world not load
	public DefaultEverpromiseRibbonCapability() {
	
	}
	
	public DefaultEverpromiseRibbonCapability(LivingEntity entity) {
	
	}
	
	@Override
	public void writeToNbt(@NotNull CompoundTag tag) {
		if (this.hasRibbon) {
			tag.putBoolean("has_everpromise_ribbon", true);
		}
	}
	
	@Override
	public void readFromNbt(CompoundTag tag) {
		this.hasRibbon = tag.getBoolean("has_everpromise_ribbon");
	}
	
	public static void attachRibbon(LivingEntity livingEntity) {
		DefaultEverpromiseRibbonCapability component = EVERPROMISE_RIBBON_COMPONENT.get(livingEntity);
		component.hasRibbon = true;
	}
	
	public static boolean hasRibbon(LivingEntity livingEntity) {
		DefaultEverpromiseRibbonCapability component = EVERPROMISE_RIBBON_COMPONENT.get(livingEntity);
		return component.hasRibbon;
	}
	
}
