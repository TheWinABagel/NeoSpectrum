package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.registries.SpectrumStatusEffectTags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WhispyCircletItem extends SpectrumTrinketItem {
	
	private final static int TRIGGER_EVERY_X_TICKS = 100;
	private final static int NEGATIVE_EFFECT_SHORTENING_TICKS = 200;
	
	public WhispyCircletItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/whispy_circlet"));
	}
	
	public static void removeSingleStatusEffect(@NotNull LivingEntity entity, MobEffectCategory category) {
		Collection<MobEffectInstance> currentEffects = entity.getActiveEffects();
		if (currentEffects.size() == 0) {
			return;
		}

		List<MobEffectInstance> negativeEffects = new ArrayList<>();
		for (MobEffectInstance statusEffectInstance : currentEffects) {
			MobEffect effect = statusEffectInstance.getEffect();
			if (effect.getCategory() == category && !SpectrumStatusEffectTags.isUncurable(effect)) {
				negativeEffects.add(statusEffectInstance);
			}
		}
		
		if (negativeEffects.size() == 0) {
			return;
		}
		
		Level world = entity.level();
		int randomIndex = world.random.nextInt(negativeEffects.size());
		entity.removeEffect(negativeEffects.get(randomIndex).getEffect());
	}
	
	public static void removeNegativeStatusEffects(@NotNull LivingEntity entity) {
		Set<MobEffect> effectsToRemove = new HashSet<>();
		Collection<MobEffectInstance> currentEffects = entity.getActiveEffects();
		for (MobEffectInstance instance : currentEffects) {
			if (affects(instance.getEffect())) {
				effectsToRemove.add(instance.getEffect());
			}
		}
		
		for (MobEffect effect : effectsToRemove) {
			entity.removeEffect(effect);
		}
	}
	
	public static void shortenNegativeStatusEffects(@NotNull LivingEntity entity, int duration) {
		Collection<MobEffectInstance> newEffects = new ArrayList<>();
		Collection<MobEffect> effectTypesToClear = new ArrayList<>();
		
		// remove them first, so hidden "stacked" effects are preserved
		for (MobEffectInstance instance : entity.getActiveEffects()) {
			if (affects(instance.getEffect())) {
				int newDurationTicks = instance.getDuration() - duration;
				if (newDurationTicks > 0) {
					newEffects.add(new MobEffectInstance(instance.getEffect(), newDurationTicks, instance.getAmplifier(), instance.isAmbient(), instance.isVisible(), true));
				}
				if (!effectTypesToClear.contains(instance.getEffect())) {
					effectTypesToClear.add(instance.getEffect());
				}
			}
		}
		
		for (MobEffect effectTypeToClear : effectTypesToClear) {
			entity.removeEffect(effectTypeToClear);
		}
		for (MobEffectInstance newEffect : newEffects) {
			entity.addEffect(newEffect);
		}
	}
	
	public static boolean affects(MobEffect statusEffect) {
		return statusEffect.getCategory() == MobEffectCategory.HARMFUL && !SpectrumStatusEffectTags.isUncurable(statusEffect);
	}
	
	public static void preventPhantomSpawns(@NotNull ServerPlayer serverPlayerEntity) {
		serverPlayerEntity.getStats().setValue(serverPlayerEntity, Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 0);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.whispy_circlet.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.whispy_circlet.tooltip2").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.whispy_circlet.tooltip3").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		super.tick(stack, slot, entity);
		
		Level world = entity.level();
		if (!world.isClientSide) {
			long time = entity.level().getGameTime();
			if (time % TRIGGER_EVERY_X_TICKS == 0) {
				shortenNegativeStatusEffects(entity, NEGATIVE_EFFECT_SHORTENING_TICKS);
			}
			if (time % 10000 == 0 && entity instanceof ServerPlayer serverPlayerEntity) {
				preventPhantomSpawns(serverPlayerEntity);
			}
		}
	}
	
}