package de.dafuqs.spectrum.cca.azure_dike;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

public interface AzureDikeCapability extends INBTSerializable<CompoundTag> { //todoforge should tick on server
	
	ResourceLocation AZURE_DIKE_BAR_TEXTURE = SpectrumCommon.locate("textures/gui/azure_dike_overlay.png");
	ResourceLocation AZURE_DIKE_ID = SpectrumCommon.locate("azure_dike");

	void setEntity(LivingEntity entity);

	int getProtection();
	
	int getMaxProtection();
	
	int getRechargeDelayDefault();
	
	int getCurrentRechargeDelay();
	
	int getRechargeDelayTicksAfterDamage();
	
	float absorbDamage(float incomingDamage);
	
	void set(int maxProtection, int rechargeDelayDefault, int fasterRechargeAfterDamageTicks, boolean resetCharge);
}