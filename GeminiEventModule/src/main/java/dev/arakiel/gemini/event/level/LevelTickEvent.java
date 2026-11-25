package dev.arakiel.gemini.event.level;

import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

public class LevelTickEvent extends ServerLevelEvent {
    private final Phase phase;

    public LevelTickEvent(ServerLevel level, Phase phase) {
        super(level);
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
