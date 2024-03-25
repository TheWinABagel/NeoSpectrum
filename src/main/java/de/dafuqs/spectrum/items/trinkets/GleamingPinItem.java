package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GleamingPinItem extends SpectrumTrinketItem implements ExtendedEnchantable {
	
	public static final int BASE_RANGE = 12;
	public static final int RANGE_BONUS_PER_LEVEL_OF_SNIPING = 4;
	public static final int EFFECT_DURATION = 240;
	public static final long COOLDOWN_TICKS = 160;
	
	public GleamingPinItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/gleaming_pin"));
	}
	
	public static void doGleamingPinEffect(@NotNull Player player, @NotNull ServerLevel world, ItemStack gleamingPinStack) {
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SpectrumSoundEvents.RADIANCE_PIN_TRIGGER, SoundSource.PLAYERS, 0.4F, 0.9F + world.getRandom().nextFloat() * 0.2F);
		SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, player.position().add(0, 0.75, 0), SpectrumParticleTypes.LIQUID_CRYSTAL_SPARKLE, 100, new Vec3(0, 0.5, 0), new Vec3(2.5, 0.1, 2.5));
		
		world.getEntities(player, player.getBoundingBox().inflate(getEffectRange(gleamingPinStack)), EntitySelector.LIVING_ENTITY_STILL_ALIVE).forEach((entity) -> {
			if (entity instanceof LivingEntity livingEntity) {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, EFFECT_DURATION, 0, true, true));
			}
		});
	}
	
	public static int getEffectRange(ItemStack stack) {
		return BASE_RANGE + RANGE_BONUS_PER_LEVEL_OF_SNIPING * EnchantmentHelper.getItemEnchantmentLevel(SpectrumEnchantments.SNIPER, stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.gleaming_pin.tooltip"));
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == SpectrumEnchantments.SNIPER;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 16;
	}
	
}