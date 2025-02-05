package de.dafuqs.spectrum.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.api.item.SlotReservingItem;
import de.dafuqs.spectrum.api.item.SplittableItem;
import de.dafuqs.spectrum.api.render.ExtendedItemBars;
import de.dafuqs.spectrum.entity.entity.DraconicTwinswordEntity;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DraconicTwinswordItem extends SwordItem implements SplittableItem, SlotReservingItem, Preenchanted, ExtendedItemBars {

    public static final float MAX_CHARGE_TIME = 60;
    private final Multimap<EntityAttribute, EntityAttributeModifier> phantomModifiers;

    public DraconicTwinswordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> phantom = ImmutableMultimap.builder();
        this.phantomModifiers = phantom.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND)
            return super.getAttributeModifiers(slot);

        var nbt = stack.getOrCreateNbt();
        if (nbt.getBoolean("cooldown") || isReservingSlot(stack))
            return phantomModifiers;

        return super.getAttributeModifiers(slot);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (isReservingSlot(stack) || remainingUseTicks == 0) {
            return;
        }

        var strength = Math.min(Math.abs(remainingUseTicks - getMaxUseTime(stack)), MAX_CHARGE_TIME) / MAX_CHARGE_TIME;
        var twinsword = initiateTwinswordEntity(stack, world, user, strength);

        world.spawnEntity(twinsword);
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_THROW;

        world.playSoundFromEntity(null, twinsword, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
        var nbt = stack.getOrCreateNbt();
        markReserved(stack, true);
        nbt.putUuid("lastTwinsword", twinsword.getUuid());

        if (!world.isClient())
            stack.damage(1, user, (p) -> p.sendToolBreakStatus(user.getActiveHand()));


        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @NotNull
    private static DraconicTwinswordEntity initiateTwinswordEntity(ItemStack stack, World world, LivingEntity user, float strength) {
        var twinsword = new DraconicTwinswordEntity(world);
        twinsword.setOwner(user);
        twinsword.setStack(stack);

        var yaw = user.getYaw();
        var pitch = user.getPitch();

        float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
        float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));

        twinsword.updatePosition(user.getX() + f * 1.334, user.getEyeY() - 0.2, user.getZ() + h * 1.334);
        twinsword.setVelocity(0, strength, 0);
        twinsword.velocityDirty = true;
        twinsword.velocityModified = true;
        twinsword.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        return twinsword;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 200;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.spectrum.dragon_talon.tooltip").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.spectrum.dragon_talon.tooltip2").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.spectrum.dragon_talon.tooltip3").formatted(Formatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            var nbt = stack.getOrCreateNbt();
            if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
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
    public ItemStack getResult(ServerPlayerEntity player, ItemStack parent) {
        var result = new ItemStack(SpectrumItems.DRAGON_TALON);
        var durability = parent.getDamage();

        if (isReservingSlot(parent)) {
            durability  += player.getAbilities().creativeMode ? 0 : 500;
            player.getItemCooldownManager().set(result.getItem(), 400);
        }

        var nbt = parent.getOrCreateNbt();
        nbt.remove("lastTwinsword");
        nbt.remove("cooldown");
        nbt.remove(SlotReservingItem.NBT_STRING);


        result.setNbt(parent.getNbt());
        result.setDamage(durability);
        sign(player, result);
        return result;
    }

    @Override
    public boolean canSplit(ServerPlayerEntity player, Hand occupiedHand, ItemStack stack) {
        if (player.getItemCooldownManager().isCoolingDown(stack.getItem()))
            return false;

        return switch (occupiedHand) {
            case MAIN_HAND -> player.getStackInHand(Hand.OFF_HAND).isEmpty();
            case OFF_HAND -> player.getStackInHand(Hand.MAIN_HAND).isEmpty();
        };
    }

    @Override
    public SoundEvent getSplitSound() {
        return SoundEvents.ITEM_LODESTONE_COMPASS_LOCK;
    }

    @Override
    public boolean isReservingSlot(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean(SlotReservingItem.NBT_STRING);
    }

    @Override
    public void markReserved(ItemStack stack, boolean reserved) {
        stack.getOrCreateNbt().putBoolean(SlotReservingItem.NBT_STRING, reserved);
    }

    public static ItemStack findThrownStack(PlayerEntity player, UUID id) {
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);
            var nbt = stack.getNbt();
            if (nbt != null && nbt.containsUuid("lastTwinsword") && nbt.getUuid("lastTwinsword").equals(id)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Map<Enchantment, Integer> getDefaultEnchantments() {
        return Map.of(Enchantments.SWEEPING, 5);
    }

    @Override
    public int barCount(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean allowVanillaDurabilityBarRendering(@Nullable PlayerEntity player, ItemStack stack) {
        if (player == null || isReservingSlot(stack) || player.getStackInHand(player.getActiveHand()) != stack)
            return true;

        return !player.isUsingItem();
    }

    @Override
    public BarSignature getSignature(@Nullable PlayerEntity player, @NotNull ItemStack stack, int index) {
        if (player == null || isReservingSlot(stack) || !player.isUsingItem())
            return ExtendedItemBars.PASS;

        var activeStack = player.getStackInHand(player.getActiveHand());
        if (activeStack != stack)
            return ExtendedItemBars.PASS;


        var progress = Math.round(MathHelper.clampedLerp(0, 13, ((float) player.getItemUseTime() / MAX_CHARGE_TIME)));
        return new BarSignature(2, 13, 13, progress, 1, 0xffffe659, 2, ExtendedItemBars.DEFAULT_BACKGROUND_COLOR);
    }
}
