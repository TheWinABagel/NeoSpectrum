package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.entity.entity.MiningProjectileEntity;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassCrestWorkstaffItem extends WorkstaffItem {
    
    public static final int COOLDOWN_DURATION_TICKS = 10;
    public static final InkCost PROJECTILE_COST = new InkCost(InkColors.WHITE, 50); // TODO: make pricier once ink networking is in
    
    public GlassCrestWorkstaffItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
    
    public static boolean canShoot(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt == null || !nbt.getBoolean(WorkstaffItem.PROJECTILES_DISABLED_NBT_STRING);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(world, user, hand);
        if (!result.getResult().consumesAction()) {
            ItemStack stack = user.getItemInHand(hand);
            if (canShoot(stack) && InkPowered.tryDrainEnergy(user, PROJECTILE_COST)) {
                user.getCooldowns().addCooldown(this, COOLDOWN_DURATION_TICKS);
                if (!world.isClientSide) {
                    user.playNotifySound(SpectrumSoundEvents.LIGHT_CRYSTAL_RING, SoundSource.PLAYERS, 0.5F, 0.75F + user.getRandom().nextFloat());
                    MiningProjectileEntity.shoot(world, user, user.getItemInHand(hand));
                }
                stack.hurtAndBreak(2, user, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                
                return InteractionResultHolder.consume(stack);
            } else {
                return InteractionResultHolder.fail(stack);
            }
        }
        return result;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
    
        if (canShoot(stack)) {
            tooltip.add(Component.translatable("item.spectrum.workstaff.tooltip.projectile").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.spectrum.workstaff.tooltip.projectiles_disabled").withStyle(ChatFormatting.DARK_RED));
        }
    }

}
