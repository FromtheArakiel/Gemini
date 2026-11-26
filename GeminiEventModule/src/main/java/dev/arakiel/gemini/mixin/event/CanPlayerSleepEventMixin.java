package dev.arakiel.gemini.mixin.event;

import com.mojang.datafixers.util.Either;
import dev.arakiel.gemini.event.EventResult;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.world.CanPlayerSleepEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class CanPlayerSleepEventMixin {
    @Inject(method = "startSleepInBed", at = @At("RETURN"), cancellable = true)
    private void gemini$canSleep(
            BlockPos pos,
            CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> callback
    ) {
        Player.BedSleepingProblem originalProblem = callback.getReturnValue().left().orElse(null);
        CanPlayerSleepEvent event = new CanPlayerSleepEvent(
                (ServerPlayer) (Object) this,
                pos,
                originalProblem
        );
        GeminiEvents.post(event);
        if (event.getResult() == EventResult.DENY) {
            callback.setReturnValue(Either.left(
                    event.getProblem() == null ? Player.BedSleepingProblem.NOT_POSSIBLE_NOW : event.getProblem()
            ));
        } else if (event.getResult() == EventResult.ALLOW) {
            callback.setReturnValue(Either.right(Unit.INSTANCE));
        } else if (event.getProblem() != originalProblem) {
            callback.setReturnValue(event.getProblem() == null
                    ? Either.right(Unit.INSTANCE)
                    : Either.left(event.getProblem()));
        }
    }
}
