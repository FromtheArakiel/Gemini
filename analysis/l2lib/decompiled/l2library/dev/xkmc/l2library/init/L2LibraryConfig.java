package dev.xkmc.l2library.init;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2core.util.ConfigInit.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class L2LibraryConfig {
   public static final L2LibraryConfig.Server SERVER = (L2LibraryConfig.Server)L2Library.REGISTRATE.registerSynced(L2LibraryConfig.Server::new);

   public static void init() {
   }

   public static class Server extends ConfigInit {
      public final BooleanValue restoreFullHealthOnRespawn;

      Server(Builder builder) {
         this.markL2();
         this.restoreFullHealthOnRespawn = builder.text("Restore full health on respawn").define("restoreFullHealthOnRespawn", true);
      }
   }
}
