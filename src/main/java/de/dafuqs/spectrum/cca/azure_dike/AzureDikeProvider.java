package de.dafuqs.spectrum.cca.azure_dike;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.CapabilityHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AzureDikeProvider {
//	public static final ComponentKey<AzureDikeCapability> AZURE_DIKE_COMPONENT = ComponentRegistry.getOrCreate(SpectrumCommon.locate("azure_dike"), AzureDikeCapability.class); // See the "Registering your component" section

	/**
	 * Uses as much Azure Dike as possible to protect the Provider from incoming damage
	 *
	 * @param provider       The Component Provider
	 * @param incomingDamage The incoming damage
	 * @return All damage that could not be protected from
	 */
	public static float absorbDamage(LivingEntity provider, float incomingDamage) {
        return CapabilityHelper.getComponentOptional(provider, DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY).map(azureDikeCapability -> azureDikeCapability.absorbDamage(incomingDamage)).orElse(incomingDamage);
    }
	
	public static int getAzureDikeCharges(LivingEntity provider) {
        return CapabilityHelper.getComponentOptional(provider, DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY).map(AzureDikeCapability::getProtection).orElse(0);
    }
	
	public static int getMaxAzureDikeCharges(LivingEntity provider) {
		return CapabilityHelper.getComponentOptional(provider, DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY).map(AzureDikeCapability::getMaxProtection).orElse(0);
	}

	public static Optional<AzureDikeCapability> getAzureDikeCapOptional(LivingEntity provider) {
		//Can be empty
		return provider.getCapability(DefaultAzureDikeCapability.AZURE_DIKE_CAPABILITY).resolve();
	}

	//Not entirely sure if this should be able to be null or not
	@Nullable
	public static AzureDikeCapability getAzureDikeComponent(LivingEntity provider) {
		return getAzureDikeCapOptional(provider).orElse(null);
	}
	
}
