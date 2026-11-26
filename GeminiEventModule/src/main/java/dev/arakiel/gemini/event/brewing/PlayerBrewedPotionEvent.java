package dev.arakiel.gemini.event.brewing;

import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class PlayerBrewedPotionEvent extends PlayerEvent {
    private final ItemStack stack;

    public PlayerBrewedPotionEvent(net.minecraft.world.entity.player.Player player, ItemStack stack) {
        super(player);
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
