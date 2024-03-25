package de.dafuqs.spectrum.blocks.boom;

import de.dafuqs.spectrum.entity.entity.ParametricMiningDeviceEntity;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParametricMiningDeviceItem extends ModularExplosionBlockItem {
	
	public ParametricMiningDeviceItem(Block block, Properties settings) {
		super(block, 5, 0, 3, settings);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		tooltip.add(Component.translatable("block.spectrum.parametric_mining_device.tooltip").withStyle(ChatFormatting.GRAY));
		super.appendHoverText(stack, world, tooltip, context);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		var stack = user.getItemInHand(hand);
		if (stack.is(this)) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.BLOCK_PARAMETRIC_MINING_DEVICE_THROWN, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!world.isClientSide()) {
				ParametricMiningDeviceEntity entity = new ParametricMiningDeviceEntity(world, user);
				entity.setItem(stack);
				entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 1.5F, 0F);
				world.addFreshEntity(entity);
			}
			if (!user.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		return InteractionResultHolder.success(stack);
	}
	
}
