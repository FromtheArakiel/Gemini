package dev.arakiel.gemini.event.chunk;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class ChunkEvent extends GeminiEvent {
    public enum Phase {
        LOAD,
        UNLOAD
    }

    private final Level level;
    private final ChunkPos pos;
    private final Phase phase;

    public ChunkEvent(Level level, ChunkPos pos, Phase phase) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Level getLevel() {
        return this.level;
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public Phase getPhase() {
        return this.phase;
    }
}
