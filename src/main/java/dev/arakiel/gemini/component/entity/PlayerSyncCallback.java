package dev.arakiel.gemini.component.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface PlayerSyncCallback {
   Event<PlayerSyncCallback> EVENT = EventFactory.createArrayBacked(PlayerSyncCallback.class, (PlayerSyncCallback)p -> {
   }, listeners -> player -> {
         for (PlayerSyncCallback callback : listeners) {
            callback.onPlayerSync(player);
         }
      });

   void onPlayerSync(ServerPlayer var1);
}
