package dev.xkmc.l2menustacker.screen.packets;

import dev.xkmc.l2menustacker.screen.base.ScreenTrackerClient;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

public record AddTrackedToClient(TrackedEntry<?> entry, int toRemove, int wid) implements SerialPacketBase<AddTrackedToClient> {
   public void handle(Player player) {
      ScreenTrackerClient.clientAddLayer(this.entry, this.toRemove, this.wid);
   }
}
