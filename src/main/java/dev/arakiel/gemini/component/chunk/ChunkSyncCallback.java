package dev.arakiel.gemini.component.chunk;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;

@FunctionalInterface
public interface ChunkSyncCallback {
   Event<ChunkSyncCallback> EVENT = EventFactory.createArrayBacked(ChunkSyncCallback.class, (ChunkSyncCallback)(p, chunk) -> {
   }, listeners -> (player, chunk) -> {
         for (ChunkSyncCallback callback : listeners) {
            callback.onChunkSync(player, chunk);
         }
      });

   void onChunkSync(ServerPlayer var1, LevelChunk var2);
}
