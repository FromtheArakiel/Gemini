package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.world.PlayerSetSpawnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerSetSpawnEventMixin {
    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void gemini$setSpawn(
            ResourceKey<Level> dimension,
            BlockPos pos,
            float angle,
            boolean forced,
            boolean sendMessage,
            CallbackInfo callback
    ) {
        PlayerSetSpawnEvent event = new PlayerSetSpawnEvent(
                (ServerPlayer) (Object) this,
                dimension,
                pos,
                forced
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}
