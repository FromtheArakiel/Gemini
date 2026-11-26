package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerContainerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class PlayerContainerEventMixin {
    @Inject(method = "openMenu", at = @At("RETURN"))
    private void gemini$openContainer(MenuProvider provider, CallbackInfoReturnable<OptionalInt> callback) {
        if (callback.getReturnValue().isPresent()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            GeminiEvents.post(new PlayerContainerEvent(
                    player,
                    PlayerContainerEvent.Phase.OPEN,
                    player.containerMenu
            ));
        }
    }

    @Inject(method = "doCloseContainer", at = @At("HEAD"))
    private void gemini$closeContainer(CallbackInfo callback) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        GeminiEvents.post(new PlayerContainerEvent(
                player,
                PlayerContainerEvent.Phase.CLOSE,
                player.containerMenu
        ));
    }
}
