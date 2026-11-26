package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerNegotiationEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class PlayerNegotiationEventMixin {
    @Shadow
    @Final
    Connection connection;

    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void gemini$playerNegotiation(ServerboundHelloPacket packet, CallbackInfo callback) {
        PlayerNegotiationEvent event = new PlayerNegotiationEvent(this.connection);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            this.connection.disconnect(Component.translatable("disconnect.loginFailed"));
            callback.cancel();
        }
    }
}
