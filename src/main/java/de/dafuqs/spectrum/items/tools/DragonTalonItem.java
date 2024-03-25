package de.dafuqs.spectrum.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import de.dafuqs.spectrum.api.item.*;
import de.dafuqs.spectrum.entity.entity.DragonTalonEntity;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DragonTalonItem extends MalachiteBidentItem implements MergeableItem, SlotReservingItem, ExtendedEnchantable, SplitDamageItem, TranstargetItem {

    protected static final UUID REACH_MODIFIER_ID = UUID.fromString("3b9a13c8-a9a7-4545-8c32-e60baf25823e");
    private final Multimap<Attribute, AttributeModifier> attributeModifiers, phantomModifiers;


    public DragonTalonItem(Tier toolMaterial, double damage, double extraReach, Properties settings) {
        super(settings, 0);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damage + toolMaterial.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -0.8, AttributeModifier.Operation.ADDITION));
        builder.put(ReachEntityAttributes.ATTACK_RANGE, new AttributeModifier(REACH_MODIFIER_ID, "Tool modifier", extraReach, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();

        ImmutableMultimap.Builder<Attribute, AttributeModifier> phantom = ImmutableMultimap.builder();
        this.phantomModifiers = phantom.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        var nbt = stack.getOrCreateTag();
        if (slot != EquipmentSlot.MAINHAND)
            return super.getDefaultAttributeModifiers(slot);
        return this.isReservingSlot(stack) || nbt.getBoolean("cooldown") ? phantomModifiers : attributeModifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("item.spectrum.dragon_needle.tooltip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.spectrum.dragon_needle.tooltip2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.spectrum.dragon_needle.tooltip3").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public float getThrowSpeed() {
        return 3.5F;
    }

    @Override
    protected void throwBident(ItemStack stack, ServerLevel world, Player playerEntity) {
        var needleEntity = new DragonTalonEntity(world);
        needleEntity.setStack(stack);
        needleEntity.setOwner(playerEntity);
        needleEntity.absMoveTo(playerEntity.getX(), playerEntity.getEyeY() - 0.1, playerEntity.getZ());
        needleEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, getThrowSpeed(), 1.0F);
        needleEntity.hasImpulse = true;
        needleEntity.hurtMarked = true;
        needleEntity.pickup = AbstractArrow.Pickup.ALLOWED;

        world.addFreshEntity(needleEntity);
        SoundEvent soundEvent = SoundEvents.TRIDENT_THROW;

        world.playSound(null, needleEntity, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
        var nbt = stack.getOrCreateTag();
        markReserved(stack, true);
        nbt.putUUID("lastNeedle", needleEntity.getUUID());
    }

    @Override
    public ItemStack getResult(ServerPlayer player, ItemStack firstHalf, ItemStack secondHalf) {
        var durability = Math.max(firstHalf.getDamageValue(), secondHalf.getDamageValue());
        var result = new ItemStack(SpectrumItems.DRACONIC_TWINSWORD);
        result.setTag(firstHalf.getTag());

        var nbt = result.getOrCreateTag();
        nbt.remove("pairSignature");
        nbt.remove("lastNeedle");
        nbt.remove("cooldown");
        nbt.remove(SlotReservingItem.NBT_STRING);

        if (isReservingSlot(firstHalf) || isReservingSlot(secondHalf)) {
            durability  += player.getAbilities().instabuild ? 0 : 500;
            player.getCooldowns().addCooldown(result.getItem(), 400);
        }
        result.setDamageValue(durability);

        return result;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        var hand = user.getUsedItemHand();
        if (hand == InteractionHand.MAIN_HAND)
            return;

        if (!isReservingSlot(stack)) {
            super.releaseUsing(user.getItemInHand(InteractionHand.OFF_HAND), world, user, remainingUseTicks);
            return;
        }

        var nbt = stack.getOrCreateTag();

        if (world.isClientSide() || !nbt.hasUUID("lastNeedle"))
            return;

        ServerLevel serverWorld = (ServerLevel) world;

        var entity = serverWorld.getEntity(nbt.getUUID("lastNeedle"));

        if (entity instanceof DragonTalonEntity needle) {
            needle.recall();
        }
    }

    @Override
    public boolean canMerge(ServerPlayer player, ItemStack parent, ItemStack other) {
        if (player.getCooldowns().isOnCooldown(parent.getItem()))
            return false;
        return (parent.getItem() == other.getItem() && verify(parent, other));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND)
            return InteractionResultHolder.fail(user.getItemInHand(hand));
        return super.use(world, user, hand);
    }

    @Override
    public SoundEvent getMergeSound() {
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
            if (nbt != null && nbt.hasUUID("lastNeedle") && nbt.getUUID("lastNeedle").equals(id)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
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
    public DamageComposition getDamageComposition(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {
        var composition = new DamageComposition();
        composition.add(SpectrumDamageTypes.evisceration(attacker.level(), attacker), damage);
        return composition;
    }

    @Override
    public boolean acceptsEnchantment(Enchantment enchantment) {
        return enchantment == Enchantments.IMPALING || enchantment == Enchantments.INFINITY_ARROWS;
    }

    @Override
    public Map<Enchantment, Integer> getDefaultEnchantments() {
        return Map.of();
    }

    @Override
    public EnchantmentCategory getRealTarget() {
        return EnchantmentCategory.WEAPON;
    }
}
