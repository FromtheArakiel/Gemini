package dev.arakiel.gemini.component.level;

import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.sync.AutoSyncedComponent;
import dev.arakiel.gemini.component.sync.ComponentPacketWriter;
import dev.arakiel.gemini.component.sync.PlayerSyncPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelData;

public final class LevelComponents {
   public static void sync(ComponentKey<?> key, MinecraftServer server) {
      LevelData props = server.getWorldData().overworldData();

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         key.syncWith(player, props.asComponentProvider());
      }
   }

   public static void sync(ComponentKey<?> key, MinecraftServer server, ComponentPacketWriter packetWriter) {
      LevelData props = server.getWorldData().overworldData();
      if (key.get(props) instanceof AutoSyncedComponent sc) {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            key.syncWith(player, props.asComponentProvider(), packetWriter, sc);
         }
      }
   }

   public static void sync(ComponentKey<?> key, MinecraftServer server, ComponentPacketWriter packetWriter, PlayerSyncPredicate predicate) {
      LevelData props = server.getWorldData().overworldData();

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         key.syncWith(player, props.asComponentProvider(), packetWriter, predicate);
      }
   }
}
