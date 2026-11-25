package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.Item;

import java.util.Objects;

public class ItemCooldownEndEvent extends GeminiEvent {
    private final Item item;

    public ItemCooldownEndEvent(Item item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    public Item getItem() {
        return item;
    }
}
