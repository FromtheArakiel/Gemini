package dev.arakiel.gemini.mixin.component.entity;

import dev.arakiel.gemini.component.entity.TrackingStartCallback;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerEntity.class})
public abstract class MixinEntityTrackerEntry {
   @Shadow
   @Final
   private Entity entity;

   @Inject(
      method = {"addPairing"},
      at = {@At("RETURN")}
   )
   private void onStartedTracking(ServerPlayer player, CallbackInfo ci) {
      ((TrackingStartCallback)TrackingStartCallback.EVENT.invoker()).onPlayerStartTracking(player, this.entity);
   }
}
