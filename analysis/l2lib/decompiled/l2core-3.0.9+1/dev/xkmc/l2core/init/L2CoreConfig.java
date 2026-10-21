package dev.xkmc.l2core.init;

import dev.xkmc.l2core.events.ClientEventHandler;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class L2CoreConfig {
   public static final L2CoreConfig.Client CLIENT = L2Core.REGISTRATE.registerClient(L2CoreConfig.Client::new);

   public static void init() {
   }

   public static class Client extends ConfigInit {
      public final IntValue overlayZVal;
      public final EnumValue<ClientEventHandler.EnchDesc> addEnchantmentDescription;
      public final BooleanValue renderOverlayIcons;

      Client(ConfigInit.Builder builder) {
         this.markL2();
         this.overlayZVal = builder.text("The height of item character overlay").defineInRange("overlayZVal", 250, -1000000, 1000000);
         this.addEnchantmentDescription = builder.text("Show Enchantment Descriptions when there are enchantments with custom parametric descriptions")
            .defineEnum("addEnchantmentDescription", ClientEventHandler.EnchDesc.ALWAYS);
         this.renderOverlayIcons = builder.text("Render overlay icons on entities").define("renderOverlayIcons", true);
      }
   }
}
