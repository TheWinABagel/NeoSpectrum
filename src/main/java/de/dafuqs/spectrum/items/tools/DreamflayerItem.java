package de.dafuqs.spectrum.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.item.ActivatableItem;
import de.dafuqs.spectrum.api.item.SplitDamageItem;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumDamageTypes;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DreamflayerItem extends SwordItem implements InkPowered, ActivatableItem, SplitDamageItem {
	
	public static final InkColor USED_COLOR = InkColors.RED;
	public static final long INK_COST_FOR_ACTIVATION = 200L;
	public static final long INK_COST_PER_SECOND = 20L;
	
	/**
	 * The less armor the attacker with this sword has and the more
	 * the one that gets attacked, the higher the damage will be
	 * <p>
	 * See LivingEntityMixin spectrum$applyDreamflayerDamage
	 */
	public static final float ARMOR_DIFFERENCE_DAMAGE_MULTIPLIER = 2.5F;
	
	public final float attackDamage;
	public final float attackSpeed;
	
	public DreamflayerItem(Tier toolMaterial, int attackDamage, float attackSpeed, Properties settings) {
		super(toolMaterial, attackDamage, attackSpeed, settings);
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;
	}
	
	public static float getDamageAfterModifier(float amount, LivingEntity attacker, LivingEntity target) {
		float damageMultiplier = (target.getArmorValue() + DreamflayerItem.ARMOR_DIFFERENCE_DAMAGE_MULTIPLIER) / (attacker.getArmorValue() + DreamflayerItem.ARMOR_DIFFERENCE_DAMAGE_MULTIPLIER);
		return amount * damageMultiplier;
	}
	
	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.onUseTick(world, user, stack, remainingUseTicks);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (hand == InteractionHand.MAIN_HAND && user.isShiftKeyDown()) {
			boolean isActivated = ActivatableItem.isActivated(stack);
			if (isActivated) {
				ActivatableItem.setActivated(stack, false);
				if (!world.isClientSide) {
					world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.DREAMFLAYER_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1F);
				}
			} else {
				if (InkPowered.tryDrainEnergy(user, USED_COLOR, INK_COST_FOR_ACTIVATION)) {
					ActivatableItem.setActivated(stack, true);
					if (!world.isClientSide) {
						world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.DREAMFLAYER_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1F);
					}
				} else if (!world.isClientSide) {
					world.playSound(null, user.getX(), user.getY(), user.getZ(), SpectrumSoundEvents.DREAMFLAYER_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1F);
				}
			}
			
			return InteractionResultHolder.pass(stack);
		}
		
		return InteractionResultHolder.success(stack);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		
		if (world.isClientSide) {
			if (ActivatableItem.isActivated(stack)) {
				Vec3 pos = entity.position();
				world.addParticle(SpectrumParticleTypes.RED_CRAFTING,
						entity.getRandomX(1.0), pos.y() + 1.05D, entity.getRandomZ(1.0),
						0.0D, 0.1D, 0.0D);
			}
		} else {
			if (world.getGameTime() % 20 == 0 && ActivatableItem.isActivated(stack)) {
				if (entity instanceof ServerPlayer player && !InkPowered.tryDrainEnergy(player, USED_COLOR, INK_COST_PER_SECOND)) {
					ActivatableItem.setActivated(stack, false);
					world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SpectrumSoundEvents.DREAMFLAYER_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 1F);
				}
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.dreamflayer.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("item.spectrum.dreamflayer.tooltip2").withStyle(ChatFormatting.GRAY));
		if (ActivatableItem.isActivated(stack)) {
			tooltip.add(Component.translatable("item.spectrum.dreamflayer.tooltip.activated").withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable("item.spectrum.dreamflayer.tooltip.deactivated").withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return reequipAnimation(oldStack, newStack);
	}
	
	private boolean reequipAnimation(ItemStack before, ItemStack after) {
		return !after.is(this) || ActivatableItem.isActivated(before) != ActivatableItem.isActivated(after);
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		if (slot == EquipmentSlot.MAINHAND) {
			if (ActivatableItem.isActivated(stack)) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage * 1.5, AttributeModifier.Operation.ADDITION));
				builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed * 0.75, AttributeModifier.Operation.ADDITION));
			} else {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
				builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
			}
		}
		return builder.build();
	}

	@Override
	public List<InkColor> getUsedColors() {
		return List.of(USED_COLOR);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addInkPoweredTooltip(List<Component> tooltip) {
		InkPowered.super.addInkPoweredTooltip(tooltip);
	}

	@Override
	public DamageComposition getDamageComposition(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {
		float newDamage = getDamageAfterModifier(damage, attacker, target);

		DamageComposition composition = new DamageComposition();
		if (ActivatableItem.isActivated(stack)) {
			composition.addPlayerOrEntity(attacker, newDamage * 0.5F);
			composition.add(attacker.damageSources().magic(), newDamage * 0.25F);
			composition.add(SpectrumDamageTypes.setHealth(attacker.level(), attacker), newDamage * 0.25F);
		} else {
			composition.addPlayerOrEntity(attacker, newDamage * 0.75F);
			composition.add(attacker.damageSources().magic(), newDamage * 0.25F);
		}
		return composition;
	}

}
