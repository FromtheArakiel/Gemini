package dev.arakiel.gemini.event.world;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

@Cancelable
public class NoteBlockEvent extends GeminiEvent {
    public enum Phase {
        PLAY,
        CHANGE
    }

    private final Level level;
    private final BlockPos pos;
    private final Phase phase;
    private int note;

    public NoteBlockEvent(Level level, BlockPos pos, Phase phase, int note) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.note = note;
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public int getNote() {
        return this.note;
    }

    public void setNote(int note) {
        this.note = Math.max(0, Math.min(24, note));
    }
}
