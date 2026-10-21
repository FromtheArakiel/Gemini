package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@SerialClass
public record PlayerCapToClient(PlayerCapToClient.Action action, ResourceLocation holderID, byte[] data, UUID playerID)
   implements SerialPacketBase<PlayerCapToClient> {
   public static <T extends PlayerCapabilityTemplate<T>> PlayerCapToClient of(
      ServerPlayer player, PlayerCapToClient.Action action, PlayerCapabilityHolder<T> holder, T handler
   ) {
      return new PlayerCapToClient(
         action, holder.id, PacketCodec.toBytes(player.level().registryAccess(), handler, holder.cls(), action.pred), player.getUUID()
      );
   }

   public void handle(Player player) {
      ClientSyncHandler.parse(
         player.level().registryAccess(), this.data, PlayerCapabilityHolder.INTERNAL_MAP.get(this.holderID), this.action.pred, this.playerID
      );
   }

   public enum Action {
      ALL(e -> true),
      CLIENT(SerialField::toClient),
      TRACK(SerialField::toTracking);

      public final Predicate<SerialField> pred;

      Action(Predicate<SerialField> pred) {
         this.pred = pred;
      }
   }
}
