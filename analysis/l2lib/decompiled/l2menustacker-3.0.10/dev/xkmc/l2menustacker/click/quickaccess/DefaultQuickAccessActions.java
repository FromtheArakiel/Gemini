package dev.xkmc.l2menustacker.click.quickaccess;

import dev.xkmc.l2menustacker.compat.arclight.CartographyMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.GrindstoneMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.LoomMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.SmithingMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.StonecutterMenuArclight;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2menustacker.screen.track.QuickAccessTraceData;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import java.util.Optional;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DefaultQuickAccessActions {
   public static void register() {
      quickAccess(MenuType.CRAFTING, Items.CRAFTING_TABLE, CraftingMenu::new, "container.crafting");
      quickAccess(MenuType.STONECUTTER, Items.STONECUTTER, StonecutterMenuArclight::new, "container.stonecutter");
      quickAccess(MenuType.GRINDSTONE, Items.GRINDSTONE, GrindstoneMenuArclight::new, "container.grindstone_title");
      quickAccess(MenuType.CARTOGRAPHY_TABLE, Items.CARTOGRAPHY_TABLE, CartographyMenuArclight::new, "container.cartography_table");
      quickAccess(MenuType.LOOM, Items.LOOM, LoomMenuArclight::new, "container.loom");
      quickAccess(MenuType.SMITHING, Items.SMITHING_TABLE, SmithingMenuArclight::new, "container.upgrade");
   }

   public static <T extends AbstractContainerMenu> void quickAccess(MenuType<T> type, Item item, SimpleMenuAction.MenuFactory factory, String id) {
      QuickAccessClickHandler.register(item, new SimpleMenuAction(factory, id));
      quickAccess(type, item);
   }

   public static <T extends AbstractContainerMenu> void quickAccess(MenuType<T> type, Item item) {
      MenuTraceRegistry.register(
         type,
         menu -> Optional.of(
            TrackedEntry.of((TrackedEntryType<QuickAccessTraceData>)L2MSReg.TE_QUICK_ACCESS.get(), new QuickAccessTraceData(item.getDefaultInstance()))
         )
      );
   }
}
