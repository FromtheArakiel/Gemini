package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterKeyMappingsEvent extends GeminiEvent {
    private final List<KeyMapping> mappings = new ArrayList<>();

    public void register(KeyMapping mapping) {
        this.mappings.add(Objects.requireNonNull(mapping, "mapping"));
    }

    public List<KeyMapping> getMappings() {
        return List.copyOf(this.mappings);
    }
}
