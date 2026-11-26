package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.world.CanContinueSleepingEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class CanContinueSleepingEventMixin {
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZ)V"
            )
    )
    private void gemini$canContinueSleeping(Player player, boolean wakeImmediately, boolean updateLevel) {
        CanContinueSleepingEvent event = new CanContinueSleepingEvent(
                player,
                player.level().isDay() ? Player.BedSleepingProblem.NOT_POSSIBLE_NOW : null
        );
        GeminiEvents.post(event);
        if (!event.mayContinueSleeping()) {
            player.stopSleepInBed(wakeImmediately, updateLevel);
        }
    }
}
