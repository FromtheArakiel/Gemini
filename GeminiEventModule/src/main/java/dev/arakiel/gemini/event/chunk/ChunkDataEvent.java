package dev.arakiel.gemini.event.chunk;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class ChunkDataEvent extends GeminiEvent {
    public enum Phase {
        LOAD,
        SAVE
    }

    private final Level level;
    private final ChunkPos pos;
    private final CompoundTag data;
    private final Phase phase;

    public ChunkDataEvent(Level level, ChunkPos pos, CompoundTag data, Phase phase) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.data = Objects.requireNonNull(data, "data");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Level getLevel() {
        return this.level;
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public CompoundTag getData() {
        return this.data;
    }

    public Phase getPhase() {
        return this.phase;
    }
}
