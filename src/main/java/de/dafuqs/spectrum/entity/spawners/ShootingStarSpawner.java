package de.dafuqs.spectrum.entity.spawners;

import de.dafuqs.revelationary.api.advancements.AdvancementHelper;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.shooting_star.ShootingStar;
import de.dafuqs.spectrum.entity.entity.ShootingStarEntity;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.CustomSpawner;
import org.jetbrains.annotations.NotNull;

public class ShootingStarSpawner implements CustomSpawner {
	
	public static final ShootingStarSpawner INSTANCE = new ShootingStarSpawner();
	
	private ShootingStarSpawner() {
	}
	
	@Override
	public int tick(ServerLevel world, boolean spawnMonsters, boolean spawnAnimals) {
		int spawns = 0;
		
		for (Player playerEntity : world.getEntities(EntityType.PLAYER, Entity::isAlive)) {
			if (!playerEntity.isSpectator()
					&& AdvancementHelper.hasAdvancement(playerEntity, SpectrumItems.STAR_FRAGMENT.getCloakAdvancementIdentifier())
					&& world.getRandom().nextFloat() < getShootingStarChanceWithMultiplier(playerEntity)) {
				
				// 1 % chance for each cycle to spawn a lot of shooting stars for the player
				// making it an amazing display
				if (world.getRandom().nextFloat() < 0.01F) {
					for (int i = 0; i < 5; i++) {
						spawnShootingStar(world, playerEntity);
					}
					spawns += 5;
				} else {
					spawnShootingStar(world, playerEntity);
					spawns++;
				}
			}
		}
		
		return spawns;
	}
	
	// If the player explicitly searches for shooting stars give them a small boost :)
	// That these things increase the visibility of shooting stars is explicitly stated
	// in the guidebook, just not that these actually give a boost, too
	protected static float getShootingStarChanceWithMultiplier(@NotNull Player playerEntity) {
		int multiplier = 1;
		for (ItemStack handStack : playerEntity.getHandSlots()) {
			if (handStack != null && handStack.is(Items.SPYGLASS)) {
				multiplier += 4;
				break;
			}
		}
		if (playerEntity.hasEffect(MobEffects.NIGHT_VISION)) {
			multiplier++;
		}
		return SpectrumCommon.CONFIG.ShootingStarChance * multiplier;
	}
	
	public static void spawnShootingStar(ServerLevel serverWorld, @NotNull Player playerEntity) {
		ShootingStarEntity shootingStarEntity = new ShootingStarEntity(serverWorld, playerEntity.position().x(), playerEntity.position().y() + 200, playerEntity.position().z());
		shootingStarEntity.setDeltaMovement(serverWorld.random.nextDouble() * 0.2D - 0.1D, 0.0D, serverWorld.random.nextDouble() * 0.2D - 0.1D);
		shootingStarEntity.setShootingStarType(ShootingStar.Type.getWeightedRandomType(serverWorld.getRandom()), false, false);
		shootingStarEntity.push(5 - serverWorld.random.nextFloat() * 10, 0, 5 - serverWorld.random.nextFloat() * 10);
		serverWorld.addFreshEntity(shootingStarEntity);
	}
	
}
