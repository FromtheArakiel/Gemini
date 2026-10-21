package dev.xkmc.l2core.serial.config;

import dev.xkmc.l2serial.network.SerialPacketBase;
import java.util.ArrayList;
import net.minecraft.world.entity.player.Player;

public record SyncPacket(String id, ArrayList<PacketHandlerWithConfig.ConfigInstance> map) implements SerialPacketBase<SyncPacket> {
   public void handle(Player player) {
      if (this.map != null) {
         PacketHandlerWithConfig handler = PacketHandlerWithConfig.INTERNAL.get(this.id);
         handler.listener.apply(this.map);
      }
   }
}
