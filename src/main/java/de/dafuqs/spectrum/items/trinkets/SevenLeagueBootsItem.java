package de.dafuqs.spectrum.items.trinkets;

import com.google.common.collect.Multimap;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import dev.emi.stepheightentityattribute.StepHeightEntityAttributeMain;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.UUID;

public class SevenLeagueBootsItem extends SpectrumTrinketItem implements ExtendedEnchantable {
	
	public static final UUID STEP_BOOST_UUID = UUID.fromString("47b19f03-3211-4b4d-abf1-0c544a19dc70");
	private static final AttributeModifier STEP_BOOST_MODIFIER = new AttributeModifier(STEP_BOOST_UUID, "spectrum:speed_boots", 0.75, AttributeModifier.Operation.ADDITION);
	
	public SevenLeagueBootsItem(Properties settings) {
		super(settings, SpectrumCommon.locate("unlocks/trinkets/seven_league_boots"));
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
		Multimap<Attribute, AttributeModifier> modifiers = super.getModifiers(stack, slot, entity, uuid);
		
		int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
		double speedBoost = 0.05 * (powerLevel + 1);
		modifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "spectrum:movement_speed", speedBoost, AttributeModifier.Operation.MULTIPLY_TOTAL));
		modifiers.put(StepHeightEntityAttributeMain.STEP_HEIGHT, STEP_BOOST_MODIFIER);
		
		return modifiers;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return stack.getCount() == 1;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.POWER_ARROWS;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 8;
	}
	
}