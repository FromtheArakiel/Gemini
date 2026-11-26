package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;

import java.util.Objects;

@Cancelable
public class ClientChatEvent extends GeminiEvent {
    private String message;
    private final String originalMessage;

    public ClientChatEvent(String message) {
        this.originalMessage = Objects.requireNonNull(message, "message");
        this.message = this.originalMessage;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = Objects.requireNonNull(message, "message");
    }

    public String getOriginalMessage() {
        return this.originalMessage;
    }
}
