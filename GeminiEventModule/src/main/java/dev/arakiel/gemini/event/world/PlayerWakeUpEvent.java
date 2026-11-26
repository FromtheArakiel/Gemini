package dev.arakiel.gemini.event.world;

import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;

public class PlayerWakeUpEvent extends PlayerEvent {
    private final boolean updateLevel;

    public PlayerWakeUpEvent(Player player, boolean updateLevel) {
        super(player);
        this.updateLevel = updateLevel;
    }

    public boolean shouldUpdateLevel() {
        return this.updateLevel;
    }
}
