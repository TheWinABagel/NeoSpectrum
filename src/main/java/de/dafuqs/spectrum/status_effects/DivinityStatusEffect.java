package de.dafuqs.spectrum.status_effects;

import de.dafuqs.spectrum.helpers.ParticleHelper;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DivinityStatusEffect extends SpectrumStatusEffect {
	
	public static final int CIRCLET_AMPLIFIER = 0;
	public static final int ASCENSION_AMPLIFIER = 1;

	public DivinityStatusEffect(MobEffectCategory statusEffectCategory, int color) {
		super(statusEffectCategory, color);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		Level world = entity.level();
		if (amplifier > CIRCLET_AMPLIFIER && world.isClientSide) { // the circlet gives divinity 0, not showing effects; the ascension one does
			ParticleHelper.playParticleWithPatternAndVelocityClient(entity.level(), entity.position(), SpectrumParticleTypes.RED_CRAFTING, VectorPattern.EIGHT, 0.2);
		}
		boolean doEffects = 40 >> amplifier == 0;
		if (entity instanceof Player player) {
			if (!world.isClientSide) {
				SpectrumAdvancementCriteria.DIVINITY_TICK.trigger((ServerPlayer) player);
			}
			if (doEffects) {
				player.getFoodData().eat(1 + amplifier, 0.25F);
			}
		}

		if (doEffects) {
			if (entity.getHealth() < entity.getMaxHealth()) {
				entity.heal(amplifier / 2F);
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.addAttributeModifiers(entity, attributes, amplifier);
		Level world = entity.level();
		if (entity instanceof Player) {
			if (entity instanceof ServerPlayer player) {
				MobEffectInstance instance = entity.getEffect(SpectrumStatusEffects.DIVINITY);
				if (instance != null && !instance.isAmbient()) {
					SpectrumS2CPacketSender.playDivinityAppliedEffects(player);
				}
			} else if (world.isClientSide) { //todoforge IMC possibly?
//				FabricLoader.getInstance().getObjectShare().put("healthoverlay:forceHardcoreHearts", true);
			}
		}
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.removeAttributeModifiers(entity, attributes, amplifier);
		Level world = entity.level();
		if (world.isClientSide) {
//			FabricLoader.getInstance().getObjectShare().put("healthoverlay:forceHardcoreHearts", false);
		}
	}

}