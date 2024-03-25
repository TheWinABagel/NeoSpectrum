package de.dafuqs.spectrum.items.magic_items.ampoules;

import de.dafuqs.spectrum.entity.entity.LightShardEntity;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassAmpouleItem extends BaseGlassAmpouleItem {
    
    public GlassAmpouleItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public boolean trigger(ItemStack stack, LivingEntity attacker, @Nullable LivingEntity target) {
		Level world = attacker.level();
        if (!world.isClientSide) {
            world.playSound(null, attacker.blockPosition(), SpectrumSoundEvents.LIGHT_CRYSTAL_RING, SoundSource.PLAYERS, 0.35F, 0.9F + attacker.getRandom().nextFloat() * 0.334F);
            LightShardEntity.summonBarrage(attacker.level(), attacker, target);
        }
        return true;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.azurite_glass_ampoule.tooltip").withStyle(ChatFormatting.GRAY));
    }
    
}
