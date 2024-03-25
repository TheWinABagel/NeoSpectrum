package de.dafuqs.spectrum.events.listeners;

import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.effect.TypedTransmission;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class ExperienceOrbEventQueue extends EventQueue<ExperienceOrbEventQueue.EventEntry> {
	
	public ExperienceOrbEventQueue(PositionSource positionSource, int range, Callback<EventEntry> listener) {
		super(positionSource, range, listener);
	}
	
	@Override
	public void acceptEvent(Level world, GameEvent.ListenerInfo event, Vec3 sourcePos) {
		if (world instanceof ServerLevel && event.context().sourceEntity() instanceof ExperienceOrb experienceOrbEntity) {
			Vec3 pos = event.source();
			EventEntry eventEntry = new EventEntry(event.gameEvent(), experienceOrbEntity, Mth.floor(pos.distanceTo(sourcePos)));
			int delay = eventEntry.distance * 2;
			this.schedule(eventEntry, delay);
			SpectrumS2CPacketSender.playTransmissionParticle((ServerLevel) world, new TypedTransmission(pos, this.positionSource, delay, TypedTransmission.Variant.EXPERIENCE));
		}
	}
	
	public static class EventEntry {
		public final GameEvent gameEvent;
		public final ExperienceOrb experienceOrbEntity;
		public final int distance;
		
		public EventEntry(GameEvent gameEvent, ExperienceOrb experienceOrbEntity, int distance) {
			this.gameEvent = gameEvent;
			this.experienceOrbEntity = experienceOrbEntity;
			this.distance = distance;
		}
	}
	
}