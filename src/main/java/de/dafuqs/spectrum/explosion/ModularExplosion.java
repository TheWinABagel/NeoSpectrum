package de.dafuqs.spectrum.explosion;

import com.mojang.datafixers.util.Pair;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.helpers.Orientation;
import de.dafuqs.spectrum.mixin.accessors.ExplosionAccessor;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModularExplosion {
	
	// Call to boom
	public static void explode(@NotNull ServerLevel world, BlockPos pos, @Nullable Player owner, double baseBlastRadius, float baseDamage, ExplosionArchetype archetype, List<ExplosionModifier> modifiers) {
		DamageSource damageSource = world.damageSources().explosion(null);
		
		float damageMod = 1F;
		float killzoneDamageMod = 1F;
		
		double blastRadius = baseBlastRadius;
		double killZoneRadius = 0;
		ExplosionShape shape = ExplosionShape.DEFAULT;
		ItemStack miningStack = new ItemStack(SpectrumItems.BEDROCK_PICKAXE);
		
		for (ExplosionModifier explosionEffect : modifiers) {
			damageMod += explosionEffect.getDamageModifier();
			killzoneDamageMod += explosionEffect.getKillZoneDamageModifier();
			
			blastRadius += explosionEffect.getBlastRadiusModifier();
			killZoneRadius += explosionEffect.getKillZoneRadius();
			
			Optional<DamageSource> effectDamage = explosionEffect.getDamageSource(owner);
			if (effectDamage.isPresent()) {
				damageSource = effectDamage.get();
			}
			Optional<ExplosionShape> optionalExplosionShape = explosionEffect.getShape();
			if (optionalExplosionShape.isPresent()) {
				shape = optionalExplosionShape.get();
			}
			explosionEffect.addEnchantments(miningStack);
		}
		
		float blastDamage = baseDamage * damageMod;
		float killZoneDamage = baseDamage * killzoneDamageMod;
		
		Vec3 center = Vec3.atCenterOf(pos);
		world.playSound(null, center.x(), center.y(), center.z(), SpectrumSoundEvents.BLOCK_MODULAR_EXPLOSIVE_EXPLODE, SoundSource.BLOCKS, 1.0F, 0.8F + world.getRandom().nextFloat() * 0.3F);
		playVisualEffects(world, center, modifiers, blastRadius);
		
		AABB blastBox = AABB.ofSize(center, blastRadius * 2, blastRadius * 2, blastRadius * 2);
		
		if (archetype.affectsEntities) {
			final double finalBlastRadius = blastRadius;
			List<Entity> affectedEntities = world.getEntities(null, blastBox).stream().filter(entity -> entity.position().distanceTo(center) < finalBlastRadius).toList();
			
			for (Entity entity : affectedEntities) {
				// damage entity
				double distance = Math.max(entity.position().distanceTo(center) - entity.getBbWidth() / 2, 0);
				if (distance <= killZoneRadius) {
					entity.hurt(damageSource, entity.getType().is(ConventionalEntityTypeTags.BOSSES) ? killZoneDamage / 25F : killZoneDamage);
				} else {
					double finalDamage = Mth.lerp(distance / blastRadius, blastDamage, blastDamage / 2);
					entity.hurt(damageSource, (float) finalDamage);
				}
				// additional effects
				if (entity.isAlive()) {
					for (ExplosionModifier explosionEffect : modifiers) {
						explosionEffect.applyToEntity(entity, distance);
					}
				}
			}
		}
		if (archetype.affectsBlocks) {
			List<BlockPos> affectedBlocks = processExplosion(world, owner, pos, shape, blastRadius, miningStack);
			
			for (ExplosionModifier explosionEffect : modifiers) {
				explosionEffect.applyToBlocks(world, affectedBlocks);
			}
		}
	}
	
	// the client does not know about the block entities data
	// we have to send it from server => client
	private static void playVisualEffects(ServerLevel world, Vec3 pos, List<ExplosionModifier> effectModifiers, double blastRadius) {
		world.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 0.8F + world.random.nextFloat() * 0.4F);
		
		RandomSource random = world.getRandom();
		ArrayList<ParticleOptions> types = new ArrayList<>(effectModifiers.stream().map(ExplosionModifier::getParticleEffects).filter(Optional::isPresent).map(Optional::get).toList());
		types.add(SpectrumParticleTypes.PRIMORDIAL_SMOKE);
		
		world.sendParticles(SpectrumParticleTypes.PRIMORDIAL_FLAME, pos.x(), pos.y(), pos.z(), 30, random.nextFloat() * 0.5 - 0.25, random.nextFloat() * 0.5 - 0.25, random.nextFloat() * 0.5 - 0.25, 0.0);
		
		double particleCount = blastRadius * blastRadius + random.nextInt((int) (blastRadius * 2)) * (types.size() / 2F + 0.5);
		for (int i = 0; i < particleCount; i++) {
			double r = random.nextDouble() * blastRadius;
			Orientation orientation = Orientation.create(random.nextDouble() * Math.PI * 2, random.nextDouble() * Math.PI * 2);
			Vec3 particle = orientation.toVector(r).add(pos);
			Collections.shuffle(types);
			
			world.sendParticles(types.get(0), particle.x(), particle.y(), particle.z(), 1, 0, 0, 0, 0);
		}
	}
	
	private static List<BlockPos> processExplosion(@NotNull ServerLevel world, @Nullable Player owner, BlockPos center, ExplosionShape shape, double blastRadius, ItemStack miningStack) {
		Explosion explosion = new Explosion(world, owner, center.getX(), center.getY(), center.getZ(), (float) blastRadius, false, Explosion.BlockInteraction.DESTROY);
		
		ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();
		List<BlockPos> affectedBlocks = new ArrayList<>();
		int radius = (int) blastRadius / 2;
		for (BlockPos p : BlockPos.withinManhattan(center, radius, radius, radius)) {
			if (!GenericClaimModsCompat.canBreak(world, p, owner)) {
				continue;
			}
			if (shape.isAffected(center, p) && processBlock(world, owner, world.random, center, p, drops, miningStack, explosion)) {
				affectedBlocks.add(new BlockPos(p.getX(), p.getY(), p.getZ()));
			}
		}
		
		boolean hasInventoryInsertion = EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.INVENTORY_INSERTION, miningStack) > 0;
		for (Pair<ItemStack, BlockPos> stackPosPair : drops) {
			if (owner != null && hasInventoryInsertion) {
				owner.getInventory().placeItemBackInInventory(stackPosPair.getFirst());
			} else {
				Block.popResource(world, stackPosPair.getSecond(), stackPosPair.getFirst());
			}
		}
		
		return affectedBlocks;
	}
	
	private static boolean processBlock(@NotNull ServerLevel world, @Nullable Entity owner, RandomSource random, BlockPos center, BlockPos pos, ObjectArrayList<Pair<ItemStack, BlockPos>> drops, ItemStack miningStack, Explosion explosion) {
		var state = world.getBlockState(pos);
		var block = state.getBlock();
		var blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

		if (state.getBlock().getExplosionResistance() <= 9) {
			if (random.nextFloat() < 0.15F) {
				world.playSound(null, center.getX(), center.getY(), center.getZ(), block.getSoundType(state).getBreakSound(), SoundSource.BLOCKS, 2F, 0.8F + random.nextFloat() * 0.5F);
			}

			if (block.dropFromExplosion(explosion)) {
				LootParams.Builder builder = (new LootParams.Builder(world)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
						.withParameter(LootContextParams.TOOL, miningStack)
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
						.withOptionalParameter(LootContextParams.THIS_ENTITY, owner));
				builder.withParameter(LootContextParams.EXPLOSION_RADIUS, ((ExplosionAccessor) explosion).getPower());
				state.spawnAfterBreak(world, pos, miningStack, true);
				state.getDrops(builder).forEach((stack) -> tryMergeStack(drops, stack, pos.immutable()));
			}
			
			world.removeBlock(pos, false);
			block.wasExploded(world, pos, explosion);
			
			return true;
		}
		return false;
	}
	
	private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
		int i = stacks.size();
		
		for (int j = 0; j < i; ++j) {
			Pair<ItemStack, BlockPos> pair = stacks.get(j);
			ItemStack itemStack = pair.getFirst();
			if (ItemEntity.areMergable(itemStack, stack)) {
				ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
				stacks.set(j, Pair.of(itemStack2, pair.getSecond()));
				if (stack.isEmpty()) {
					return;
				}
			}
		}
		
		stacks.add(Pair.of(stack, pos));
	}
	
}
