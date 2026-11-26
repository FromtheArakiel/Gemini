package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterClientReloadListenersEvent extends GeminiEvent {
    private final List<IdentifiableResourceReloadListener> listeners = new ArrayList<>();

    public void register(IdentifiableResourceReloadListener listener) {
        this.listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public List<IdentifiableResourceReloadListener> getListeners() {
        return List.copyOf(this.listeners);
    }
}
