package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.packs.PackType;

import java.util.Objects;

public class AddPackFindersEvent extends GeminiEvent {
    private final PackType type;

    public AddPackFindersEvent(PackType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public PackType getType() {
        return this.type;
    }
}
