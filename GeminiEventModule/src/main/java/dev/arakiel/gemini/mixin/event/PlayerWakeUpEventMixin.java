package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.world.PlayerWakeUpEvent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerWakeUpEventMixin {
    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void gemini$wakeUp(boolean wakeImmediately, boolean updateLevel, CallbackInfo callback) {
        GeminiEvents.post(new PlayerWakeUpEvent(
                (ServerPlayer) (Object) this,
                updateLevel
        ));
    }
}
