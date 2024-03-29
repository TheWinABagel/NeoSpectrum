package de.dafuqs.spectrum.particle.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.VibrationSignalParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class TransmissionParticle extends VibrationSignalParticle {

	public TransmissionParticle(ClientLevel world, double x, double y, double z, PositionSource positionSource, int maxAge) {
		super(world, x, y, z, positionSource, maxAge);
		this.quadSize = 0.175F;
		this.alpha = 0.8F;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		final Vec3 cameraPos = camera.getPosition();
		final float x = (float) (Mth.lerp(tickDelta, xo, this.x) - cameraPos.x());
		final float y = (float) (Mth.lerp(tickDelta, yo, this.y) - cameraPos.y());
		final float z = (float) (Mth.lerp(tickDelta, zo, this.z) - cameraPos.z());
		final int light = getLightColor(tickDelta);

		final Quaternionf quaternion = camera.rotation();
		final Vector3f[] vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		final float size = getQuadSize(tickDelta);

		for (int k = 0; k < 4; ++k) {
			final Vector3f vec2 = vec3fs[k];
			vec2.rotate(quaternion);
			vec2.mul(size);
			vec2.add(x, y, z);
		}

		final float minU = getU0();
		final float maxU = getU1();
		final float minV = getV0();
		final float maxV = getV1();
		vertexConsumer.vertex(vec3fs[0].x(), vec3fs[0].y(), vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
		vertexConsumer.vertex(vec3fs[1].x(), vec3fs[1].y(), vec3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
		vertexConsumer.vertex(vec3fs[2].x(), vec3fs[2].y(), vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
		vertexConsumer.vertex(vec3fs[3].x(), vec3fs[3].y(), vec3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
	}
	
}
