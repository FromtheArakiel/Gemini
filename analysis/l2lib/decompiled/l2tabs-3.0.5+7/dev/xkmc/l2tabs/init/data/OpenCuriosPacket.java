package dev.xkmc.l2tabs.init.data;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record OpenCuriosPacket(OpenCurioHandler event) implements SerialPacketBase<OpenCuriosPacket> {
   public void handle(Player player) {
      if (player instanceof ServerPlayer sp) {
         this.event.invoke(sp);
      }
   }
}
