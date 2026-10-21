package dev.xkmc.l2tabs.compat.common;

import dev.xkmc.l2tabs.compat.api.AccessoriesMultiplex;
import dev.xkmc.l2tabs.compat.api.IAccessoriesWrapper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CuriosListMenu extends BaseCuriosListMenu<CuriosListMenu> {
   public static CuriosListMenu fromNetwork(MenuType<CuriosListMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
      int page = buf.readInt();
      return new CuriosListMenu(type, wid, plInv, AccessoriesMultiplex.get().wrap(plInv.player, page));
   }

   public CuriosListMenu(MenuType<?> type, int wid, Inventory plInv, IAccessoriesWrapper curios) {
      super(type, wid, plInv, curios);
   }

   @Override
   public void switchPage(ServerPlayer sp, int page) {
      new CuriosMenuPvd((MenuType<CuriosListMenu>)AccessoriesMultiplex.MT_CURIOS.get(), page).open(sp);
   }
}
