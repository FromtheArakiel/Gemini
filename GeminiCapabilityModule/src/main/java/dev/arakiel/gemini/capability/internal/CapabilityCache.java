package dev.arakiel.gemini.capability.internal;

import java.util.concurrent.atomic.AtomicLong;

public final class CapabilityCache {
    private final AtomicLong generation = new AtomicLong();

    public long generation() {
        return this.generation.get();
    }

    public void invalidate() {
        this.generation.incrementAndGet();
    }
}
