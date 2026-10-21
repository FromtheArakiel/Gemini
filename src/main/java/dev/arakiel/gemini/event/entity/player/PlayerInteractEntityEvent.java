package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

@Cancelable
public class PlayerInteractEntityEvent extends PlayerEvent {
    private final Entity target;
    private final InteractionHand hand;

    public PlayerInteractEntityEvent(Player player, Entity target, InteractionHand hand) {
        super(player);
        this.target = Objects.requireNonNull(target, "target");
        this.hand = Objects.requireNonNull(hand, "hand");
    }

    public Entity getTarget() {
        return target;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
