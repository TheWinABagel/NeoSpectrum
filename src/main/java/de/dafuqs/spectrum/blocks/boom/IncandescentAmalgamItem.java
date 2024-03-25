package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.api.item.DamageAwareItem;
import de.dafuqs.spectrum.api.item.FermentedItem;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.items.food.beverages.properties.BeverageProperties;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IncandescentAmalgamItem extends BlockItem implements DamageAwareItem, FermentedItem {
	
	public IncandescentAmalgamItem(Block block, Properties settings) {
		super(block, settings);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		stack = super.finishUsingItem(stack, world, user);
		
		user.hurt(SpectrumDamageTypes.incandescence(world), 500.0F);
		
		float explosionPower = getExplosionPower(stack, false);
		world.explode(user, SpectrumDamageTypes.incandescence(world), new EntityBasedExplosionDamageCalculator(user), user.getX(), user.getY(), user.getZ(), explosionPower / 5, false, Level.ExplosionInteraction.BLOCK);
		world.explode(user, SpectrumDamageTypes.incandescence(world), new EntityBasedExplosionDamageCalculator(user), user.getX(), user.getY(), user.getZ(), explosionPower, true, Level.ExplosionInteraction.NONE);
		
		if (user.isAlive() && user instanceof ServerPlayer serverPlayerEntity && !serverPlayerEntity.isCreative()) {
			Support.grantAdvancementCriterion(serverPlayerEntity, "survive_drinking_incandescent_amalgam", "survived_drinking_incandescent_amalgam");
		}
		
		return stack;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("block.spectrum.incandescent_amalgam.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("block.spectrum.incandescent_amalgam.tooltip_power", getExplosionPower(stack, false)).withStyle(ChatFormatting.GRAY));
		if (FermentedItem.isPreviewStack(stack)) {
			tooltip.add(Component.translatable("block.spectrum.incandescent_amalgam.tooltip.preview").withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public void onItemEntityDamaged(DamageSource source, float amount, ItemEntity itemEntity) {
		// remove the itemEntity before dealing damage, otherwise it would cause a stack overflow
		ItemStack stack = itemEntity.getItem();
		itemEntity.remove(Entity.RemovalReason.KILLED);
		
		float explosionPower = getExplosionPower(stack, true);
		var world = itemEntity.level();
		world.explode(itemEntity, SpectrumDamageTypes.incandescence(world, itemEntity), new EntityBasedExplosionDamageCalculator(itemEntity), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), explosionPower / 8F, false, Level.ExplosionInteraction.BLOCK);
		world.explode(itemEntity, SpectrumDamageTypes.incandescence(world, itemEntity), new EntityBasedExplosionDamageCalculator(itemEntity), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), explosionPower, true, Level.ExplosionInteraction.NONE);
	}

	@Override
	public BeverageProperties getBeverageProperties(ItemStack stack) {
		return BeverageProperties.getFromStack(stack);
	}

	public float getExplosionPower(ItemStack stack, boolean useCount) {
		float alcPercent = getBeverageProperties(stack).alcPercent;
		if (alcPercent <= 0) {
			return 6;
		} else {
			return alcPercent + (useCount ? stack.getCount() / 8F : 0);
		}
	}
	
}
