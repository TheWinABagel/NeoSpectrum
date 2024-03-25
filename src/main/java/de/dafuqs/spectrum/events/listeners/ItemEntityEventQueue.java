package de.dafuqs.spectrum.events.listeners;

import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.effect.TypedTransmission;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class ItemEntityEventQueue extends EventQueue<ItemEntityEventQueue.EventEntry> {
	
	public ItemEntityEventQueue(PositionSource positionSource, int range, Callback<EventEntry> listener) {
		super(positionSource, range, listener);
	}
	
	@Override
	public void acceptEvent(Level world, GameEvent.ListenerInfo event, Vec3 sourcePos) {
		if (world instanceof ServerLevel && event.context().sourceEntity() instanceof ItemEntity itemEntity) {
			Vec3 pos = event.source();
			EventEntry eventEntry = new EventEntry(event.gameEvent(), itemEntity, Mth.floor(pos.distanceTo(sourcePos)));
			int delay = eventEntry.distance * 2;
			this.schedule(eventEntry, delay);
			SpectrumS2CPacketSender.playTransmissionParticle((ServerLevel) world, new TypedTransmission(pos, this.positionSource, delay, TypedTransmission.Variant.ITEM));
		}
	}
	
	public static class EventEntry {
		public final GameEvent gameEvent;
		public final ItemEntity itemEntity;
		public final int distance;
		
		public EventEntry(GameEvent gameEvent, ItemEntity itemEntity, int distance) {
			this.gameEvent = gameEvent;
			this.itemEntity = itemEntity;
			this.distance = distance;
		}
	}
	
}