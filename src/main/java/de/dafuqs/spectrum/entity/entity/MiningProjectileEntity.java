package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.helpers.AoEHelper;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.spells.MoonstoneStrike;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Predicate;

public class MiningProjectileEntity extends MagicProjectileEntity {
	
	private static final int MINING_RANGE = 1;
	private ItemStack toolStack = ItemStack.EMPTY;

	public MiningProjectileEntity(EntityType<MiningProjectileEntity> type, Level world) {
		super(type, world);
	}

	public MiningProjectileEntity(double x, double y, double z, Level world) {
		this(SpectrumEntityTypes.MINING_PROJECTILE, world);
		this.moveTo(x, y, z, this.getYRot(), this.getXRot());
		this.reapplyPosition();
	}

	public MiningProjectileEntity(Level world, LivingEntity owner) {
		this(owner.getX(), owner.getEyeY() - 0.1, owner.getZ(), world);
		this.setOwner(owner);
		this.setRot(owner.getYRot(), owner.getXRot());
	}
	
	public static void shoot(Level world, LivingEntity entity, ItemStack stack) {
		MiningProjectileEntity projectile = new MiningProjectileEntity(world, entity);
		projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 2.0F, 1.0F);
		projectile.toolStack = stack.copy();
		world.addFreshEntity(projectile);
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	public void tick() {
		super.tick();
		this.spawnParticles(1);
	}

	private void spawnParticles(int amount) {
		for (int j = 0; j < amount; ++j) {
			this.level().addParticle(SpectrumParticleTypes.WHITE_CRAFTING, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0, 0);
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		MoonstoneStrike.create(this.level(), this, null, this.getX(), this.getY(), this.getZ(), 1);
		this.discard();
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);

		MoonstoneStrike.create(this.level(), this, null, this.getX(), this.getY(), this.getZ(), 1);

		Entity entity = getOwner();
		if (entity instanceof Player player) {
			Predicate<BlockState> minablePredicate = state -> {
				int miningLevel = this.toolStack.getItem() instanceof TieredItem toolItem ? toolItem.getTier().getLevel() : 1;
				int efficiency = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, this.toolStack);
				return state.getBlock().defaultDestroyTime() <= miningLevel + efficiency;
			};
			AoEHelper.breakBlocksAround(player, this.toolStack, blockHitResult.getBlockPos(), MINING_RANGE, minablePredicate);
		}

		this.discard();
	}

	@Override
	public DyeColor getDyeColor() {
		return DyeColor.WHITE;
	}

}
