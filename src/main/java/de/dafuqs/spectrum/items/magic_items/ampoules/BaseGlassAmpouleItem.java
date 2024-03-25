package de.dafuqs.spectrum.items.magic_items.ampoules;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGlassAmpouleItem extends Item {
    
    public BaseGlassAmpouleItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (trigger(stack, user, null)) {
            if (!user.isCreative()) {
                stack.shrink(1);
            }
        }
        return user.isCreative() ? super.use(world, user, hand) : InteractionResultHolder.consume(stack);
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (trigger(stack, attacker, target)) {
            if (!(attacker instanceof Player player && player.isCreative())) {
                stack.shrink(1);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
    
    public abstract boolean trigger(ItemStack stack, LivingEntity attacker, @Nullable LivingEntity target);
    
}
