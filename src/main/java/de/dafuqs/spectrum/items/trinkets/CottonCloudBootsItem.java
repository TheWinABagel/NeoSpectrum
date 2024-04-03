package de.dafuqs.spectrum.items.trinkets;

import de.dafuqs.spectrum.SpectrumCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CottonCloudBootsItem extends SpectrumTrinketItem {
	
	public CottonCloudBootsItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/cotton_cloud_boots"));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		super.curioTick(slotContext, stack);
		LivingEntity entity = slotContext.entity();
		Level level = entity.level();
		if (entity.isSprinting() && !entity.onGround() && !entity.isShiftKeyDown()) {
			Vec3 velocity = entity.getDeltaMovement();
			if (velocity.y < 0) {
				entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0.1, 1));
				if (level.isClientSide) {
					RandomSource random = level.random;
					level.addParticle(ParticleTypes.CLOUD, entity.getX(), entity.getY(), entity.getZ(),
							0.125 - random.nextFloat() * 0.25, 0.04 - random.nextFloat() * 0.08, 0.125 - random.nextFloat() * 0.25);
				}
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.cotton_cloud_boots.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.cotton_cloud_boots.tooltip2").withStyle(ChatFormatting.GRAY));
	}
	
}