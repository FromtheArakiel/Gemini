package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.EventResult;
import dev.arakiel.gemini.event.GeminiEvent;
import dev.arakiel.gemini.event.HasResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

@HasResult
public class CreateFluidSourceEvent extends GeminiEvent {
    private final Level level;
    private final BlockPos pos;

    public CreateFluidSourceEvent(Level level, BlockPos pos) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean allowsSource() {
        return this.getResult() != EventResult.DENY;
    }
}
