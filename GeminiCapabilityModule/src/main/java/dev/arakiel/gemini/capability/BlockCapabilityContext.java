package dev.arakiel.gemini.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record BlockCapabilityContext(
        Level level,
        BlockPos pos,
        BlockState state,
        @Nullable BlockEntity blockEntity,
        @Nullable Direction direction
) implements CapabilityContext {
    public BlockCapabilityContext {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(state, "state");
    }
}
