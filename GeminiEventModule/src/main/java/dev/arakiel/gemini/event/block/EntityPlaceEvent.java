package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class EntityPlaceEvent extends GeminiEvent {
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;
    private final @Nullable Entity entity;

    public EntityPlaceEvent(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Entity entity) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
        this.entity = entity;
    }

    public LevelAccessor getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }

    public @Nullable Entity getEntity() {
        return this.entity;
    }
}
