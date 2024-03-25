package de.dafuqs.spectrum.cca.azure_dike;

import de.dafuqs.spectrum.SpectrumCommon;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.resources.ResourceLocation;

public interface AzureDikeComponent extends ServerTickingComponent {
	
	ResourceLocation AZURE_DIKE_BAR_TEXTURE = SpectrumCommon.locate("textures/gui/azure_dike_overlay.png");
	
	int getProtection();
	
	int getMaxProtection();
	
	int getRechargeDelayDefault();
	
	int getCurrentRechargeDelay();
	
	int getRechargeDelayTicksAfterDamage();
	
	float absorbDamage(float incomingDamage);
	
	void set(int maxProtection, int rechargeDelayDefault, int fasterRechargeAfterDamageTicks, boolean resetCharge);
}