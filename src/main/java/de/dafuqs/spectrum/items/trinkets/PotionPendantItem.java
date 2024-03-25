package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionPendantItem extends SpectrumTrinketItem implements InkPoweredPotionFillable {
	
	private final static int TRIGGER_EVERY_X_TICKS = 300;
	private final static int EFFECT_DURATION = TRIGGER_EVERY_X_TICKS + 220; // always keeps the effect active & prevents the 10 seconds of screen flashing when night vision runs out
	
	private final int maxEffectCount;
	private final int maxAmplifier;
	
	public PotionPendantItem(Properties settings, int maxEffectCount, int maxAmplifier, ResourceLocation unlockIdentifier) {
		super(settings, unlockIdentifier);
		this.maxEffectCount = maxEffectCount;
		this.maxAmplifier = maxAmplifier;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		appendPotionFillableTooltip(stack, tooltip, Component.translatable("item.spectrum.potion_pendant.when_worn"), false);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || PotionUtils.getCustomEffects(stack).size() > 0;
	}
	
	@Override
	public int maxEffectCount() {
		return maxEffectCount;
	}
	
	@Override
	public int maxEffectAmplifier() {
		return maxAmplifier;
	}
	
	@Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
		Level world = entity.level();
		super.onEquip(stack, slot, entity);
		if (!world.isClientSide && entity instanceof Player player) {
			grantEffects(stack, player);
		}
	}
	
	@Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		Level world = entity.level();
		super.tick(stack, slot, entity);
		if (!world.isClientSide && entity.level().getGameTime() % TRIGGER_EVERY_X_TICKS == 0 && entity instanceof Player player) {
			grantEffects(stack, player);
		}
	}
	
	private void grantEffects(ItemStack stack, Player player) {
		for (InkPoweredStatusEffectInstance inkPoweredEffect : getEffects(stack)) {
			if (InkPowered.tryDrainEnergy(player, inkPoweredEffect.getInkCost())) {
				MobEffectInstance effect = inkPoweredEffect.getStatusEffectInstance();
				player.addEffect(new MobEffectInstance(effect.getEffect(), EFFECT_DURATION, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), true));
			}
		}
	}
	
}
