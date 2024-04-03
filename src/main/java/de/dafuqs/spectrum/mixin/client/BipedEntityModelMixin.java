package de.dafuqs.spectrum.mixin.client;

import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(HumanoidModel.class)
public class BipedEntityModelMixin {

	@Shadow
	@Final
	public ModelPart rightArm;
	@Shadow
	@Final
	public ModelPart leftArm;
	@Shadow
	@Final
	public ModelPart rightLeg;
	@Shadow
	@Final
	public ModelPart leftLeg;

	@Inject(method = {"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at = @At("TAIL"), cancellable = true)
	public void poseArms(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		Optional<ICuriosItemHandler> curiosComponent = CuriosApi.getCuriosInventory(livingEntity).resolve();
		if (curiosComponent.isPresent() && !curiosComponent.get().findCurios(SpectrumItems.NEAT_RING).isEmpty()) {
			this.rightLeg.xRot = 0;
			this.rightLeg.yRot = 0;
			this.leftLeg.xRot = 0;
			this.leftLeg.yRot = 0;

			this.rightArm.xRot = (float) Math.PI / 2;
			this.rightArm.yRot = -1.5F;
			this.leftArm.xRot = (float) Math.PI / 2;
			this.leftArm.yRot = 1.5F;

			ci.cancel();
		}
	}
}
