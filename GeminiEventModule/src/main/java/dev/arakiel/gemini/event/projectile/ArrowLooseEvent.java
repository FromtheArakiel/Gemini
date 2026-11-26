package dev.arakiel.gemini.event.projectile;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ArrowLooseEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack bow;
    private int charge;

    public ArrowLooseEvent(Player player, ItemStack bow, int charge) {
        this.player = Objects.requireNonNull(player, "player");
        this.bow = Objects.requireNonNull(bow, "bow");
        this.charge = charge;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getBow() {
        return this.bow;
    }

    public int getCharge() {
        return this.charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}
