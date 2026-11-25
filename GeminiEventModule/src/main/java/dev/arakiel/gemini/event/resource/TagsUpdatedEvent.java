package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.RegistryAccess;

import java.util.Objects;

public class TagsUpdatedEvent extends GeminiEvent {
    private final RegistryAccess registries;
    private final boolean client;

    public TagsUpdatedEvent(RegistryAccess registries, boolean client) {
        this.registries = Objects.requireNonNull(registries, "registries");
        this.client = client;
    }

    public RegistryAccess getRegistries() {
        return this.registries;
    }

    public boolean isClient() {
        return this.client;
    }
}
