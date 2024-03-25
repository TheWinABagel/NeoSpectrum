package de.dafuqs.spectrum.progression;

import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class GravityAdvancementsManager {
	
	private static long lastGravityTick = 0;
	private static ServerPlayer lastServerPlayerEntity = null;
	private static double appliedGravityThisTick = 0;
	
	/**
	 * Since each players inventory should be ticked one after another we
	 * can rely on just checking the player and the current tick for summing up
	 * gravity effect for checking for advancements
	 *
	 * @param serverPlayerEntity The player with gravity items in inventory
	 * @param additionalGravity  The additional gravity the entity got applied for a single stack. Will be added up for each tick in this function
	 */
	public static void processAppliedGravityForAdvancements(@NotNull ServerPlayer serverPlayerEntity, double additionalGravity) {
		ServerLevel serverWorld = (ServerLevel) serverPlayerEntity.level();
		if (serverWorld != null) {
			if (serverWorld.getGameTime() != lastGravityTick || lastServerPlayerEntity != serverPlayerEntity) {
				lastServerPlayerEntity = serverPlayerEntity;
				lastGravityTick = serverWorld.getGameTime();
				appliedGravityThisTick = 0.0D;
			}
			appliedGravityThisTick += additionalGravity;
			
			// taking flight
			if (appliedGravityThisTick > 0.081) {
				Support.grantAdvancementCriterion(serverPlayerEntity, "lategame/carry_too_many_low_gravity_blocks", "gravity");
				// unable to jump a full block
			} else if (appliedGravityThisTick < -0.025) {
				Support.grantAdvancementCriterion(serverPlayerEntity, "midgame/carry_too_many_heavy_gravity_blocks", "gravity");
			}
		}
	}
	
}
