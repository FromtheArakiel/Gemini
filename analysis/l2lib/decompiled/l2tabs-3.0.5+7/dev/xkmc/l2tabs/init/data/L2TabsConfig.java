package dev.xkmc.l2tabs.init.data;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2core.util.ConfigInit.Builder;
import dev.xkmc.l2tabs.init.L2Tabs;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.util.Lazy;

public class L2TabsConfig {
   public static final L2TabsConfig.Client CLIENT = (L2TabsConfig.Client)L2Tabs.REGISTRATE.registerClient(L2TabsConfig.Client::new);

   public static void init() {
   }

   public static class Client extends ConfigInit {
      public final BooleanValue showTabs;
      public final BooleanValue showTabsOnlyCurio;
      public final BooleanValue redirectInventoryTabToCuriosInventory;
      public final IntValue attributeLinePerPage;
      public final EnumValue<AttrDispEntry.AttributeDisplay> attributeSettings;
      public final ConfigValue<List<? extends String>> hiddenTabs;

      Client(Builder builder) {
         this.markL2();
         this.showTabs = builder.text("Show inventory tabs").define("showTabs", true);
         this.showTabsOnlyCurio = builder.text("Tabs with curios only")
            .comment(
               new String[]{
                  "Show inventory tabs only in curio page.",
                  "Only works when showTabs is true and curio is installed.",
                  "For users who have other tabs conflicting with this mod"
               }
            )
            .define("showTabsOnlyCurio", false);
         this.redirectInventoryTabToCuriosInventory = builder.text("Redirect Inventory Tab to Curios Inventory")
            .define("redirectInventoryTabToCuriosInventory", true);
         this.attributeLinePerPage = builder.text("Number of attribute lines per page").defineInRange("attributeLinePerPage", 15, 1, 100);
         this.attributeSettings = builder.text("Attribute display")
            .comment("COMMON: Show only common attributes and L2 attributes")
            .comment("ALL: Show all attributes in attribute type, similar to Apothic Attributes")
            .comment("ALL_EXCEPT_UNCHANGED: Show all, but hide attributes that are unchanged")
            .defineEnum("attributeSettings", AttrDispEntry.AttributeDisplay.COMMON);
         Lazy<Set<String>> keys = Lazy.of(() -> L2Tabs.TABS.get().keySet().stream().map(ResourceLocation::getPath).collect(Collectors.toSet()));
         this.hiddenTabs = builder.text("Hidden Tabs")
            .comment("List of tabs to hide")
            .comment("Example: \"attribute\" for attribute tab")
            .comment("Example: \"curios\" for curios tab")
            .comment("Example: \"pandora\" for pandora tab")
            .defineListAllowEmpty("hiddenTabs", new ArrayList(List.of()), () -> "attribute", e -> ((Set)keys.get()).contains((String)e));
      }
   }
}
