package de.dafuqs.spectrum.items.magic_items.ampoules;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.dafuqs.spectrum.entity.entity.LightSpearEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BloodstoneGlassAmpouleItem extends BaseGlassAmpouleItem {
	
	protected static final float EXTRA_REACH = 12.0F;
	protected static final UUID REACH_MODIFIER_ID = UUID.fromString("c81a7152-313c-452f-b15e-fcf51322ccc0");
	
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;
	
	public BloodstoneGlassAmpouleItem(Properties settings) {
		super(settings);
		
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_MODIFIER_ID, "Weapon modifier", EXTRA_REACH, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getDefaultAttributeModifiers(slot);
	}
	
	@Override
	public boolean trigger(ItemStack stack, LivingEntity attacker, @Nullable LivingEntity target) {
		Level world = attacker.level();
		if (target == null) {
			return false;
		}
		if (!world.isClientSide) {
			LightSpearEntity.summonBarrage(attacker.level(), attacker, target);
		}
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.bloodstone_glass_ampoule.tooltip").withStyle(ChatFormatting.GRAY));
	}

}
