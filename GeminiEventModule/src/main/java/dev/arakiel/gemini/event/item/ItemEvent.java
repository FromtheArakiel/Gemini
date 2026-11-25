package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ItemEvent extends GeminiEvent {
    private final ItemStack stack;

    public ItemEvent(ItemStack stack) {
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public ItemStack getStack() {
        return stack;
    }
}
