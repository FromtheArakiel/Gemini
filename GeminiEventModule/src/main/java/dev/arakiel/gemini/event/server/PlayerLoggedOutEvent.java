package dev.arakiel.gemini.event.server;

import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;

public class PlayerLoggedOutEvent extends PlayerEvent {
    public PlayerLoggedOutEvent(Player player) {
        super(player);
    }
}
