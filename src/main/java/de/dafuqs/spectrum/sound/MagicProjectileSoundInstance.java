package de.dafuqs.spectrum.sound;

import de.dafuqs.spectrum.entity.entity.MagicProjectileEntity;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class MagicProjectileSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {

    private final ResourceKey<Level> worldKey;
    private final MagicProjectileEntity projectile;
    private final int maxDurationTicks = 280;

    private int ticksPlayed = 0;
    private boolean done;
    private boolean playedExplosion;

    protected MagicProjectileSoundInstance(ResourceKey<Level> worldKey, MagicProjectileEntity projectile) {
        super(SpectrumSoundEvents.INK_PROJECTILE_LAUNCH, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());

        this.worldKey = worldKey;
        this.projectile = projectile;

        this.attenuation = Attenuation.NONE;
        this.x = this.projectile.getX();
        this.y = this.projectile.getY();
        this.z = this.projectile.getZ();

        this.looping = false;
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Environment(EnvType.CLIENT)
    public static void startSoundInstance(MagicProjectileEntity inkProjectile) {
		Minecraft client = Minecraft.getInstance();
        MagicProjectileSoundInstance newInstance = new MagicProjectileSoundInstance(client.level.dimension(), inkProjectile);
        Minecraft.getInstance().getSoundManager().play(newInstance);
    }
	
	@Override
	public boolean isStopped() {
		return this.done;
	}
	
	@Override
	public boolean canStartSilent() {
		return true;
	}
	
	@Override
	public void tick() {
		Minecraft client = Minecraft.getInstance();
        this.ticksPlayed++;

        this.x = this.projectile.getX();
        this.y = this.projectile.getY();
        this.z = this.projectile.getZ();

        this.volume = Math.max(0.0F, 0.7F - Math.max(0.0F, projectile.blockPosition().distManhattan(client.player.blockPosition()) / 128F - 0.2F));

        if (ticksPlayed > maxDurationTicks
                || !Objects.equals(this.worldKey, Minecraft.getInstance().level.dimension())
                || projectile.isRemoved()) {

            this.setDone();
        }
    }
	
	protected final void setDone() {
		Minecraft client = Minecraft.getInstance();
		this.ticksPlayed = this.maxDurationTicks;
		this.done = true;
		this.looping = false;

		if (projectile.isRemoved() && !playedExplosion) {
			client.player.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, Math.max(0.1F, this.volume / 4), 1.1F + client.level.random.nextFloat() * 0.2F);
            spawnImpactParticles(this.projectile);
			playedExplosion = true;
		}
	}

    private void spawnImpactParticles(MagicProjectileEntity projectile) {
        DyeColor dyeColor = projectile.getDyeColor();
        Level world = projectile.level();
        Vec3 targetPos = projectile.position();
        Vec3 velocity = projectile.getDeltaMovement();

        world.addParticle(SpectrumParticleTypes.getExplosionParticle(dyeColor), targetPos.x, targetPos.y, targetPos.z, 0, 0, 0);
        for (int i = 0; i < 10; i++) {
            world.addParticle(SpectrumParticleTypes.getCraftingParticle(dyeColor), targetPos.x, targetPos.y, targetPos.z, -velocity.x * 3, -velocity.y * 3, -velocity.z * 3);
        }
    }

}