package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class PlayerEvent extends LivingEvent {
    private final Player player;

    public PlayerEvent(Player player) {
        super(Objects.requireNonNull(player, "player"));
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
