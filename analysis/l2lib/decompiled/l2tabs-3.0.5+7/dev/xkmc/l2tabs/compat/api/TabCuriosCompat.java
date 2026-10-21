package dev.xkmc.l2tabs.compat.api;

import dev.xkmc.l2tabs.compat.track.CurioTraceData;
import net.minecraft.server.level.ServerPlayer;

public class TabCuriosCompat {
   public static void onStartup() {
      AccessoriesMultiplex.getOptional().ifPresent(AccessoriesMultiplex::onStartUp);
   }

   public static void onCommonSetup() {
      AccessoriesMultiplex.getOptional().ifPresent(AccessoriesMultiplex::onCommonSetup);
   }

   public static void onClientInit() {
      AccessoriesMultiplex.getOptional().ifPresent(AccessoriesMultiplex::onClientInit);
   }

   public static void openCuriosTab(ServerPlayer player) {
      if (AccessoriesMultiplex.isPresent()) {
         AccessoriesMultiplex.openScreen(player, new CurioTraceData(0));
      }
   }
}
