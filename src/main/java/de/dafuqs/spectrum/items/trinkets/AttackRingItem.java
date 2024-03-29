package de.dafuqs.spectrum.items.trinkets;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.dafuqs.spectrum.SpectrumCommon;
import dev.emi.trinkets.api.SlotReference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttackRingItem extends SpectrumTrinketItem {
	
	public static final UUID ATTACK_RING_DAMAGE_UUID = UUID.fromString("15d1fb68-6440-404a-aa31-7bf3310d3f52");
	public static final String ATTACK_RING_DAMAGE_NAME = "spectrum:jeopardant";
	
	public AttackRingItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/jeopardant"));
	}
	
	public static double getAttackModifierForEntity(LivingEntity entity) {
		if (entity == null) {
			return 0;
		} else {
			double mod = entity.getMaxHealth() / (entity.getHealth() * entity.getHealth() + 1); // starting with 1 % damage at 14 health up to 300 % damage at 1/20 health
			return Math.max(0, 1 + Math.log10(mod));
		}
	}
	
	@Override
	public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
		super.onUnequip(stack, slot, entity);
		if (entity.getAttributes().hasModifier(Attributes.ATTACK_DAMAGE, AttackRingItem.ATTACK_RING_DAMAGE_UUID)) {
			Multimap<Attribute, AttributeModifier> map = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
			AttributeModifier modifier = new AttributeModifier(AttackRingItem.ATTACK_RING_DAMAGE_UUID, ATTACK_RING_DAMAGE_NAME, AttackRingItem.getAttackModifierForEntity(entity), AttributeModifier.Operation.MULTIPLY_TOTAL);
			map.put(Attributes.ATTACK_DAMAGE, modifier);
			entity.getAttributes().removeAttributeModifiers(map);
		}
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		Minecraft client = Minecraft.getInstance();
		long mod = Math.round(getAttackModifierForEntity(client.player) * 100);
		if (mod == 0) {
			tooltip.add(Component.translatable("item.spectrum.jeopardant.tooltip.damage_zero"));
		} else {
			tooltip.add(Component.translatable("item.spectrum.jeopardant.tooltip.damage", mod));
		}
	}
	
}
