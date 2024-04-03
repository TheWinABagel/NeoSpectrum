package de.dafuqs.spectrum.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.InkPoweredStatusEffectInstance;
import de.dafuqs.spectrum.api.item.InkPoweredPotionFillable;
import de.dafuqs.spectrum.helpers.ColorHelper;
import de.dafuqs.spectrum.particle.effect.DynamicParticleEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class NightfallsBladeItem extends TieredItem implements Vanishable, InkPoweredPotionFillable {
	
	private static final ResourceLocation UNLOCK_IDENTIFIER = SpectrumCommon.locate("unlocks/equipment/nightfalls_blade");
	protected static final UUID REACH_MODIFIER_ID = UUID.fromString("8e2e05ef-a48a-4e2d-9633-388edcb21ea3");

	private final Multimap<Attribute, AttributeModifier> attributeModifiers;
	
	public NightfallsBladeItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
		super(material, settings);

		var damage = (float) attackDamage + material.getAttackDamageBonus();
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
		builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_MODIFIER_ID, "Weapon modifier", -1.5F, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getDefaultAttributeModifiers(slot);
	}
	
	@Override
	public int maxEffectCount() {
		return 1;
	}
	
	@Override
	public int maxEffectAmplifier() {
		return 2;
	}

	@Override
	public boolean isWeapon() {
		return true;
	}
	
	@Override
	public long adjustFinalCostFor(@NotNull InkPoweredStatusEffectInstance instance) {
		return Math.round(Math.pow(instance.getInkCost().getCost(), 1.75 + instance.getStatusEffectInstance().getAmplifier()));
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if(target.isAlive() && attacker instanceof Player player) {
			if (AdvancementHelper.hasAdvancement(player, UNLOCK_IDENTIFIER)) {
				List<InkPoweredStatusEffectInstance> effects = getEffects(stack);
				for(InkPoweredStatusEffectInstance instance : effects) {
					if(InkPowered.tryDrainEnergy(player, instance.getInkCost().getColor(), instance.getInkCost().getCost())) {
						Level world = attacker.level();
						if (world.isClientSide) {
							world.addParticle(new DynamicParticleEffect(ParticleTypes.EFFECT, 0.1F, ColorHelper.colorIntToVec(instance.getStatusEffectInstance().getEffect().getColor()), 0.5F, 120, true, true),
									target.getRandomX(0.5D), target.getY(0.5D), target.getRandomZ(0.5D),
									world.random.nextFloat() - 0.5, world.random.nextFloat() - 0.5, world.random.nextFloat() - 0.5
							);
						} else {
							target.addEffect(instance.getStatusEffectInstance(), attacker);
						}
					}
				}
			}
		}
		return super.hurtEnemy(stack, target, attacker);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || !PotionUtils.getCustomEffects(stack).isEmpty();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		appendPotionFillableTooltip(stack, tooltip, Component.translatable("item.spectrum.nightfalls_blade.when_struck"), true);
	}

}