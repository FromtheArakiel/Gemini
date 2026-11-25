package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.level.LevelEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class BlockEvent extends LevelEvent {
    private final BlockPos pos;
    private final BlockState state;

    public BlockEvent(Level level, BlockPos pos, BlockState state) {
        super(level);
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }
}
