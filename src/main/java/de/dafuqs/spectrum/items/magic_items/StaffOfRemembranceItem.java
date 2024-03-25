package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.item.PrioritizedEntityInteraction;
import de.dafuqs.spectrum.blocks.memory.MemoryItem;
import de.dafuqs.spectrum.compat.claims.GenericClaimModsCompat;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumEntityTypeTags;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StaffOfRemembranceItem extends Item implements InkPowered, PrioritizedEntityInteraction {
	
	public static final InkColor USED_COLOR = InkColors.LIGHT_GRAY;
	public static final InkCost TURN_NEUTRAL_TO_MEMORY_COST = new InkCost(USED_COLOR, 1000);
	public static final InkCost TURN_HOSTILE_TO_MEMORY_COST = new InkCost(USED_COLOR, 10000);
	
	public static final ResourceLocation UNLOCK_HOSTILE_MEMORIZING_ID = SpectrumCommon.locate("milestones/unlock_hostile_memorizing");
	
	public StaffOfRemembranceItem(Properties settings) {
		super(settings);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, tooltip, tooltipContext);
		
		tooltip.add(Component.translatable("item.spectrum.staff_of_remembrance.tooltip").withStyle(ChatFormatting.GRAY));
		addInkPoweredTooltip(tooltip);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		Level world = user.level();
		Vec3 pos = entity.position();
		
		if (!GenericClaimModsCompat.canInteract(world, entity, user)) {
			return InteractionResult.FAIL;
		}
		
		if (!world.isClientSide && entity instanceof Mob mobEntity) {
			if (turnEntityToMemory(user, mobEntity)) {
				SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) world, entity.position(), SpectrumParticleTypes.LIGHT_GRAY_SPARKLE_RISING, 10, Vec3.ZERO, new Vec3(0.2, 0.2, 0.2));
				SpectrumS2CPacketSender.playParticleWithExactVelocity((ServerLevel) world, entity.position(), SpectrumParticleTypes.LIGHT_GRAY_EXPLOSION, 1, Vec3.ZERO);
				world.playSound(null, pos.x(), pos.y(), pos.z(), SpectrumSoundEvents.RADIANCE_STAFF_PLACE, SoundSource.PLAYERS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			} else {
				world.playSound(null, pos.x(), pos.y(), pos.z(), SpectrumSoundEvents.USE_FAIL, SoundSource.PLAYERS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			}
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}
	
	private boolean turnEntityToMemory(Player user, Mob entity) {
		if (!entity.isAlive() || entity.isRemoved() || entity.isVehicle()) {
			return false;
		}
		if (entity.getType().is(SpectrumEntityTypeTags.STAFF_OF_REMEMBRANCE_BLACKLISTED)) {
			return false;
		}
		
		MobCategory spawnGroup = entity.getType().getCategory();
		if (spawnGroup == MobCategory.MONSTER && (user.isCreative() || AdvancementHelper.hasAdvancement(user, UNLOCK_HOSTILE_MEMORIZING_ID))) {
			if (!InkPowered.tryDrainEnergy(user, TURN_HOSTILE_TO_MEMORY_COST)) {
				return false;
			}
		} else if (!InkPowered.tryDrainEnergy(user, TURN_NEUTRAL_TO_MEMORY_COST)) {
			return false;
		}
		
		entity.dropLeash(true, true);
		entity.playAmbientSound();
		entity.spawnAnim();
		
		ItemStack memoryStack = MemoryItem.getMemoryForEntity(entity);
		MemoryItem.setTicksToManifest(memoryStack, 1);
		MemoryItem.setSpawnAsAdult(memoryStack, true);
		
		Vec3 entityPos = entity.position();
		ItemEntity itemEntity = new ItemEntity(entity.level(), entityPos.x(), entityPos.y(), entityPos.z(), memoryStack);
		itemEntity.setDeltaMovement(new Vec3(0.0, 0.15, 0.0));
		entity.level().addFreshEntity(itemEntity);
		entity.remove(Entity.RemovalReason.DISCARDED);
		
		return true;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.SPEAR;
	}
	
	@Override
	public List<InkColor> getUsedColors() {
		return List.of(USED_COLOR);
	}
	
}
