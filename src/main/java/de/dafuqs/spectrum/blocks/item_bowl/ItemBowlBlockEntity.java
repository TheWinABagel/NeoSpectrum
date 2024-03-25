package de.dafuqs.spectrum.blocks.item_bowl;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.color.ColorRegistry;
import de.dafuqs.spectrum.blocks.InWorldInteractionBlockEntity;
import de.dafuqs.spectrum.events.ExactPositionSource;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.effect.ColoredTransmission;
import de.dafuqs.spectrum.particle.effect.ColoredTransmissionParticleEffect;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemBowlBlockEntity extends InWorldInteractionBlockEntity {
	
	protected static final int INVENTORY_SIZE = 1;
	
	public ItemBowlBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.ITEM_BOWL, pos, state, INVENTORY_SIZE);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (!this.deserializeLootTable(nbt)) {
			ContainerHelper.loadAllItems(nbt, this.items);
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (!this.serializeLootTable(nbt)) {
			ContainerHelper.saveAllItems(nbt, this.items);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		this.checkLootInteraction(null);
		return super.getUpdateTag();
	}
	
	public static void clientTick(@NotNull Level world, BlockPos blockPos, BlockState blockState, ItemBowlBlockEntity itemBowlBlockEntity) {
		ItemStack storedStack = itemBowlBlockEntity.getItem(0);
		if (!storedStack.isEmpty()) {
			Optional<DyeColor> optionalItemColor = ColorRegistry.ITEM_COLORS.getMapping(storedStack.getItem());
			if (optionalItemColor.isPresent()) {
				int particleCount = Support.getIntFromDecimalWithChance(Math.max(0.1, (float) storedStack.getCount() / (storedStack.getMaxStackSize() * 2)), world.random);
				spawnRisingParticles(world, blockPos, storedStack, particleCount);
			}
		}
	}
	
	public static void spawnRisingParticles(Level world, BlockPos blockPos, ItemStack itemStack, int amount) {
		if (amount > 0) {
			Optional<DyeColor> optionalItemColor = ColorRegistry.ITEM_COLORS.getMapping(itemStack.getItem());
			if (optionalItemColor.isPresent()) {
				ParticleOptions particleEffect = SpectrumParticleTypes.getSparkleRisingParticle(optionalItemColor.get());
				
				for (int i = 0; i < amount; i++) {
					float randomX = 0.1F + world.random.nextFloat() * 0.8F;
					float randomZ = 0.1F + world.random.nextFloat() * 0.8F;
					world.addParticle(particleEffect, blockPos.getX() + randomX, blockPos.getY() + 0.75, blockPos.getZ() + randomZ, 0.0D, 0.05D, 0.0D);
				}
			}
		}
	}
	
	public int decrementBowlStack(Vec3 orbTargetPos, int amount, boolean doEffects) {
		ItemStack storedStack = this.getItem(0);
		if (storedStack.isEmpty()) {
			return 0;
		}
		
		int decrementAmount = Math.min(amount, storedStack.getCount());
		ItemStack remainder = storedStack.getRecipeRemainder();
		if (!remainder.isEmpty()) {
			if (storedStack.getCount() == 1) {
				setItem(0, remainder);
			} else {
				getItem(0).shrink(decrementAmount);
				remainder.setCount(decrementAmount);
				
				ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, remainder);
				itemEntity.push(0, 0.1, 0);
				level.addFreshEntity(itemEntity);
			}
		} else {
			getItem(0).shrink(decrementAmount);
		}
		
		if (decrementAmount > 0) {
			if (doEffects) {
				spawnOrbParticles(orbTargetPos);
			}
			updateInClientWorld();
			setChanged();
		}
		
		return decrementAmount;
	}
	
	public void spawnOrbParticles(Vec3 orbTargetPos) {
		ItemStack storedStack = this.getItem(0);
		if (!storedStack.isEmpty()) {
			Optional<DyeColor> optionalItemColor = ColorRegistry.ITEM_COLORS.getMapping(storedStack.getItem(), DyeColor.PURPLE);
			if (optionalItemColor.isPresent()) {
				ParticleOptions sparkleRisingParticleEffect = SpectrumParticleTypes.getSparkleRisingParticle(optionalItemColor.get());
				
				if (this.getLevel() instanceof ServerLevel serverWorld) {
					SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) level,
							new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5),
							sparkleRisingParticleEffect, 50,
							new Vec3(0.4, 0.2, 0.4), new Vec3(0.06, 0.16, 0.06));
					
					SpectrumS2CPacketSender.playColorTransmissionParticle(serverWorld, new ColoredTransmission(new Vec3(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 1.0D, this.worldPosition.getZ() + 0.5D), new ExactPositionSource(orbTargetPos), 20, optionalItemColor.get()));
				} else if (this.getLevel() instanceof ClientLevel clientWorld) {
					for (int i = 0; i < 50; i++) {
						float randomOffsetX = worldPosition.getX() + 0.3F + level.random.nextFloat() * 0.6F;
						float randomOffsetY = worldPosition.getY() + 0.3F + level.random.nextFloat() * 0.6F;
						float randomOffsetZ = worldPosition.getZ() + 0.3F + level.random.nextFloat() * 0.6F;
						float randomVelocityX = 0.03F - level.random.nextFloat() * 0.06F;
						float randomVelocityY = level.random.nextFloat() * 0.16F;
						float randomVelocityZ = 0.03F - level.random.nextFloat() * 0.06F;
						
						clientWorld.addParticle(sparkleRisingParticleEffect,
								randomOffsetX, randomOffsetY, randomOffsetZ,
								randomVelocityX, randomVelocityY, randomVelocityZ);
					}
					
					ParticleOptions sphereParticleEffect = new ColoredTransmissionParticleEffect(new ExactPositionSource(orbTargetPos), 20, optionalItemColor.get());
					clientWorld.addParticle(sphereParticleEffect, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 1.0D, this.worldPosition.getZ() + 0.5D, (orbTargetPos.x() - this.worldPosition.getX()) * 0.045, 0, (orbTargetPos.z() - this.worldPosition.getZ()) * 0.045);
				}
				
				level.playSound(null, this.worldPosition, SpectrumSoundEvents.ENCHANTER_DING, SoundSource.BLOCKS, SpectrumCommon.CONFIG.BlockSoundVolume, 0.7F + level.random.nextFloat() * 0.6F);
			}
		}
	}
	
}
