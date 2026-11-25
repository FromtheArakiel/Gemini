package dev.arakiel.gemini.event;

public final class Subscription implements AutoCloseable {
    private final GeminiEventBus bus;
    private final Object owner;
    private boolean closed;

    Subscription(GeminiEventBus bus, Object owner) {
        this.bus = bus;
        this.owner = owner;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        bus.unregister(owner);
    }
}
