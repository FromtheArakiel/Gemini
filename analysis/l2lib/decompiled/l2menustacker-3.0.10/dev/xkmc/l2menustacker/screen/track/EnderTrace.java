package dev.xkmc.l2menustacker.screen.track;

import dev.xkmc.l2menustacker.screen.base.LayerPopType;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class EnderTrace extends TrackedEntryType<NoData> {
   public LayerPopType restoreMenuNotifyClient(ServerPlayer player, NoData data, @Nullable Component comp) {
      if (comp == null) {
         comp = Component.translatable("container.enderchest");
      }

      player.openMenu(new SimpleMenuProvider((wid, inv, pl) -> ChestMenu.threeRows(wid, inv, pl.getEnderChestInventory()), comp));
      return LayerPopType.REMAIN;
   }
}
