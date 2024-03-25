package de.dafuqs.spectrum.spells;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class InkSpellEffect {
	
	final InkColor color;
	
	public InkSpellEffect(InkColor color) {
		this.color = color;
	}
	
	public abstract void playEffects(Level world, Vec3 origin, float potency);
	
	abstract void affectEntity(Entity entity, Vec3 origin, float potency);
	
	abstract void affectArea(Level world, BlockPos origin, float potency);
	
	public static void trigger(InkColor inkColor, Level world, Vec3 position, float potency) {
		InkSpellEffect effect = InkSpellEffects.getEffect(inkColor);
		if (effect != null) {
			if (world instanceof ServerLevel) {
				SpectrumS2CPacketSender.playInkEffectParticles((ServerLevel) world, inkColor, position, potency);
			} else {
				effect.playEffects(world, position, potency);
			}
			List<Entity> entities = world.getEntitiesOfClass(Entity.class, AABB.ofSize(position, potency / 2, potency / 2, potency / 2));
			for (Entity entity : entities) {
				effect.affectEntity(entity, position, potency);
			}
			effect.affectArea(world, BlockPos.containing(position.x, position.y, position.z), potency);
		}
	}
	
}
