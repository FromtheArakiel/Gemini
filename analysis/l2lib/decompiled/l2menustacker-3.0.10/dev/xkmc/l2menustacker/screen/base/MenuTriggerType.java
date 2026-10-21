package dev.xkmc.l2menustacker.screen.base;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public enum MenuTriggerType {
   OPEN_MENU,
   DISABLED;

   public boolean restore(ServerPlayer player, MenuProvider pvd, @Nullable FriendlyByteBuf buf) {
      if (this == OPEN_MENU) {
         if (buf == null) {
            player.openMenu(pvd);
         } else {
            player.openMenu(pvd, e -> {
               buf.resetReaderIndex();
               e.writeBytes(buf);
            });
         }

         return true;
      } else {
         return false;
      }
   }
}
