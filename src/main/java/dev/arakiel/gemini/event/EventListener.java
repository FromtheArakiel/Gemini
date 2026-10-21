package dev.arakiel.gemini.event;

@FunctionalInterface
public interface EventListener {
    void invoke(GeminiEvent event);
}
