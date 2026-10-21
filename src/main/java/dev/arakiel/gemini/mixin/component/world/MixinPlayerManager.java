package dev.arakiel.gemini.mixin.component.world;

import dev.arakiel.gemini.component.api.ComponentCallbacks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerList.class})
public abstract class MixinPlayerManager {
   @Inject(
      method = {"sendLevelInfo"},
      at = {@At("RETURN")}
   )
   private void sendWorldInfo(ServerPlayer player, ServerLevel world, CallbackInfo ci) {
      ((ComponentCallbacks.WorldSync)ComponentCallbacks.WorldSync.EVENT.invoker()).onPlayerStartTracking(player, world);
   }
}
