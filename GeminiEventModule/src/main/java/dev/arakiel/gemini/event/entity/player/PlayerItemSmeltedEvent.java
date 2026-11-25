package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class PlayerItemSmeltedEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack stack;

    public PlayerItemSmeltedEvent(Player player, ItemStack stack) {
        this.player = Objects.requireNonNull(player, "player");
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
