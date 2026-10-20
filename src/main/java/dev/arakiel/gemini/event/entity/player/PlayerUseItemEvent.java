package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.api.Cancelable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class PlayerUseItemEvent extends PlayerEvent {
    private final InteractionHand hand;
    private final ItemStack stack;

    public PlayerUseItemEvent(Player player, InteractionHand hand, ItemStack stack) {
        super(player);
        this.hand = Objects.requireNonNull(hand, "hand");
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public InteractionHand getHand() {
        return hand;
    }

    public ItemStack getStack() {
        return stack;
    }
}
