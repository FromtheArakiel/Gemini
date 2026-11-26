package dev.arakiel.gemini.event.world;

import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class CanContinueSleepingEvent extends PlayerEvent {
    private final @Nullable Player.BedSleepingProblem originalProblem;
    private @Nullable Player.BedSleepingProblem problem;
    private boolean continueSleeping;

    public CanContinueSleepingEvent(Player player, @Nullable Player.BedSleepingProblem problem) {
        super(player);
        this.originalProblem = problem;
        this.problem = problem;
        this.continueSleeping = problem == null;
    }

    public @Nullable Player.BedSleepingProblem getProblem() {
        return this.problem;
    }

    public void setProblem(@Nullable Player.BedSleepingProblem problem) {
        this.problem = problem;
    }

    public boolean mayContinueSleeping() {
        return this.continueSleeping;
    }

    public void setContinueSleeping(boolean continueSleeping) {
        this.continueSleeping = continueSleeping;
    }

    public @Nullable Player.BedSleepingProblem getOriginalProblem() {
        return this.originalProblem;
    }
}
