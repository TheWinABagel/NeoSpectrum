package de.dafuqs.spectrum.items.magic_items.ampoules;

import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import de.dafuqs.spectrum.entity.entity.LightMineEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MalachiteGlassAmpouleItem extends BaseGlassAmpouleItem implements InkPoweredPotionFillable {
    
    public MalachiteGlassAmpouleItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public boolean trigger(ItemStack stack, LivingEntity attacker, @Nullable LivingEntity target) {
        List<MobEffectInstance> e = new ArrayList<>();
        if (attacker instanceof Player player) {
            List<InkPoweredStatusEffectInstance> effects = getEffects(stack);
            for (InkPoweredStatusEffectInstance effect : effects) {
                if (InkPowered.tryDrainEnergy(player, effect.getInkCost())) {
                    e.add(effect.getStatusEffectInstance());
                }
            }
        }
        LightMineEntity.summonBarrage(attacker.level(), attacker, target, e);
        return true;
    }
    
    @Override
    public int maxEffectCount() {
        return 1;
    }
    
    @Override
    public int maxEffectAmplifier() {
        return 0;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(Component.translatable("item.spectrum.malachite_glass_ampoule.tooltip").withStyle(ChatFormatting.GRAY));
        appendPotionFillableTooltip(stack, tooltip, Component.translatable("item.spectrum.malachite_glass_ampoule.tooltip"), false);
    }
    
}
