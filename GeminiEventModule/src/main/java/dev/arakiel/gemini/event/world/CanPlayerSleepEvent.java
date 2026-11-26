package dev.arakiel.gemini.event.world;

import dev.arakiel.gemini.event.HasResult;
import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@HasResult
public class CanPlayerSleepEvent extends PlayerEvent {
    private final BlockPos pos;
    private @Nullable Player.BedSleepingProblem problem;

    public CanPlayerSleepEvent(Player player, BlockPos pos, @Nullable Player.BedSleepingProblem problem) {
        super(player);
        this.pos = Objects.requireNonNull(pos, "pos");
        this.problem = problem;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public @Nullable Player.BedSleepingProblem getProblem() {
        return this.problem;
    }

    public void setProblem(@Nullable Player.BedSleepingProblem problem) {
        this.problem = problem;
    }
}
