package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ItemEntityPickupEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final Player player;
    private final ItemEntity itemEntity;
    private final ItemStack stack;
    private final Phase phase;

    public ItemEntityPickupEvent(Player player, ItemEntity itemEntity, ItemStack stack, Phase phase) {
        this.player = Objects.requireNonNull(player, "player");
        this.itemEntity = Objects.requireNonNull(itemEntity, "itemEntity");
        this.stack = Objects.requireNonNull(stack, "stack");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemEntity getItemEntity() {
        return this.itemEntity;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public Phase getPhase() {
        return this.phase;
    }
}
