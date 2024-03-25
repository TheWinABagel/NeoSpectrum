package de.dafuqs.spectrum.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.dafuqs.spectrum.api.item.ExtendedEnchantable;
import de.dafuqs.spectrum.api.item.Preenchanted;
import de.dafuqs.spectrum.entity.entity.BidentBaseEntity;
import de.dafuqs.spectrum.entity.entity.BidentEntity;
import de.dafuqs.spectrum.entity.entity.BidentMirrorImageEntity;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class MalachiteBidentItem extends TridentItem implements Preenchanted, ExtendedEnchantable {
	
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;
	
	public MalachiteBidentItem(Properties settings, double damage) {
		super(settings);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.6, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
	}
	
	@Override
	public Map<Enchantment, Integer> getDefaultEnchantments() {
		return Map.of(Enchantments.IMPALING, 6);
	}
	
	@Override
	public ItemStack getDefaultInstance() {
		return getDefaultEnchantedStack(this);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack handStack = user.getItemInHand(hand);
		if (handStack.getDamageValue() >= handStack.getMaxDamage() - 1) {
			return InteractionResultHolder.fail(handStack);
		}
		user.startUsingItem(hand);
		return InteractionResultHolder.consume(handStack);
	}
	
	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof Player player) {
			int useTime = this.getUseDuration(stack) - remainingUseTicks;
			if (useTime >= 10) {
				player.awardStat(Stats.ITEM_USED.get(this));

				if (canStartRiptide(player, stack)) {
					riptide(world, player, getRiptideLevel(stack));
				} else if (!world.isClientSide) {
					stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(user.getUsedItemHand()));
					throwBident(stack, (ServerLevel) world, player);
				}
			}
		}
	}
	
	public int getRiptideLevel(ItemStack stack) {
		return EnchantmentHelper.getRiptide(stack);
	}
	
	protected void riptide(Level world, Player playerEntity, int riptideLevel) {
		yeetPlayer(playerEntity, (float) riptideLevel);
		playerEntity.startAutoSpinAttack(20);
		if (playerEntity.onGround()) {
			playerEntity.move(MoverType.SELF, new Vec3(0.0, 1.2, 0.0));
		}
		
		SoundEvent soundEvent;
		if (riptideLevel >= 3) {
			soundEvent = SoundEvents.TRIDENT_RIPTIDE_3;
		} else if (riptideLevel == 2) {
			soundEvent = SoundEvents.TRIDENT_RIPTIDE_2;
		} else {
			soundEvent = SoundEvents.TRIDENT_RIPTIDE_1;
		}
		
		world.playSound(null, playerEntity, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
	}
	
	protected void yeetPlayer(Player playerEntity, float riptideLevel) {
		float f = playerEntity.getYRot();
		float g = playerEntity.getXRot();
		float h = -Mth.sin(f * 0.017453292F) * Mth.cos(g * 0.017453292F);
		float k = -Mth.sin(g * 0.017453292F);
		float l = Mth.cos(f * 0.017453292F) * Mth.cos(g * 0.017453292F);
		float m = Mth.sqrt(h * h + k * k + l * l);
		float n = 3.0F * ((1.0F + riptideLevel) / 4.0F);
		h *= n / m;
		k *= n / m;
		l *= n / m;
		playerEntity.push(h, k, l);
	}
	
	protected void throwBident(ItemStack stack, ServerLevel world, Player playerEntity) {
		boolean mirrorImage = isThrownAsMirrorImage(stack, world, playerEntity);
		
		BidentBaseEntity bidentBaseEntity = mirrorImage ? new BidentMirrorImageEntity(world) : new BidentEntity(world);
		bidentBaseEntity.setStack(stack);
		bidentBaseEntity.setOwner(playerEntity);
		bidentBaseEntity.absMoveTo(playerEntity.getX(), playerEntity.getEyeY() - 0.1, playerEntity.getZ());
		bidentBaseEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, getThrowSpeed(), 1.0F);
		if (!mirrorImage && playerEntity.getAbilities().instabuild) {
			bidentBaseEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		}
		
		world.addFreshEntity(bidentBaseEntity);
		SoundEvent soundEvent = SoundEvents.TRIDENT_THROW;
		if (mirrorImage) {
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, bidentBaseEntity.position(), SpectrumParticleTypes.MIRROR_IMAGE, 8, Vec3.ZERO, new Vec3(0.2, 0.2, 0.2));
			bidentBaseEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
			soundEvent = SpectrumSoundEvents.BIDENT_MIRROR_IMAGE_THROWN;
		} else if (playerEntity.getAbilities().instabuild) {
			bidentBaseEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		}
		
		world.playSound(null, bidentBaseEntity, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
		if (!playerEntity.getAbilities().instabuild && !mirrorImage) {
			playerEntity.getInventory().removeItem(stack);
		}
	}
	
	public float getThrowSpeed() {
		return 2.5F;
	}
	
	public boolean canStartRiptide(Player player, ItemStack stack) {
		return getRiptideLevel(stack) > 0 && player.isInWaterOrRain();
	}
	
	public boolean isThrownAsMirrorImage(ItemStack stack, ServerLevel world, Player player) {
		return false;
	}
	
	@Override
	public boolean acceptsEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.SHARPNESS || enchantment == Enchantments.SMITE || enchantment == Enchantments.BANE_OF_ARTHROPODS || enchantment == Enchantments.MOB_LOOTING || enchantment == SpectrumEnchantments.CLOVERS_FAVOR;
	}

}
