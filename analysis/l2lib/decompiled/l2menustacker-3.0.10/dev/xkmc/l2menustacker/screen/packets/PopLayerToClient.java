package dev.xkmc.l2menustacker.screen.packets;

import dev.xkmc.l2menustacker.screen.base.LayerPopType;
import dev.xkmc.l2menustacker.screen.base.ScreenTrackerClient;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

public record PopLayerToClient(LayerPopType type, int wid) implements SerialPacketBase<PopLayerToClient> {
   public void handle(Player player) {
      ScreenTrackerClient.clientPop(this.type, this.wid);
   }
}
