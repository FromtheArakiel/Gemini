package dev.arakiel.gemini.event.enchant;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class PlayerEnchantItemEvent extends PlayerEvent {
    private final ItemStack stack;
    private final int levels;

    public PlayerEnchantItemEvent(net.minecraft.world.entity.player.Player player, ItemStack stack, int levels) {
        super(player);
        this.stack = Objects.requireNonNull(stack, "stack");
        this.levels = Math.max(0, levels);
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public int getLevels() {
        return this.levels;
    }
}
