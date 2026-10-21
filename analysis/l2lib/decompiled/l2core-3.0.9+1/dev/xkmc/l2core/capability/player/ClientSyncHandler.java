package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;

public class ClientSyncHandler {
   public static <T extends PlayerCapabilityTemplate<T>> void parse(
      RegistryAccess access, byte[] tag, PlayerCapabilityHolder<T> holder, Predicate<SerialField> pred, UUID playerId
   ) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Player player = level.getPlayerByUUID(playerId);
         if (player != null) {
            PacketCodec.fromBytes(access, tag, holder.cls(), (PlayerCapabilityTemplate)player.getData(holder.type()), pred);
         }
      }
   }
}
