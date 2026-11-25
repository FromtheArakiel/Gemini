package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;

public class DataPackReloadEvent extends GeminiEvent {
    public enum Phase {
        START,
        END
    }

    private final MinecraftServer server;
    private final Phase phase;
    private final boolean successful;

    public DataPackReloadEvent(MinecraftServer server, Phase phase, boolean successful) {
        this.server = Objects.requireNonNull(server, "server");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.successful = successful;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public boolean isSuccessful() {
        return this.successful;
    }
}
