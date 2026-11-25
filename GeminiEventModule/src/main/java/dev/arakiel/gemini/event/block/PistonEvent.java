package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;

import java.util.Objects;

@Cancelable
public class PistonEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final Level level;
    private final BlockPos pos;
    private final Direction direction;
    private final boolean extending;
    private final Phase phase;
    private final PistonStructureResolver resolver;

    public PistonEvent(
            Level level,
            BlockPos pos,
            Direction direction,
            boolean extending,
            Phase phase,
            PistonStructureResolver resolver
    ) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.direction = Objects.requireNonNull(direction, "direction");
        this.extending = extending;
        this.phase = Objects.requireNonNull(phase, "phase");
        this.resolver = Objects.requireNonNull(resolver, "resolver");
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isExtending() {
        return this.extending;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public PistonStructureResolver getResolver() {
        return this.resolver;
    }
}
