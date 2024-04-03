package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.registries.SpectrumStatusEffects;
import de.dafuqs.spectrum.status_effects.DivinityStatusEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CircletOfArroganceItem extends SpectrumTrinketItem {

    private final static int TRIGGER_EVERY_X_TICKS = 240;
    private final static int EFFECT_DURATION = TRIGGER_EVERY_X_TICKS + 10;

    public CircletOfArroganceItem(Properties settings) {
        super(settings, SpectrumCommon.locate("unlocks/trinkets/circlet_of_arrogance"));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        giveEffect(slotContext.entity());
        if (slotContext.entity() instanceof ServerPlayer serverPlayerEntity) {
            SpectrumS2CPacketSender.playDivinityAppliedEffects(serverPlayerEntity);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        Level level = slotContext.entity().level();
        if (!level.isClientSide && level.getGameTime() % TRIGGER_EVERY_X_TICKS == 0) {
            giveEffect(slotContext.entity());
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