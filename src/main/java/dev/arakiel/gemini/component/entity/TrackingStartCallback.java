package dev.arakiel.gemini.component.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface TrackingStartCallback {
   Event<TrackingStartCallback> EVENT = EventFactory.createArrayBacked(TrackingStartCallback.class, (TrackingStartCallback)(p, e) -> {
   }, listeners -> (player, entity) -> {
         for (TrackingStartCallback callback : listeners) {
            callback.onPlayerStartTracking(player, entity);
         }
      });

   void onPlayerStartTracking(ServerPlayer var1, Entity var2);
}
