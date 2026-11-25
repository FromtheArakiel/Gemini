package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class PlayerItemPickupEvent extends PlayerEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final ItemEntity itemEntity;
    private final ItemStack stack;
    private final Phase phase;

    public PlayerItemPickupEvent(Player player, ItemEntity itemEntity, ItemStack stack) {
        this(player, itemEntity, stack, Phase.PRE);
    }

    public PlayerItemPickupEvent(Player player, ItemEntity itemEntity, ItemStack stack, Phase phase) {
        super(player);
        this.itemEntity = Objects.requireNonNull(itemEntity, "itemEntity");
        this.stack = Objects.requireNonNull(stack, "stack");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Phase getPhase() {
        return this.phase;
    }
}
