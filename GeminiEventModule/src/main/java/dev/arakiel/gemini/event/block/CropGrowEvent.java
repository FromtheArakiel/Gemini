package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

@Cancelable
public class CropGrowEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final Phase phase;

    public CropGrowEvent(Level level, BlockPos pos, BlockState state, Phase phase) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }

    public Phase getPhase() {
        return this.phase;
    }
}
