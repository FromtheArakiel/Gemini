package dev.arakiel.gemini.component;

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

   public static void sync(ComponentKey<?> key, MinecraftServer server, ComponentSync.PacketWriter packetWriter) {
      LevelData props = server.getWorldData().overworldData();
      if (key.get(props) instanceof ComponentSync.AutoSyncedComponent sc) {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            key.syncWith(player, props.asComponentProvider(), packetWriter, sc);
         }
      }
   }

   public static void sync(ComponentKey<?> key, MinecraftServer server, ComponentSync.PacketWriter packetWriter, ComponentSync.PlayerPredicate predicate) {
      LevelData props = server.getWorldData().overworldData();

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         key.syncWith(player, props.asComponentProvider(), packetWriter, predicate);
      }
   }
}