package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MysteriousLocketItem extends Item {
	
	public MysteriousLocketItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (!world.isClientSide) {
			ItemStack handStack = user.getItemInHand(hand);
			if (isSocketed(handStack)) {
				handStack.shrink(1);
				user.getInventory().placeItemBackInInventory(SpectrumItems.MYSTERIOUS_COMPASS.getDefaultInstance());
				world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.UNLOCK, SoundSource.NEUTRAL, 1.0F, 1.0F);
			}
		}
		return super.use(world, user, hand);
	}
	
	
	public static boolean isSocketed(ItemStack compassStack) {
		CompoundTag nbt = compassStack.getTag();
		return nbt != null && nbt.getBoolean("socketed");
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.mysterious_locket.tooltip").withStyle(ChatFormatting.GRAY));
		if (isSocketed(stack)) {
			tooltip.add(Component.translatable("item.spectrum.mysterious_locket.tooltip_socketed").withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable("item.spectrum.mysterious_locket.tooltip_empty").withStyle(ChatFormatting.GRAY));
		}
	}
	
}
