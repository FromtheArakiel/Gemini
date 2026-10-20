package dev.arakiel.gemini.event.server;

import net.minecraft.server.MinecraftServer;

import java.util.Objects;

public class ServerTickEvent extends ServerEvent {
    private final Phase phase;

    public ServerTickEvent(MinecraftServer server, Phase phase) {
        super(server);
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Phase getPhase() {
        return phase;
    }

    public enum Phase {
        PRE,
        POST
    }
}
