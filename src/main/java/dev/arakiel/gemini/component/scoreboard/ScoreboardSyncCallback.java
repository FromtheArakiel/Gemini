package dev.arakiel.gemini.component.scoreboard;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Scoreboard;

@FunctionalInterface
public interface ScoreboardSyncCallback {
   Event<ScoreboardSyncCallback> EVENT = EventFactory.createArrayBacked(ScoreboardSyncCallback.class, (ScoreboardSyncCallback)(p, t) -> {
   }, listeners -> (player, scoreboard) -> {
         for (ScoreboardSyncCallback callback : listeners) {
            callback.onScoreboardSync(player, scoreboard);
         }
      });

   void onScoreboardSync(ServerPlayer var1, Scoreboard var2);
}
