package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.api.Cancelable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class PlayerItemPickupEvent extends PlayerEvent {
    private final ItemEntity itemEntity;
    private final ItemStack stack;

    public PlayerItemPickupEvent(Player player, ItemEntity itemEntity, ItemStack stack) {
        super(player);
        this.itemEntity = Objects.requireNonNull(itemEntity, "itemEntity");
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public ItemStack getStack() {
        return stack;
    }
}
