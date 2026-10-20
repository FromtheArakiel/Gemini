package dev.arakiel.gemini.event.server;

import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;

public class PlayerLoggedInEvent extends PlayerEvent {
    public PlayerLoggedInEvent(Player player) {
        super(player);
    }
}
