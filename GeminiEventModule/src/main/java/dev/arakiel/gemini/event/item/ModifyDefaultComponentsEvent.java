package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ModifyDefaultComponentsEvent extends GeminiEvent {
    private final ItemStack defaultStack;

    public ModifyDefaultComponentsEvent(ItemStack defaultStack) {
        this.defaultStack = Objects.requireNonNull(defaultStack, "defaultStack");
    }

    public ItemStack getDefaultStack() {
        return this.defaultStack;
    }
}
