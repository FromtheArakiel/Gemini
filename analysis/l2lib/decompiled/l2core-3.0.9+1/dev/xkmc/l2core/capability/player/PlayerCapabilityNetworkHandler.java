package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2core.init.L2Core;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCapabilityNetworkHandler<T extends PlayerCapabilityTemplate<T>> {
   public final PlayerCapabilityHolder<T> holder;

   public PlayerCapabilityNetworkHandler(PlayerCapabilityHolder<T> holder) {
      this.holder = holder;
   }

   public void toClient(ServerPlayer e) {
      this.holder
         .getExisting(e)
         .ifPresent(x -> L2Core.PACKET_HANDLER.toClientPlayer(PlayerCapToClient.of(e, PlayerCapToClient.Action.CLIENT, this.holder, (T)x), e));
   }

   public void toTracking(ServerPlayer e) {
      this.holder
         .getExisting(e)
         .ifPresent(x -> L2Core.PACKET_HANDLER.toTrackingOnly(PlayerCapToClient.of(e, PlayerCapToClient.Action.TRACK, this.holder, (T)x), e));
   }

   public void startTracking(ServerPlayer tracker, ServerPlayer target) {
      this.holder
         .getExisting(target)
         .ifPresent(x -> L2Core.PACKET_HANDLER.toClientPlayer(PlayerCapToClient.of(target, PlayerCapToClient.Action.TRACK, this.holder, (T)x), tracker));
   }
}
