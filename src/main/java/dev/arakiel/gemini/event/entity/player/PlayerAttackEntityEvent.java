package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

@Cancelable
public class PlayerAttackEntityEvent extends PlayerEvent {
    private final Entity target;

    public PlayerAttackEntityEvent(Player player, Entity target) {
        super(player);
        this.target = Objects.requireNonNull(target, "target");
    }

    public Entity getTarget() {
        return target;
    }
}
