package de.dafuqs.spectrum.spells;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.blocks.idols.FirestarterIdolBlock;
import de.dafuqs.spectrum.helpers.BlockVariantHelper;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InkSpellEffects {
	
	public static final Map<InkColor, InkSpellEffect> effects = new HashMap<>();
	
	public static @Nullable InkSpellEffect getEffect(InkColor inkColor) {
		return effects.getOrDefault(inkColor, null);
	}
	
	public static void registerEffect(InkSpellEffect effect) {
		effects.put(effect.color, effect);
	}
	
	public static void register() {
		
		registerEffect(new InkSpellEffect(InkColors.PINK) {
			@Override
			public void playEffects(Level world, Vec3 origin, float potency) {
				int count = 12 + (int) (potency * 3);
				RandomSource random = world.random;
				for (int i = 0; i < count; i++) {
					world.addParticle(ParticleTypes.WAX_OFF,
							origin.x + potency - random.nextFloat() * potency * 2,
							origin.y + potency - random.nextFloat() * potency * 2,
							origin.z + potency - random.nextFloat() * potency * 2,
							0, 0, 0);
				}
			}
			
			@Override
            void affectEntity(Entity entity, Vec3 origin, float potency) {
				// heal living entities
				Level world = entity.level();
				if (entity instanceof LivingEntity livingEntity && (livingEntity.getHealth() < livingEntity.getMaxHealth() || livingEntity.isInvertedHealAndHarm())) {
					float amount = potency - (float) entity.position().distanceTo(origin);
					if (amount >= 1) {
						livingEntity.heal(amount);
						entity.level().playSound(null, entity.blockPosition(), SpectrumSoundEvents.BLOCK_CITRINE_BLOCK_CHIME, SoundSource.NEUTRAL, 1.0F, 0.9F + world.random.nextFloat() * 0.2F);
						SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) entity.level(), entity.position(), ParticleTypes.WAX_OFF, 10, new Vec3(0.5, 0.5, 0.5), new Vec3(0, 0, 0));
					}
				}
			}
			
			@Override
			void affectArea(Level world, BlockPos origin, float potency) {
				// repair damaged blocks
				int range = Support.getIntFromDecimalWithChance(potency, world.random);
				for (BlockPos blockPos : BlockPos.withinManhattan(origin, range, range, range)) {
					Block repairedBlock = BlockVariantHelper.getCursedRepairedBlockVariant(world, blockPos);
					if (repairedBlock != Blocks.AIR) {
						world.setBlockAndUpdate(blockPos, repairedBlock.defaultBlockState());
					}
				}
			}
		});
		
		registerEffect(new InkSpellEffect(InkColors.ORANGE) {
			@Override
			public void playEffects(Level world, Vec3 origin, float potency) {
				world.addParticle(ParticleTypes.EXPLOSION_EMITTER, origin.x, origin.y, origin.z, 0, 0, 0);
				world.playLocalSound(origin.x, origin.y, origin.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F, false);
				
				int count = 10 + (int) (potency * 3);
				RandomSource random = world.random;
				for (int i = 0; i < count; i++) {
					world.addParticle(ParticleTypes.DRIPPING_LAVA,
							origin.x + potency - random.nextFloat() * potency * 2,
							origin.y + potency - random.nextFloat() * potency * 2,
							origin.z + potency - random.nextFloat() * potency * 2,
							0.2 - random.nextFloat() * 0.4, -0.5, 0.2 - random.nextFloat() * 0.4);
				}
			}
			
			@Override
            void affectEntity(Entity entity, Vec3 origin, float potency) {
				Level world = entity.level();
				// set entities on fire
				if (!entity.fireImmune()) {
					int duration = (int) (10 * potency) - (int) (5 * entity.position().distanceTo(origin));
					if (duration >= 1) {
						entity.setRemainingFireTicks(duration);
						entity.level().playSound(null, entity.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.NEUTRAL, 1.0F, 0.9F + world.random.nextFloat() * 0.2F);
						SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) entity.level(), entity.position(), ParticleTypes.ASH, 10, new Vec3(0.5, 0.5, 0.5), new Vec3(0, 0, 0));
					}
				}
			}
			
			@Override
			void affectArea(Level world, BlockPos origin, float potency) {
				// burn & cause fires
				if (world instanceof ServerLevel serverWorld) {
					int range = Support.getIntFromDecimalWithChance(potency, world.random);
					for (BlockPos blockPos : BlockPos.withinManhattan(origin, range, range, range)) {
						int distance = 1 + blockPos.distManhattan(origin);
						float div = (float) range / distance;
						if (div >= 1 || world.random.nextFloat() < div) {
							FirestarterIdolBlock.causeFire(serverWorld, blockPos, Direction.getRandom(world.random));
						}
					}
				}
			}
		});
		
		
	}
	
}
