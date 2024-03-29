package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.particle.VectorPattern;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import dev.emi.trinkets.api.SlotReference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TakeOffBeltItem extends SpectrumTrinketItem implements ExtendedEnchantable {
	
	public static final int CHARGE_TIME_TICKS = 20;
	public static final int MAX_CHARGES = 8;
	
	private static final HashMap<LivingEntity, Long> sneakingTimes = new HashMap<>();
	
	public TakeOffBeltItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/take_off_belt"));
	}
	
	public static int getJumpBoostAmplifier(int sneakTime, int powerEnchantmentLevel) {
		return (int) Math.floor(sneakTime * (2.0 + powerEnchantmentLevel * 0.5));
	}
	
	public static int getCurrentCharge(Player playerEntity) {
		if (sneakingTimes.containsKey(playerEntity)) {
			return (int) (playerEntity.level().getGameTime() - sneakingTimes.get(playerEntity)) / CHARGE_TIME_TICKS;
		}
		return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.take_off_belt.tooltip").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		Level world = entity.level();
		super.tick(stack, slot, entity);
		
		if (!world.isClientSide) {
			if (entity.isShiftKeyDown() && entity.onGround()) {
				if (sneakingTimes.containsKey(entity)) {
					long sneakTicks = entity.level().getGameTime() - sneakingTimes.get(entity);
					if (sneakTicks % CHARGE_TIME_TICKS == 0) {
						if (sneakTicks > CHARGE_TIME_TICKS * MAX_CHARGES) {
							entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SpectrumSoundEvents.USE_FAIL, SoundSource.NEUTRAL, 4.0F, 1.05F);
							SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerLevel) entity.level(), entity.position(), SpectrumParticleTypes.BLACK_CRAFTING, 20, new Vec3(0, 0, 0), new Vec3(0.1, 0.05, 0.1));
							entity.removeEffect(MobEffects.JUMP);
						} else {
							int sneakTimeMod = (int) sneakTicks / CHARGE_TIME_TICKS;
							
							entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SpectrumSoundEvents.BLOCK_TOPAZ_BLOCK_HIT, SoundSource.NEUTRAL, 1.0F, 1.0F);
							for (Vec3 vec : VectorPattern.SIXTEEN.getVectors()) {
								SpectrumS2CPacketSender.playParticleWithExactVelocity((ServerLevel) entity.level(), entity.position(), SpectrumParticleTypes.LIQUID_CRYSTAL_SPARKLE, 1, vec.scale(0.5));
							}
							
							int powerEnchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
							int featherFallingEnchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FALL_PROTECTION, stack);
							entity.addEffect(new MobEffectInstance(MobEffects.JUMP, CHARGE_TIME_TICKS, getJumpBoostAmplifier(sneakTimeMod, powerEnchantmentLevel), true, false, true));
							if (featherFallingEnchantmentLevel > 0) {
								entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, CHARGE_TIME_TICKS + featherFallingEnchantmentLevel * 20, 0, true, false, true));
							}
						}
					}
				} else {
					sneakingTimes.put(entity, entity.level().getGameTime());
					if (entity instanceof ServerPlayer serverPlayerEntity) {
						SpectrumS2CPacketSender.sendPlayTakeOffBeltSoundInstance(serverPlayerEntity);
					}
				}
			} else if (entity.level().getGameTime() % CHARGE_TIME_TICKS == 0 && sneakingTimes.containsKey(entity)) {
				long lastSneakingTime = sneakingTimes.get(entity);
				if (lastSneakingTime < entity.level().getGameTime() + CHARGE_TIME_TICKS) {
					sneakingTimes.remove(entity);
				}
			}
		}
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	
	@Override
	public int getEnchantmentValue() {
		return 8;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.POWER_ARROWS || enchantment == Enchantments.FALL_PROTECTION;
	}
	
}
