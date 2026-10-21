package dev.arakiel.gemini.component;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentCallbacksTest {
    @Test
    void playerSyncEventDispatchesListeners() {
        AtomicInteger calls = new AtomicInteger();
        ComponentCallbacks.PlayerSync.EVENT.register(player -> calls.incrementAndGet());

        ((ComponentCallbacks.PlayerSync) ComponentCallbacks.PlayerSync.EVENT.invoker()).onPlayerSync(null);

        assertEquals(1, calls.get());
    }
}
