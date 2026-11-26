package dev.arakiel.gemini.mixin.client.event;

import com.mojang.authlib.GameProfile;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.ClientChatReceivedEvent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
public abstract class ClientChatReceivedEventMixin {
    @Unique
    private ClientChatReceivedEvent gemini$chatEvent;

    @ModifyVariable(
            method = "showMessageToPlayer",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Component gemini$modifyPlayerChat(Component message) {
        this.gemini$chatEvent = new ClientChatReceivedEvent(message);
        GeminiEvents.post(this.gemini$chatEvent);
        return this.gemini$chatEvent.getMessage();
    }

    @Inject(method = "showMessageToPlayer", at = @At("RETURN"), cancellable = true)
    private void gemini$cancelPlayerChat(
            ChatType.Bound bound,
            PlayerChatMessage signed,
            Component message,
            GameProfile sender,
            boolean filtered,
            Instant timestamp,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (this.gemini$chatEvent != null && this.gemini$chatEvent.isCanceled()) {
            callback.setReturnValue(false);
        }
        this.gemini$chatEvent = null;
    }

    @ModifyVariable(method = "handleSystemMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component gemini$modifySystemChat(Component message) {
        this.gemini$ensureSystemEvent(message);
        return this.gemini$chatEvent.getMessage();
    }

    @Inject(method = "handleSystemMessage", at = @At("HEAD"), cancellable = true)
    private void gemini$cancelSystemChat(Component message, boolean overlay, CallbackInfo callback) {
        if (this.gemini$ensureSystemEvent(message).isCanceled()) {
            callback.cancel();
        }
        this.gemini$chatEvent = null;
    }

    @Unique
    private ClientChatReceivedEvent gemini$ensureSystemEvent(Component message) {
        if (this.gemini$chatEvent == null) {
            this.gemini$chatEvent = new ClientChatReceivedEvent(message);
            GeminiEvents.post(this.gemini$chatEvent);
        }
        return this.gemini$chatEvent;
    }
}
