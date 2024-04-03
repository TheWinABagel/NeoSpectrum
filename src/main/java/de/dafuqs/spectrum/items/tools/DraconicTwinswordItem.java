package de.dafuqs.spectrum.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.api.item.SlotReservingItem;
import de.dafuqs.spectrum.api.item.SplittableItem;
import de.dafuqs.spectrum.api.render.ExtendedItemBars;
import de.dafuqs.spectrum.entity.entity.DraconicTwinswordEntity;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DraconicTwinswordItem extends SwordItem implements SplittableItem, SlotReservingItem, Preenchanted, ExtendedItemBars {

    public static final float MAX_CHARGE_TIME = 60;
    private final Multimap<Attribute, AttributeModifier> phantomModifiers;

    public DraconicTwinswordItem(Tier toolMaterial, int attackDamage, float attackSpeed, Properties settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> phantom = ImmutableMultimap.builder();
        this.phantomModifiers = phantom.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND)
            return super.getDefaultAttributeModifiers(slot);

        var nbt = stack.getOrCreateTag();
        if (nbt.getBoolean("cooldown") || isReservingSlot(stack))
            return phantomModifiers;

        return super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (isReservingSlot(stack) || remainingUseTicks == 0) {
            return;
        }

        var strength = Math.min(Math.abs(remainingUseTicks - getUseDuration(stack)), MAX_CHARGE_TIME) / MAX_CHARGE_TIME;
        var twinsword = initiateTwinswordEntity(stack, world, user, strength);

        world.addFreshEntity(twinsword);
        SoundEvent soundEvent = SoundEvents.TRIDENT_THROW;

        world.playSound(null, twinsword, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
        var nbt = stack.getOrCreateTag();
        markReserved(stack, true);
        nbt.putUUID("lastTwinsword", twinsword.getUUID());

        if (!world.isClientSide())
            stack.hurtAndBreak(1, user, (p) -> p.broadcastBreakEvent(user.getUsedItemHand()));


        super.releaseUsing(stack, world, user, remainingUseTicks);
    }

    @NotNull
    private static DraconicTwinswordEntity initiateTwinswordEntity(ItemStack stack, Level world, LivingEntity user, float strength) {
        var twinsword = new DraconicTwinswordEntity(world);
        twinsword.setOwner(user);
        twinsword.setStack(stack);

        var yaw = user.getYRot();
        var pitch = user.getXRot();

        float f = -Mth.sin(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
        float h = Mth.cos(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));

        twinsword.absMoveTo(user.getX() + f * 1.334, user.getEyeY() - 0.2, user.getZ() + h * 1.334);
        twinsword.setDeltaMovement(0, strength, 0);
        twinsword.hasImpulse = true;
        twinsword.hurtMarked = true;
        twinsword.pickup = AbstractArrow.Pickup.DISALLOWED;
        return twinsword;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 200;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(user.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("item.spectrum.dragon_talon.tooltip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.spectrum.dragon_talon.tooltip2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.spectrum.dragon_talon.tooltip3").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player) {
            var nbt = stack.getOrCreateTag();
            if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                if (!nbt.getBoolean("cooldown")) {
                    nbt.putBoolean("cooldown", true);
                }
            }
            else if(nbt.contains("cooldown")) {
                nbt.remove("cooldown");
            }
        }
    }

    @Override
    public ItemStack getResult(ServerPlayer player, ItemStack parent) {
        var result = new ItemStack(SpectrumItems.DRAGON_TALON);
        var durability = parent.getDamageValue();

        if (isReservingSlot(parent)) {
            durability  += player.getAbilities().instabuild ? 0 : 500;
            player.getCooldowns().addCooldown(result.getItem(), 400);
        }

        var nbt = parent.getOrCreateTag();
        nbt.remove("lastTwinsword");
        nbt.remove("cooldown");
        nbt.remove(SlotReservingItem.NBT_STRING);


        result.setTag(parent.getTag());
        result.setDamageValue(durability);
        sign(player, result);
        return result;
    }

    @Override
    public boolean canSplit(ServerPlayer player, InteractionHand occupiedHand, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem()))
            return false;

        return switch (occupiedHand) {
            case MAIN_HAND -> player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
            case OFF_HAND -> player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
        };
    }

    @Override
    public SoundEvent getSplitSound() {
        return SoundEvents.LODESTONE_COMPASS_LOCK;
    }

    @Override
    public boolean isReservingSlot(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(SlotReservingItem.NBT_STRING);
    }

    @Override
    public void markReserved(ItemStack stack, boolean reserved) {
        stack.getOrCreateTag().putBoolean(SlotReservingItem.NBT_STRING, reserved);
    }

    public static ItemStack findThrownStack(Player player, UUID id) {
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            var stack = inventory.getItem(i);
            var nbt = stack.getTag();
            if (nbt != null && nbt.hasUUID("lastTwinsword") && nbt.getUUID("lastTwinsword").equals(id)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Map<Enchantment, Integer> getDefaultEnchantments() {
        return Map.of(Enchantments.SWEEPING_EDGE, 5);
    }

    @Override
    public int barCount(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean allowVanillaDurabilityBarRendering(@Nullable Player player, ItemStack stack) {
        if (player == null || isReservingSlot(stack) || player.getItemInHand(player.getUsedItemHand()) != stack)
            return true;

        return !player.isUsingItem();
    }

    @Override
    public BarSignature getSignature(@Nullable Player player, @NotNull ItemStack stack, int index) {
        if (player == null || isReservingSlot(stack) || !player.isUsingItem())
            return ExtendedItemBars.PASS;

        var activeStack = player.getItemInHand(player.getUsedItemHand());
        if (activeStack != stack)
            return ExtendedItemBars.PASS;


        var progress = Math.round(Mth.clampedLerp(0, 13, ((float) player.getTicksUsingItem() / MAX_CHARGE_TIME)));
        return new BarSignature(2, 13, 13, progress, 1, 0xffffe659, 2, ExtendedItemBars.DEFAULT_BACKGROUND_COLOR);
    }
}
