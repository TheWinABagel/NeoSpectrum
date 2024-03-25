package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// riptide w/o weather requirement; damages enemies on touch; iframes?
public class FerociousBidentItem extends MalachiteBidentItem {
	
	public static final InkCost RIPTIDE_COST = new InkCost(InkColors.WHITE, 10);
	public static final int BUILTIN_RIPTIDE_LEVEL = 1;

	public FerociousBidentItem(Properties settings, double damage) {
		super(settings, damage);
	}
	
	@Override
	public int getRiptideLevel(ItemStack stack) {
		return Math.max(EnchantmentHelper.getRiptide(stack), BUILTIN_RIPTIDE_LEVEL);
	}

	@Override
	public boolean canStartRiptide(Player player, ItemStack stack) {
		return super.canStartRiptide(player, stack) || InkPowered.tryDrainEnergy(player, RIPTIDE_COST);
	}
	
	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.onUseTick(world, user, stack, remainingUseTicks);
		if (user.isAutoSpinAttack() && user instanceof Player player) {
			
			int useTime = this.getUseDuration(stack) - remainingUseTicks;
			if (useTime % 10 == 0) {
				if (InkPowered.tryDrainEnergy(player, RIPTIDE_COST)) {
					stack.hurtAndBreak(1, user, (p) -> p.broadcastBreakEvent(user.getUsedItemHand()));
				} else {
					user.releaseUsingItem();
					return;
				}
			}
			
			yeetPlayer(player, getRiptideLevel(stack) / 128F - 0.75F);
			player.startAutoSpinAttack(20);
			
			for (LivingEntity entityAround : world.getEntities(EntityTypeTest.forClass(LivingEntity.class), player.getBoundingBox().inflate(2), LivingEntity::isAlive)) {
				if (entityAround != player) {
					entityAround.hurt(world.damageSources().playerAttack(player), 2);
				}
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.ferocious_glass_crest_bident.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.ferocious_glass_crest_bident.tooltip2").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.ferocious_glass_crest_bident.tooltip3").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.white").withStyle(ChatFormatting.GRAY));
	}
	
}
