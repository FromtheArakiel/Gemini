package dev.arakiel.gemini.component.client.internal.scoreboard;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.client.internal.network.ComponentClientInternals;
import dev.arakiel.gemini.component.internal.scoreboard.GeminiScoreboardComponents;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class GeminiScoreboardClient {
   public static void initClient() {
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         ClientPacketListener networkHandler = client.getConnection();
         if (networkHandler != null) {
            Scoreboard scoreboard = networkHandler.scoreboard();
            scoreboard.asComponentProvider().getComponentContainer().tickClientComponents();

            for (PlayerTeam team : scoreboard.getPlayerTeams()) {
               team.asComponentProvider().getComponentContainer().tickClientComponents();
            }
         }
      });
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentClientInternals.registerComponentSync(
            GeminiScoreboardComponents.TEAM_PACKET_ID,
            (payload, ctx) -> payload.componentKey()
                  .flatMap(
                     key -> (Optional<? extends Component>)key.maybeGet(
                           Objects.requireNonNull(ctx.client().level).getScoreboard().getPlayerTeam((String)payload.targetData())
                        )
                  )
         );
         ComponentClientInternals.registerComponentSync(
            GeminiScoreboardComponents.SCOREBOARD_PACKET_ID,
            (payload, ctx) -> payload.componentKey()
                  .flatMap(key -> (Optional<? extends Component>)key.maybeGet(Objects.requireNonNull(ctx.client().level).getScoreboard()))
         );
      }
   }
}
