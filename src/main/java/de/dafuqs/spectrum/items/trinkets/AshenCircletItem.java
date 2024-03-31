package de.dafuqs.spectrum.items.trinkets;

import com.google.common.collect.Multimap;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.cca.OnPrimordialFireComponent;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class AshenCircletItem extends SpectrumTrinketItem {
	
	public static final int FIRE_RESISTANCE_EFFECT_DURATION = 600;
	public static final long COOLDOWN_TICKS = 3000;
	
	public static final double LAVA_MOVEMENT_SPEED_MOD = 0.4; // vanilla uses 0.5 to slow the player down to half its speed
	public static final double LAVA_VIEW_DISTANCE_MOD = 24.0;
	
	public AshenCircletItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/ashen_circlet"));
	}
	
	public static long getCooldownTicks(@NotNull ItemStack ashenCircletStack, @NotNull Level world) {
		CompoundTag nbt = ashenCircletStack.getTag();
		if (nbt == null || !nbt.contains("last_cooldown_start", Tag.TAG_LONG)) {
			return 0;
		} else {
			long lastCooldownStart = nbt.getLong("last_cooldown_start");
			return Math.max(0, lastCooldownStart - world.getGameTime() + COOLDOWN_TICKS);
		}
	}
	
	private static void setCooldown(@NotNull ItemStack ashenCircletStack, @NotNull Level world) {
		CompoundTag nbt = ashenCircletStack.getOrCreateTag();
		nbt.putLong("last_cooldown_start", world.getGameTime());
		ashenCircletStack.setTag(nbt);
	}
	
	public static void grantFireResistance(@NotNull ItemStack ashenCircletStack, @NotNull LivingEntity livingEntity) {
		if (!livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIRE_RESISTANCE_EFFECT_DURATION, 0, true, true));
			livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
			setCooldown(ashenCircletStack, livingEntity.level());
		}
	}
	
	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		super.tick(stack, slot, entity);
		if (entity.isOnFire()) {
			entity.setRemainingFireTicks(0);
		}
		if (getCooldownTicks(stack, entity.level()) == 0 && OnPrimordialFireComponent.putOut(entity)) {
			entity.level().playSound(null, entity.blockPosition(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
			setCooldown(stack, entity.level());
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.ashen_circlet.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.ashen_circlet.tooltip2").withStyle(ChatFormatting.GRAY));
		
		if (world != null) {
			long cooldownTicks = getCooldownTicks(stack, world);
			if (cooldownTicks == 0) {
				tooltip.add(Component.translatable("item.spectrum.ashen_circlet.tooltip.cooldown_full"));
			} else {
				tooltip.add(Component.translatable("item.spectrum.ashen_circlet.tooltip.cooldown_seconds", cooldownTicks / 20));
			}
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
		Multimap<Attribute, AttributeModifier> modifiers = super.getModifiers(stack, slot, entity, uuid);
		
		modifiers.put(AdditionalEntityAttributes.LAVA_SPEED, new AttributeModifier(uuid, "spectrum:ashen_circlet", LAVA_MOVEMENT_SPEED_MOD, AttributeModifier.Operation.ADDITION));
		modifiers.put(AdditionalEntityAttributes.LAVA_VISIBILITY, new AttributeModifier(uuid, "spectrum:ashen_circlet", LAVA_VIEW_DISTANCE_MOD, AttributeModifier.Operation.ADDITION));
		
		return modifiers;
	}
	
}