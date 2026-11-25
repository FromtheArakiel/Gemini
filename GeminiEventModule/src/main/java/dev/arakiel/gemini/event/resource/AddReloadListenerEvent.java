package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddReloadListenerEvent extends GeminiEvent {
    private final List<IdentifiableResourceReloadListener> listeners = new ArrayList<>();

    public AddReloadListenerEvent() {
    }

    public void addListener(IdentifiableResourceReloadListener listener) {
        this.listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public List<IdentifiableResourceReloadListener> getListeners() {
        return List.copyOf(this.listeners);
    }
}
