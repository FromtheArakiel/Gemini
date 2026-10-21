package dev.xkmc.l2menustacker.init;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2core.util.ConfigInit.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class L2MSConfig {
   public static final L2MSConfig.Server SERVER = (L2MSConfig.Server)L2MenuStacker.REGISTRATE.registerSynced(L2MSConfig.Server::new);

   public static void init() {
   }

   public static class Server extends ConfigInit {
      public final BooleanValue tabSafeMode;

      Server(Builder builder) {
         this.markL2();
         this.tabSafeMode = builder.text("Safe Mode for menu stacking")
            .comment("When enabled, some modded container will be skipped when exiting")
            .define("tabSafeMode", false);
      }
   }
}
