package dev.arakiel.gemini.event.entity.player;

import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class PlayerTickEvent extends PlayerEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final Phase phase;

    public PlayerTickEvent(Player player, Phase phase) {
        super(player);
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Phase getPhase() {
        return this.phase;
    }
}
