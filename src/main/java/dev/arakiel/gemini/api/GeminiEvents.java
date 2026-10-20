package dev.arakiel.gemini.api;

import dev.arakiel.gemini.event.api.EventPriority;
import dev.arakiel.gemini.event.api.GeminiEvent;
import dev.arakiel.gemini.event.api.GeminiEventBus;
import dev.arakiel.gemini.event.api.Subscription;
import dev.arakiel.gemini.event.bridge.FabricEventBridge;

import java.util.function.Consumer;

public final class GeminiEvents {
    public static final GeminiEventBus BUS = new GeminiEventBus();
    private static boolean initialized;

    private GeminiEvents() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        FabricEventBridge.register();
    }

    public static <T extends GeminiEvent> T post(T event) {
        return BUS.post(event);
    }

    public static void register(Object target) {
        BUS.register(target);
    }

    public static void unregister(Object target) {
        BUS.unregister(target);
    }

    public static <T extends GeminiEvent> Subscription listen(Class<T> eventType, Consumer<? super T> consumer) {
        return BUS.listen(eventType, consumer);
    }

    public static <T extends GeminiEvent> Subscription listen(
            Class<T> eventType,
            EventPriority priority,
            Consumer<? super T> consumer
    ) {
        return BUS.listen(eventType, priority, consumer);
    }

    public static <T extends GeminiEvent> Subscription listen(
            Class<T> eventType,
            EventPriority priority,
            boolean receiveCanceled,
            Consumer<? super T> consumer
    ) {
        return BUS.listen(eventType, priority, receiveCanceled, consumer);
    }
}
