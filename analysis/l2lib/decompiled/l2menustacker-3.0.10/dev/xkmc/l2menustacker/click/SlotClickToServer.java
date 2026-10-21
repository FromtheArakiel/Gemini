package dev.xkmc.l2menustacker.click;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record SlotClickToServer(ResourceLocation type, int index, int slot, int wid) implements SerialPacketBase<SlotClickToServer> {
   public void handle(Player player) {
      SlotClickHandler handler = SlotClickHandler.MAP.get(this.type);
      if (handler != null && player instanceof ServerPlayer sp) {
         handler.handle(sp, this.index, this.slot, this.wid);
      }
   }
}
