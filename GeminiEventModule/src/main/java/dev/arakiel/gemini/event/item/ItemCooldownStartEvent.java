package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.Item;

import java.util.Objects;

@Cancelable
public class ItemCooldownStartEvent extends GeminiEvent {
    private final Item item;
    private final int ticks;

    public ItemCooldownStartEvent(Item item, int ticks) {
        this.item = Objects.requireNonNull(item, "item");
        this.ticks = ticks;
    }

    public Item getItem() {
        return item;
    }

    public int getTicks() {
        return ticks;
    }
}
