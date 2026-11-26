package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.world.SleepFinishedTimeEvent;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class SleepFinishedTimeEventMixin {
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V",
                    ordinal = 0
            )
    )
    private void gemini$sleepFinishedTime(ServerLevel level, long newTime) {
        SleepFinishedTimeEvent event = new SleepFinishedTimeEvent(level, newTime);
        GeminiEvents.post(event);
        level.setDayTime(event.getNewTime());
    }
}
