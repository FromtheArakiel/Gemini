package dev.xkmc.l2menustacker.screen.packets;

import dev.xkmc.l2menustacker.init.L2MenuStacker;
import dev.xkmc.l2menustacker.screen.base.ClientCloseResult;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.base.ScreenTracker;
import dev.xkmc.l2menustacker.screen.triggers.ExitMenuTrigger;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record RestoreMenuToServer(int wid) implements SerialPacketBase<RestoreMenuToServer> {
   public void handle(Player player) {
      if (player instanceof ServerPlayer sp) {
         if (this.wid < 0) {
            if (this.wid == ClientCloseResult.POP_ALL.id && ScreenTracker.size(sp) >= 2) {
               ((ExitMenuTrigger)L2MSReg.EXIT_MENU.get()).trigger(sp, true);
            }

            ScreenTracker.removeAll(sp);
         } else {
            AbstractContainerMenu menu = player.containerMenu;
            if (menu.containerId != this.wid || !ScreenTracker.get(player).serverRestore(sp, this.wid)) {
               L2MenuStacker.PACKET_HANDLER.toClientPlayer(new SetScreenToClient(ScreenType.NONE), sp);
            }
         }
      }
   }
}
