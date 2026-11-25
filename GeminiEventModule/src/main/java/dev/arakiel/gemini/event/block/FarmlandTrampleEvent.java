package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

@Cancelable
public class FarmlandTrampleEvent extends GeminiEvent {
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final Entity entity;
    private final double fallDistance;

    public FarmlandTrampleEvent(Level level, BlockPos pos, BlockState state, Entity entity, double fallDistance) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
        this.entity = Objects.requireNonNull(entity, "entity");
        this.fallDistance = fallDistance;
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

    public Entity getEntity() {
        return this.entity;
    }

    public double getFallDistance() {
        return this.fallDistance;
    }
}
