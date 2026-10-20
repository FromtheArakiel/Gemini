package dev.arakiel.gemini.component.world;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface WorldSyncCallback {
   Event<WorldSyncCallback> EVENT = EventFactory.createArrayBacked(WorldSyncCallback.class, (WorldSyncCallback)(p, e) -> {
   }, listeners -> (player, world) -> {
         for (WorldSyncCallback callback : listeners) {
            callback.onPlayerStartTracking(player, world);
         }
      });

   void onPlayerStartTracking(ServerPlayer var1, ServerLevel var2);
}
