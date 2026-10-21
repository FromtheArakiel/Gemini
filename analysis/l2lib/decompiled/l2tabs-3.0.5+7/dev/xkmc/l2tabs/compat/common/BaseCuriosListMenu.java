package dev.xkmc.l2tabs.compat.common;

import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2core.base.menu.base.BaseContainerMenu.BaseContainer;
import dev.xkmc.l2tabs.compat.api.IAccessoriesSlotWrapper;
import dev.xkmc.l2tabs.compat.api.IAccessoriesWrapper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class BaseCuriosListMenu<T extends BaseCuriosListMenu<T>> extends BaseContainerMenu<T> {
   public static final SpriteManager[] MANAGER = new SpriteManager[4];
   public final IAccessoriesWrapper curios;

   private static SpriteManager getManager(int size) {
      return MANAGER[Math.min(Math.max(size - 3, 0), 3)];
   }

   protected BaseCuriosListMenu(MenuType<?> type, int wid, Inventory plInv, IAccessoriesWrapper curios) {
      super(type, wid, plInv, getManager(curios.getRows()), e -> new BaseContainer(curios.getSize(), e), false);
      this.addCurioSlot("grid", curios);
      this.curios = curios;
   }

   protected void addCurioSlot(String name, IAccessoriesWrapper curios) {
      int current = this.added;
      int[] removed = new int[]{0};
      this.getLayout().getSlot(name, (x, y) -> {
         int i = this.added - current;
         IAccessoriesSlotWrapper slot = curios.getSlotAtPosition(i + removed[0]);
         if (slot == null) {
            removed[0]++;
            return null;
         } else {
            Slot ans = slot.toSlot(x, y);
            this.added++;
            return ans;
         }
      }, (x$0, x$1, x$2, x$3) -> this.addSlot(x$0, x$1, x$2, x$3));
   }

   private boolean checkSwitch(Player player, int page) {
      if (page >= 0 && page < this.curios.total) {
         if (player instanceof ServerPlayer sp) {
            CuriosEventHandler.openMenuWrapped(sp, () -> this.switchPage(sp, page));
         } else {
            this.slots.clear();
         }

         return true;
      } else {
         return false;
      }
   }

   public abstract void switchPage(ServerPlayer var1, int var2);

   public boolean clickMenuButton(Player player, int btn) {
      if (btn == 1) {
         return this.checkSwitch(player, this.curios.page - 1);
      } else {
         return btn == 2 ? this.checkSwitch(player, this.curios.page + 1) : super.clickMenuButton(player, btn);
      }
   }

   static {
      for (int i = 0; i < 4; i++) {
         MANAGER[i] = new SpriteManager("l2tabs", "curios_" + (i + 3));
      }
   }
}
