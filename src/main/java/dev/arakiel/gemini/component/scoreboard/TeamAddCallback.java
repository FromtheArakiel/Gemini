package dev.arakiel.gemini.component.scoreboard;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.scores.PlayerTeam;

@FunctionalInterface
public interface TeamAddCallback {
   Event<TeamAddCallback> EVENT = EventFactory.createArrayBacked(TeamAddCallback.class, (TeamAddCallback)t -> {
   }, listeners -> team -> {
         for (TeamAddCallback callback : listeners) {
            callback.onTeamAdded(team);
         }
      });

   void onTeamAdded(PlayerTeam var1);
}
