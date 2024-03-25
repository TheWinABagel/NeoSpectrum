package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import de.dafuqs.spectrum.status_effects.DivinityStatusEffect;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CircletOfArroganceItem extends SpectrumTrinketItem {

    private final static int TRIGGER_EVERY_X_TICKS = 240;
    private final static int EFFECT_DURATION = TRIGGER_EVERY_X_TICKS + 10;

    public CircletOfArroganceItem(Properties settings) {
        super(settings, SpectrumCommon.locate("unlocks/trinkets/circlet_of_arrogance"));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        giveEffect(entity);
        if (entity instanceof ServerPlayer serverPlayerEntity) {
            SpectrumS2CPacketSender.playDivinityAppliedEffects(serverPlayerEntity);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity);
        Level world = entity.level();
        if (!world.isClientSide && world.getGameTime() % TRIGGER_EVERY_X_TICKS == 0) {
            giveEffect(entity);
        }
    }

    private static void giveEffect(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(SpectrumStatusEffects.DIVINITY, EFFECT_DURATION, DivinityStatusEffect.CIRCLET_AMPLIFIER, true, false, true));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(Component.translatable("item.spectrum.circlet_of_arrogance.tooltip").withStyle(ChatFormatting.GRAY));
    }
}