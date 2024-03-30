package de.dafuqs.spectrum.cca.azure_dike;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.atomic.AtomicReference;

public class AzureDikeProvider {
	public static final ComponentKey<AzureDikeCapability> AZURE_DIKE_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("azure_dike"), AzureDikeCapability.class); // See the "Registering your component" section

	/**
	 * Uses as much Azure Dike as possible to protect the Provider from incoming damage
	 *
	 * @param provider       The Component Provider
	 * @param incomingDamage The incoming damage
	 * @return All damage that could not be protected from
	 */
	public static float absorbDamage(LivingEntity provider, float incomingDamage) {
		var cap = provider.getCapability(DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY);
		if (cap.isPresent() && cap.resolve().isPresent()) {
			return cap.resolve().get().absorbDamage(incomingDamage);
		}
		return incomingDamage;
	}
	
	public static int getAzureDikeCharges(LivingEntity provider) {
		return AZURE_DIKE_COMPONENT.get(provider).getProtection();
	}
	
	public static int getMaxAzureDikeCharges(LivingEntity provider) {
		return AZURE_DIKE_COMPONENT.get(provider).getMaxProtection();
	}
	
	public static AzureDikeCapability getAzureDikeComponent(LivingEntity provider) {
		return AZURE_DIKE_COMPONENT.get(provider);
	}
	
}
