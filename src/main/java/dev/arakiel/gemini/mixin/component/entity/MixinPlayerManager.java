package dev.arakiel.gemini.mixin.component.entity;

import dev.arakiel.gemini.component.entity.PlayerSyncCallback;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerList.class})
public abstract class MixinPlayerManager {
   @Inject(
      method = {"placeNewPlayer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/players/PlayerList;sendActivePlayerEffects(Lnet/minecraft/server/level/ServerPlayer;)V"
      )}
   )
   private void onPlayerLogIn(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
      ((PlayerSyncCallback)PlayerSyncCallback.EVENT.invoker()).onPlayerSync(player);
   }

   @Inject(
      method = {"sendAllPlayerInfo"},
      at = {@At("RETURN")}
   )
   private void sendPlayerStatus(ServerPlayer player, CallbackInfo info) {
      ((PlayerSyncCallback)PlayerSyncCallback.EVENT.invoker()).onPlayerSync(player);
   }

   @Inject(
      method = {"respawn"},
      at = {@At("RETURN")}
   )
   private void respawnPlayer(ServerPlayer player, boolean alive, RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
      ((PlayerSyncCallback)PlayerSyncCallback.EVENT.invoker()).onPlayerSync((ServerPlayer)cir.getReturnValue());
   }
}
