package dev.xkmc.l2tabs.compat.common;

import dev.xkmc.l2tabs.compat.api.AccessoriesMultiplex;
import dev.xkmc.l2tabs.init.data.L2TabsLangData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public record CuriosMenuPvd(MenuType<CuriosListMenu> type, int page) implements MenuProvider {
   public CuriosMenuPvd(MenuType<CuriosListMenu> type) {
      this(type, 0);
   }

   public Component getDisplayName() {
      return L2TabsLangData.CURIOS.get();
   }

   public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
      return new CuriosListMenu(this.type, wid, inv, AccessoriesMultiplex.get().wrap(player, this.page));
   }

   public void writeBuf(FriendlyByteBuf buf) {
      buf.writeInt(this.page);
   }

   public void open(ServerPlayer player) {
      player.openMenu(this, this::writeBuf);
   }
}
