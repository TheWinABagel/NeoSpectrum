package de.dafuqs.spectrum.cca.azure_dike;

import de.dafuqs.spectrum.SpectrumCommon;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.world.entity.LivingEntity;

public class AzureDikeProvider {
	
	public static final ComponentKey<AzureDikeComponent> AZURE_DIKE_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("azure_dike"), AzureDikeComponent.class); // See the "Registering your component" section
	
	/**
	 * Uses as much Azure Dike as possible to protect the Provider from incoming damage
	 *
	 * @param provider       The Component Provider
	 * @param incomingDamage The incoming damage
	 * @return All damage that could not be protected from
	 */
	public static float absorbDamage(LivingEntity provider, float incomingDamage) {
		return AZURE_DIKE_COMPONENT.get(provider).absorbDamage(incomingDamage);
	}
	
	public static int getAzureDikeCharges(LivingEntity provider) {
		return AZURE_DIKE_COMPONENT.get(provider).getProtection();
	}
	
	public static int getMaxAzureDikeCharges(LivingEntity provider) {
		return AZURE_DIKE_COMPONENT.get(provider).getMaxProtection();
	}
	
	public static AzureDikeComponent getAzureDikeComponent(LivingEntity provider) {
		return AZURE_DIKE_COMPONENT.get(provider);
	}
	
}
