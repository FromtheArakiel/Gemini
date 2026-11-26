package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.network.chat.Component;

import java.util.Objects;

@Cancelable
public class ClientChatReceivedEvent extends GeminiEvent {
    private Component message;

    public ClientChatReceivedEvent(Component message) {
        this.message = Objects.requireNonNull(message, "message");
    }

    public Component getMessage() {
        return this.message;
    }

    public void setMessage(Component message) {
        this.message = Objects.requireNonNull(message, "message");
    }
}
