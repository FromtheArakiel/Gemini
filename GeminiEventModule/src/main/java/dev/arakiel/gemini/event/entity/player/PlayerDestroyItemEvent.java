package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PlayerDestroyItemEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack original;
    private final @Nullable InteractionHand hand;

    public PlayerDestroyItemEvent(Player player, ItemStack original, @Nullable InteractionHand hand) {
        this.player = Objects.requireNonNull(player, "player");
        this.original = Objects.requireNonNull(original, "original");
        this.hand = hand;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getOriginal() {
        return this.original;
    }

    public @Nullable InteractionHand getHand() {
        return this.hand;
    }
}
