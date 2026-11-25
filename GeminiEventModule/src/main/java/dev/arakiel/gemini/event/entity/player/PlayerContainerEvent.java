package dev.arakiel.gemini.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Objects;

public class PlayerContainerEvent extends PlayerEvent {
    public enum Phase {
        OPEN,
        CLOSE
    }

    private final Phase phase;
    private final AbstractContainerMenu menu;

    public PlayerContainerEvent(Player player, Phase phase, AbstractContainerMenu menu) {
        super(player);
        this.phase = Objects.requireNonNull(phase, "phase");
        this.menu = Objects.requireNonNull(menu, "menu");
    }

    public Phase getPhase() {
        return this.phase;
    }

    public AbstractContainerMenu getMenu() {
        return this.menu;
    }
}
