package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class BlockGrowFeatureEvent extends GeminiEvent {
    private final ServerLevel level;
    private final BlockPos pos;
    private final @Nullable ServerPlayer player;
    private BlockState targetState;

    public BlockGrowFeatureEvent(
            ServerLevel level,
            BlockPos pos,
            @Nullable ServerPlayer player,
            BlockState targetState
    ) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.player = player;
        this.targetState = Objects.requireNonNull(targetState, "targetState");
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public @Nullable ServerPlayer getPlayer() {
        return this.player;
    }

    public BlockState getTargetState() {
        return this.targetState;
    }

    public void setTargetState(BlockState targetState) {
        this.targetState = Objects.requireNonNull(targetState, "targetState");
    }
}
