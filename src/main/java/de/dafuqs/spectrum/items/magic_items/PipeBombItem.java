package de.dafuqs.spectrum.items.magic_items;

import de.dafuqs.spectrum.api.item.DamageAwareItem;
import de.dafuqs.spectrum.api.item.TickAwareItem;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.PipeBombChargingSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PipeBombItem extends Item implements DamageAwareItem, TickAwareItem {

    public PipeBombItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide) {
            startSoundInstance(user);
        }
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Environment(EnvType.CLIENT)
    public void startSoundInstance(Player user) {
        Minecraft.getInstance().getSoundManager().play(new PipeBombChargingSoundInstance(user));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        var nbt = stack.getOrCreateTag();

        nbt.putBoolean("armed", true);
        nbt.putLong("timestamp", world.getGameTime());
        nbt.putUUID("owner", user.getUUID());
        user.playSound(SpectrumSoundEvents.INCANDESCENT_ARM, 2F, 0.9F);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (world.isClientSide())
            return;

        var nbt = stack.getOrCreateTag();
        if (!nbt.contains("armed"))
            return;

        if (tryGetOwner(stack, (ServerLevel) world) == entity && world.getGameTime() - nbt.getLong("timestamp") < 100)
            return;

        explode(stack, (ServerLevel) world, entity.position(), Optional.of(entity));
    }

    @Override
    public void onItemEntityTicked(ItemEntity itemEntity) {
        var world = itemEntity.level();
        var stack = itemEntity.getItem();
        var nbt = stack.getOrCreateTag();

        if (world.isClientSide() || !nbt.contains("armed"))
            return;

        if (world.getGameTime() - nbt.getLong("timestamp") > 100)
            explode(stack, (ServerLevel) world, itemEntity.getEyePosition(), Optional.empty());
    }

    @Override
    public void onItemEntityDamaged(DamageSource source, float amount, ItemEntity itemEntity) {
        if ((source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_EXPLOSION)) && !itemEntity.level().isClientSide()) {
            explode(itemEntity.getItem(), (ServerLevel) itemEntity.level(), itemEntity.position(), Optional.empty());
        }
    }

    private void explode(ItemStack stack, ServerLevel world, Vec3 pos, Optional<Entity> target) {
        stack.shrink(1);
		Entity owner = tryGetOwner(stack, world);

        target.ifPresent(entity -> entity.hurt(SpectrumDamageTypes.incandescence(world, owner instanceof LivingEntity living ? living : null), 200F));
        world.explode(null, SpectrumDamageTypes.incandescence(world), new ExplosionDamageCalculator(), pos.x(), pos.y(), pos.z(), 7.5F, true, Level.ExplosionInteraction.NONE);
    }

    public Entity tryGetOwner(ItemStack stack, ServerLevel world) {
        CompoundTag nbt = stack.getTag();

        if(nbt == null || !nbt.contains("owner")) {
            return null;
        }

        return world.getEntity(nbt.getUUID("owner"));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 55;
    }
    
    public static float isArmed(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        var nbt = stack.getOrCreateTag();
        if (!nbt.contains("armed"))
            return 0F;

        return nbt.getBoolean("armed") ? 1F : 0F;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(Component.translatable("item.spectrum.pipe_bomb.tooltip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.spectrum.pipe_bomb.tooltip2").withStyle(ChatFormatting.GRAY));
    tooltip.add(Component.translatable("item.spectrum.pipe_bomb.tooltip3").withStyle(ChatFormatting.GRAY));
    }

}
