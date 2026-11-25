package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

@Cancelable
public class PlayerXpPickupEvent extends PlayerEvent {
    private final ExperienceOrb orb;
    private int amount;

    public PlayerXpPickupEvent(Player player, ExperienceOrb orb, int amount) {
        super(player);
        this.orb = Objects.requireNonNull(orb, "orb");
        this.amount = amount;
    }

    public ExperienceOrb getOrb() {
        return orb;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }
}
