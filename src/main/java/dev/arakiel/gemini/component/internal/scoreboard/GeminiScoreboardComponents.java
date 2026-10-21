package dev.arakiel.gemini.component.internal.scoreboard;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.ComponentCallbacks;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.network.MorePacketCodecs;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.scores.PlayerTeam;

public final class GeminiScoreboardComponents {
   public static final Type<ComponentUpdatePayload<Unit>> SCOREBOARD_PACKET_ID = ComponentUpdatePayload.id("scoreboard_sync");
   public static final Type<ComponentUpdatePayload<String>> TEAM_PACKET_ID = ComponentUpdatePayload.id("team_sync");

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentUpdatePayload.register(SCOREBOARD_PACKET_ID, MorePacketCodecs.EMPTY);
         ComponentUpdatePayload.register(TEAM_PACKET_ID, ByteBufCodecs.STRING_UTF8);
         ComponentCallbacks.ScoreboardSync.EVENT.register((ComponentCallbacks.ScoreboardSync)(player, tracked) -> {
            for (ComponentKey<?> key : tracked.asComponentProvider().getComponentContainer().keys()) {
               key.syncWith(player, tracked.asComponentProvider());
            }

            for (PlayerTeam team : tracked.getPlayerTeams()) {
               for (ComponentKey<?> key : team.asComponentProvider().getComponentContainer().keys()) {
                  key.syncWith(player, team.asComponentProvider());
               }
            }
         });
         ComponentCallbacks.TeamAdd.EVENT.register((ComponentCallbacks.TeamAdd)tracked -> {
            for (ComponentKey<?> key : tracked.asComponentProvider().getComponentContainer().keys()) {
               tracked.syncComponent(key);
            }
         });
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ServerLifecycleEvents.SERVER_STARTED
            .register((ServerStarted)server -> ((ComponentProvider)server.getScoreboard()).getComponentContainer().onServerLoad());
         ServerLifecycleEvents.SERVER_STOPPED
            .register((ServerStopped)server -> ((ComponentProvider)server.getScoreboard()).getComponentContainer().onServerUnload());
      }

      StaticScoreboardComponentPlugin.INSTANCE.ensureInitialized();
   }
}