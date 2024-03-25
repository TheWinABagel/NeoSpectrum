package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.blocks.bottomless_bundle.BottomlessBundleItem;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStarDispenserBehavior;
import de.dafuqs.spectrum.entity.entity.GlassArrowEntity;
import de.dafuqs.spectrum.items.tools.GlassArrowVariant;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

public class SpectrumDispenserBehaviors {
	
	public static void register() {
		DispenserBlock.registerBehavior(SpectrumItems.BOTTOMLESS_BUNDLE, new BottomlessBundleItem.BottomlessBundlePlacementDispenserBehavior());
		DispenserBlock.registerBehavior(SpectrumItems.BEDROCK_SHEARS, new ShearsDispenseItemBehavior());
		
		// Shooting Stars
		DispenserBlock.registerBehavior(SpectrumBlocks.COLORFUL_SHOOTING_STAR, new ShootingStarDispenserBehavior());
		DispenserBlock.registerBehavior(SpectrumBlocks.FIERY_SHOOTING_STAR, new ShootingStarDispenserBehavior());
		DispenserBlock.registerBehavior(SpectrumBlocks.GEMSTONE_SHOOTING_STAR, new ShootingStarDispenserBehavior());
		DispenserBlock.registerBehavior(SpectrumBlocks.GLISTERING_SHOOTING_STAR, new ShootingStarDispenserBehavior());
		DispenserBlock.registerBehavior(SpectrumBlocks.PRISTINE_SHOOTING_STAR, new ShootingStarDispenserBehavior());
		
		// Fluid Buckets
		DispenseItemBehavior fluidBucketBehavior = SpectrumBlocks.ENDER_DROPPER.getDefaultBehaviorForItem(Items.WATER_BUCKET.getDefaultInstance());
		DispenserBlock.registerBehavior(SpectrumItems.MUD_BUCKET, fluidBucketBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.LIQUID_CRYSTAL_BUCKET, fluidBucketBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.MIDNIGHT_SOLUTION_BUCKET, fluidBucketBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.DRAGONROT_BUCKET, fluidBucketBehavior);
		
		// Arrows
		for (GlassArrowVariant variant : SpectrumRegistries.GLASS_ARROW_VARIANT) {
			DispenserBlock.registerBehavior(variant.getArrow(), new AbstractProjectileDispenseBehavior() {
				@Override
				protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
					GlassArrowEntity arrow = new GlassArrowEntity(world, position.x(), position.y(), position.z());
					arrow.pickup = AbstractArrow.Pickup.ALLOWED;
					arrow.setVariant(variant);
					return arrow;
				}
			});
		}
		
		// Spawn Eggs
		DispenseItemBehavior spawnEggBehavior = SpectrumBlocks.ENDER_DROPPER.getDefaultBehaviorForItem(Items.SHEEP_SPAWN_EGG.getDefaultInstance());
		DispenserBlock.registerBehavior(SpectrumItems.EGG_LAYING_WOOLY_PIG_SPAWN_EGG, spawnEggBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.KINDLING_SPAWN_EGG, spawnEggBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.LIZARD_SPAWN_EGG, spawnEggBehavior);
        DispenserBlock.registerBehavior(SpectrumItems.PRESERVATION_TURRET_SPAWN_EGG, spawnEggBehavior);
        DispenserBlock.registerBehavior(SpectrumItems.ERASER_SPAWN_EGG, spawnEggBehavior);
		
		// Equipping Mob Heads
		DispenseItemBehavior armorEquipBehavior = SpectrumBlocks.ENDER_DROPPER.getDefaultBehaviorForItem(Items.PLAYER_HEAD.getDefaultInstance());
		for (Block skullBlock : SpectrumBlocks.MOB_HEADS.values()) {
			DispenserBlock.registerBehavior(skullBlock, armorEquipBehavior);
		}
		
		// Decay
		DispenseItemBehavior blockPlacementDispenserBehavior = new ShulkerBoxDispenseBehavior();
		
		DispenserBlock.registerBehavior(SpectrumItems.BOTTLE_OF_FADING, blockPlacementDispenserBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.BOTTLE_OF_FAILING, blockPlacementDispenserBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.BOTTLE_OF_RUIN, blockPlacementDispenserBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.BOTTLE_OF_FORFEITURE, blockPlacementDispenserBehavior);
		DispenserBlock.registerBehavior(SpectrumItems.BOTTLE_OF_DECAY_AWAY, blockPlacementDispenserBehavior);
	}
	
}
