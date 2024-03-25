package de.dafuqs.spectrum.entity.render;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.entity.SpectrumFishingBobberEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LagoonFishingBobberEntityRenderer extends SpectrumFishingBobberEntityRenderer {
	
	protected final ResourceLocation TEXTURE = SpectrumCommon.locate("textures/entity/fishing_hooks/lagoon_fishing_hook.png");
	protected final ResourceLocation TEXTURE_OPEN_WATERS = SpectrumCommon.locate("textures/entity/fishing_hooks/lagoon_fishing_hook_open_waters.png");
	protected final RenderType LAYER = RenderType.entityCutout(TEXTURE);
	protected final RenderType LAYER_OPEN_WATERS = RenderType.entityCutout(TEXTURE_OPEN_WATERS);
	
	public LagoonFishingBobberEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	public ResourceLocation getTextureLocation(SpectrumFishingBobberEntity fishingBobberEntity) {
		if (fishingBobberEntity.isInTheOpen()) {
			return TEXTURE_OPEN_WATERS;
		} else {
			return TEXTURE;
		}
	}
	
	@Override
	public RenderType getLayer(SpectrumFishingBobberEntity bobber) {
		if (bobber.isInTheOpen(bobber.blockPosition())) {
			return LAYER_OPEN_WATERS;
		} else {
			return LAYER;
		}
	}
	
}
