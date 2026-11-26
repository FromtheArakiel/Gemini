package dev.arakiel.gemini.event.combat;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

@Cancelable
public class SweepAttackEvent extends GeminiEvent {
    private final Player player;
    private final Entity target;
    private final boolean vanillaSweep;
    private boolean sweeping;

    public SweepAttackEvent(Player player, Entity target, boolean vanillaSweep) {
        this.player = Objects.requireNonNull(player, "player");
        this.target = Objects.requireNonNull(target, "target");
        this.vanillaSweep = vanillaSweep;
        this.sweeping = vanillaSweep;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Entity getTarget() {
        return this.target;
    }

    public boolean isVanillaSweep() {
        return this.vanillaSweep;
    }

    public boolean isSweeping() {
        return this.sweeping;
    }

    public void setSweeping(boolean sweeping) {
        this.sweeping = sweeping;
    }
}
