package dev.arakiel.gemini.event.server;

import net.minecraft.server.MinecraftServer;

import java.util.Objects;

public class ServerSaveEvent extends ServerEvent {
    private final Phase phase;
    private final boolean flush;
    private final boolean forced;

    public ServerSaveEvent(MinecraftServer server, Phase phase, boolean flush, boolean forced) {
        super(server);
        this.phase = Objects.requireNonNull(phase, "phase");
        this.flush = flush;
        this.forced = forced;
    }

    public Phase getPhase() {
        return phase;
    }

    public boolean isFlush() {
        return flush;
    }

    public boolean isForced() {
        return forced;
    }

    public enum Phase {
        PRE,
        POST
    }
}
