package dev.xkmc.l2itemselector.init.data;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2core.util.ConfigInit.Builder;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.overlay.InfoSideBar;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;

public class L2ISConfig {
   public static final L2ISConfig.Client CLIENT = (L2ISConfig.Client)L2ItemSelector.REGISTRATE.registerClient(L2ISConfig.Client::new);

   public static void init() {
   }

   public static class Client extends ConfigInit {
      public final DoubleValue infoAlpha;
      public final EnumValue<InfoSideBar.Anchor> infoAnchor;
      public final DoubleValue infoMaxWidth;
      public final BooleanValue selectionDisplayRequireShift;
      public final BooleanValue selectionScrollRequireShift;

      Client(Builder builder) {
         this.markL2();
         this.infoAlpha = builder.text("Info box background transparency")
            .comment("Background transparency for info text box overlay. 1 means opaque.")
            .defineInRange("infoAlpha", 0.5, 0.0, 1.0);
         this.infoAnchor = builder.text("Info box vertical alignment").defineEnum("infoAnchor", InfoSideBar.Anchor.CENTER);
         this.infoMaxWidth = builder.text("Info box max width")
            .comment("Max width for info box. 0.5 means half screen. default: 0.3")
            .defineInRange("infoMaxWidth", 0.3, 0.0, 0.5);
         this.selectionDisplayRequireShift = builder.text("Render selector only when pressing [Hold Selection]").define("selectionDisplayRequireShift", false);
         this.selectionScrollRequireShift = builder.text("Scroll for selection only when pressing [Hold Selection]")
            .define("selectionScrollRequireShift", true);
      }
   }
}
