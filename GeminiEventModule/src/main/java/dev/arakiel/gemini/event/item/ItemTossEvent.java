package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class ItemTossEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack stack;
    private final @Nullable ItemEntity itemEntity;

    public ItemTossEvent(Player player, ItemEntity itemEntity) {
        this(player, itemEntity.getItem(), itemEntity);
    }

    public ItemTossEvent(Player player, ItemStack stack) {
        this(player, stack, null);
    }

    private ItemTossEvent(Player player, ItemStack stack, @Nullable ItemEntity itemEntity) {
        this.player = Objects.requireNonNull(player, "player");
        this.stack = Objects.requireNonNull(stack, "stack");
        this.itemEntity = itemEntity;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public @Nullable ItemEntity getItemEntity() {
        return this.itemEntity;
    }
}
