package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.entity.entity.GlassArrowEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassArrowItem extends ArrowItem {
	
	public final GlassArrowVariant variant;
	
	public GlassArrowItem(Properties settings, GlassArrowVariant variant, ParticleOptions particleEffect) {
		super(settings);
		this.variant = variant;
		variant.setData(this, particleEffect);
	}
	
	@Override
	public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
		GlassArrowEntity entity = new GlassArrowEntity(world, shooter);
		entity.setVariant(variant);
		return entity;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		
		
		tooltip.add(Component.translatable("item.spectrum.glass_arrow.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.glass_arrow.tooltip2").withStyle(ChatFormatting.GRAY));
		if (variant != GlassArrowVariant.MALACHITE) {
			tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
		}
	}
	
}
