package dev.arakiel.gemini.event.projectile;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ArrowNockEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack bow;
    private final InteractionHand hand;
    private boolean hasAmmo;

    public ArrowNockEvent(Player player, ItemStack bow, InteractionHand hand, boolean hasAmmo) {
        this.player = Objects.requireNonNull(player, "player");
        this.bow = Objects.requireNonNull(bow, "bow");
        this.hand = Objects.requireNonNull(hand, "hand");
        this.hasAmmo = hasAmmo;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getBow() {
        return this.bow;
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public boolean hasAmmo() {
        return this.hasAmmo;
    }

    public void setHasAmmo(boolean hasAmmo) {
        this.hasAmmo = hasAmmo;
    }
}
