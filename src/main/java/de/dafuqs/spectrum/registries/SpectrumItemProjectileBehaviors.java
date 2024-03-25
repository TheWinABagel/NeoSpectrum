package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.interaction.ItemProjectileBehavior;
import de.dafuqs.spectrum.api.interaction.OmniAcceleratorProjectile;
import de.dafuqs.spectrum.api.item.ExperienceStorageItem;
import de.dafuqs.spectrum.blocks.boom.IncandescentAmalgamBlock;
import de.dafuqs.spectrum.entity.entity.ItemProjectileEntity;
import de.dafuqs.spectrum.items.CraftingTabletItem;
import de.dafuqs.spectrum.items.magic_items.EnchantmentCanvasItem;
import de.dafuqs.spectrum.items.magic_items.KnowledgeGemItem;
import de.dafuqs.spectrum.items.tools.OmniAcceleratorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpectrumItemProjectileBehaviors {
	
	public static void register() {
		registerHarmless();
		if (SpectrumCommon.CONFIG.OmniAcceleratorPvP) {
			registerPvP();
		}
	}
	
	protected static void registerHarmless() {
		ItemProjectileBehavior.register(new ItemProjectileBehavior() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, Entity owner, EntityHitResult hitResult) {
				if (strikeLightning(hitResult.getEntity().level(), hitResult.getEntity().blockPosition())) {
					stack.shrink(1);
				}
				return stack;
			}
			
			@Override
			public ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack stack, Entity owner, BlockHitResult hitResult) {
				if (strikeLightning(projectile.level(), hitResult.getBlockPos())) {
					stack.shrink(1);
				}
				return stack;
			}
			
			private boolean strikeLightning(Level world, BlockPos pos) {
				if (world.canSeeSky(pos.above())) {
					LightningBolt lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
					if (lightningEntity != null) {
						lightningEntity.moveTo(Vec3.atBottomCenterOf(pos));
						world.addFreshEntity(lightningEntity);
						return true;
					}
				}
				return false;
			}
		}, SpectrumItems.STORM_STONE);
		
		ItemProjectileBehavior.register(ItemProjectileBehavior.damaging(4F, true), SpectrumItemTags.GEMSTONE_SHARDS);
		ItemProjectileBehavior.register(ItemProjectileBehavior.damaging(6F, true), Items.POINTED_DRIPSTONE);
		ItemProjectileBehavior.register(ItemProjectileBehavior.damaging(6F, true), Items.END_ROD);
		ItemProjectileBehavior.register(ItemProjectileBehavior.damaging(6F, true), Items.BLAZE_ROD);
		ItemProjectileBehavior.register(ItemProjectileBehavior.damaging(8F, true), SpectrumItems.STAR_FRAGMENT);

		ItemProjectileBehavior.register(new ItemProjectileBehavior.Damaging() {
			
			@Override
			public boolean destroyItemOnHit() {
				return false;
			}
			
			@Override
			public boolean dealDamage(ThrowableItemProjectile projectile, Entity owner, Entity target) {
				return target.hurt(target.damageSources().thrown(projectile, owner), 6F);
			}
			
			@Override
			public ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, BlockHitResult hitResult) {
				Level world = projectile.level();
				BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
				if (blockEntity instanceof JukeboxBlockEntity jukeboxBlockEntity && !blockEntity.isRemoved()) {
					ItemStack currentStack = jukeboxBlockEntity.getItem(0);
					if (!currentStack.isEmpty()) {
						jukeboxBlockEntity.popOutRecord();
					}
					jukeboxBlockEntity.setFirstItem(stack.copy());
					stack.shrink(1);
				}
				return stack;
			}
		}, ItemTags.MUSIC_DISCS);
		
		ItemProjectileBehavior.register(new ItemProjectileBehavior.Default() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				Entity entity = hitResult.getEntity();
				if (!entity.fireImmune()) {
					entity.setSecondsOnFire(15);
					if (entity.hurt(entity.damageSources().inFire(), 4.0F)) {
						entity.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + entity.level().getRandom().nextFloat() * 0.4F);
					}
					stack.shrink(1);
				}
				return stack;
			}
		}, Items.FIRE_CHARGE);
		
		ItemProjectileBehavior.register(new ItemProjectileBehavior() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				IncandescentAmalgamBlock.explode(projectile.level(), BlockPos.containing(hitResult.getLocation()), owner, stack);
				stack.shrink(1);
				return stack;
			}
			
			@Override
			public ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, BlockHitResult hitResult) {
				IncandescentAmalgamBlock.explode(projectile.level(), BlockPos.containing(hitResult.getLocation()), owner, stack);
				stack.shrink(1);
				return stack;
			}
		}, SpectrumBlocks.INCANDESCENT_AMALGAM.asItem());
		
		ItemProjectileBehavior.register(new ItemProjectileBehavior() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				return stack;
			}
			
			@Override
			public ItemStack onBlockHit(ItemProjectileEntity projectile, ItemStack accelerator, @Nullable Entity owner, BlockHitResult hitResult) {
				Optional<ItemStack> optionalAcceleratorContentStack = OmniAcceleratorItem.getFirstStack(accelerator);
				if (optionalAcceleratorContentStack.isPresent() && owner instanceof LivingEntity livingOwner) {
					ItemStack acceleratorContentStack = optionalAcceleratorContentStack.get();
					
					Level world = projectile.level();
					OmniAcceleratorProjectile newProjectile = OmniAcceleratorProjectile.get(optionalAcceleratorContentStack.get());
					Entity newEntity = newProjectile.createProjectile(acceleratorContentStack, livingOwner, world);
					
					if (newEntity != null) {
						Vec3 pos = hitResult.getLocation();
						newEntity.setPosRaw(pos.x(), pos.y(), pos.z());
						OmniAcceleratorProjectile.setVelocity(newEntity, projectile, 20, world.getRandom().nextFloat() * 360, 0.0F, 2.0F, 1.0F);
						world.playSound(null, pos.x(), pos.y(), pos.z(), newProjectile.getSoundEffect(), SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
						OmniAcceleratorItem.decrementFirstItem(accelerator);
					}
				}
				return accelerator;
			}
		}, SpectrumItems.OMNI_ACCELERATOR);

		ItemProjectileBehavior.register(new ItemProjectileBehavior.Default() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				Entity target = hitResult.getEntity();
				if (target instanceof LivingEntity livingTarget) {
					livingTarget.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 0));
					livingTarget.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
				}
				stack.shrink(1);
				return stack;
			}
		}, Items.CAKE);
	}
	
	protected static void registerPvP() {
		ItemProjectileBehavior.register(new ItemProjectileBehavior.Default() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				Entity target = hitResult.getEntity();
				List<ItemStack> equipment = new ArrayList<>();
				target.getAllSlots().forEach(equipment::add);
				Collections.shuffle(equipment);
				
				for (ItemStack equip : equipment) {
					if (EnchantmentCanvasItem.tryExchangeEnchantments(stack, equip, target)) {
						return stack;
					}
				}
				return stack;
			}
		}, SpectrumItems.ENCHANTMENT_CANVAS);
		
		ItemProjectileBehavior.register(new ItemProjectileBehavior.Default() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				if (hitResult.getEntity() instanceof Player target) {
					int playerExperience = target.totalExperience;
					if (playerExperience > 0) {
						KnowledgeGemItem item = (KnowledgeGemItem) stack.getItem();
						long transferableExperiencePerTick = item.getTransferableExperiencePerTick(stack);
						int xpToTransfer = (int) Math.min(target.totalExperience, transferableExperiencePerTick * 100);
						int experienceOverflow = ExperienceStorageItem.addStoredExperience(stack, xpToTransfer);
						
						target.giveExperiencePoints(-xpToTransfer + experienceOverflow);
						target.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3F, 0.8F + target.level().getRandom().nextFloat() * 0.4F);
						return stack;
					}
				}
				return stack;
			}
		}, SpectrumItems.KNOWLEDGE_GEM);
		
		ItemProjectileBehavior.register(new ItemProjectileBehavior.Default() {
			@Override
			public ItemStack onEntityHit(ItemProjectileEntity projectile, ItemStack stack, @Nullable Entity owner, EntityHitResult hitResult) {
				Recipe<?> recipe = CraftingTabletItem.getStoredRecipe(projectile.level(), stack);
				if (recipe instanceof CraftingRecipe craftingRecipe && hitResult.getEntity() instanceof ServerPlayer target) {
					CraftingTabletItem.tryCraftRecipe(target, craftingRecipe);
				}
				return stack;
			}
		}, SpectrumItems.CRAFTING_TABLET);
	}
	
}
