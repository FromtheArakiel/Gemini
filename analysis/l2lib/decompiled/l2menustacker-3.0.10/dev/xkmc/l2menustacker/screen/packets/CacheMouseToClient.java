package dev.xkmc.l2menustacker.screen.packets;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

public record CacheMouseToClient() implements SerialPacketBase<CacheMouseToClient> {
   public void handle(Player player) {
      MouseCache.cacheMousePos();
   }
}
