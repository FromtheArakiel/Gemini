package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.ClientPlayerNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayerNetworkEventMixin {
    @Unique
    private LocalPlayer gemini$oldPlayer;

    @Inject(method = "handleRespawn", at = @At("HEAD"))
    private void gemini$captureOldPlayer(ClientboundRespawnPacket packet, CallbackInfo callback) {
        this.gemini$oldPlayer = Minecraft.getInstance().player;
    }

    @Inject(method = "handleRespawn", at = @At("RETURN"))
    private void gemini$clientClone(ClientboundRespawnPacket packet, CallbackInfo callback) {
        LocalPlayer replacement = Minecraft.getInstance().player;
        GeminiEvents.post(new ClientPlayerNetworkEvent(
                ClientPlayerNetworkEvent.Phase.CLONE,
                this.gemini$oldPlayer,
                replacement
        ));
        this.gemini$oldPlayer = null;
    }
}
