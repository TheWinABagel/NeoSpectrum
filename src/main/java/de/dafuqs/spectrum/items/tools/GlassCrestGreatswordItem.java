package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.api.energy.InkCost;
import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.item.SplitDamageItem;
import de.dafuqs.spectrum.api.render.ExtendedItemBars;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.GreatswordChargingSoundInstance;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassCrestGreatswordItem extends GreatswordItem implements SplitDamageItem, ExtendedItemBars {
	
	private static final InkCost GROUND_SLAM_COST = new InkCost(InkColors.WHITE, 25);
	public static final float MAGIC_DAMAGE_SHARE = 0.25F;
	public final int GROUND_SLAM_CHARGE_TICKS = 32;
	
	public GlassCrestGreatswordItem(Tier material, int attackDamage, float attackSpeed, float extraReach, Properties settings) {
		super(material, attackDamage, attackSpeed, extraReach, settings);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("item.spectrum.glass_crest_ultra_greatsword.tooltip", (int) (MAGIC_DAMAGE_SHARE * 100)));
		tooltip.add(Component.translatable("item.spectrum.glass_crest_ultra_greatsword.tooltip2"));
		tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.white"));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (getGroundSlamStrength(user.getItemInHand(hand)) > 0 && InkPowered.tryDrainEnergy(user, GROUND_SLAM_COST)) {
			if (world.isClientSide) {
				startSoundInstance(user);
			}
			return ItemUtils.startUsingInstantly(world, user, hand);
		}
		return InteractionResultHolder.pass(user.getItemInHand(hand));
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return GROUND_SLAM_CHARGE_TICKS;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.SPEAR;
	}

	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.onUseTick(world, user, stack, remainingUseTicks);
		if (world.isClientSide) {
			RandomSource random = world.random;
			for (int i = 0; i < (GROUND_SLAM_CHARGE_TICKS - remainingUseTicks) / 8; i++) {
				world.addParticle(ParticleTypes.INSTANT_EFFECT,
						user.getRandomX(1.0), user.getY(), user.getRandomZ(1.0),
						random.nextDouble() * 5.0D - 2.5D, random.nextDouble() * 1.2D, random.nextDouble() * 5.0D - 2.5D);
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		if (!world.isClientSide) {
			int groundSlamStrength = getGroundSlamStrength(stack);
			if (groundSlamStrength > 0) {
				performGroundSlam(world, user.position(), user, groundSlamStrength);
				stack.hurtAndBreak(1, user, (p) -> p.broadcastBreakEvent(user.getUsedItemHand()));
			}
		}
		
		return stack;
	}
	
	public int getGroundSlamStrength(ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, stack);
	}
	
	public void performGroundSlam(Level world, Vec3 pos, LivingEntity attacker, float strength) {
		world.gameEvent(attacker, GameEvent.ENTITY_ROAR, BlockPos.containing(pos.x, pos.y, pos.z));
		MoonstoneStrike.create(world, attacker, null, attacker.getX(), attacker.getY(), attacker.getZ(), strength, 1.75F);
		world.playSound(null, attacker.blockPosition(), SpectrumSoundEvents.GROUND_SLAM, SoundSource.PLAYERS, 0.7F, 1.0F);
		world.playSound(null, attacker.blockPosition(), SpectrumSoundEvents.DEEP_CRYSTAL_RING, SoundSource.PLAYERS, 0.7F, 1.0F);
		world.playSound(null, attacker.blockPosition(), SpectrumSoundEvents.DEEP_CRYSTAL_RING, SoundSource.PLAYERS, 0.4F, 0.334F);

		if (attacker instanceof ServerPlayer serverPlayer) {
			serverPlayer.awardStat(Stats.ITEM_USED.get(this));
		}
	}

	@Environment(EnvType.CLIENT)
	public void startSoundInstance(Player user) {
		Minecraft.getInstance().getSoundManager().play(new GreatswordChargingSoundInstance(user, this.GROUND_SLAM_CHARGE_TICKS));
	}

	@Override
	public DamageComposition getDamageComposition(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {
		DamageComposition composition = new DamageComposition();
		composition.addPlayerOrEntity(attacker, damage * (1 - MAGIC_DAMAGE_SHARE));
		composition.add(attacker.damageSources().magic(), damage * MAGIC_DAMAGE_SHARE);
		return composition;
	}

	@Override
	public int barCount(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean allowVanillaDurabilityBarRendering(@Nullable Player player, ItemStack stack) {
		if (player == null || player.getItemInHand(player.getUsedItemHand()) != stack)
			return true;

		return !player.isUsingItem();
	}

	@Override
	public BarSignature getSignature(@Nullable Player player, @NotNull ItemStack stack, int index) {
		if (player == null || !player.isUsingItem())
			return ExtendedItemBars.PASS;

		var activeStack = player.getItemInHand(player.getUsedItemHand());
		if (activeStack != stack)
			return ExtendedItemBars.PASS;


		var progress = Math.round(Mth.clampedLerp(0, 13, ((float) player.getTicksUsingItem() / GROUND_SLAM_CHARGE_TICKS)));
		return new BarSignature(2, 13, 13, progress, 1, 0xFFFFFFFF, 2, ExtendedItemBars.DEFAULT_BACKGROUND_COLOR);
	}
}
