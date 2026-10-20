package dev.arakiel.gemini.event.api;

@FunctionalInterface
public interface EventListener {
    void invoke(GeminiEvent event);
}
